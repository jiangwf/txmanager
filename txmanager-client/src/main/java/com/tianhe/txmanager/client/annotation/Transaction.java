package com.tianhe.txmanager.client.annotation;

import java.lang.annotation.*;

/**
 * @author: he.tian
 * @time: 2018-11-26 17:43
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transaction {

}
