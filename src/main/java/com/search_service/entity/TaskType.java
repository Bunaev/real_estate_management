package com.search_service.entity;


import lombok.Getter;

@Getter
public enum TaskType {

    CHANGE_PRICE("Изменение цены", "%s будет %s в %s"),
    NEWS("Новости", "%s"),
    START_OF_SALES("Старт продаж", "%s старт продаж в %s");

    private final String displayName;
    private final String template;

    TaskType(String displayName, String template) {
        this.displayName = displayName;
        this.template = template;
    }

}
