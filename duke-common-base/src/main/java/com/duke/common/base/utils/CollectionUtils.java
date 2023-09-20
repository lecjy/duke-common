package com.duke.common.base.utils;

import java.util.Collection;

public class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException("com.duke.operation not supported");
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> int hashMapInitialCapacity(int totalElement) {
        return (int) (totalElement / 0.75f + 1);
    }
}
