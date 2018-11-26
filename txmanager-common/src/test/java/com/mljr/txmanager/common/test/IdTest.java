package com.mljr.txmanager.common.test;

import com.mljr.txmanager.common.utils.IdUtil;
import org.junit.Test;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:26
 */
public class IdTest {

    @Test
    public void getId(){
        System.out.println(IdUtil.getTransactionGroupId());
    }
}
