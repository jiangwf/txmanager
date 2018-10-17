package com.mljr.txmanager.common.enums;

/**
 * netty client向netty server发起的请求动作
 * @author: he.tian
 * @time: 2018-10-16 16:20
 */
public enum ActionEnum {

    CREATE_TRANSACTION_GROUP(0,"创建事务组"),

    ADD_TRANSACTION(1,"添加事务"),

    UPDATE_TRANSACTION(2,"更新事务"),

    PRE_COMMIT(3,"预提交"),

    COMMIT(4,"提交"),

    ROLLBACK(5,"回滚"),

    FAIL(6,"失败"),

    GET_TRANSDACTION_GROUP_STATUS(10,"获取事务组状态"),

    FIND_TRANSACTION_GROUP(11,"查找事务组");

    private int code;

    private String name;

    ActionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
