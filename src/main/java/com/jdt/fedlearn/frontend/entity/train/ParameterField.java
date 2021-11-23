package com.jdt.fedlearn.frontend.entity.train;

public class ParameterField {
    private String field;
    private Object value;

    public ParameterField(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public ParameterField() {
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
