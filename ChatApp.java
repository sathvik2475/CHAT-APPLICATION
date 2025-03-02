import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class ChatApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private JTextField messageField;
    private JTextArea messageArea;
    private JButton sendButton, videoCallButton, clearChatButton;
    private String currentUser;
    private Timer messagePollingTimer;
    private Set<Integer> displayedMessageIds;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    // Constructor accepting the username
    public ChatApp(String username) {
        currentUser = username;
        connectDatabase();
        displayedMessageIds = new HashSet<>();

        // Main layout panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Message display area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));
        mainPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Left panel for recipient list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Recipients"));

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        leftPanel.add(userScrollPane, BorderLayout.CENTER);
        fetchUsers();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Message sending panel
        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.setBorder(BorderFactory.createTitledBorder("Send Message"));

        messageField = new JTextField();
        sendPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendPanel.add(sendButton, BorderLayout.EAST);

        videoCallButton = new JButton("Start Video Call");
        sendPanel.add(videoCallButton, BorderLayout.SOUTH);

        clearChatButton = new JButton("Clear Chat");
        sendPanel.add(clearChatButton, BorderLayout.WEST);

        mainPanel.add(sendPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Event listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        videoCallButton.addActionListener(e -> startVideoCall());
        clearChatButton.addActionListener(e -> messageArea.setText(""));

        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    messageArea.append("Chatting with: " + selectedUser + "\n");
                }
            }
        });

        // Window settings
        setTitle("Chat Application - " + currentUser);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        startMessagePolling();
    }

    // Fetch users
    private void fetchUsers() {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT username FROM users");
            ResultSet rs = pst.executeQuery();

            userListModel.clear();
            while (rs.next()) {
                String username = rs.getString("username");
                userListModel.addElement(username);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch users: " + e.getMessage());
        }
    }

    // Connect to the database
    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatdata_b", "sathvik", "Sathvik2472005@");
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    // Send a message
    private void sendMessage() {
        String recipient = userList.getSelectedValue();
        String message = messageField.getText();

        if (recipient == null || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a recipient and type a message.");
            return;
        }

        try {
            PreparedStatement pst = connection.prepareStatement("INSERT INTO messages(sender, recipient, message) VALUES(?, ?, ?)");
            pst.setString(1, currentUser);
            pst.setString(2, recipient);
            pst.setString(3, message);
            pst.executeUpdate();

            messageArea.append("To " + recipient + ": " + message + "\n");
            messageField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Message sending failed: " + e.getMessage());
        }
    }

    // Poll for new messages
    private void startMessagePolling() {
        messagePollingTimer = new Timer(3000, e -> fetchMessages());
        messagePollingTimer.start();
    }

    // Fetch messages
    private void fetchMessages() {
        try {
            PreparedStatement pst = connection.prepareStatement("SELECT id, sender, message FROM messages WHERE recipient = ?");
            pst.setString(1, currentUser);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int messageId = rs.getInt("id");
                String sender = rs.getString("sender");
                String message = rs.getString("message");

                if (!displayedMessageIds.contains(messageId)) {
                    messageArea.append("From " + sender + ": " + message + "\n");
                    displayedMessageIds.add(messageId);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching messages: " + e.getMessage());
        }
    }

    // Placeholder method for video call feature
    private void startVideoCall() {
        JOptionPane.showMessageDialog(this, "Video call feature coming soon!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatApp("TestUser").setVisible(true));
    }
}
