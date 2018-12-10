package com.tianhe.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:32
 */
public enum IdEnum {

    TRANSACTION_GROUP("10","事务组"),

    TRANSACTION_ITEM("20","事务信息"),

    TASK("30","事务请求");

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String name;

    IdEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static IdEnum get(String code){
        IdEnum idEnum = null;
        for (IdEnum value : IdEnum.values()) {
            if(value.getCode().equals(code)){
                idEnum = value;
                break;
            }
        }
        return idEnum;
    }
}
