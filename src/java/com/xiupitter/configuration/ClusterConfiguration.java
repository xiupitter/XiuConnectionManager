package com.xiupitter.configuration;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.Log;

import com.xiupitter.ServerInfo;


public class ClusterConfiguration {

	public ClusterConfiguration() {
		// TODO Auto-generated constructor stub
	}
	static{
		ConfigMonitor.getInstance().startMonitor();
	}
	public static int getServerReplica(){
		return JiveGlobals.getXMLProperty("cluster.serverReplica", 8);
	}
	
	public static Class getLocatorStrategy(){
		try {
			return Class.forName(JiveGlobals.getXMLProperty("cluster.locatorStrategy", "cn.newgrand.locator.SimpleLocator"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.error("get locatorStrategy class fail", e);
		}
		return null;
	}
	
	
	/**
	 * <cluster>
	 * 		<nodes>
	 * 			<hostname>127.0.0.1:5262</hostname>
	 * 			<hostname>127.0.0.1:5262</hostname>
	 * 		</nodes>
	 * </cluster>
	 * 
	 * @return
	 */
	public static List<ServerInfo> getClusterNodes(){
		List<ServerInfo> rets = new ArrayList<ServerInfo>();
		String[] strs=  JiveGlobals.getXMLPropertiesx("cluster.nodes.hostname");
		String masterIP = JiveGlobals.getXMLProperty("cluster.master.ip", "127.0.0.1");
		//masterEnable为true
		boolean masterInclude = JiveGlobals.getBooleanProperty("cluster.master.include", true);

		if(isMasterEnable()){
			if(masterInclude){
				ServerInfo server  = new ServerInfo();
				server.setIp(masterIP);
				server.setPort(5262);
				rets.add(server);
			}
		}else{
			for(String s: strs){
				String[] ss = s.split(":");
				ServerInfo server2  = new ServerInfo();
				server2.setIp(ss[0].trim());
				server2.setPort(Integer.parseInt(ss[1].trim()));
				rets.add(server2);
			}
		}
		return rets;
	}
	public static ServerInfo getMasterNode(){
		String masterIP = JiveGlobals.getXMLProperty("cluster.master.ip", "127.0.0.1");
		int masterPort = JiveGlobals.getIntProperty("cluster.master.port", 6666);
		boolean masterEnable = JiveGlobals.getBooleanProperty("cluster.master.enable", true);

		if(masterEnable){
			ServerInfo server  = new ServerInfo();
			server.setIp(masterIP);
			server.setPort(masterPort);
			return server;
		}
		return null;
	}
	public static boolean isMasterEnable(){
		return  JiveGlobals.getBooleanProperty("cluster.master.enable", true);
	}
}
