package com.xiupitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.util.Log;

import com.xiupitter.configuration.ClusterConfiguration;
import com.xiupitter.locator.ServerLocator;

public class ServerManager {

	private List<ServerInfo> servers = new CopyOnWriteArrayList<ServerInfo>();
	private ServerInfo server = new ServerInfo();

	private Map<String, ServerInfo> cache = new ConcurrentHashMap<String, ServerInfo>();

	private static ServerManager instance;
	private ServerLocator locator;
	private static Object lock = new Object();

	public static ServerManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ServerManager();
					try {
						instance.locator = (ServerLocator) ClusterConfiguration
								.getLocatorStrategy().newInstance();
						instance.locator.refrashServers(instance.servers);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.error("init locator fail", e);
						instance = null;
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.error("init locator fail", e);
						instance = null;
					}
				}
			}
		}
		return instance;
	}

	private ServerManager() {
		// TODO Auto-generated constructor stub
		servers.addAll(ClusterConfiguration.getClusterNodes());
		if (ClusterConfiguration.isMasterEnable()) {
			server = ClusterConfiguration.getMasterNode();
		}
	}

	public List<ServerInfo> getServers() {
		return servers;
	}

	public ServerInfo getMasterServer() {
		return server;
	}

	public synchronized void kickDeadServer(ServerInfo info) {
		int size = servers.size();
		for (ServerInfo server : servers) {
			if (info.equals(server)) {
				servers.remove(server);
			}
		}
		if (size > servers.size()) {
			// locator= new KetamaLocator(servers, 8);
			locator.refrashServers(servers);
			for (Map.Entry<String, ServerInfo> entry : cache.entrySet()) {
				if (entry.getValue().equals(info)) {
					cache.remove(entry.getKey());
				}
			}
		}
	}

	public synchronized void addNewServer(ServerInfo info) {
		servers.add(info);
		// locator= new KetamaLocator(servers, 8);
		locator.refrashServers(servers);
	}

	public ServerInfo getServerInfo(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}
		ServerInfo info = this.locator.getExpectServerInfo(id);
		cache.put(id, info);
		return info;
	}
}
