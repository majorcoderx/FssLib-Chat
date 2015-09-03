package com.fis.client;

import java.util.Vector;

import com.fss.ddtp.DDTP;
import com.fss.ddtp.SocketTransmitter;

public class ChatPanel {
	
	private ChatMonitor cmnt;
	private SocketTransmitter sktransmiter;
	
	private String username;
	private String password;
	
	public ChatPanel(ChatMonitor cmnt) {
		// TODO Auto-generated constructor stub
		this.cmnt = cmnt;
		this.sktransmiter = cmnt.getSocketTransmiter();
	}
	
	public void sendMessage(String msg, String idSendMsg){
		DDTP request = new DDTP();
	}
	
	public void login(String username, String password){
		
		if(cmnt.clientThread.isInterrupted()){
			cmnt.clientThread.start();
		}
		this.username = username;
		this.password = password;
		
		DDTP request = new DDTP();
		request.setRequestID(String.valueOf(System.currentTimeMillis()));
		request.setString("username", username);
		request.setString("password", password);
		request.setString("ip", sktransmiter.getUserID());
		
		//get response from server
		try{
			DDTP response = sktransmiter.sendRequest("ProcessorServer", "login", request);
			if(response.getString("strLog").equals("1")){
				// TODO when login success
				cmnt.isLogin = true;
				cmnt.itemLogin.setEnabled(false);
				cmnt.itemLogout.setEnabled(true);
			}
			else if(response.getString("strLog").equals("-1")){
				cmnt.textChat.append("Have a other logined ! try again...\n");
			}
			else{
				cmnt.textChat.append("Login fail, try again...\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void sendMessageToAll(String msg){
		DDTP ddtp = new DDTP();
		ddtp.setString("username", username);
		ddtp.setString("strMsg", msg);
		try{
			sktransmiter.sendRequest("ProcessorServer", "sendMessageToAll", ddtp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendMessageToClone(String msg,String recv){
		DDTP request = packetDDTP(msg, recv);
		try{
			sktransmiter.sendRequest("ProcessorServer", "sendMessageToClone", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendMessageToGroup(String msg,String recv){
		DDTP request = packetDDTP(msg, recv);
		try{
			sktransmiter.sendRequest("ProcessorServer", "sendMessageToGroup", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void destroyGroup(String namegroup){
		DDTP request = new DDTP();
		request.setString("username", username);
		request.setString("group", namegroup);
		try{
			sktransmiter.sendRequest("ProcessorServer", "destroyGroup", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void logoutGroup(String username,String namegroup){
		DDTP request = new DDTP();
		request.setString("group", namegroup);
		request.setString("username", username);
		try{
			sktransmiter.sendRequest("ProcessorServer", "logoutGroup", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removeUserGroup(String user,String namegr){
		DDTP request = new DDTP();
		request.setString("group", namegr);
		request.setString("username", user);
		try{
			sktransmiter.sendRequest("ProcessorServer", "removeUserGroup", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addUserGroup(String user,String namegr){
		DDTP request = new DDTP();
		request.setString("group", namegr);
		request.setString("username", user);
		try{
			sktransmiter.sendRequest("ProcessorServer", "addUserGroup", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void logoutSystem(){
		DDTP request = new DDTP();
		request.setString("username", username);
		try{
			sktransmiter.sendRequest("ProcessorServer", "logoutSystem", request);
			cmnt.clientThread.interrupt();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void createGroup(String nameGroup,Vector<String> luser){
		DDTP request = new DDTP();
		request.setString("namegr", nameGroup);
		request.setString("ucreate", username);
		request.setVector("lgroup", luser);
		try{
			sktransmiter.sendRequest("ProcessorServer", "createGroup", request);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private DDTP packetDDTP(String msg,String recv){
		DDTP request = new DDTP();
		request.setString("username", username);
		request.setString("strMsg", msg);
		request.setString("receiver", recv);
		return request;
	}
}
