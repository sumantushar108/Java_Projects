import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginPage extends JFrame {

    private JTextField userField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login Page");
        setSize(300,150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel userLabel = new JLabel("User:");
        JLabel passwordLabel = new JLabel("Password:");

        userField = new JTextField(15); // Set the preferred width
        passwordField = new JPasswordField(15); // Set the preferred width

        JButton loginButton = new JButton("Login");
        JButton clearButton = new JButton("Clear");
        JButton exitButton = new JButton("Exit");
        JButton registerButton = new JButton("Register");
        
        loginButton.addActionListener(e -> authenticateUser());
        clearButton.addActionListener(e -> clearFields());
        exitButton.addActionListener(e -> System.exit(0));
//        registerButton.addActionListener(e -> registerUser());
        
        JPanel userPanel = new JPanel(new FlowLayout());
        userPanel.add(userLabel);
        userPanel.add(userField);

        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
//        buttonPanel.add(registerButton);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(userPanel);
        mainPanel.add(passwordPanel);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
//    private void registerUser() {
//        DatabaseManagementApp registrationApp = new DatabaseManagementApp();
//        
//        dispose();
//    }

    
    private void authenticateUser() {
        try {
            File file = new File("Connections.json");
            if (file.exists()) {
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                JSONObject connectionsData = (JSONObject) parser.parse(reader);

                String enteredUser = userField.getText().trim();
                String enteredPassword = new String(passwordField.getPassword()).trim();

                // Iterate through all user entries in the JSON file
                for (Object obj : connectionsData.values()) {
                    JSONObject userObject = (JSONObject) obj;

                    // Check if the entered credentials match any user in the JSON file
                    if (enteredUser.equals(userObject.get("User")) && enteredPassword.equals(userObject.get("Password"))) {
                        JOptionPane.showMessageDialog(this, "Login Successful!");

                        // Open the main application window
                        SwingUtilities.invokeLater(() -> {
                            new DatabaseManagementApp();
                        });

                        dispose();
                        return;  // Exit the method if login is successful
                    }
                }

                // If no matching user is found
                JOptionPane.showMessageDialog(this, "Incorrect User or Password. Try again.");

                reader.close();
            } else {
                JOptionPane.showMessageDialog(this, "No connections data found. Register first.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during authentication.");
        }
    }

    private void clearFields() {
        userField.setText("");
        passwordField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
