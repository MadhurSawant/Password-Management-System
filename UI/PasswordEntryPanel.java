package UI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class PasswordEntryPanel extends JPanel {
    private JTextField siteNameField;
    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton copyButton;
    private JButton viewButton;
    private JButton saveButton;
    private boolean isPasswordVisible = false;

    // Custom Colors
    private final Color bgColor = new Color(18, 18, 30);
    private final Color fieldColor = new Color(28, 28, 48);
    private final Color textColor = new Color(200, 200, 220);
    private final Color accentColor = new Color(70, 130, 180); // Steel Blue
    private final Color borderColor = new Color(40, 40, 60);

    private JPanel createPasswordEntryPanel1() {
        // Panel setup
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Site Name
        JLabel siteLabel = createLabel("Site Name:");
        siteNameField = createTextField();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(siteLabel, gbc);
        gbc.gridx = 1;
        panel.add(siteNameField, gbc);

        // URL
        JLabel urlLabel = createLabel("URL:");
        urlField = createTextField();
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(urlLabel, gbc);
        gbc.gridx = 1;
        panel.add(urlField, gbc);

        // Username
        JLabel usernameLabel = createLabel("Username:");
        usernameField = createTextField();
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password + Copy + View
        JLabel passwordLabel = createLabel("Password:");
        passwordField = createPasswordField();
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(bgColor);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(bgColor);

        copyButton = createIconButton("Copy");
        viewButton = createIconButton("View");

        copyButton.addActionListener(e -> copyPasswordToClipboard());
        viewButton.addActionListener(e -> togglePasswordVisibility());

        buttonPanel.add(copyButton);
        buttonPanel.add(viewButton);
        passwordPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(passwordPanel, gbc);

        // Save Changes Button
        saveButton = createIconButton("Save Changes");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        // Placeholder save action
        saveButton.addActionListener(e -> saveChanges());

        return panel;
    }


    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setBackground(fieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(fieldColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JButton createIconButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accentColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private void copyPasswordToClipboard() {
        StringSelection selection = new StringSelection(String.valueOf(passwordField.getPassword()));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setEchoChar(isPasswordVisible ? (char) 0 : '•');
        viewButton.setText(isPasswordVisible ? "Hide" : "View");
    }

    private void saveChanges() {
        String site = siteNameField.getText();
        String url = urlField.getText();
        String user = usernameField.getText();
        String pass = String.valueOf(passwordField.getPassword());

        // TODO: Save logic (update DB, file, etc.)
        JOptionPane.showMessageDialog(this, "Changes saved:\n" +
                "Site: " + site + "\n" +
                "URL: " + url + "\n" +
                "Username: " + user + "\n" +
                "Password: " + pass, "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    // Main for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Password Entry Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new PasswordEntryPanel());
            frame.setSize(520, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
