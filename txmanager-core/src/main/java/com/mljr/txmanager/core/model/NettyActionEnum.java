package com.mljr.txmanager.core.model;

/**
 * @author: he.tian
 * @time: 2018-11-02 11:11
 */
public enum NettyActionEnum {

    CREATE_GROUP(1,"创建事务组"),

    ADD_TRANSACTION(2,"添加事务"),

    UPDATE_TRANSACTION(3,"更新事务"),

    PRE_COMMIT(4,"预提交"),

    COMMIT(5,"提交"),

    ROLLBACK(6,"回滚"),

    FAIL(7,"失败"),

    HEART(8,"心跳监测"),

    GET_TRANSACTION(9,"获取事务组"),

    FIND_TRANACTION(10,"查找事务组");

    private int code;
    private String name;

    NettyActionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
