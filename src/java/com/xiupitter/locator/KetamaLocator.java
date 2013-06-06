package com.xiupitter.locator;

import java.util.List;
import java.util.TreeMap;

import com.xiupitter.ServerInfo;
import com.xiupitter.configuration.ClusterConfiguration;

public class KetamaLocator implements ServerLocator {

	private HashAlgorithm alg =new  HashAlgorithmImpl();
	private TreeMap<Long,ServerInfo> map = new  TreeMap<Long,ServerInfo>();
	private int nCopys = ClusterConfiguration.getServerReplica();

	public KetamaLocator(){
	}
	
	@Override
	public void refrashServers(List<ServerInfo> servers){
		map.clear();
		for(ServerInfo info: servers){
			for(int i=0;i<nCopys/4;i++){
				byte[] digest = alg.md5(info.toString()+i);
				for(int j=0;j<4;j++){
					long key = ((long) (digest[3+j*4] & 0xFF) << 24)
			                | ((long) (digest[2+j*4] & 0xFF) << 16)
			                | ((long) (digest[1+j*4] & 0xFF) << 8)
			                | (digest[j*4] & 0xFF)& 0xffffffffL;
					map.put(key, info);
				}
			}
		}
	}
	
	
	@Override
	public ServerInfo getExpectServerInfo(String id){
		 Long key  = alg.hash(id);  
        //如果找到这个节点，直接取节点，返回  
        if(!map.containsKey(key)) {  
	          key = map.ceilingKey(key);  
	          if (key == null) {  
	              key = map.firstKey();  
	          }  
        }  
        return map.get(key);  
	}
}
