package com.fis.client;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.fis.util.Group;

import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class DialogChat extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JTextArea textChat;
 	public JMenuItem icg;
 	public JMenuItem ilog;
 	
 	public JTextArea textMsg;
 	public JButton btnSend;
 	
	private ChatMonitor cmnt;
	
	JButton btnadd;
	JComboBox cbblauser;
	JComboBox cbblruser;
	JButton btnremove;
	JMenu mopt;
	
	public String addSelected = "";
	public String rmSelected = "";
	
	public String title;
 	
	public DialogChat(ChatMonitor cmnt,String title) {
		this.title = title;
		setResizable(false);
		this.cmnt = cmnt;
		setBounds(100, 100, 450, 328);
		getContentPane().setLayout(null);
		this.setTitle(title);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 60, 414, 166);
		getContentPane().add(scrollPane);
		
		textChat = new JTextArea();
		textChat.setLineWrap(true);
		textChat.setEditable(false);
		scrollPane.setViewportView(textChat);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(10, 237, 316, 48);
		getContentPane().add(scrollPane_1);
		
		textMsg = new JTextArea();
		scrollPane_1.setViewportView(textMsg);
		
		btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO send message to clone or group
				if(!textMsg.getText().isEmpty()){
					textChat.append(">>"+textMsg.getText()+"\n");
					if(title.indexOf(" G") > 1){
						cmnt.sendMessageToGroup(textMsg.getText(), title);
					}
					else{
						cmnt.sendMessageToClone(textMsg.getText(), title);
					}
					textMsg.setText("");
				}
			}
		});
		btnSend.setBounds(335, 240, 89, 34);
		getContentPane().add(btnSend);
		
		JMenuBar menubar = new JMenuBar();
		menubar.setSize(450, 20);
		
		mopt = new JMenu("Option");
		ilog = new JMenuItem("logout Group");
		ilog.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO logout group
				if(ilog.isEnabled()){
					cmnt.logoutGroup(title);
					textChat.setText(">you are logout group chat...\n");
					textChat.setEnabled(false);
					textMsg.setEnabled(false);
					mopt.setEnabled(false);
				}
			}
		});
		icg = new JMenuItem("Close Group");
		icg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO destroy group
				if(icg.isEnabled()){
					cmnt.closeGroup(title);
					textChat.setText(">you were closed group chat....\n");
					textChat.setEnabled(false);
					textMsg.setEnabled(false);
					mopt.setEnabled(false);
				}
			}
		});
		mopt.add(ilog);
		mopt.add(icg);
		menubar.add(mopt);
		getContentPane().add(menubar);
		
		cbblruser = new JComboBox();
		cbblruser.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// TODO event change user selected for remove
				if(cbblruser.getSelectedItem() != null){
					rmSelected = cbblruser.getSelectedItem().toString();
					if(rmSelected.equals(cmnt.username)){
						btnremove.setEnabled(false);
					}
					else if(cbblruser.getItemCount() > 3){
						btnremove.setEnabled(true);
					}
				}
			}
		});
		cbblruser.setBounds(247, 31, 119, 20);
		getContentPane().add(cbblruser);
		
		btnremove = new JButton("-");
		btnremove.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO remove item event
				if(btnremove.isEnabled() && !rmSelected.equals(cmnt.username)){
					cmnt.rmUserGroup(rmSelected, title);
					cbblruser.removeItem(rmSelected);
				}
			}
		});
		btnremove.setEnabled(false);
		btnremove.setBounds(376, 28, 48, 23);
		getContentPane().add(btnremove);
		
		cbblauser = new JComboBox();
		cbblauser.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(cbblauser.getSelectedItem() != null){
					addSelected = cbblauser.getSelectedItem().toString();
				}
			}
		});
		cbblauser.setBounds(10, 29, 119, 20);
		getContentPane().add(cbblauser);
		
		btnadd = new JButton("+");
		btnadd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO action add new user to group
				if(btnadd.isEnabled()){
					cmnt.addUserGroup(addSelected, title);
					cbblauser.removeItem(addSelected);
				}
			}
		});
		btnadd.setEnabled(false);
		btnadd.setBounds(139, 28, 41, 23);
		getContentPane().add(btnadd);
		
		if(title.indexOf(" G") > 0){
			Group gr = cmnt.lGroup.get(title);
			listRemoveUser(gr);
			listAddUser(gr);
			mopt.setEnabled(true);
			updateUI(gr);
		}else{
			cbblauser.setEnabled(false);
			cbblruser.setEnabled(false);
			mopt.setEnabled(false);
		}
		btnremove.setEnabled(false);
	}
	
	public void updateUI(Group p){
		System.out.println(p.getucreate());
		System.out.println(cmnt.username);
		if(p.getucreate().equals(cmnt.username)){
			//admin
			btnadd.setEnabled(false);
			btnremove.setEnabled(false);
			icg.setEnabled(true);
			ilog.setEnabled(true);
			if(!rmSelected.equals(cmnt.username) && cbblruser.getItemCount() > 3){
				btnremove.setEnabled(true);
			}
			if(cbblauser.getItemCount() > 0){
				btnadd.setEnabled(true);
			}
		}
		else{
			//member
			btnadd.setEnabled(false);
			btnremove.setEnabled(false);
			icg.setEnabled(false);
			ilog.setEnabled(true);
		}
	}
	
	public void listAddUser(Group gr){
		for(int i = 0 ; i < cmnt.lConn.size(); ++i){
			if(gr.getluser().lastIndexOf(cmnt.lConn.get(i)) < 0){
				cbblauser.addItem(cmnt.lConn.get(i));
			}
		}
	}
	
	public void listRemoveUser(Group gr){
		for(int i = 0 ;i < gr.getluser().size(); ++i){
			cbblruser.addItem(gr.getluser().get(i));
		}
		if(cbblruser.getItemCount() > 0){
			btnremove.setEnabled(true);
		}
		else{
			btnremove.setEnabled(false);
		}
	}
}
