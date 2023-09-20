package com.duke.common.tree;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TreeUtils {
    private static final String ID = "id";
    private static final String PID = "pid";
    private static final String CHILDREN = "children";

    private TreeUtils() {
        throw new UnsupportedOperationException("com.duke.operation not support");
    }

    /**
     * @param list 需要被转换成树结构的数据列表，list中的元素至少包含id, pid两个属性及对应的getter方法，返回的列表中每个实体的children属性表示每个元素的子节点
     * @return
     */
    public static <T> List<T> convert2Tree(List<T> list) {
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        return convert2Tree(list, null, ID, PID, CHILDREN, null, null, null);
    }

    public static <T> List<T> convert2Tree(List<T> list, TreeConfigure configure) {
        SortDirection sortDirection = null;
        String sortProperty = null;
        String fullPathName = null;
        String idProperty = ID;
        String pidProperty = PID;
        String childrenProperty = CHILDREN;
        Object topId = null;
        if (configure != null) {
            topId = configure.getTopId();
            if (configure.getIdProperty() != null && !"".equals(configure.getIdProperty())) {
                idProperty = configure.getIdProperty();
            }
            if (configure.getPidProperty() != null && !"".equals(configure.getPidProperty())) {
                pidProperty = configure.getPidProperty();
            }
            if (configure.getChildrenProperty() != null && !"".equals(configure.getChildrenProperty())) {
                childrenProperty = configure.getChildrenProperty();
            }
            SortFeature sortFeature = configure.getSortFeature();
            if (sortFeature != null) {
                sortDirection = sortFeature.getSortDirection();
                if (sortFeature.getSortProperty() != null && !"".equals(sortFeature.getSortProperty())) {
                    sortProperty = sortFeature.getSortProperty();
                }
            }
            FullPathFeature fullPathFeature = configure.getFullPathFeature();
            if (fullPathFeature != null) {
                if (fullPathFeature.getName() != null && !"".equals(fullPathFeature.getName())) {
                    fullPathName = fullPathFeature.getName();
                }

            }
        }
        return convert2Tree(list, topId, idProperty, pidProperty, childrenProperty, sortProperty, sortDirection, fullPathName);
    }

    /**
     * @param list             需要被转换成树结构的数据列表
     * @param idProperty       实体的id属性名
     * @param pidProperty      实体的pid属性名
     * @param childrenProperty 返回的列表中表示每个元素的子节点的属性名
     * @return
     */

    public static <T> List<T> convert2Tree(List<T> list, Object topId, String idProperty, String pidProperty, String childrenProperty, String sortProperty, SortDirection sortDirection, String fullPathName) {
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        Map<Object, T> allNodeMap = new HashMap<>((list.size() * 2));
        Class<?> clazz = list.get(0).getClass();
        List<Field> fieldList = new ArrayList<>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        // 多重继承的类，需要递归处理
//        fieldList.addAll(Arrays.asList(clazz.getSuperclass().getSuperclass().getDeclaredFields()));
        HashMap properties = new HashMap();
        properties.put(childrenProperty, List.class);
        if (fullPathName != null && !"".equals(fullPathName)) {
            properties.put(fullPathName, String.class);
        }
        for (Field field : fieldList) {
            if (!"serialVersionUID".equals(field.getName())) {
                properties.put(field.getName(), field.getType());
            }
        }
        TreeBeanHelper bean = new TreeBeanHelper(properties, clazz);
        clazz = bean.getObject().getClass();
        try {
            // 有属性，有方法，直接遍历加入map
            // 有属性，没有方法
            // 没有属性，没有方法
            // 没有属性，有方法
            String idSuffix = idProperty.substring(0, 1).toUpperCase() + idProperty.substring(1);
            String pidSuffix = pidProperty.substring(0, 1).toUpperCase() + pidProperty.substring(1);
            String childrenSuffix = childrenProperty.substring(0, 1).toUpperCase() + childrenProperty.substring(1);
            // 下面这4个方法是必须的
            Method getId = clazz.getMethod("get" + idSuffix);
            Method getPid = clazz.getMethod("get" + pidSuffix);
            Method getChildren = clazz.getMethod("get" + childrenSuffix);
            Method setChildren = clazz.getMethod("set" + childrenSuffix, List.class);
            // 下面这三个方法可能没有
            Method getSort = null;
            Method getPath = null;
            Method setPath = null;
            if (fullPathName != null && !"".equals(fullPathName)) {
                String pathSuffix = fullPathName.substring(0, 1).toUpperCase() + fullPathName.substring(1);
                getPath = clazz.getMethod("get" + pathSuffix);
                setPath = clazz.getMethod("set" + pathSuffix, String.class);
            }
            if (sortProperty != null && !"".equals(sortProperty)) {
                String sortPropertySuffix = sortProperty.substring(0, 1).toUpperCase() + sortProperty.substring(1);
                getSort = clazz.getMethod("get" + sortPropertySuffix);
            }
            for (T node : list) {
                Object value = null;
                TreeBeanHelper beanHelper = new TreeBeanHelper(properties, clazz);
                for (Field field : fieldList) {
                    field.setAccessible(true);
                    if (idProperty.equals(field.getName())) {
                        value = field.get(node);
                    }
                    beanHelper.setValue(field.getName(), field.get(node));
                }
                allNodeMap.put(value, (T) beanHelper.getObject());
            }
            return doConvert(allNodeMap, topId, getId, getPid, getChildren, setChildren, getSort, getPath, setPath, sortDirection);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static <T> List<T> doConvert(Map<Object, T> allNodeMap, Object topId, Method getId, Method getPid, Method getChildren, Method setChildren, Method getSort, Method getPath, Method setPath, SortDirection sortDirection) {
        List<T> topNodes = new ArrayList<>(allNodeMap.size());
        try {
            for (Object key : allNodeMap.keySet()) {
                T node = allNodeMap.get(key);
                Object pid = getPid.invoke(node, null);
                Object parent = allNodeMap.get(pid);
                if (parent == null) {
                    topNodes.add(node);
                } else {
                    List children = (List) getChildren.invoke(parent, null);
                    if (children == null) {
                        children = new ArrayList();
                    }
                    children.add(node);
                    if (getSort != null) {
                        sort(children, getSort, sortDirection);
                    }
                    setChildren.invoke(parent, children);
                }
            }
            topNodes = topId == null || allNodeMap.get(topId) == null ? topNodes : (List<T>) getChildren.invoke(allNodeMap.get(topId), null);
            if (getSort != null) {
                sort(topNodes, getSort, sortDirection);
            }
            if (getPath != null && setPath != null) {
                for (Object node : topNodes) {
                    setPath.invoke(node, getId.invoke(node, null) + "");
                    fillPath(node, getChildren, getPath, setPath, getId);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("生成树结构失败！");
        }
        return topNodes;
    }

    private static <T> void sort(List<T> list, Method getSort, SortDirection sortDirection) {
        Collections.sort(list, (Object currentNode, Object nextNode) -> {
            try {
                Object currentNodeOrder = getSort.invoke(currentNode, null);
                if (currentNodeOrder instanceof Integer) {
                    Integer sorting1 = (Integer) getSort.invoke(currentNode, null);
                    Integer sorting2 = (Integer) getSort.invoke(nextNode, null);
                    if (sortDirection != null && sortDirection == SortDirection.DESC) {
                        return (sorting2 == null ? 0 : sorting2) - (sorting1 == null ? 0 : sorting1);
                    }
                    return (sorting1 == null ? 0 : sorting1) - (sorting2 == null ? 0 : sorting2);
                } else {
                    String sort = getSort.invoke(currentNode, null) + "";
                    if (sortDirection != null && sortDirection == SortDirection.DESC) {
                        return (getSort.invoke(nextNode, null) + "").compareToIgnoreCase(sort);
                    }
                    return sort.compareToIgnoreCase("" + getSort.invoke(nextNode, null));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException("排序出错！");
            }
        });
    }

    private static void fillPath(Object parentNode, Method getChildren, Method getPath, Method setPath, Method getId) throws InvocationTargetException, IllegalAccessException {
        if (parentNode != null) {
            List children = (List) getChildren.invoke(parentNode, null);
            if (children == null || children.size() == 0) {
                for (Object node : children) {
                    setPath.invoke(node, getPath.invoke(parentNode, null) + "," + getId.invoke(node, null));
                    fillPath(node, getChildren, getPath, setPath, getId);
                }
            }
        }
    }

    private static class TreeBeanHelper {
        private Object object = null;

        private BeanMap beanMap = null;

        TreeBeanHelper() {
            super();
        }

        @SuppressWarnings("unchecked")
        TreeBeanHelper(Map<String, Class> properties, Class<?> sourceType) {
            this.object = generateBean(properties, sourceType);
            this.beanMap = BeanMap.create(this.object);
        }

        void setValue(String property, Object value) {
            beanMap.put(property, value);
        }

        Object getValue(String property) {
            return beanMap.get(property);
        }

        Object getObject() {
            return object;
        }

        @SuppressWarnings("unchecked")
        Object generateBean(Map<String, Class> properties, Class<?> sourceType) {
            BeanGenerator generator = new BeanGenerator();
            generator.setSuperclass(sourceType);
            Set keySet = properties.keySet();
            for (Iterator i = keySet.iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                generator.addProperty(key, properties.get(key));
            }
            return generator.create();
        }
    }
}
