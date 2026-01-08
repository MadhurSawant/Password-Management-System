package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Server.EmailOTPSender;
import db1.db;
import Server.hashing;

import static Server.hashing.generateSalt;
import static Server.hashing.hashPassword;

public class ModernAuthUI extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;
    public static int user_id;
    String username1;

    public ModernAuthUI() {
        setTitle("Login / Signup");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();
        JPanel forgotPasswordPanel = createForgotPasswordPanel();

        mainPanel.add(loginPanel, "login");
        mainPanel.add(signupPanel, "signup");
        mainPanel.add(forgotPasswordPanel,"forgotPassword");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = basePanel("Login");
        JLabel title = titleLabel("Login");

        JTextField username = styledTextField("Username");
        JPasswordField password = styledPasswordField("Password");

        JButton loginBtn = styledButton("Login");
        JButton switchBtn = transparentButton("Don't have an account? Sign up");
        switchBtn.addActionListener(e -> cardLayout.show(mainPanel, "signup"));

        JButton forgotBtn = transparentButton("Forgot Password?");
        forgotBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotBtn.addActionListener(e -> cardLayout.show(mainPanel, "forgotPassword"));





        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(username);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(password);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(switchBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(forgotBtn);





        loginBtn.addActionListener(a -> {
            String usernameInput = username.getText().trim();  // Trim to avoid space errors
            String passwordInput = new String(password.getPassword());

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password.");
                return;
            }

             // Get user data by username

            String[] userData = db.get_users(usernameInput);
            if (userData == null || userData.length < 3) {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
                return;
            }


            String salt = userData[0];
            String storedHash = userData[1];
            String email = userData[3];
            int user_id;

            try {
                user_id = Integer.parseInt(userData[2]);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid user ID format.");
                return;
            }

            String inputHash = hashPassword(passwordInput, salt);

            if (inputHash != null && inputHash.equals(storedHash)) {
                new PasswordManagerDashboard(usernameInput,user_id);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Login Unsuccessful.");
            }
        });





        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = basePanel("Signup");
        JLabel title = titleLabel("Sign Up");


        JTextField username = styledTextField("Username");
        JTextField email = styledTextField("Email");
        JPasswordField password = styledPasswordField("Password");

        JButton signupBtn = styledButton("Sign Up");
        JButton switchBtn = transparentButton("Already have an account? Login");
        switchBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(email);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(username);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(password);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(signupBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(switchBtn);




        signupBtn.addActionListener(a -> {
            String usernameInput = username.getText();
            String emailInput = email.getText();
            String passwordInput = new String(password.getPassword());

            if (usernameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.");
                return;
            }

            String salt = generateSalt();
            String hashedPassword = hashPassword(passwordInput, salt);

            boolean success = db.set_users(usernameInput, emailInput, hashedPassword, salt);

            if (success) {
                JOptionPane.showMessageDialog(null, "Signup successful!");
            } else {
                JOptionPane.showMessageDialog(null, "Signup failed. Try again.");
            }
        });


        return panel;
    }


    private JPanel basePanel(String name) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 23, 42)); // Dark Blue/Black
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        return panel;
    }

    private JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(96, 165, 250)); // Blue
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        return label;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(new Color(30, 41, 59));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(96, 165, 250), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                }
            }
        });
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(400, 40));
        field.setMargin(new Insets(10, 10, 10, 10));
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return field;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(400, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(new Color(30, 41, 59));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(96, 165, 250), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        field.setEchoChar((char) 0);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                }
            }
        });

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }


    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(300, 40));
        button.setBackground(new Color(96, 165, 250));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton transparentButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(new Color(147, 197, 253));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    private JPanel createForgotPasswordPanel() {
        JPanel panel = basePanel("Forgot Password");
        JLabel title = titleLabel("Reset Password via OTP");

        JTextField usernameField = styledTextField("Enter Username");

        JButton sendOtpBtn = styledButton("Send OTP to Registered Email");
        JTextField otpField = styledTextField("Enter OTP");
        otpField.setVisible(false);

        JLabel timerLabel = new JLabel(" "); // Initially empty
        timerLabel.setForeground(Color.RED);
        timerLabel.setVisible(false);

        JPasswordField newPasswordField = styledPasswordField("New Password");
        newPasswordField.setVisible(false);
        JPasswordField confirmPasswordField = styledPasswordField("Confirm Password");
        confirmPasswordField.setVisible(false);

        JButton resetBtn = styledButton("Reset Password");
        resetBtn.setVisible(false);
        JButton backBtn = transparentButton("← Back to Login");

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(sendOtpBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(otpField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(timerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(newPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(confirmPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(resetBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backBtn);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        final String[] generatedOtp = new String[1];
        final int[] timeLeft = {60}; // OTP valid for 60 seconds
        final Timer[] otpTimer = new Timer[1];

        sendOtpBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter your username.");
                return;
            }

            String[] userData = db.get_users(username); // Returns [salt, hash, user_id, email]
            if (userData == null || userData.length < 4) {
                JOptionPane.showMessageDialog(null, "User not found or email not available.");
                return;
            }

            String email = userData[3];
            String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
            generatedOtp[0] = otp;

            boolean emailSent = EmailOTPSender.sendOTP(email, otp);
            if (emailSent) {
                JOptionPane.showMessageDialog(null, "OTP sent to your registered email.");

                otpField.setVisible(true);
                timerLabel.setVisible(true);
                newPasswordField.setVisible(true);
                confirmPasswordField.setVisible(true);
                resetBtn.setVisible(true);
                sendOtpBtn.setEnabled(false);
                usernameField.setEnabled(false);

                // Reset timer
                timeLeft[0] = 60;
                timerLabel.setText("OTP valid for: 60 seconds");

                if (otpTimer[0] != null && otpTimer[0].isRunning()) otpTimer[0].stop();

                otpTimer[0] = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        timeLeft[0]--;
                        if (timeLeft[0] > 0) {
                            timerLabel.setText("OTP valid for: " + timeLeft[0] + " seconds");
                        } else {
                            ((Timer) evt.getSource()).stop();
                            timerLabel.setText("OTP expired. Please resend.");
                            resetBtn.setEnabled(false);
                        }
                    }
                });
                otpTimer[0].start();

            } else {
                JOptionPane.showMessageDialog(null, "Failed to send OTP. Try again.");
            }
        });

        resetBtn.addActionListener(e -> {
            if (timeLeft[0] <= 0) {
                JOptionPane.showMessageDialog(null, "OTP expired. Please resend.");
                return;
            }

            String otpInput = otpField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (otpInput.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required.");
                return;
            }

            if (!otpInput.equals(generatedOtp[0])) {
                JOptionPane.showMessageDialog(null, "Incorrect OTP.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match.");
                return;
            }

            String[] userData = db.get_users(usernameField.getText().trim());
            int user_id = Integer.parseInt(userData[2]);
            String newSalt = generateSalt();
            String newHash = hashPassword(newPassword, newSalt);

            db.update_password(user_id, newHash, newSalt);

            if (otpTimer[0] != null) otpTimer[0].stop();

            JOptionPane.showMessageDialog(null, "Password updated successfully.");
            cardLayout.show(mainPanel, "login");
        });

        return panel;
    }






    public static void main(String[] args) {
        SwingUtilities.invokeLater(ModernAuthUI::new);
    }
}
