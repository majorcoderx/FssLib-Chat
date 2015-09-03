package com.fis.server;

import java.util.Date;

import com.fss.ddtp.DDTP;
import com.fss.ddtp.SocketTransmitter;

import sun.management.VMManagement;

public class ControllerServer implements Runnable{

	private ManagerServer svmgr;
	
	public ControllerServer() {
		// TODO Auto-generated constructor stub
	}
	
	public ControllerServer(ManagerServer svmgr){
		this.svmgr = svmgr;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(svmgr.isOpen()){
			try{
				// TODO controller list sockettransmiter
				// clear socketTransmitter == null when monitor die
				if(!svmgr.hmchanel.isEmpty()){
					for(String key : svmgr.hmchanel.keySet()){
						if(!svmgr.hmchanel.get(key).isOpen()){
							// TODO send close to all
							DDTP ddtp = new DDTP();
							ddtp.setString("username", key);
							svmgr.exitUserGroup(key);
							svmgr.sendRequestToAll("ChatProcessor", "userDisconnect", ddtp);
							svmgr.hmchanel.remove(key);
							svmgr.mngth.logMonitor(key+" disconnect server chat");
						}
					}
				}
				Thread.sleep(3000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeAll(){
		if(!svmgr.hmchanel.isEmpty()){
			for(Object value : svmgr.hmchanel.values()){
				((SocketTransmitter)value).close();
			}
			svmgr.mngth.logMonitor("close all connected to server");
		}
	}

}
