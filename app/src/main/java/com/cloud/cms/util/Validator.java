package com.cloud.cms.util;

import java.util.Collection;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;
import java.lang.CharSequence;
import java.lang.reflect.Array;

public class Validator {

    /**
     * 空
     * @param value
     * @return
     */
    public static boolean isNullOrEmpty(Object value){
        if(value==null){
            return true;
        }
        if(value instanceof CharSequence && value==""){
            return true;
        }
        if(isCollectionsSupportType(value)){
            return sizeIsEmpty(value);
        }
        return false;
    }

    /**
     * 非空
     * @param value
     * @return
     */
    public static boolean isNotNullOrEmpty(Object value){
        return !isNullOrEmpty(value);
    }

    private static boolean sizeIsEmpty(final Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        } else if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        } else if (object instanceof Object[]) {
            return ((Object[]) object).length == 0;
        } else if (object instanceof Iterator<?>) {
            return ((Iterator<?>) object).hasNext() == false;
        } else if (object instanceof Enumeration<?>) {
            return ((Enumeration<?>) object).hasMoreElements() == false;
        } else {
            try {
                return Array.getLength(object) == 0;
            } catch (final IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
    }

    private static boolean isCollectionsSupportType(Object value){
        // 集合或者map
        boolean isCollectionOrMap = value instanceof Collection || value instanceof Map;

        // 枚举 或者是 Iterator迭代器
        boolean isEnumerationOrIterator = value instanceof Enumeration || value instanceof Iterator;

        return isCollectionOrMap//集合或者map
                || isEnumerationOrIterator//枚举 或者是 Iterator迭代器
                || value.getClass().isArray()//判断数组
                ;
    }
}
