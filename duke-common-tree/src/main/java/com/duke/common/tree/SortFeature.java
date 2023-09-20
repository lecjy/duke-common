package com.duke.common.tree;

public class SortFeature {
    private SortDirection sortDirection;
    private String sortProperty;

    public SortFeature(SortDirection sortDirection, String sortProperty) {
        this.sortDirection = sortDirection;
        this.sortProperty = sortProperty;
    }

    public SortFeature(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public String getSortProperty() {
        return sortProperty;
    }
}
