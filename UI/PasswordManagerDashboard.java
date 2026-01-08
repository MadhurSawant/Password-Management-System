package UI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

import Server.AESExample;
import Server.SecureNoteLoader;
import Server.SecureNoteSaver;
import UI.PasswordEntryPanel.*;
import db1.db;
import UI .ModernAuthUI.*;

import static Server.SecureNoteLoader.loadDecryptedNote;


public class PasswordManagerDashboard extends JFrame {

     JPanel mainContent;
   JTextField siteNameField;
  JTextField urlField;
     JTextField usernameField;
     JTextField tagfiled;
     JPasswordField passwordField;
    JButton copyButton;
    JButton viewButton;
     JButton saveButton;
    private boolean isPasswordVisible = false;

    // Custom Colors
    private final Color bgColor = new Color(18, 18, 30);
    private final Color fieldColor = new Color(28, 28, 48);
    private final Color textColor = new Color(200, 200, 220);
    private final Color accentColor = new Color(70, 130, 180); // Steel Blue
    private final Color borderColor = new Color(40, 40, 60);

    String loginUsername;
    int entity_id;
    int notes_id;
     public  static  int user_id;


    public void set_entry_id(int entry_id)
    {
        entity_id=entry_id;
    }

    public void set_notes_id(int notes_id1)
    {
        notes_id=notes_id1;
    }



    public PasswordManagerDashboard(String username1,int user_id1) {

        loginUsername=username1;
        user_id=user_id1;
        System.out.println(loginUsername);
        System.out.println(user_id);

        setTitle("🔐 Password Manager Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();          // Sidebar navigation
        JPanel topbar = createTopBar();    // Top bar includes username
        mainContent = createVaultPanel();          // Default content panel

        add(sidebar, BorderLayout.WEST);
        add(topbar, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        setResizable(false);
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, getHeight()));
        panel.setBackground(new Color(15, 23, 42));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🔐 VaultSafe");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(new Color(96, 165, 250));
        logo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(logo);

        String[] menuItems = {"Vault", "Add New", "Secure Notes","Add_notes","Settings"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            button.setBackground(new Color(30, 41, 59));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.addActionListener(e -> swapContent(item));
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(button);
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(getWidth(), 60));
        panel.setBackground(new Color(30, 41, 59));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Password Manager Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(147, 197, 253));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(96, 165, 250));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e->
                {
                    new ModernAuthUI();
                    dispose();
                }

        );

        panel.add(title, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        return panel;
    }

    public   void swapContent(String section) {
        remove(mainContent);


        switch (section) {

            case "Vault" -> mainContent = createVaultPanel();
            case "Add New" -> mainContent = createAddNewPanel();
            case "Secure Notes" -> mainContent = createSecureNotesPanel();
            case "Settings" -> mainContent = createSettingsPanel();
            case "passwordEntryPanel"->mainContent = createPasswordEntryPanel1();
            case "secureNoteViewPanel" -> mainContent = createSecureNoteViewPanel();
            case "Add_notes" -> mainContent = createAddNotePanel() ;
            case "passwordHistoryPanel" -> mainContent = SettingsPanel.createPasswordHistoryPanel(user_id);
            case "updateEmailPanel" -> mainContent = SettingsPanel.createUpdateEmailPanel(user_id);
            case "updatePasswordPanel" -> mainContent = SettingsPanel.createUpdatePasswordPanel(user_id);



            default -> mainContent = new JPanel();
        }

        add(mainContent, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createPasswordEntryPanel1() {
        // Use entity_id as a class variable
        String[] res = db.get_vault_entries_entry_id(entity_id);
        // Expected: res[0]=site, res[1]=url, res[2]=username, res[3]=encrypted password, res[4]=tags

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
        siteNameField.setText(res[0]);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(siteLabel, gbc);
        gbc.gridx = 1;
        panel.add(siteNameField, gbc);

        // URL
        JLabel urlLabel = createLabel("URL:");
        urlField = createTextField();
        urlField.setText(res[1]);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(urlLabel, gbc);
        gbc.gridx = 1;
        panel.add(urlField, gbc);

        // Username
        JLabel usernameLabel = createLabel("Username:");
        usernameField = createTextField();
        usernameField.setText(res[2]);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Tags
        JLabel tagLabel = createLabel("Tags:");
        tagfiled = createTextField();
        tagfiled.setText(res[4]);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(tagLabel, gbc);
        gbc.gridx = 1;
        panel.add(tagfiled, gbc);

        // Password
        JLabel passwordLabel = createLabel("Password:");
        passwordField = createPasswordField();

        try {
            String decryptedPassword = AESExample.decrypt(res[3]); // Replace with real method
            passwordField.setText(decryptedPassword);
        } catch (Exception e) {
            passwordField.setText("ERROR");
            e.printStackTrace();
        }

        gbc.gridx = 0; gbc.gridy = 4;
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

        // Save and Back Buttons
        saveButton = createIconButton("Save Changes");
        JButton backButton = createIconButton("Back");

        saveButton.addActionListener(e -> saveChanges());
        backButton.addActionListener(e -> swapContent("Vault"));



        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomButtonPanel.setBackground(bgColor);
        bottomButtonPanel.add(backButton);
        bottomButtonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(bottomButtonPanel, gbc);

        return panel;
    }





    private JPanel createVaultPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setBackground(new Color(17, 24, 39));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<String[]> result = db.get_vault_entries(loginUsername); // Each entry: [website, username, password]

        for (int i = 0; i < Math.min(result.size(), 15); i++) {
            String[] entry = result.get(i);
            String websiteStr = entry[0];
            String usernameStr = entry[2];
            String passwordStr = entry[2];
            String ent1=entry[5];
            int ent2 = Integer.parseInt(ent1);

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(30, 41, 59));
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    set_entry_id(ent2);
                    swapContent("passwordEntryPanel"); // Assume this switches the panel
                }

                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(51, 65, 85));
                }

                public void mouseExited(MouseEvent e) {
                    card.setBackground(new Color(30, 41, 59));
                }
            });

            JLabel website = new JLabel("Website: " + websiteStr);
            website.setFont(new Font("Segoe UI", Font.BOLD, 16));
            website.setForeground(Color.WHITE);

            JLabel username = new JLabel("Username: " + usernameStr);
            username.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            username.setForeground(new Color(200, 200, 200));

            JLabel password = new JLabel("Password: ********"); // Hide actual password
            password.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            password.setForeground(new Color(200, 200, 200));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(new Color(30, 41, 59));
            textPanel.add(website);
            textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            textPanel.add(username);
            textPanel.add(password);

            card.add(textPanel, BorderLayout.CENTER);
            gridPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(59, 130, 246);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(17, 24, 39));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }


    private JPanel createAddNewPanel() {
       {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(17, 24, 39));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));

            JLabel title = new JLabel("Add New Password");
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(new Color(147, 197, 253));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextField websiteField = createStyledTextField();
            JTextField usernameField = createStyledTextField();
            JPasswordField passwordField = createStyledPasswordField();
            JTextField tagField = createStyledTextField();
            JTextField urlField = createStyledTextField();

            JButton saveButton = new JButton("Save");
            saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            saveButton.setBackground(new Color(96, 165, 250));
            saveButton.setForeground(Color.BLACK);
            saveButton.setFocusPainted(false);
            saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            saveButton.addActionListener(e -> {
               String pass1=new String(passwordField.getPassword());
               try{
                   String pass_encrpt=AESExample.encrypt(pass1);
                   db.set_vault_entries(user_id,websiteField.getText(),urlField.getText(),usernameField.getText(),pass_encrpt,tagField.getText(),loginUsername);

               }
               catch (Exception a)
               {
                   JOptionPane.showMessageDialog(null,a);
               }

            });

            panel.add(title);
            panel.add(Box.createRigidArea(new Dimension(0, 30)));
            panel.add(labeledField("Website", websiteField));
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(labeledField("Username", usernameField));
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(labeledField("Password", passwordField));
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(labeledField("Tags", tagField));
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(labeledField("URL", urlField));
            panel.add(Box.createRigidArea(new Dimension(0, 30)));
            panel.add(saveButton);

            return panel;
        }
    }


    private JPanel createSecureNotesPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setBackground(new Color(17, 24, 39));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<String[]> result = db.get_secure_notes(loginUsername); // Each entry: [title, contentPreview, dateCreated, note_id]

        for (int i = 0; i < Math.min(result.size(),15); i++) {
            String[] entry = result.get(i);
            String titleStr = entry[0];
            String previewStr = entry[1];
            String dateStr = entry[2];
            String not1 = entry[4];
            int not2 =Integer.parseInt(not1);



            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(30, 41, 59));
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                       set_notes_id(not2);
                       swapContent("secureNoteViewPanel"); // Switch to detailed note view
                }

                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(51, 65, 85));
                }

                public void mouseExited(MouseEvent e) {
                    card.setBackground(new Color(30, 41, 59));
                }
            });

            JLabel title = new JLabel("Title: " + titleStr);
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            title.setForeground(Color.WHITE);

            JLabel preview = new JLabel("<html><body style='width:200px'>" + previewStr + "</body></html>");
            preview.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            preview.setForeground(new Color(200, 200, 200));

            JLabel date = new JLabel("Date: " + dateStr);
            date.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            date.setForeground(new Color(150, 150, 150));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(new Color(30, 41, 59));
            textPanel.add(title);
            textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            textPanel.add(preview);
            textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            textPanel.add(date);

            card.add(textPanel, BorderLayout.CENTER);
            gridPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(59, 130, 246);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(17, 24, 39));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }




    public  JPanel createSettingsPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 1, 20, 20));
        gridPanel.setBackground(new Color(17, 24, 39));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[][] settingsOptions = {
                {"🔐 Password History", "View your saved password entries", "passwordHistoryPanel"},
                {"✉️ Update Email", "Change your registered email address", "updateEmailPanel"},
                {"🔒 Update Password", "Change your account password", "updatePasswordPanel"}
        };

        for (String[] option : settingsOptions) {
            String title = option[0];
            String description = option[1];
            String panelKey = option[2];

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(30, 41, 59));
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    swapContent(panelKey);


                }

                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(new Color(51, 65, 85));
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(new Color(30, 41, 59));
                }
            });

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);

            JLabel descriptionLabel = new JLabel(description);
            descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descriptionLabel.setForeground(new Color(200, 200, 200));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(new Color(30, 41, 59));
            textPanel.add(titleLabel);
            textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            textPanel.add(descriptionLabel);

            card.add(textPanel, BorderLayout.CENTER);
            gridPanel.add(card);
        }

        // Return button
        JButton returnButton = new JButton("← Return");
        returnButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        returnButton.setBackground(new Color(96, 165, 250));
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        returnButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        returnButton.addActionListener(e ->swapContent("Vault" ));

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonWrapper.setBackground(new Color(17, 24, 39));
        buttonWrapper.add(returnButton);

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(new Color(17, 24, 39));
        scrollWrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(59, 130, 246);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(17, 24, 39));
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(buttonWrapper, BorderLayout.SOUTH);

        return wrapper;
    }



private JPanel labeledField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(400, 50));
        panel.setBackground(new Color(17, 24, 39));

        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Color.WHITE);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(jLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
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
        String tags = tagfiled.getText();
        try {
            String pass_encrpt=AESExample.encrypt(pass);
            db.update_vault_entries(user_id,site,url,user,pass_encrpt,tags,entity_id);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,e);
        }




    }















    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(31, 41, 55));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(31, 41, 55));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }


    public JPanel createSecureNoteViewPanel() {
        // Get note metadata: [title, filepath, dateCreated]
        String[] note = db.get_secure_notes_notes_id(notes_id);
        System.out.println(notes_id);

        String titleStr = note[0];     // title
        String filePath = note[2];     // encrypted file path
        String dateStr = note[2];      // creation date

        // Load and decrypt the content
        String contentStr = SecureNoteLoader.loadDecryptedNote(filePath);
        if (contentStr == null) {
            contentStr = "[Error reading or decrypting note content.]";
        }

        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
        viewPanel.setBackground(new Color(17, 24, 39));
        viewPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel title = new JLabel(titleStr);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel date = new JLabel("Created: " + dateStr);
        date.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        date.setForeground(new Color(160, 160, 160));
        date.setAlignmentX(Component.LEFT_ALIGNMENT);
        date.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));

        JTextArea content = new JTextArea(contentStr);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        content.setWrapStyleWord(true);
        content.setLineWrap(true);
        content.setEditable(false);
        content.setBackground(new Color(30, 41, 59));
        content.setForeground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setPreferredSize(new Dimension(700, 400));
        contentScroll.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 1));
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(59, 130, 246));
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(100, 40));
        backButton.addActionListener(e -> swapContent("Secure Notes"));

        viewPanel.add(backButton);
        viewPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        viewPanel.add(title);
        viewPanel.add(date);
        viewPanel.add(contentScroll);

        return viewPanel;
    }



    public JPanel createAddNotePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(17, 24, 39));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel heading = new JLabel("Add New Note");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title label + field
        JLabel titleLabel = new JLabel("Title");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBackground(new Color(31, 41, 55));
        titleField.setForeground(Color.WHITE);
        titleField.setCaretColor(Color.WHITE);
        titleField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tag label + field
        JLabel tagLabel = new JLabel("Tags (comma separated)");
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagLabel.setForeground(Color.WHITE);
        tagLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JTextField tagField = new JTextField();
        tagField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        tagField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagField.setBackground(new Color(31, 41, 55));
        tagField.setForeground(Color.WHITE);
        tagField.setCaretColor(Color.WHITE);
        tagField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tagField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Content label + area
        JLabel contentLabel = new JLabel("Content");
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        contentLabel.setForeground(Color.WHITE);
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JTextArea contentArea = new JTextArea();
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setBackground(new Color(31, 41, 55));
        contentArea.setForeground(Color.WHITE);
        contentArea.setCaretColor(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(700, 300));
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 1));
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Save button
        JButton saveButton = new JButton("Save Note");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(59, 130, 246));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(150, 40));

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String tags = tagField.getText().trim();
            String content = contentArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in title, content (and optionally tags).", "Missing Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Step 1: Save note to file and encrypt
                Map<String, String> result = SecureNoteSaver.saveNote(title, content);

                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Error saving file.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String filename = result.get("filename");
                String filepath = result.get("filepath");

                // Step 2: Save metadata in the database
                db.set_secure_notes(user_id, title, filepath, tags,loginUsername); // You can pass filepath or filename

                JOptionPane.showMessageDialog(panel, "Note saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                swapContent("Secure Notes");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error saving note: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(heading);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(tagLabel);
        panel.add(tagField);
        panel.add(contentLabel);
        panel.add(contentScroll);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(saveButton);

        return panel;
    }







}
