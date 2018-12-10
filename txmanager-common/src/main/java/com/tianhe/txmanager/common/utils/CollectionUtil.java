package com.tianhe.txmanager.common.utils;

import java.util.Collection;

/**
 * @author: he.tian
 * @time: 2018-12-10 14:54
 */
public abstract class CollectionUtil {

    public static boolean isEmpty(Collection collection){
        return collection == null || (collection.size() == 0);
    }

    public static boolean isNotEmpty(Collection collection){
        return !(isEmpty(collection));
    }
}
