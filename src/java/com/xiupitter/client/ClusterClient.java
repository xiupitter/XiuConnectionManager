package com.xiupitter.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.jivesoftware.util.Log;

import com.xiupitter.ServerManager;

public class ClusterClient {

	private Thread heartbeat;
	private HeartbeatWorker worker;
	private ClientBootstrap bootstrap;
	
	
	public void start(){
	    ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
	            Executors.newCachedThreadPool());

	    bootstrap = new ClientBootstrap(factory);
	    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
	        public ChannelPipeline getPipeline() {
	            return Channels.pipeline(new StringEncoder(CharsetUtil.UTF_8),new StringDecoder(CharsetUtil.UTF_8) ,new ServerEventHandler());
	        }
	    });
	    bootstrap.setOption("tcpNoDelay", true);
	    bootstrap.setOption("keepAlive", true);
	    bootstrap.setOption("connectTimeoutMillis", 5000);
	    bootstrap.setOption("remoteAddress", new InetSocketAddress(ServerManager.getInstance().getMasterServer().getIp(),ServerManager.getInstance().getMasterServer().getPort()));
	    final ChannelFuture f = bootstrap.connect().awaitUninterruptibly();
	    if (!f.isSuccess()) {
			f.getCause().printStackTrace();
			Log.error("connect to master fail");
			System.exit(0);
	    }
	    else{
	    	worker= new HeartbeatWorker(f);
	    	heartbeat = new Thread(worker);
	    	heartbeat.start();
	    }
	}
	
	public void exitClient(){
		System.exit(0);
	}
	
	private class HeartbeatWorker implements Runnable{

		private boolean exit = false;
		private ChannelFuture f;
		
		public HeartbeatWorker(ChannelFuture f) {
			// TODO Auto-generated constructor stub
			this.f = f;
		}
		
		public void exit(){
			exit = true;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ChannelFuture ff;
			ff=f;
			while(!exit){
				ff = ff.awaitUninterruptibly().getChannel().write("aa");
			    try {
					Thread.sleep(20*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.error("heartbeat write error exit application");
			f.addListener(ChannelFutureListener.CLOSE);
	        System.out.println("master connect is closed");
		}
	}
}
