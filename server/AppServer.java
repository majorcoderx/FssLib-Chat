package com.fis.server;

import java.net.ServerSocket;
import java.util.Date;

import com.fss.thread.ManageableThread;
import com.fss.thread.ThreadConstant;

public class AppServer extends ManageableThread{

	ManagerServer svmgr = null;

	@Override
	public void beforeSession() throws Exception{
		super.beforeSession();
		svmgr = new ManagerServer(22, this);
	}
	
	@Override
	protected void processSession() throws Exception {
		while(miThreadCommand == ThreadConstant.THREAD_NONE){
			if(miThreadCommand == ThreadConstant.THREAD_STOP){
				logMonitor("stoped....");
				svmgr.stopServer();
			}
			logMonitor("running.....");
			Thread.sleep(5000);
		}	
	}
	
	@Override
	public void afterSession() throws Exception {
		super.afterSession();
		svmgr.stopServer();
	}
}
