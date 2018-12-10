package com.tianhe.txmanager.common.exception;

/**
 * @author: he.tian
 * @time: 2018-10-31 19:14
 */
public class TransactionException extends RuntimeException{

    public TransactionException(){
        super();
    }

    public TransactionException(String msg){
        super(msg);
    }
}
