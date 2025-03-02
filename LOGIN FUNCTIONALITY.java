// Login method
private void loginUser() {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());
    
    if (username.isEmpty() || password.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Please enter both username and password.");
    return;
    }
    
    try {
    PreparedStatement pst = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
    