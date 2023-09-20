package com.duke.common.tree;

public class TreeConfigure<T> {
    private String idProperty;
    private String pidProperty;
    private String childrenProperty;
    private T topId;
    private SortFeature sortFeature;
    private FullPathFeature fullPathFeature;
    private String prefix;
    private String suffix;

    public String getIdProperty() {
        return idProperty;
    }

    public String getPidProperty() {
        return pidProperty;
    }

    public String getChildrenProperty() {
        return childrenProperty;
    }

    public T getTopId() {
        return topId;
    }

    public SortFeature getSortFeature() {
        return sortFeature;
    }

    public FullPathFeature getFullPathFeature() {
        return fullPathFeature;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public static TreeConfigureBuilder builder() {
        return new TreeConfigureBuilder();
    }

    public static final class TreeConfigureBuilder<T> {
        private String idProperty;
        private String pidProperty;
        private String childrenProperty;
        private T topId;
        private SortFeature sortFeature;
        private FullPathFeature fullPathFeature;
        private String prefix;
        private String suffix;

        private TreeConfigureBuilder() {
        }

        public TreeConfigureBuilder idProperty(String idProperty) {
            this.idProperty = idProperty;
            return this;
        }

        public TreeConfigureBuilder pidProperty(String pidProperty) {
            this.pidProperty = pidProperty;
            return this;
        }

        public TreeConfigureBuilder childrenProperty(String childrenProperty) {
            this.childrenProperty = childrenProperty;
            return this;
        }

        public TreeConfigureBuilder topId(T topId) {
            this.topId = topId;
            return this;
        }

        public TreeConfigureBuilder sortFeature(String sortProperty, SortDirection sortDirection) {
            this.sortFeature = new SortFeature(sortDirection, sortProperty);
            return this;
        }

        public TreeConfigureBuilder sortFeature(String sortProperty) {
            this.sortFeature = new SortFeature(sortProperty);
            return this;
        }

        public TreeConfigureBuilder fullPathFeature(String fullPathProerty) {
            this.fullPathFeature = new FullPathFeature(fullPathProerty);
            return this;
        }

        public TreeConfigureBuilder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public TreeConfigureBuilder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public TreeConfigure build() {
            TreeConfigure configure = new TreeConfigure();
            configure.idProperty = idProperty;
            configure.pidProperty = pidProperty;
            configure.childrenProperty = childrenProperty;
            configure.topId = topId;
            configure.sortFeature = sortFeature;
            configure.fullPathFeature = fullPathFeature;
            configure.prefix = prefix;
            configure.suffix = suffix;
            return configure;
        }
    }
}
