package com.tianhe.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: he.tian
 * @time: 2018-11-22 16:32
 */
public enum ResultEnum {

    SUCCESS("SUCCESS","成功"),

    FAIL("FAIL","失败"),

    TIME_OUT("TIME_OUT","网络超时");

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String name;

    ResultEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ResultEnum get(String code){
        ResultEnum resultEnum = null;
        for (ResultEnum value : ResultEnum.values()) {
            if(value.getCode().equals(code)){
                resultEnum = value;
                break;
            }
        }
        return resultEnum;
    }
}
