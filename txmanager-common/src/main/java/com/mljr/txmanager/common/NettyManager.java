package com.mljr.txmanager.common;

import com.mljr.txmanager.common.utils.RemotingHelper;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: he.tian
 * @time: 2018-11-23 16:42
 */
public class NettyManager {

    private static NettyManager INSTANCE = new NettyManager();

    private NettyManager(){}

    @Getter
    @Setter
    private int maxConnection = 100;

    @Getter
    @Setter
    private int currentConnection;

    @Getter
    @Setter
    private boolean allowConnection = true;

    @Getter
    @Setter
    private List<Channel> clientChannelList = new ArrayList<>();

    public void addChannel(Channel channel){
        clientChannelList.add(channel);
        currentConnection = clientChannelList.size();
        allowConnection = maxConnection != currentConnection;
    }

    public void removeChannel(Channel channel){
        clientChannelList.remove(channel);
        currentConnection  = clientChannelList.size();
        allowConnection = maxConnection != currentConnection;
    }

    public static NettyManager getInstance(){
        return INSTANCE;
    }

    /**
     * 根据ip地址获取channel
     * @param remoteAddr
     * @return
     */
    public Channel getChannelByRemoteAddr(String remoteAddr){
        Channel remoteChannel = null;
        for (Channel channel : clientChannelList) {
            if(StringUtils.isNotEmpty(remoteAddr) && remoteAddr.equals(RemotingHelper.parseChannelRemoteAddr(channel))){
                remoteChannel = channel;
            }
        }
        return remoteChannel;
    }
}
