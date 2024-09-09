package client;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatClientGUI {
    private ChatClient client;
    private JFrame frame;
    private JTextField inputField;
    private JTextArea chatArea;

    public ChatClientGUI(ChatClient client) {
        this.client = client;
        buildGUI();
        startMessageReceiver();
    }

    private void buildGUI() {
        frame = new JFrame("Chat Room");
        inputField = new JTextField(40);
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        JButton sendButton = new JButton("Send");

        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        client.sendMessage(message);
        inputField.setText("");
    }

    private void startMessageReceiver() {
        new Thread(() -> {
            try {
                String message;
                while ((message = client.receiveMessage()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient("localhost", 12345);
            new ChatClientGUI(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
