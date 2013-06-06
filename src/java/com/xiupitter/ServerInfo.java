package com.xiupitter;

public class ServerInfo {

	private String ip;
	private int port;

	public ServerInfo() {
		// TODO Auto-generated constructor stub
	}

	public ServerInfo(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return ip + ':' + port;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (super.equals(obj)) {
			return true;
		}
		if (obj instanceof ServerInfo) {
			return obj.toString().equals(this.toString());
		}
		return false;
	}
}
