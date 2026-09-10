package com.search_service.exception;

import lombok.Getter;

@Getter
public enum TypeError {
    FILE_READ("Ошибка чтения файла. "),
    LIST_READ("Ошибка чтения списка в файле. "),
    ENTITY_CREATE("Ошибка создания сущности. "),
    EXCEL_CREATE("Ошибка создания Excel файла. "),
    ABSENT_ANNOTATION("Отсутствует аннотация. "),
    ABSENT_METHOD("Отсутствует метод. "),
    FIELDS_NOT_MATCH("Поля не совпадают. "),
    INVALID_FILE_FORMAT("Неверный формат файла. "),
    FILE_IS_EMPTY("Файл пустой. "),
    CELL_CREATE("Ошибка создания ячейки. "),
    ELASTIC_ERROR("Ошибка индексации ES"),
    SAVE_FILE("Ошибка сохранения файла");


    private final String message;

    TypeError(String message) {
        this.message = message;
    }
}