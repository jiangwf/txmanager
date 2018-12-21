package com.tianhe.txmanager.example.controller;

import com.tianhe.txmanager.client.remoting.netty.NettyClientHandler;
import com.tianhe.txmanager.common.enums.ActionEnum;
import com.tianhe.txmanager.example.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: he.tian
 * @time: 2018-12-19 15:45
 */
@Controller
public class RemotingController {

    @Autowired
    private NettyClientHandler clientHandler;

    @RequestMapping("/remoting")
    @ResponseBody
    public Map<String,Object> send(String input,String group,String item){
        Map<String,Object> map = new HashMap();
        Object result = null;
        if(ActionEnum.HEART_BEAT.getCode().equals(input)){
             result = clientHandler.send(RequestHelper.buildRequestHeartBeat());
        }
        if(ActionEnum.CREATE_TRANSACTION_GROUP.getCode().equals(input)){
             result = clientHandler.send(RequestHelper.buildRequestCreateTransactionGroup());
        }
        if(ActionEnum.ADD_TRANSACTION.getCode().equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestAddTransaction(group));
        }
        if(ActionEnum.GET_TRANSDACTION_GROUP_STATUS.getCode().equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestFindTransactionGroupStatus(group));
        }
        if(ActionEnum.FIND_TRANSACTION_GROUP.getCode().equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestFindTransactionGroup(group));
        }
        if(ActionEnum.ROLLBACK.getCode().equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestRollback(group));
        }
        if(ActionEnum.PRE_COMMIT.getCode().equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestPrecommit(group));
        }
        if(ActionEnum.COMPLETE_COMMIT.getCode().equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestCommit(group,item));
        }
        map.put("result",result);
        return map;
    }
}
