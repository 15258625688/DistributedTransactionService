package com.tcp.netty.entity;

public class DistributedTransactionConstant {

    /**
     * 创建事务
     */
    public final static int CREATE_TRANSACTION = 1;

    /**
     * 新增事务
     */
    public final static int ADD_TRANSACTION = 2;

    /**
     * 查询事务
     */
    public final static int QUERY_TRANSACTION = 3;

    /**
     * 事务状态 等待
     */
    public final static int TRANSACTION_WAIT = 0;

    /**
     * 事务状态 成功
     */
    public final static int TRANSACTION_SUCCESS = 1;

    /**
     * 事务状态 失败
     */
    public final static int TRANSACTION_ERROR = 2;

}
