package com.fis.util;

import java.util.Vector;

public class Group {
	private String ucreate = "";
	private Vector<String> luser;
	public Group(String ucreate,Vector<String> luser){
		this.ucreate = ucreate;
		this.luser = luser;
	}
	
	public String getucreate(){
		return this.ucreate;
	}
	
	public Vector<String> getluser(){
		return this.luser;
	}
	
	public void changeucreate(String newucreate){
		this.ucreate = newucreate;
	}
	
	public void removeuser(String uname){
		luser.remove(uname);
	}
	
	public void adduser(String uname){
		luser.add(uname);
	}
	
	public String getUser(int i){
		return luser.get(i);
	}
}
