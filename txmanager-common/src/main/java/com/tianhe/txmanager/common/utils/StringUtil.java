package com.tianhe.txmanager.common.utils;

/**
 * @author: he.tian
 * @time: 2018-12-10 14:53
 */
public abstract class StringUtil {

    public static boolean isEmpty(String str){
        return str == null || ("".equals(str));
    }

    public static boolean isNotEmpty(String str){
        return !(isEmpty(str));
    }
}
