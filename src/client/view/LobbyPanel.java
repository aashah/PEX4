package client.view;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;

import client.model.Client;

public class LobbyPanel extends javax.swing.JPanel {
	private Client model;
	
    private javax.swing.JButton sendMessage;
    private DefaultListModel<String> userListModel;
    private javax.swing.JList<String> userList;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane messagesScrollPane;
    private javax.swing.JScrollPane userListScrollPane;
    private javax.swing.JTextArea message;
    private javax.swing.JTextField messageInput;
    
    public LobbyPanel(Client model) {
    	this.model = model;
        initComponents();
    }
                         
    private void initComponents() {

        messagesScrollPane = new javax.swing.JScrollPane();
        message = new javax.swing.JTextArea();
        userListScrollPane = new javax.swing.JScrollPane();
        userListModel = new DefaultListModel<String>();
        userList = new javax.swing.JList<String>();
        sendMessage = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        messageInput = new javax.swing.JTextField();

        message.setColumns(20);
        message.setRows(5);
        message.setWrapStyleWord(true);
        messagesScrollPane.setViewportView(message);

        userList.setModel(userListModel);
        userListScrollPane.setViewportView(userList);

        sendMessage.setText("sendMessage");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        messageInput.setText("jTextField1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(messageInput, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(userListScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(messagesScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(sendMessage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(messagesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendMessage)
                    .addComponent(messageInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }

	public void addUser(String username) {
		userListModel.add(userListModel.getSize(), username);
	}
	
	public void removeUser(String username) {
		userListModel.removeElement(username);
		System.out.println("Size: " + userListModel.getSize());
	}
	
	public void addSendMessageButtonListener(ActionListener listener) {
		this.sendMessage.addActionListener(listener);
	}
	public void addSendMessageListener(KeyListener keyListener) {
		this.messageInput.addKeyListener(keyListener);
	}

	public String getInputMessage() {
		return this.messageInput.getText();
	}

	public void newMessage(String username, String string) {
		this.message.append(username + ": " + string + "\n");		
	}
}
