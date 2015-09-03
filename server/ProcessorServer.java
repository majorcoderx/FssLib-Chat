package com.fis.server;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.fis.util.Group;
import com.fss.ddtp.DDTP;
import com.fss.ddtp.SocketProcessor;
import com.fss.ddtp.SocketTransmitter;

public class ProcessorServer extends SocketProcessor{
	
	public static Object object;
	
	public static void setpvs(Object obj){
		object = obj;
	}
	
    public void logoutSystem(){
    	String key = request.getString("username");
    	DDTP request = new DDTP();
    	request.setString("username", key);
    	//remove list on server ?
    	ManagerServer svmgr = (ManagerServer) object;
    	svmgr.hmchanel.get(key).close(); //close sockettransmiter
    	svmgr.hmchanel.remove(key);
    	svmgr.lacc.remove(key);
    	svmgr.mngth.logMonitor(key+" logout chat system");
    	try{
    		svmgr.sendRequestToAll("ChatProcessor", "userDisconnect", request);
    	}catch(Exception e){

    	}
    }
	
	public void destroyGroup(){
		ManagerServer svmgr = (ManagerServer) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		Group gr = svmgr.lgr.get(namegr);
		try{
			gr.getluser().remove(username);
			for(int i = 0 ;i < gr.getluser().size(); ++i ){
				svmgr.hmchanel.get(gr.getluser().get(i)).sendRequest("ChatProcessor", "destroyGroup", request);
			}
		}catch(Exception e){

		}
	}
	
	public void logoutGroup(){
		ManagerServer svmgr = (ManagerServer) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		Group gr = svmgr.lgr.get(namegr);
		try{
			DDTP ddtp;
			gr.getluser().remove(username);
			if(gr.getucreate().equals(username)){
				// TODO admin logout group
				gr.changeucreate(gr.getluser().firstElement());
				
				ddtp = new DDTP();
				ddtp.setString("group", namegr);
				ddtp.setString("username", gr.getluser().firstElement());
				for(int i = 0 ;i < gr.getluser().size(); ++i ){
					svmgr.hmchanel.get(gr.getluser().get(i)).sendRequest("ChatProcessor", "setAddminGroup", ddtp);
				}
			}
			for(int i = 0 ;i < gr.getluser().size(); ++i ){
				svmgr.hmchanel.get(gr.getluser().get(i)).sendRequest("ChatProcessor", "logoutGroup", request);
			}
		}catch(Exception e){

		}
	}
	
	public void removeUserGroup(){
		ManagerServer svmgr = (ManagerServer) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		Group gr = svmgr.lgr.get(namegr);
		try{
			for(int i = 0 ; i < gr.getluser().size(); ++i){
				svmgr.hmchanel.get(gr.getluser().get(i)).sendRequest("ChatProcessor", "rmUsergroup", request);
			}
		}catch(Exception e){

		}
		gr.getluser().remove(username);
	}
	
	public void addUserGroup(){
		ManagerServer svmgr = (ManagerServer) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		Group gr = svmgr.lgr.get(namegr);
		// TODO send to all
		try{
			for(int i = 0; i < gr.getluser().size(); ++i ){
				svmgr.hmchanel.get(gr.getUser(i)).sendRequest("ChatProcessor", "addUserGroup", request);
			}
			
			gr.adduser(username);
			DDTP ddtp = new DDTP();
			ddtp.setString("namegr", namegr);
			ddtp.setString("ucreate", gr.getucreate());
			ddtp.setVector("lgroup", gr.getluser());
			
			svmgr.hmchanel.get(username).sendRequest("ChatProcessor", "newGroup", ddtp);
		}catch(Exception e){

		}
		// TODo send to new user 
	}
	
	public void createGroup(){
		// TODO create group
		ManagerServer svmgr = (ManagerServer) object;
		String namegr = request.getString("namegr");
		String ucreate = request.getString("ucreate");
		Vector<String> luser = request.getVector("lgroup");
		Group gr = new Group(ucreate, luser);
		
		svmgr.lgr.put(namegr, gr);
		
		try{
			for(int i= 0; i< gr.getluser().size(); ++i){
				String key = gr.getluser().get(i);
				if(!key.equals(ucreate) && svmgr.hmchanel.containsKey(key)){
					svmgr.hmchanel.get(key).sendRequest("ChatProcessor", "newGroup", request);
				}
			}
		}catch(Exception e){

		}
	}
	
	public void sendMessageToGroup(){
		ManagerServer svmgr = (ManagerServer) object;
		try{
			for(String key : svmgr.lgr.keySet()){
				if(key.equals(request.getString("receiver"))){
					Group gr = svmgr.lgr.get(key);
					for(int i = 0 ;i < gr.getluser().size(); ++i){
						String user = gr.getluser().get(i);
						if(svmgr.hmchanel.get(user) != null){
							svmgr.hmchanel.get(user).sendRequest("ChatProcessor", "messageToGroup", request);
						}
					}
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendMessageToAll(){
		ManagerServer svmgr = (ManagerServer) object;
		try{
			svmgr.sendRequestToAll("ChatProcessor", "messageToAll", request);
		}catch(Exception e){

		}
	}
	
	public void sendMessageToClone(){
		ManagerServer svmgr = (ManagerServer) object;
		try{
			for(String key : svmgr.hmchanel.keySet()){
				if(key.equals(request.getString("receiver"))){
					svmgr.hmchanel.get(key).sendRequest("ChatProcessor", "messageToClone", request);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
