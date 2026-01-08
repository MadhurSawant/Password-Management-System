package UI;
import Server.hashing;
import db1.db;
import  java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import static db1.db.get_password_history;

public class SettingsPanel
{

    public static JPanel createPasswordHistoryPanel(int user_id) {
        System.out.println(user_id);
        JPanel passwordHistoryPanel = new JPanel(new BorderLayout());
        passwordHistoryPanel.setBackground(new Color(17, 24, 39)); // Dark background
        passwordHistoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Password History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        // Subtitle
        JLabel subtitle = new JLabel("View your saved vault password entries");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(180, 180, 180));

        // Header Panel (Title + Subtitle)
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(17, 24, 39));
        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 5)));
        header.add(subtitle);





        String[] columns = {"Title", "Username", "Last Updated", "Old Password", "New Password"};

        List<String[]> dataList = get_password_history(user_id);

        Object[][] data = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setBackground(new Color(31, 41, 55));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(75, 85, 99));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(55, 65, 81));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(75, 85, 99)));

        // Add components
        passwordHistoryPanel.add(header, BorderLayout.NORTH);
        passwordHistoryPanel.add(scrollPane, BorderLayout.CENTER);

        return passwordHistoryPanel;
    }

    public static JPanel createUpdateEmailPanel(int user_id) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(17, 24, 39));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("✉️ Update Email");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Change your registered email address");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(180, 180, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField currentEmailField = new JTextField();
        styleTextField(currentEmailField, "Current Email");

        JTextField newEmailField = new JTextField();
        styleTextField(newEmailField, "New Email");

        JButton updateButton = new JButton("Update Email");
        styleButton(updateButton);

        updateButton.addActionListener(e -> {
            String currentEmail = currentEmailField.getText().trim();
            String newEmail = newEmailField.getText().trim();

            if (currentEmail.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in both email fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!db.isEmailMatch(user_id, currentEmail)) {
                JOptionPane.showMessageDialog(panel, "Current email does not match our records.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = db.updateEmail(newEmail, user_id);
            JOptionPane.showMessageDialog(panel, success ? "Email updated successfully." : "Failed to update email.",
                    success ? "Success" : "Error", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(currentEmailField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(newEmailField);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(updateButton);

        return panel;
    }


    public static JPanel createUpdatePasswordPanel(int user_id) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(17, 24, 39));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title
        JLabel titleLabel = new JLabel("🔒 Update Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Change your account password");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(180, 180, 180));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password Fields
        JPasswordField currentPass = new JPasswordField();
        stylePasswordField(currentPass, "Current Password");

        JPasswordField newPass = new JPasswordField();
        stylePasswordField(newPass, "New Password");

        JPasswordField confirmPass = new JPasswordField();
        stylePasswordField(confirmPass, "Confirm Password");

        // Update Button
        JButton updateButton = new JButton("Update Password");
        styleButton(updateButton);

        updateButton.addActionListener(e -> {
            String current = new String(currentPass.getPassword()).trim();
            String newPassword = new String(newPass.getPassword()).trim();
            String confirmPassword = new String(confirmPass.getPassword()).trim();

            if (current.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in all password fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(panel, "New password and confirm password do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Optional: Verify current password with DB here

            String salt = hashing.generateSalt();
            String hashedNewPass = hashing.hashPassword(newPassword, salt);

            boolean success = db.updatePassword(hashedNewPass, salt, user_id);
            JOptionPane.showMessageDialog(panel,
                    success ? "Password updated successfully." : "Failed to update password.",
                    success ? "Success" : "Error",
                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        // Layout
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(currentPass);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(newPass);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(confirmPass);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(updateButton);

        return panel;
    }



    private static void styleTextField(JTextField field, String title) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(31, 41, 55));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 99)),
                title, 0, 0, new Font("Segoe UI", Font.PLAIN, 13), Color.GRAY
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static void stylePasswordField(JPasswordField field, String title) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(31, 41, 55));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 99)),
                title, 0, 0, new Font("Segoe UI", Font.PLAIN, 13), Color.GRAY
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static void styleButton(JButton button) {
        button.setBackground(new Color(96, 165, 250));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }





}
