package com.tianhe.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * netty client向netty server发起的请求动作
 * @author: he.tian
 * @time: 2018-10-16 16:20
 */
public enum ActionEnum {

    CREATE_TRANSACTION_GROUP("CREATE_TRANSACTION_GROUP","创建事务组"),

    ADD_TRANSACTION("ADD_TRANSACTION","添加事务"),

    UPDATE_TRANSACTION("UPDATE_TRANSACTION","更新事务"),

    PRE_COMMIT("PRE_COMMIT","预提交"),

    COMPLETE_COMMIT("COMPLETE_COMMIT","完成提交"),

    ROLLBACK("ROLLBACK","回滚"),

    FAIL("FAIL","失败"),

    HEART_BEAT("HEART_BEAT","心跳检测"),

    GET_TRANSDACTION_GROUP_STATUS("GET_TRANSACTION_GROUP_STATUS","获取事务组状态"),

    FIND_TRANSACTION_GROUP("FIND_TRANSACTION_GROUP","查找事务组"),

    RECEIVE("RECEIVE","接收"),

    FIND_TRANSACTION_EXIST("FIND_TRANSACTION","查询当前线程是否已创建事务组"),

    SAVE_TRANSACTION_GROUP("SAVE_TRANSACTION_GROUP","保存事务组id"),

    REGIST_TRANSACTION_ITEM("REGISTER_TRANSACTION_ITEM","注册事务单元");

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String name;

    ActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ActionEnum get(String code){
        ActionEnum actionEnum = null;
        for (ActionEnum value : ActionEnum.values()) {
            if(value.getCode().equals(code)){
                actionEnum = value;
                break;
            }
        }
        return actionEnum;
    }

}
