private void registerUser() {
String username = usernameField.getText();
String password = new String(passwordField.getPassword());

if (username.isEmpty() || password.isEmpty()) {
JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
return;
}

try {
PreparedStatement pst = connection.prepareStatement("INSERT INTO users(username, password) VALUES(?, ?)");
