package com.mljr.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:32
 */
public enum IdEnum {

    TRANSACTION_GROUP("tg","事务组"),

    TRANSACTION_ITEM("ti","事务信息"),

    TRANSACTION_REQUEST("tr","事务请求");

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
