import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    // Constructor
    public LoginPage() {
        // Initialize database connection
        connectDatabase();

        // Set up the login form
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0, 128, 128)); // Set background color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(loginButton, gbc);

        // Register button
        registerButton = new JButton("Register");
        gbc.gridx = 1;
        mainPanel.add(registerButton, gbc);

        // Add main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Add event listeners for login and register
        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> registerUser());

        // Setup window
        setTitle("Login - Chat Application");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Connect to the MySQL database
    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chatdata_b", 
                "sathvik", 
                "Sathvik2472005@"
            );
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    // Login method
    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try (PreparedStatement pst = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?")) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    new ChatApp(username).setVisible(true);
                    dispose(); // Close the login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage());
        }
    }

    // Register method
    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
            return;
        }

        try (PreparedStatement checkUser = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ?")) {
            checkUser.setString(1, username);
            try (ResultSet rs = checkUser.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.");
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking user existence: " + e.getMessage());
            return;
        }

        try (PreparedStatement pst = connection.prepareStatement(
                "INSERT INTO users(username, password) VALUES(?, ?)")) {
            pst.setString(1, username);
            pst.setString(2, password);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please log in.");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration error: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
