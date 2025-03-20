import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.MessageDigest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DatabaseManagementApp extends JFrame {

    private DefaultListModel<String> connlm;
    private JList<String> connl;
    private JTextArea detailsTextArea;
    private JSONObject connectionsData;

    public DatabaseManagementApp() {
        setTitle("Database Management App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connlm = new DefaultListModel<>();
        connl = new JList<>(connlm);
        detailsTextArea = new JTextArea();

        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton editButton = new JButton("Edit");
        JButton saveDatabaseButton = new JButton("Save");
        JButton exitButton = new JButton("Exit");

        addButton.addActionListener(e -> addConnection());
        removeButton.addActionListener(e -> removeConnection());
        editButton.addActionListener(e -> editConnection());
        exitButton.addActionListener(e -> System.exit(0));

        saveDatabaseButton.addActionListener(e -> saveWholeDatabase());

        connl.addListSelectionListener(e -> displayConnectionDetails());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveDatabaseButton);
        buttonPanel.add(exitButton);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(new JScrollPane(connl));
        mainPanel.add(new JScrollPane(detailsTextArea));

        add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel);
        
        loadConnections();
        setVisible(true);
    }

    private void addConnection() {
        JDialog addDialog = new JDialog(this, "Add Connection", true);
        addDialog.setLayout(new GridLayout(8, 2));  // Increased the number of rows to accommodate the new field

        JTextField nameField = new JTextField();
        JTextField hostField = new JTextField();
        JTextField portField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> engineComboBox = new JComboBox<>(new String[]{"Oracle", "Mysql", "SqlServer", "DB2", "PostGres"});

        addDialog.add(new JLabel("Name:"));
        addDialog.add(nameField);
        addDialog.add(new JLabel("Database Engine:"));
        addDialog.add(engineComboBox);
        addDialog.add(new JLabel("Host:"));
        addDialog.add(hostField);
        addDialog.add(new JLabel("Port:"));
        addDialog.add(portField);
        addDialog.add(new JLabel("Username:"));
        addDialog.add(userField);
        addDialog.add(new JLabel("Password:"));
        addDialog.add(passwordField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String connname = nameField.getText().trim();
            if (!connname.isEmpty()) {
                connlm.addElement(connname);
                String hashedPassword = hashPassword(new String(passwordField.getPassword()).trim());
                saveConnections(connname, hostField.getText().trim(),
                        (String) engineComboBox.getSelectedItem(),
                        Integer.parseInt(portField.getText().trim()),
                        userField.getText().trim(),
                        hashedPassword); 
                addDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(addDialog, "Name cannot be empty.");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.add(saveButton);
        addDialog.add(cancelButton);

        addDialog.setSize(300, 250);  // Adjusted the size to accommodate the new field
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void saveWholeDatabase() {
        try {
            File file = new File("connections.json");

            if (connectionsData != null) {
                FileWriter fw = new FileWriter(file);
                fw.write(connectionsData.toJSONString());
                fw.close();
                JOptionPane.showMessageDialog(this, "Database saved successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No data to save.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving the database.");
        }
    }

    private void removeConnection() {
        int selectedIndex = connl.getSelectedIndex();
        if (selectedIndex != -1) {
            String connName = connlm.getElementAt(selectedIndex);

            int option = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove the connection '" + connName + "'?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                connlm.remove(selectedIndex);

                if (connectionsData != null) {
                    connectionsData.remove(connName);
                    saveWholeDatabase();
                }

                detailsTextArea.setText("");

                JOptionPane.showMessageDialog(this,
                        "Connection '" + connName + "' removed successfully.",
                        "Removal Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void editConnection() {
        int selectedIndex = connl.getSelectedIndex();
        if (selectedIndex != -1) {
            String connName = connlm.getElementAt(selectedIndex);

            JDialog editDialog = new JDialog(this, "Edit Connection", true);
            editDialog.setLayout(new GridLayout(7, 2));

            JSONObject connObject = (JSONObject) connectionsData.get(connName);

            JTextField nameField = new JTextField(connName);
            JTextField hostField = new JTextField((String) connObject.get("Host"));
            JTextField portField = new JTextField(String.valueOf(connObject.get("Port")));
            JTextField userField = new JTextField((String) connObject.get("User"));
            JPasswordField passwordField = new JPasswordField((String) connObject.get("Password"));
            JComboBox<String> engineComboBox = new JComboBox<>(new String[]{"Oracle", "Mysql", "SqlServer", "DB2", "PostGres"});
            engineComboBox.setSelectedItem(connObject.get("DataBaseEngine"));

            editDialog.add(new JLabel("Name:"));
            editDialog.add(nameField);
            editDialog.add(new JLabel("DataBaseEngine:"));
            editDialog.add(engineComboBox);
            editDialog.add(new JLabel("Host:"));
            editDialog.add(hostField);
            editDialog.add(new JLabel("Port:"));
            editDialog.add(portField);
            editDialog.add(new JLabel("Username:"));
            editDialog.add(userField);
            editDialog.add(new JLabel("Password:"));
            editDialog.add(passwordField);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String editedName = nameField.getText().trim();
                if (!editedName.isEmpty()) {
                	String hashedPassword = hashPassword(new String(passwordField.getPassword()).trim());
                	
                	connObject.put("DataBaseEngine", (String) engineComboBox.getSelectedItem());
                    connObject.put("Host", hostField.getText().trim());
                    connObject.put("Port", Integer.parseInt(portField.getText().trim()));
                    connObject.put("User", userField.getText().trim());
                    connObject.put("Password", hashedPassword);

                    // Save the updated database
                    saveWholeDatabase();
                    loadConnections(); // Reload connections to update the JList
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Name cannot be empty.");
                }
            });

            editDialog.add(saveButton);

            editDialog.setSize(300, 200);
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void updateConnection(String name, String engine, String host, int port, String user, String password) {
        try {
            if (connectionsData != null && connectionsData.containsKey(name)) {
                JSONObject connObject = new JSONObject();
                connObject.put("DataBaseEngine", engine);
                connObject.put("Host", host);
                connObject.put("Port", (long) port);
                connObject.put("User", user);
                connObject.put("Password", password);
                  // Added the new field

                connectionsData.put(name, connObject);

                saveWholeDatabase();
                loadConnections(); // Reload connections to update the JList
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadConnections() {
        try {
            File file = new File("connections.json");
            if (file.exists()) {
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                connectionsData = (JSONObject) parser.parse(reader);

                for (Object key : connectionsData.keySet()) {
                    String connName = (String) key;
                    connlm.addElement(connName);
                }

                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveConnections(String name, String engine, String host, int port, String user, String password) {
        try {
            if (connectionsData == null) {
                connectionsData = new JSONObject();
            }

            JSONObject connObject = new JSONObject();
            connObject.put("DataBaseEngine", engine);
            connObject.put("Host", host);
            connObject.put("Port", (long) port);
            connObject.put("User", user);
            connObject.put("Password", password);

            connectionsData.put(name, connObject);

            saveWholeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayConnectionDetails() {
        int selectedIndex = connl.getSelectedIndex();
        if (selectedIndex != -1) {
            String connName = connlm.getElementAt(selectedIndex);

            if (connectionsData != null) {
                JSONObject connObject = (JSONObject) connectionsData.get(connName);

                if (connObject != null) {
                	String engine = (String) connObject.get("DataBaseEngine");
                    String host = (String) connObject.get("Host");
                    long port = (Long) connObject.get("Port");
                    String user = (String) connObject.get("User");

                    String details = "Name: " + connName + "\nEngine: " + engine + "\nHost: " + host + "\nPort: " + port
                            + "\nUser: " + user;
                    detailsTextArea.setText(details);
                } else {
                    detailsTextArea.setText("No connection details available.");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseManagementApp::new);
    }
}
