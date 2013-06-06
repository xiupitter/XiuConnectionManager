package com.xiupitter.configuration;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.Log;

public class ConfigMonitor implements Runnable{

	private static ConfigMonitor instance=null;
    private ScheduledExecutorService executor = null;

    private long lastModify;
    
	private ConfigMonitor() {
		// TODO Auto-generated constructor stub
		File configFile = new File(JiveGlobals.getConfigurationPath());
		if(configFile.exists()){
			lastModify = configFile.lastModified();
		}else{
			Log.warn("configuration file not exist. path:"+JiveGlobals.getConfigurationPath());
		}
	}
	
	public static ConfigMonitor getInstance(){
		if(instance==null){
			instance = new ConfigMonitor();
		}
		return instance;
	}
	
	public void  startMonitor(){
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(this, 0, 30, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!JiveGlobals.getBooleanProperty("cluster.stopMonitor", false)){
			File configFile = new File(JiveGlobals.getConfigurationPath());
			if(configFile.exists()){
				long lastModify2 = configFile.lastModified();
				if(lastModify<lastModify2){
					JiveGlobals.refrashSetupProperties();
					lastModify = lastModify2;
				}
			}else{
				Log.warn("configuration file not exist. path:"+JiveGlobals.getConfigurationPath());
			}
		}
	}
}
