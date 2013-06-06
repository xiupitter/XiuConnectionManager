package com.xiupitter.locator;

import java.util.List;

import com.xiupitter.ServerInfo;


public interface ServerLocator {

	public abstract ServerInfo getExpectServerInfo(String id);
	public abstract void refrashServers(List<ServerInfo> servers);
}