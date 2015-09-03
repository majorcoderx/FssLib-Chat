package com.fis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.HashMap;

import com.fis.util.Group;
import com.fss.ddtp.DDTP;
import com.fss.ddtp.SocketTransmitter;
import com.fss.thread.ManageableThread;
import com.fss.thread.ThreadConstant;
import com.fss.thread.ThreadManager;
import com.sun.javafx.iio.ios.IosDescriptor;

public class ManagerServer{

	public ServerSocket serverSocket = null;
	public ThreadServer tServer;
	public ControllerServer cServer;
	public Thread threadsv;
	public Thread threadct;
	public HashMap<String, SocketTransmitter> hmchanel;
	public HashMap<String, String> lacc;
	public HashMap<String, Group> lgr;
	
	public int PORT;
	
	public ManageableThread mngth;

	
	public ManagerServer(int port,ManageableThread mngth){
		// TODO Auto-generated constructor stub
		this.mngth = mngth;
		this.PORT = port;
		try{
			hmchanel = new HashMap<String,SocketTransmitter>();
			serverSocket = new ServerSocket(port);
			
			tServer = new ThreadServer(serverSocket, this);
			threadsv = new Thread(tServer);
			threadsv.start();
			
			cServer = new ControllerServer(this);
			threadct = new Thread(cServer);
			threadct.start();
			
			lacc = new HashMap<String,String>();
			lgr = new HashMap<String,Group>();
			
			ProcessorServer.setpvs(this);
		}catch(IOException e){

		}
		mngth.logMonitor("open server chat");
	}
	
	public void sendRequestToAll(String strClass,String strFunction,DDTP ddtp) throws Exception{
		for(Object value : hmchanel.values()){
			((SocketTransmitter) value).sendRequest(strClass, strFunction, ddtp);
		}
	}
	
	public boolean login(String username,String password){
		return true;
	}
	
	public void addChanel(SocketTransmitter chanel){
		hmchanel.put(chanel.getUserName(), chanel);
	}
	
	public void removeChanel(SocketTransmitter chanel){
		hmchanel.remove(chanel.getUserName());
	}

	
	public boolean isOpen(){
		try{
			return !serverSocket.isClosed();
		}catch(Exception e){
			return false;
		}
	}
	
	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
	public boolean isConnected(String username){
		for(Object value : hmchanel.values()){
			if(username.equals(((SocketTransmitter)value).getUserName())){
				return true;
			}
		}
		return false;
	}
	//send list online to new user
	public void sendListOnline(SocketTransmitter chanel) throws Exception{
		for(Object value : hmchanel.values()){
			DDTP request = new DDTP();
			request.setString("username", ((SocketTransmitter)value).getUserName());
			chanel.sendRequest("ChatProcessor", "newListOnl", request);
		}
	}
	
	public void resetServer(){
		hmchanel.clear();
		lacc.clear();
		lgr.clear();
		mngth.logMonitor("reset server");
	}
	
	// TODO when user die / exit all group
	public void exitUserGroup(String user) throws Exception{
		for(String kv : lgr.keySet()){
			if(lgr.get(kv).getluser().remove(user)){
				if(user.equals(lgr.get(kv).getucreate())){
					DDTP request = new DDTP();
					request.setString("group", kv);
					sendRequestToGroup(kv, "destroyGroup", request);
				}
				else{
					DDTP request = new DDTP();
					request.setString("group", kv);
					request.setString("username", user);
					sendRequestToGroup(kv, "logoutGroup", request);
				}
			}
		}
	}
	
	public void sendRequestToGroup(String namegr, String func, DDTP request) throws Exception{
		for(String s : lgr.get(namegr).getluser()){
			hmchanel.get(s).sendRequest("ChatProcessor", func, request);
		}
	}
	
	public void stopServer(){
		try {
			// TODO close server
			cServer.closeAll();
			threadsv.interrupt();
			threadct.interrupt();
			serverSocket.close();
			resetServer();
			mngth.logMonitor("stop server chat");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		while(true){
//			try{
//				if( mngth != null && mngth.miThreadCommand == ThreadConstant.THREAD_STOP){
//					stopServer();
//				}
//				Thread.sleep(500);
//			}catch(InterruptedException e){
//				e.printStackTrace();
//			}
//		}
//	}
}

