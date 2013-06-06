package com.xiupitter.locator;

import java.util.List;

import com.xiupitter.ServerInfo;


public class SimpleLocator  implements ServerLocator{

	private List<ServerInfo> servers;
	private int count;
	public SimpleLocator() {
		// TODO Auto-generated constructor stub
		count=0;
	}
	
	@Override
	public void refrashServers(List<ServerInfo> servers){
		this.servers = servers;
		count = 0;
	}
	
	@Override
	public ServerInfo getExpectServerInfo(String id) {
		// TODO Auto-generated method stub
		ServerInfo info = null;
		if(servers.size()!=0){
			info  = servers.get(count%servers.size());
			count++;
		}
		return info;
	}
}
