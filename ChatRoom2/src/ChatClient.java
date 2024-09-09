import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient().start());
    }

    private void start() {
        JFrame frame = createFrame();
        try {
            setupConnection();
            authenticateUser();
            startMessageListener(frame);
        } catch (IOException e) {
            showError("Connection error: " + e.getMessage());
        }
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JTextField textField = new JTextField();
        frame.add(textField, BorderLayout.SOUTH);

        textField.addActionListener(e -> sendMessage(textField.getText()));

        frame.setVisible(true);
        return frame;
    }

    private void setupConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void authenticateUser() throws IOException {
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        out.println(username);
        out.println(password);
    }

    private void startMessageListener(JFrame frame) {
        JTextArea textArea = (JTextArea) ((JScrollPane) frame.getContentPane().getComponent(0)).getViewport().getView();

        new Thread(() -> {
            try {
                MessageHolder holder = new MessageHolder();
                String receivedMessage;
                while ((receivedMessage = in.readLine()) != null) {
                    holder.setMessage(receivedMessage);
                    SwingUtilities.invokeLater(() -> textArea.append(holder.getMessage() + "\n"));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> showError("Error reading message: " + e.getMessage()));
            }
        }).start();
    }

    private void sendMessage(String message) {
        out.println(message);
    }

    private void showError(String errorMessage) {
        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE));
    }
}
