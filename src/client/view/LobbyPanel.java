package client.view;

import client.model.Client;

public class LobbyPanel extends javax.swing.JPanel {   
	
    private javax.swing.JButton sendMessageBUtton;
    private javax.swing.JList userList;
    private javax.swing.JScrollPane messageScrollPane;
    private javax.swing.JScrollPane userListScrollPane;
    private javax.swing.JTextArea messages;
    private javax.swing.JTextField sendNewMessage;
    /**
     * Creates new form NewJPanel
     */
    public LobbyPanel(Client model) {
        initComponents();
    }
    
    @SuppressWarnings("unchecked")                      
    private void initComponents() {

        messageScrollPane = new javax.swing.JScrollPane();
        messages = new javax.swing.JTextArea();
        userListScrollPane = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        sendNewMessage = new javax.swing.JTextField();
        sendMessageBUtton = new javax.swing.JButton();

        messages.setColumns(20);
        messages.setRows(5);
        messageScrollPane.setViewportView(messages);

        userList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Username", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        userListScrollPane.setViewportView(userList);

        sendNewMessage.setText("");

        sendMessageBUtton.setText("Send Message");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sendNewMessage)
                    .addComponent(messageScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userListScrollPane)
                    .addComponent(sendMessageBUtton, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(messageScrollPane)
                    .addComponent(userListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sendMessageBUtton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(sendNewMessage)))
                .addContainerGap())
        );
    }               
}

