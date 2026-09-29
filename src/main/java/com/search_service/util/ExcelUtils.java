package com.search_service.util;

import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import com.search_service.util.cache.GlobalCacheManager;
import com.search_service.util.cache.LocalCacheContext;
import com.search_service.util.cache.SynonymResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class ExcelUtils {

    public static final int MAX_WIDTH_COLUMN = 10000;
    public static final String XLSX_EXTENSION = ".xlsx";

    private ExcelUtils() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса: " + ExcelUtils.class.getSimpleName());
    }

    public static <T> List<T> readExcelFile(MultipartFile file, Class<T> targetClass) {
        validateFile(file);
        LocalCacheContext<T> context = GlobalCacheManager.getContext(targetClass);
        List<T> result = new ArrayList<>();
        try {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                readWorkBook(workbook, context, result);
            }
            return result;
        } catch (IOException exception) {
            throw new GeneralFormatException(TypeError.FILE_READ, file.getOriginalFilename());
        }
    }

    private static <T> void readWorkBook(Workbook workbook, LocalCacheContext<T> context, List<T> result) {
        for (Sheet sheet : workbook) {
            if (!SynonymResolver.resolveClass(context, sheet.getSheetName())) {
                throw new GeneralFormatException(TypeError.LIST_READ, "Проверь имя листа: " + sheet.getSheetName());
            }
            result.addAll(readSheetFromWorkbook(sheet, context));
        }
    }

    private static <T> List<T> readSheetFromWorkbook(Sheet sheet, LocalCacheContext<T> context) {
        Row header = sheet.getRow(sheet.getFirstRowNum());
        if (!validateSheet(header, context)) {
            throw new GeneralFormatException(TypeError.FIELDS_NOT_MATCH,
                    "Строки в книге: " + sheet.getSheetName() + " не совпадают с необходимыми.\nПроверьте аннотации или содержание файла.");
        }
        return readDataRows(sheet, header, context);
    }

    private static <T> List<T> readDataRows(Sheet sheet, Row header, LocalCacheContext<T> context) {
        List<T> result = new ArrayList<>();
        for (int rowIndex = header.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); ++rowIndex) {
            Row row = sheet.getRow(rowIndex);
            if (isRowEmpty(row, header.getLastCellNum())) {
                continue;
            }
            result.add(readCellFromSheet(row, header, context));
        }
        return result;
    }

    private static boolean isRowEmpty(Row row, int lastColumnIndex) {
        if (row == null) {
            return true;
        }
        for (int columnIndex = 0; columnIndex < lastColumnIndex; ++columnIndex) {
            if (!cellIsBlank(row.getCell(columnIndex))) {
                return false;
            }
        }
        return true;
    }

    private static <T> T readCellFromSheet(Row row, Row header, LocalCacheContext<T> context) {
        try {
            T instance = context.getTargetClass().getDeclaredConstructor().newInstance();
            for (int columnIndex = header.getFirstCellNum(); columnIndex < header.getLastCellNum(); ++columnIndex) {
                String fieldName = SynonymResolver.resolveField(context, getHeaderValue(header.getCell(columnIndex)));
                Field field = SynonymResolver.findFieldIgnoreCase(context, fieldName);
                Class<?> fieldType = context.getFieldTypes().get(fieldName);
                Method setter = field == null ? null : context.getSetters().get(field);
                if (setter == null || fieldType == null) {
                    continue;
                }
                Object value = convertCellToFieldType(row.getCell(columnIndex), fieldType);
                setter.invoke(instance, value);
            }
            return instance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            log.error("Ошибка создания сущности: {}", context.getTargetClass().getSimpleName());
            throw new GeneralFormatException(TypeError.ENTITY_CREATE, exception.getMessage());
        }
    }

    private static <T> boolean validateSheet(Row row, LocalCacheContext<T> context) {
        if (row == null) {
            return false;
        } else {
            Set<String> headerNames = new HashSet<>();
            for (Cell cell : row) {
                checkCell(context, cell, headerNames);
            }
            return headerNames.containsAll(context.getRequiredFields());
        }
    }

    private static <T> void checkCell(LocalCacheContext<T> context, Cell cell, Set<String> headerNames) {
        String resolved = SynonymResolver.resolveField(context, getHeaderValue(cell));
        if (resolved != null) {
            headerNames.add(resolved);
        }
    }

    private static String getHeaderValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            default -> null;
        };
    }

    private static void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new GeneralFormatException(TypeError.FILE_IS_EMPTY, "Загрузите файл формата " + XLSX_EXTENSION);
        } else {
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(XLSX_EXTENSION)) {
                throw new GeneralFormatException(TypeError.INVALID_FILE_FORMAT, "Загружайте файл с расширением " + XLSX_EXTENSION);
            }
        }
    }

    private static boolean cellIsBlank(Cell cell) {
        if (cell == null) {
            return true;
        } else {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim().isBlank();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue()).trim().isBlank();
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim().isBlank();
                default -> true;
            };
        }
    }

    private static <T> T convertCellToFieldType(Cell cell, Class<T> type) {
        String resultValue = getResultValue(cell);
        if (isBlankValue(resultValue)) {
            return null;
        }
        return getParse(type, resultValue.trim());
    }

    private static boolean isBlankValue(String value) {
        return value == null || value.isBlank();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getParse(Class<T> type, String resultValue) {
        if (type == String.class) return (T) resultValue;
        if (type == BigDecimal.class) return (T) parseDecimal(resultValue);
        if (type == Double.class || type == double.class) return (T) Double.valueOf(parseDecimal(resultValue).doubleValue());
        if (type == Float.class || type == float.class) return (T) Float.valueOf(parseDecimal(resultValue).floatValue());
        if (type == Integer.class || type == int.class) return (T) Integer.valueOf(parseDecimal(resultValue).intValueExact());
        if (type == Long.class || type == long.class) return (T) Long.valueOf(parseDecimal(resultValue).longValueExact());
        if (type == Boolean.class || type == boolean.class) return (T) Boolean.valueOf(Boolean.parseBoolean(resultValue));
        if (type.isEnum()) {
            try {
                Method method = type.getMethod("parse", String.class);
                return (T) method.invoke(null, resultValue);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                return (T) Enum.valueOf((Class<Enum>) type, resultValue.toUpperCase());
            }
        }
        return null;
    }

    private static String getResultValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> readNumericValue(cell);
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private static String readNumericValue(Cell cell) {
        return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
    }

    private static BigDecimal parseDecimal(String rawValue) {
        String normalized = rawValue.trim()
                .replace(" ", "")
                .replace("\u00A0", "")
                .replace("_", "");
        int lastDot = normalized.lastIndexOf('.');
        int lastComma = normalized.lastIndexOf(',');
        if (lastDot >= 0 && lastComma >= 0) {
            normalized = lastComma > lastDot
                    ? normalized.replace(".", "").replace(",", ".")
                    : normalized.replace(",", "");
        } else if (lastComma >= 0) {
            normalized = normalized.replace(",", ".");
        }
        return new BigDecimal(normalized);
    }

    public static <T> byte[] exportEntityToExcel(List<T> entityList, Class<T> tClass) {
        try (Workbook workbook = new XSSFWorkbook()) {
            LocalCacheContext<T> context = GlobalCacheManager.getContext(tClass);
            Sheet sheet = workbook.createSheet(tClass.getSimpleName());
            createHeader(sheet, context.getColumnNames());
            addData(sheet, entityList, context);
            autoSize(sheet, context.getColumnNames().size());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException var9) {
            throw new GeneralFormatException(TypeError.EXCEL_CREATE);
        }
    }

    private static void autoSize(Sheet sheet, int size) {
        for (int i = 0; i < size; ++i) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            if (width > MAX_WIDTH_COLUMN) {
                sheet.setColumnWidth(i, MAX_WIDTH_COLUMN);
            }
        }
    }

    private static <T> void addData(Sheet sheet, List<T> entityList, LocalCacheContext<T> context) {
        for (int i = 0; i < entityList.size(); ++i) {
            parseEntityToRow(sheet.createRow(i + 1), entityList.get(i), context);
        }
    }

    private static <T> void parseEntityToRow(Row row, T item, LocalCacheContext<T> context) {
        try {
            int index = 0;
            for (Map.Entry<Field, String> entry : context.getColumnNames().entrySet()) {
                Object value = context.getGetters().get(entry.getKey()).invoke(item);
                addDataInCell(row, index, value);
                ++index;
            }
        } catch (IllegalAccessException | InvocationTargetException var7) {
            throw new GeneralFormatException(TypeError.CELL_CREATE);
        }
    }

    private static void addDataInCell(Row row, int index, Object value) {
        Cell cell = row.createCell(index);
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof DisplayNameProvider) {
            cell.setCellValue(((DisplayNameProvider) value).getDisplayName());
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static void createHeader(Sheet sheet, Map<Field, String> fields) {
        if (fields.isEmpty()) {
            throw new GeneralFormatException(TypeError.EXCEL_CREATE, "Нет полей для экспорта");
        } else {
            int index = 0;
            Row headerRow = sheet.createRow(index);
            for (Map.Entry<Field, String> entry : fields.entrySet()) {
                headerRow.createCell(index++).setCellValue(entry.getValue());
            }
        }
    }
}