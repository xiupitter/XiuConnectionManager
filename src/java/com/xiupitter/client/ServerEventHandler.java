package com.xiupitter.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jivesoftware.multiplexer.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiupitter.ServerInfo;
import com.xiupitter.ServerManager;


public class ServerEventHandler extends SimpleChannelHandler{
    private static Logger logger = LoggerFactory.getLogger(ServerEventHandler.class);
    public ServerEventHandler() {
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        String s =(String) e.getMessage();
        String[] addrs =  s.split(",");
    	for(String si:addrs){
            if(si!=null){
                String[] ss = si.split(":");
                if(ss.length==3){
                	if(ss[0].equals(ClusterEventEnum.Add.toString())){
                		ServerInfo se = new ServerInfo();
                		se.setIp(ss[1]);
                		se.setPort(Integer.parseInt(ss[2]));
                		ServerManager.getInstance().addNewServer(se);
                		ConnectionManager.getInstance().getServerSurrogate().startNewPool(se);
                	}else if(ss[0].equals(ClusterEventEnum.Remove.toString())){
                		ServerInfo se = new ServerInfo();
                		se.setIp(ss[1]);
                		se.setPort(Integer.parseInt(ss[2]));
                		ServerManager.getInstance().kickDeadServer(se);
                    	ConnectionManager.getInstance().getServerSurrogate().shutdown(se.toString(), false);
                	}else{
                    	logger.error("receive a error server update event. message:"+s);
                	}
                }else{
                	logger.error("receive a error server update event. message:"+s);
                }
            }else{
            	logger.warn("receive a null server update event");
            }
    	}

    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel channel = e.getChannel();
        logger.debug("CLIENT - CONNECTED: " + channel.getRemoteAddress());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        Channel channel = e.getChannel();
        logger.debug("CLIENT - DISCONNECTED: " + channel.getRemoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.debug("CLIENT - EXCEPTION: " + e.getCause().getMessage());
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) {
    	String message = (String) e.getMessage();
        ChannelBuffer buffer = ChannelBuffers.directBuffer(message.getBytes(CharsetUtil.UTF_8).length);
        if (!message.isEmpty()) {
            buffer.writeBytes(message.getBytes());
            Channels.write(ctx, e.getFuture(), buffer);
        }
    }
}
