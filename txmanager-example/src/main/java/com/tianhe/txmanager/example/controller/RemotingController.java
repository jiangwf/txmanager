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

    @RequestMapping("/html")
   public String send(){
       return "remoting/send";
   }

    @RequestMapping("/remoting")
    @ResponseBody
    public Map<String,Object> send(String input){
        Map<String,Object> map = new HashMap();
        Object result = null;
        if(ActionEnum.HEART_BEAT.equals(input)){
             result = clientHandler.send(RequestHelper.buildRequestHeartBeat());
        }else if(ActionEnum.CREATE_TRANSACTION_GROUP.equals(input)){
             result = clientHandler.send(RequestHelper.buildRequestCreateTransactionGroup());
        }else if(ActionEnum.ADD_TRANSACTION.equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestAddTransaction());
        }else if(ActionEnum.GET_TRANSDACTION_GROUP_STATUS.equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestFindTransactionGroupStatus());
        }else if(ActionEnum.FIND_TRANSACTION_GROUP.equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestFindTransactionGroup());
        }else if(ActionEnum.ROLLBACK.equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestRollback());
        }else if(ActionEnum.PRE_COMMIT.equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestPrecommit());
        }else if(ActionEnum.COMPLETE_COMMIT.equals(input)){
            result = clientHandler.send(RequestHelper.buildRequestCommit());
        }
        map.put("result",result);
        return map;
    }
}
