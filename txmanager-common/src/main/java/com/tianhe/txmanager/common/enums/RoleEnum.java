package com.tianhe.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:49
 */
public enum RoleEnum {

    START("START","发起者"),

    JOIN("JOIN","参与者"),

    GROUP("GROUP","事务组");

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String name;

    RoleEnum(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static RoleEnum get(String code){
        RoleEnum roleEnum = null;
        for (RoleEnum value : RoleEnum.values()) {
            if(value.getCode().equals(code)){
                roleEnum = value;
                break;
            }
        }
        return roleEnum;
    }
}
