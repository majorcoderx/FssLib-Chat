package com.fis.client;


import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.List;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class DialogGroup extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ChatMonitor cmnt;
	
	public JComboBox<String> cbbListUser;
	public List listUser;
	public Vector<String> luser;
	public String uSelected = "";
	public String getu = "";
	
	JButton btnAdd;
	JButton btnCreate;
	JButton btnDel;
	
	public DialogGroup(ChatMonitor cmnt) {
		setResizable(false);
		this.cmnt = cmnt;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
			}
		});
		luser = new Vector<>();
		
		setBounds(100, 100, 217, 325);
		getContentPane().setLayout(null);
		
		listUser = new List();
		listUser.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// TODO get user selected
				getu = listUser.getSelectedItem();
				if(getu.equals(cmnt.username)){
					btnDel.setEnabled(false);
				}
				else{
					btnDel.setEnabled(true);
				}
			}
		});
		listUser.setBounds(10, 49, 181, 199);
		getContentPane().add(listUser);
		
		paintCbb();
		uSelected = cbbListUser.getItemAt(0).toString();
		
		btnCreate = new JButton("Create");
		btnCreate.setEnabled(false);
		btnCreate.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO create new group
				if(luser.size() > 2){
					cmnt.reqCreateGroup(luser);
					cmnt.dgg.setVisible(false);
				}
			}
		});
		btnCreate.setBounds(20, 253, 89, 23);
		getContentPane().add(btnCreate);
		
		btnAdd = new JButton("+");
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO add user to group
				if(!luser.contains(uSelected)){
					listUser.add(uSelected);
					luser.add(uSelected);
					cbbListUser.removeItem(uSelected);
				}
				if(luser.size() > 2){
					btnCreate.setEnabled(true);
				}
				if(luser.size() > 0){
					btnDel.setEnabled(true);
				}
			}
		});
		btnAdd.setBounds(150, 10, 41, 23);
		getContentPane().add(btnAdd);
		
		btnDel = new JButton("-");
		btnDel.setEnabled(false);
		btnDel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO remove user
				if(luser.size() > 0 && !getu.equals(cmnt.username) ){
					btnDel.setEnabled(true);
					listUser.remove(getu);
					luser.remove(getu);
					cbbListUser.addItem(getu);
				}
				if(luser.size() > 2){
					btnCreate.setEnabled(true);
				} else btnCreate.setEnabled(false);
			}
		});
		btnDel.setBounds(139, 253, 52, 23);
		getContentPane().add(btnDel);
		
	}
	
	public void paintCbb(){
		// TODO something
		Vector<String> lgr = new Vector<>(cmnt.lConn);
		lgr.remove(cmnt.username);
		listUser.add(cmnt.username);
		luser.add(cmnt.username);
		
		cbbListUser = new JComboBox<>(lgr);
		cbbListUser.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(cbbListUser.getSelectedItem() != null){
					uSelected = cbbListUser.getSelectedItem().toString();
					if(luser.contains(uSelected)){
						btnAdd.setEnabled(false);
					}
					else{
						btnAdd.setEnabled(true);
					}
				}
			}
		});
		cbbListUser.setBounds(10, 11, 130, 20);
		getContentPane().add(cbbListUser);
	}
}
