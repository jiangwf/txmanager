package com.tianhe.txmanager.common.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * netty client向netty server发起的请求动作
 * @author: he.tian
 * @time: 2018-10-16 16:20
 */
public enum ActionEnum {

    CREATE_TRANSACTION_GROUP("create_transaction_group","创建事务组"),

    ADD_TRANSACTION("add_transaction","添加事务"),

    UPDATE_TRANSACTION("update_transaction","更新事务"),

    PRE_COMMIT("pre_commit","预提交"),

    COMMIT("commit","提交"),

    ROLLBACK("rollback","回滚"),

    FAIL("fail","失败"),

    HEART_BEAT("heart_beat","心跳检测"),

    GET_TRANSDACTION_GROUP_STATUS("get_transaction_group_status","获取事务组状态"),

    FIND_TRANSACTION_GROUP("find_transaction_group","查找事务组"),

    RECEIVE("receive","接收");

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
