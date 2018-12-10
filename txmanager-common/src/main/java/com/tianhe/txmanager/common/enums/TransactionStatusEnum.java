package com.tianhe.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:59
 */
public enum TransactionStatusEnum {

    BEGIN("BEGIN","开始"),

    RUNNING("RUNNING","执行中"),

    LOCK("LOCK","锁定"),

    PRE_COMMIT("PRE_COMMIT","预提交"),

    COMMIT("COMMIT","提交"),

    ROLLBACK("ROLLBACK","回滚"),

    FAIL("FAIL","失败");

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String name;

    TransactionStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TransactionStatusEnum get(String code){
        TransactionStatusEnum transactionStatusEnum = null;
        for (TransactionStatusEnum value : TransactionStatusEnum.values()) {
            if(value.getCode().equals(code)){
                transactionStatusEnum = value;
                break;
            }
        }
        return transactionStatusEnum;
    }
}
