package com.search_service.util.cache;

import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class LocalCacheContext<T> {

    private final Class<T> targetClass;
    private final Map<String, Class<?>> fieldTypes;
    private final Map<String, List<String>> fieldSynonyms;
    private final List<String> classSynonyms;
    private final Map<Field, String> columnNames;
    private final Map<Field, Method> setters;
    private final Map<Field, Method> getters;

    private LocalCacheContext(Class<T> tClass, Map<String, Class<?>> fieldTypes, Map<String, List<String>> fieldSynonyms,
                              List<String> classSynonyms, LinkedHashMap<Field, String> columnName,
                              Map<Field, Method> setters, Map<Field, Method> getters) {
        this.targetClass = tClass;
        this.fieldTypes = Collections.unmodifiableMap(fieldTypes);
        this.fieldSynonyms = Collections.unmodifiableMap(fieldSynonyms);
        this.classSynonyms = Collections.unmodifiableList(classSynonyms);
        this.columnNames = Collections.unmodifiableMap(columnName);
        this.setters = Collections.unmodifiableMap(setters);
        this.getters = Collections.unmodifiableMap(getters);
    }

    static <T> LocalCacheContext<T> create(Class<T> tClass) {
        ExcelClass excelClass = tClass.getAnnotation(ExcelClass.class);
        List<Field> annotatedFields = getAnnotationFields(tClass);
        if (excelClass != null) {
            return new LocalCacheContext<>(tClass, readFieldTypes(annotatedFields), readSynonymFields(annotatedFields),
                    readSynonymsClass(excelClass), readColumnName(annotatedFields), getSettersFields(annotatedFields, tClass),
                    getGettersFields(annotatedFields, tClass));
        } else {
            log.error("Над классом {} нет аннотации @ExcelClass.", tClass.getSimpleName());
            throw new GeneralFormatException(TypeError.ABSENT_ANNOTATION, "Класс " + tClass.getName() + " не аннотирован @ExcelClass");
        }
    }

    private static <T> Map<Field, Method> getGettersFields(List<Field> annotatedFields, Class<T> tClass) {
        try {
            Map<Field, Method> result = new HashMap<>();
            for (Field field : annotatedFields) {
                String name = field.getName();
                String methodName = "get" + capitalizeWord(name);
                Method method = tClass.getMethod(methodName);
                result.put(field, method);
            }
            return result;
        } catch (NoSuchMethodException var8) {
            throw new GeneralFormatException(TypeError.ABSENT_METHOD, "GET");
        }
    }

    private static <T> Map<Field, Method> getSettersFields(List<Field> annotatedFields, Class<T> tClass) {
        try {
            Map<Field, Method> result = new HashMap<>();
            for (Field field : annotatedFields) {
                String name = field.getName();
                String methodName = "set" + capitalizeWord(name);
                Method method = tClass.getMethod(methodName, field.getType());
                result.put(field, method);
            }
            return result;
        } catch (NoSuchMethodException var8) {
            throw new GeneralFormatException(TypeError.ABSENT_METHOD, "SET");
        }
    }

    private static String capitalizeWord(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static LinkedHashMap<Field, String> readColumnName(List<Field> fields) {
        LinkedHashMap<Field, String> columnNames = new LinkedHashMap<>();
        for (Field field : fields) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            String name = annotation.columnName();
            if (name == null || name.isBlank()) {
                name = field.getName();
            }
            columnNames.put(field, name);
        }
        return columnNames;
    }

    private static Map<String, List<String>> readSynonymFields(List<Field> fields) {
        return fields.stream().collect(Collectors.toMap(
                Field::getName,
                field -> Arrays.stream(field.getAnnotation(ExcelColumn.class).synonyms())
                        .map(synonym -> synonym.replaceAll("\\s+", ""))
                        .map(String::toLowerCase)
                        .toList()
        ));
    }

    private static Map<String, Class<?>> readFieldTypes(List<Field> fields) {
        return fields.stream().collect(java.util.stream.Collectors.toMap(Field::getName, Field::getType));
    }

    private static <T> List<Field> getAnnotationFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelColumn.class) != null).toList();
    }

    private static List<String> readSynonymsClass(ExcelClass excelClass) {
        return Arrays.stream(excelClass.synonyms()).map(String::toLowerCase).toList();
    }
}