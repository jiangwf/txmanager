package com.mljr.txmanager.common.utils;

import io.netty.channel.Channel;

import java.net.SocketAddress;

/**
 * @author: he.tian
 * @time: 2018-11-22 17:25
 */
public abstract class RemotingHelper {

    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }
}
