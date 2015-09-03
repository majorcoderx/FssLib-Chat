package com.fis.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.List;
import javax.swing.JTextPane;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.Dialog;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.fss.ddtp.SocketTransmitter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;

import com.fis.util.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ChatMonitor implements Runnable{

	private JFrame frmChat;
 	public DialogLogin dialogLogin;
	public List listContact;
	public JTextArea textChat;
	public JTextArea textMsg;
	public JButton btnCreateGroup;
	public JButton btnCloseGroup;
	
	private ChatProcessor mntpcs;
	private ChatPanel mntpn;
	private Socket csocket = null;
	public DialogGroup dgg = null;
	public boolean isLogin = false;
	public JMenuItem itemLogin;
	public JMenuItem itemLogout;
	public String username = "";
	
	public Vector<String> lConn; //have for control list account <type,account>
	public HashMap<String, Group> lGroup;
	public HashMap<String, DialogChat> lFChat; //have for list form <name,Dialog>
	
	private SocketTransmitter sktransmiter;
	
	private String cnt = "";
	
	public Thread clientThread;
	
	public static void main(String[] args) {
		ChatMonitor clientFrom;
		clientFrom = new ChatMonitor();
	}

	public ChatMonitor(){
		initialize();
		frmChat.setVisible(true);
		showLoginDialog();
		clientThread = new Thread(this);
		clientThread.start();
	}
	
	private void initialize(){
		frmChat = new JFrame();
		frmChat.setResizable(false);
		frmChat.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmChat.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int keep = JOptionPane.showConfirmDialog(null,
						"Would you like close application?", "Shutdown",
						JOptionPane.YES_NO_OPTION);
				if(keep == 0){
					System.exit(0);
				}
			}
		});
		frmChat.setBounds(100, 100, 302, 531);
		frmChat.getContentPane().setLayout(null);
		
		JButton btnSendMsg = new JButton("Send");
		btnSendMsg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//send msg
				mntpn.sendMessageToAll(textMsg.getText());
				textMsg.setText("");
			}
		});
		
		btnSendMsg.setBounds(223, 457, 65, 30);
		frmChat.getContentPane().add(btnSendMsg);
		
		listContact = new List();
		listContact.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// get item chat
				if(!listContact.getSelectedItem().equals(username)){
					if(lFChat.get(listContact.getSelectedItem()) != null){
						lFChat.get(listContact.getSelectedItem()).setVisible(true);
					}
					else{
						createFormChat(listContact.getSelectedItem());
					}
					cnt = listContact.getSelectedItem();
					if(cnt.indexOf(" G") > 0 && lGroup.get(cnt).getucreate().equals(username)){
						btnCloseGroup.setEnabled(true);
					}
					else{
						btnCloseGroup.setEnabled(false);
					}
				}
				// TODO when selected group enable button dis group
			}
		});
		listContact.setBounds(10, 45, 130, 216);
		frmChat.getContentPane().add(listContact);
		
		JLabel lblContact = new JLabel("Message");
		lblContact.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblContact.setBounds(41, 25, 82, 14);
		frmChat.getContentPane().add(lblContact);
		
		btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.setEnabled(false);
		btnCreateGroup.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//create group
				if(isLogin && lConn.size() > 2){
					createGroup();
				}
			}
		});
		btnCreateGroup.setBounds(155, 189, 121, 23);
		frmChat.getContentPane().add(btnCreateGroup);
		
		btnCloseGroup = new JButton("Close Group");
		btnCloseGroup.setEnabled(false);
		btnCloseGroup.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO close group when selected is group item
				if(btnCloseGroup.isEnabled()){
					closeGroup(cnt);
				}
			}
		});
		btnCloseGroup.setBounds(155, 223, 120, 23);
		frmChat.getContentPane().add(btnCloseGroup);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setSize(302, 20);
		JMenu menu = new JMenu("System");
		itemLogin = new JMenuItem("login");
		itemLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO login
				if(!isLogin){
					dialogLogin.setVisible(true);
					itemLogin.setEnabled(false);
					itemLogout.setEnabled(true);
				}
			}
		});
		menu.add(itemLogin);
		itemLogout = new JMenuItem("Logout");
		itemLogout.setEnabled(false);
		itemLogout.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent args0){
				// TODO logout system
				if(isLogin){
					mntpn.logoutSystem();
					listContact.removeAll();
					lFChat.clear();
					lGroup.clear();
					lConn.clear();
					textChat.setText("");
					itemLogin.setEnabled(true);
					itemLogout.setEnabled(false);
					isLogin = false;
				}
			}
		});
		menu.add(itemLogout);
		menuBar.add(menu);
		frmChat.getContentPane().add(menuBar);
		
		JLabel labelImage = new JLabel("");
		labelImage.setBounds(155, 47, 120, 120);
		try{
			BufferedImage image = ImageIO.read(new File("D:\\Users\\spider man\\Documents\\workspaces\\FssChat\\src\\Image\\fptis.jpg"));
			labelImage.setIcon(new ImageIcon(image.getScaledInstance(120, 120, 0)));
		}catch(IOException e){
			e.printStackTrace();
		}
		frmChat.getContentPane().add(labelImage);
		
		JScrollPane scrollPaneChat = new JScrollPane();
		scrollPaneChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneChat.setBounds(10, 273, 276, 173);
		frmChat.getContentPane().add(scrollPaneChat);
		
		textChat = new JTextArea();
		textChat.setLineWrap(true);
		textChat.setEditable(false);
		textChat.setFont(new Font("Monospaced", Font.PLAIN, 13));
		scrollPaneChat.setViewportView(textChat);
		
		JScrollPane scrollPaneMsg = new JScrollPane();
		scrollPaneMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneMsg.setBounds(10, 449, 203, 41);
		frmChat.getContentPane().add(scrollPaneMsg);
		
		textMsg = new JTextArea();
		scrollPaneMsg.setViewportView(textMsg);
	}
	
	public void createGroup(){
		if(dgg == null){
			dgg = new DialogGroup(this);
			dgg.setVisible(true);
		}else{
			dgg.setVisible(true);
		}
	}
	
	public void createFormChat(String key){
		DialogChat dgc = new DialogChat(this,key);
		lFChat.put(key, dgc);
		dgc.setVisible(true);
	}
	
	public void showLoginDialog(){
		dialogLogin = new DialogLogin(this);
		dialogLogin.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogLogin.setVisible(true);
	}
	
	public boolean connectToServer(){
		try{
			csocket = new Socket("localhost", 22);
			sktransmiter = new SocketTransmitter(csocket);
		}catch(IOException e){
			return false;
		}
		return true;
	}
	
	@Override
	public void run(){
		// TODO something
		//check server have online
		while(true){
			try{
				if(lConn != null &&  lConn.size() > 2){
					btnCreateGroup.setEnabled(true);
				}
				else{
					btnCreateGroup.setEnabled(false);
				}
				if( isConnected() && !sktransmiter.isOpen()){
					textChat.append(">Server die, wait...... \n");
					for(Object value : lFChat.values()){
						((DialogChat)value).textChat.append(">Server die, wait...... \n");
					}
					lConn.clear();
					lFChat.clear();
					listContact.removeAll();
					isLogin = false;
					btnCloseGroup.setEnabled(false);
					btnCreateGroup.setEnabled(false);
					itemLogin.setEnabled(true);
					itemLogout.setEnabled(false);
					Thread.sleep(2000);
					login();
				}
				else{
					Thread.sleep(1000);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	public boolean isConnected(){
		if(csocket != null){
			return true;
		} return false;
	}
	
	public void login(){
		if(connectToServer()){
			username = dialogLogin.getUserName();
			frmChat.setTitle(username);
			mntpn = new ChatPanel(this);
			ChatProcessor.setRoot(this);
			lConn = new Vector<String>();
			lFChat = new HashMap<String,DialogChat>();
			lGroup = new HashMap<String,Group>();
			dialogLogin.setVisible(false);
			sktransmiter.setUserName(username);
			sktransmiter.setUserID(csocket.getInetAddress().toString()); // TODO option
			sktransmiter.setPackage("com.fis.server.");
			sktransmiter.start();
			mntpn.login(username, dialogLogin.getPassword());
		}
		else{
			JOptionPane.showMessageDialog(frmChat, "Not Connected, check your internet !");
		}
	}
	
	public void reqCreateGroup(Vector<String> luser){
		// TODO get name for group
		dgg.setVisible(false);
		String nameg = dialogLogin.getUserName();
		for(int i = 0 ; i < luser.size(); ++i){
			nameg+= luser.get(i).substring(0, 1);
		}
		nameg+=" G";
		Group gr = new Group(username, luser);
		lGroup.put(nameg,gr);
		
		// TODO import list user
		listContact.add(nameg);
		mntpn.createGroup(nameg,luser);
		DialogChat dgc = new DialogChat(this, nameg);
		lFChat.put(nameg, dgc);
		// TODO notify to user
		String msg = ">group: "+nameg+" has been created, you joined!\n";
		textChat.append(msg);	
	}
	
	public void addUserGroup(String username,String namegr){
		// TODO insert new user to group
		lGroup.get(namegr).adduser(username);
		mntpn.addUserGroup(username,namegr);
	}
	
	public void rmUserGroup(String username,String namegr){
		lGroup.get(namegr).removeuser(username);
		// TODO remove user
		mntpn.removeUserGroup(username,namegr);
	}
	
	public void logoutGroup(String namegr){
		// TODO logout group
		lGroup.remove(namegr);
		lFChat.remove(namegr);
		listContact.remove(namegr);
		mntpn.logoutGroup(username, namegr);
	}
	
	public void closeGroup(String namegr){
		// TODO destroy group
		lGroup.remove(namegr);
		lFChat.remove(namegr);
		listContact.remove(namegr);
		mntpn.destroyGroup(namegr);
	}
	
	public void sendMessageToClone(String msg,String key){
		mntpn.sendMessageToClone(msg, key);
	}
	
	public void sendMessageToGroup(String msg,String key){
		// key is group receiver
		mntpn.sendMessageToGroup(msg, key);
	}
	
	public void disableDialog(){
		dialogLogin.setVisible(false);
	}
	
	public Socket getSocket(){
		return csocket;
	}
	
	public SocketTransmitter getSocketTransmiter(){
		return this.sktransmiter;
	}
	
	public ChatPanel getMonitorPanel(){
		return this.mntpn;
	}
	
	public ChatProcessor getMonitorProcessor(){
		return this.mntpcs;
	}
}
