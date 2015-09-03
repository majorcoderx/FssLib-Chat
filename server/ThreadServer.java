package com.fis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.JTextArea;

import com.fss.ddtp.DDTP;
import com.fss.ddtp.SocketTransmitter;

public class ThreadServer implements Runnable{
	
	private ManagerServer svmgr;
	private ServerSocket svSocket;
	
	public ThreadServer() {
		// TODO Auto-generated constructor stub
	}
	
	public ThreadServer(ServerSocket svsocket, ManagerServer svmgr){
		this.svmgr = svmgr;
		this.svSocket = svsocket;

	}
	
	public void setResetServer(ServerSocket svsocket){
		this.svSocket = svsocket;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(svmgr.isOpen()){
			Socket sck;
			String username = "";
			String password = "";
			String requestID = "";
			String ipclient = "";
			try{
				sck = svSocket.accept();
				sck.setSoLinger(true, 0);
				sck.setKeepAlive(true);
			}catch(IOException e){
				e.printStackTrace();
				continue;
			}
			try {
				DDTP request = new DDTP(sck.getInputStream());
				username = request.getString("username");
				password = request.getString("password");
				ipclient = request.getString("ip");
				requestID = request.getRequestID();
				
				svmgr.mngth.logMonitor("new connect from "+ipclient);
				svmgr.mngth.logMonitor(username+" request login");
				if(!request.getFunctionName().equals("login"))
					continue;
				SocketTransmitter chanel = new SocketTransmitter(sck);
				chanel.setUserName(username);
				chanel.setUserID(ipclient);
				chanel.setPackage("com.fis.client.");
				chanel.start();
				
				DDTP response = new DDTP();
				response.setResponseID(requestID);
				
				//if connected, return log fail
				if(svmgr.isConnected(username)){
					response.setString("strLog", "-1");
					chanel.sendResponse(response);
					svmgr.mngth.logMonitor(username+" login fail");
					continue;
				}
				
				if(svmgr.login(username, password)){
					svmgr.sendListOnline(chanel);
					svmgr.addChanel(chanel);
					
					response.setString("strLog", "1");
					chanel.sendResponse(response);
					DDTP dreq = new DDTP();
					dreq.setString("username", username);
					svmgr.sendRequestToAll("ChatProcessor","newConnected",dreq);
					svmgr.lacc.put(username, "user");
					svmgr.mngth.logMonitor(username+" login success");
				}
				else{
					response.setString("strLog", "0");
					chanel.sendResponse(response);
					svmgr.mngth.logMonitor(username+" login fail");
				}
				
				Thread.sleep(500);
			} catch (IOException e) {

			}catch (InterruptedException e) {

			}catch(Exception e){

			}
		}
	}
}
