package com.mljr.txmanager.common.utils;

/**
 * @author: he.tian
 * @time: 2018-10-16 10:39
 */
public abstract class PlatformUtil {

    public static String OS_NAME = System.getProperty("os.name");
    private static boolean IS_LINUX = false;

    static {
        if(OS_NAME != null && OS_NAME.toLowerCase().contains("linux")){
            IS_LINUX = true;
        }
    }

    public static boolean isLinux(){
        return IS_LINUX;
    }
}
