package com.fis.client;

import java.util.Vector;

import com.fis.util.Group;
import com.fss.ddtp.*;

public class ChatProcessor extends SocketProcessor{
	
	public static Object object;
	
	public static void setRoot(Object obj){
		object = obj;
	}
		
	public void messageToAll(){
		String msg = "[" + request.getString("username") + "]: " + request.getString("strMsg")+"\n";
		((ChatMonitor)object).textChat.append(msg);
	}
	
	public void messageToClone(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String msg = "["+request.getString("username")+"]: "+ request.getString("strMsg")+"\n";
		//if not available form chat then create new form
		String key = request.getString("username");
		if(cmnt.lFChat.get(key) != null){
			cmnt.lFChat.get(key).textChat.append(msg);
			cmnt.lFChat.get(key).setVisible(true);
		}
		else{
			DialogChat dgc = new DialogChat(cmnt, key);
			dgc.textChat.append(msg);
			cmnt.lFChat.put(key, dgc);
			dgc.setVisible(true);
		}
	}
	
	public void messageToGroup(){
		// TODO message request from group
		ChatMonitor cmnt = (ChatMonitor) object;
		String msg = "["+request.getString("username")+"]: "+ request.getString("strMsg")+"\n";
		String namegr = request.getString("receiver");
		String usend = request.getString("username");
		if(!cmnt.dialogLogin.getUserName().equals(usend)){
			cmnt.lFChat.get(namegr).textChat.append(msg);
			cmnt.lFChat.get(namegr).setVisible(true);
		}
	}
	
	public void logAction(){}
	
	public void newConnected(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String username = request.getString("username");
		cmnt.textChat.append("<"+username + " join>\n");
		cmnt.listContact.add(username);
		cmnt.lConn.add(username);
		
		try{
			for(String key : cmnt.lFChat.keySet()){
				if(key.indexOf(" G") > 0){
					cmnt.lFChat.get(key).cbblauser.addItem(username);
					cmnt.lFChat.get(key).updateUI(cmnt.lGroup.get(key));
				}
			}
		}catch(Exception e){
			
		}
		
	}
	
	public void newListOnl(){
		ChatMonitor cmnt = (ChatMonitor) object;
		cmnt.listContact.add(request.getString("username"));
		cmnt.lConn.add(request.getString("username"));
	}
	
	public void newGroup(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String namegr = request.getString("namegr");
		String ucreate = request.getString("ucreate");
		Vector<String> luser = request.getVector("lgroup");
		
		Group gr = new Group(ucreate, luser);
		
		cmnt.listContact.add(request.getString("namegr"));
		cmnt.lGroup.put(namegr, gr);
		DialogChat dgc = new DialogChat(cmnt, namegr);
		cmnt.lFChat.put(namegr, dgc);
		// TODO notify to user
		String msg = ">group: "+namegr+" has been created, you joined!\n";
		cmnt.textChat.append(msg);
	}
	
	public void destroyGroup(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String namegr = request.getString("group");
		cmnt.lGroup.remove(namegr);
		cmnt.lFChat.get(namegr).textChat.append(">group has been closed !\n");
		cmnt.lFChat.get(namegr).textMsg.setEditable(false);
		cmnt.lFChat.get(namegr).btnSend.setEnabled(false);
		cmnt.lFChat.remove(namegr);
		cmnt.textChat.append(">group "+namegr+" has been closed !\n");
		cmnt.listContact.remove(namegr);
	}
	
	public void logoutGroup(){
		// TODO when user group logout
		ChatMonitor cmnt = (ChatMonitor) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		cmnt.lGroup.get(namegr).removeuser(username);
		cmnt.lFChat.get(namegr).cbblauser.addItem(username);
		cmnt.lFChat.get(namegr).cbblruser.removeItem(username);
		cmnt.lFChat.get(namegr).textChat.append(">"+username+" has been logout group ...\n");
		cmnt.lFChat.get(namegr).updateUI(cmnt.lGroup.get(namegr));
	}
	
	public void setAddminGroup(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		// TODO update admin
		cmnt.lGroup.get(namegr).changeucreate(username);
		cmnt.lFChat.get(namegr).updateUI(cmnt.lGroup.get(namegr));
	}
	
	public void addUserGroup(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		cmnt.lGroup.get(namegr).adduser(username);
		cmnt.lFChat.get(namegr).cbblruser.addItem(username);
		cmnt.lFChat.get(namegr).cbblauser.removeItem(username);
		cmnt.lFChat.get(namegr).textChat.append(">"+username+" has been join...\n");
		cmnt.lFChat.get(namegr).updateUI(cmnt.lGroup.get(namegr));
	}
	
	public void rmUsergroup(){
		ChatMonitor cmnt = (ChatMonitor) object;
		String namegr = request.getString("group");
		String username = request.getString("username");
		if(cmnt.username.equals(username)){
			cmnt.lGroup.remove(namegr);
			cmnt.lFChat.get(namegr).textChat.append(">you have been kicked from the chat group !\n");
			cmnt.lFChat.get(namegr).textMsg.setEnabled(false);
			cmnt.lFChat.get(namegr).btnSend.setEnabled(false);
			cmnt.lFChat.get(namegr).btnremove.setEnabled(false);
			cmnt.lFChat.get(namegr).btnadd.setEnabled(false);
			cmnt.lFChat.remove(namegr);
			cmnt.textChat.append(">you have been kicked from "+namegr+"!\n");
			cmnt.listContact.remove(namegr);
		}
		else{
			cmnt.lGroup.get(namegr).getluser().remove(username);
			cmnt.lFChat.get(namegr).cbblauser.addItem(username);
			cmnt.lFChat.get(namegr).cbblruser.removeItem(username);
			cmnt.lFChat.get(namegr).textChat.append(">"+username+" has been kicked from group \n");
			cmnt.lFChat.get(namegr).updateUI(cmnt.lGroup.get(namegr));
		}
	}
	
	public void userDisconnect(){
		String key = request.getString("username");
		ChatMonitor cmnt = (ChatMonitor) object;
		cmnt.listContact.remove(key);
		cmnt.lConn.remove(key);
		cmnt.textChat.append("<"+key+" out>\n");
		// TODO notify all form chat logout
		if(cmnt.lFChat.get(key) != null){
			cmnt.lFChat.get(key).textChat.append("<"+key+" out>\n");
			cmnt.lFChat.remove(key);
		}
		// TODO for group
		try{
			for(String kv : cmnt.lFChat.keySet()){
				if(kv.indexOf(" G") > 0){
					cmnt.lFChat.get(kv).cbblauser.removeItem(key);
					cmnt.lGroup.get(kv).getluser().remove(key);
					cmnt.lFChat.get(kv).updateUI(cmnt.lGroup.get(kv));
				}
			}
		}catch(Exception e){
			
		}
	}
}
