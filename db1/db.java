package db1;
import Server.AESExample;
import Server.hashing;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class db {

    public static void set_vault_entries(int user_id, String title, String url1, String username, String password_encrypted, String tags, String loginusername) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            // ✔️ Fixed: now includes 7 columns
            String sql = "INSERT INTO vault_entries (user_id, title, url, username, password_encrypted, tags, loginusername) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, user_id);
                pst.setString(2, title);
                pst.setString(3, url1);
                pst.setString(4, username);
                pst.setString(5, password_encrypted);
                pst.setString(6, tags);
                pst.setString(7, loginusername); // ✔️ now matches the 7th ?
                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }


    public static List<String[]> get_vault_entries(String username) {
        String url = "jdbc:mysql://localhost:3306/vz295";
        List<String[]> result = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "SELECT * FROM vault_entries WHERE loginusername= ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String s1 = rs.getString("title");
                    String s2 = rs.getString("url");
                    String s3 = rs.getString("username");
                    String s4 = rs.getString("password_encrypted");
                    String s5 = rs.getString("tags");
                    String s6 = Integer.toString(rs.getInt("entry_id"));

                    result.add(new String[]{s1, s2, s3, s4, s5, s6});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return result;
    }

    public static boolean set_users(String username, String email, String password_hash, String salt) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "INSERT INTO users (username, email, password_hash, salt) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, email);
                pst.setString(3, password_hash);
                pst.setString(4, salt);

                int rowsAffected = pst.executeUpdate();
                return rowsAffected > 0;  // return true if insert was successful
            }

        } catch (Exception e) {
            System.err.println("Error inserting user: " + e.getMessage());  // Log instead of showing dialog
            return false;
        }
    }





    public static String[] get_users(String username) {
        String url = "jdbc:mysql://localhost:3306/vz295";
        String[] result = null;

        try (Connection con = DriverManager.getConnection(url, "t", "")) {
            String sql = "SELECT * from users WHERE username = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String salt = rs.getString("salt");
                    String hash = rs.getString("password_hash");
                    String userId = Integer.toString(rs.getInt("user_id"));
                    String email= rs.getString("email");

                    result = new String[] { salt, hash, userId ,email };
                }
            }
        } catch (Exception e) {
            System.err.println("DB error: " + e.getMessage());
        }

        return result;
    }

    public static void set_password_history(int user_id,int entry_id,String password_encrypted) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "insert into password_history (entry_id,user_id,old_password_encrypted) values(?,?,?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1,entry_id);
                pst.setInt(2,user_id);
                pst.setString(3,password_encrypted);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());


        }
    }


    public static List<String[]> get_password_history(int user_id) {
        String url = "jdbc:mysql://localhost:3306/vz295";
        List<String[]> dataList = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "SELECT * FROM password_change_log WHERE user_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, user_id);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String title = rs.getString("title");
                    String username = rs.getString("username");
                    String lastUpdated = rs.getString("changed_at");
                    String oldPassword = rs.getString("old_password");
                    String newPassword = rs.getString("new_password");

                   String oldpass = AESExample.decrypt(oldPassword);
                   String newpass = AESExample.decrypt(newPassword);
                    dataList.add(new String[]{title, username, lastUpdated, oldpass, newpass});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return dataList;
    }





    public static void update_vault_entries(int user_id, String title, String url1, String username, String password_encrypted, String tags,int entity_id) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {

            // Step 1: Fetch current vault entry
            String fetchSql = "SELECT title, url, username, password_encrypted, tags FROM vault_entries WHERE entry_id = ?";
            try (PreparedStatement fetchStmt = con.prepareStatement(fetchSql)) {
                fetchStmt.setInt(1, entity_id);
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    String currentTitle = rs.getString("title");
                    String currentUrl = rs.getString("url");
                    String currentUsername = rs.getString("username");
                    String currentPassword = rs.getString("password_encrypted");
                    String currentTags = rs.getString("tags");

                    boolean isTitleChanged = !title.equals(currentTitle);
                    boolean isUrlChanged = !url1.equals(currentUrl);
                    boolean isUsernameChanged = !username.equals(currentUsername);
                    boolean isPasswordChanged = !password_encrypted.equals(currentPassword);
                    boolean isTagsChanged = !tags.equals(currentTags);

                    // Step 2: Log password change only
                    if (isPasswordChanged) {
                        String logSql = "INSERT INTO password_change_log (user_id,entry_id, old_password, new_password,title,username) VALUES (?, ?, ?,?,?,?)";
                        try (PreparedStatement logStmt = con.prepareStatement(logSql)) {
                            logStmt.setInt(1, user_id);
                            logStmt.setInt(2, entity_id);
                            logStmt.setString(3, currentPassword);
                            logStmt.setString(4, password_encrypted);
                            logStmt.setString(5, title);
                            logStmt.setString(6, username);
                            logStmt.executeUpdate();
                        }
                    }

                    // Step 3: Update vault entry if any field changed
                    if (isTitleChanged || isUrlChanged || isUsernameChanged || isPasswordChanged || isTagsChanged) {
                        String updateSql = "UPDATE vault_entries SET title = ?, url = ?, username = ?, password_encrypted = ?, tags = ? WHERE user_id = ?";
                        try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                            updateStmt.setString(1, title);
                            updateStmt.setString(2, url1);
                            updateStmt.setString(3, username);
                            updateStmt.setString(4, password_encrypted);
                            updateStmt.setString(5, tags);
                            updateStmt.setInt(6, user_id);
                            updateStmt.executeUpdate();

                            JOptionPane.showMessageDialog(null, "Vault entry updated successfully.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No changes detected.");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Vault entry not found for user ID: " + user_id);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }


    public static boolean updateEmail(String email, int user_id) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "UPDATE users SET email = ? WHERE user_id = ?";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, email);
                pst.setInt(2, user_id);

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Email updated successfully.");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "User ID not found.");
                    return false;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating email: " + e.getMessage());
            return false;
        }
    }


    public static boolean updatePassword(String password_hash, String salt, int user_id) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, password_hash);
                pst.setString(2, salt);
                pst.setInt(3, user_id);

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    // Optionally show message here if used for admin operations
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "User ID not found.");
                    return false;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating password and salt: " + e.getMessage());
            return false;
        }
    }





    public static String[] get_vault_entries_entry_id(int entry_id)
    {
        String url = "jdbc:mysql://localhost:3306/vz295";
        String[] result=null;

        try (Connection con = DriverManager.getConnection(url, "","")) {
            String sql = "SELECT * FROM vault_entries WHERE entry_id= ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, entry_id);
                ResultSet rs = pst.executeQuery();

                while (rs.next())
                {
                    String s1 = rs.getString("title");
                    String s2 = rs.getString("url");
                    String s3 = rs.getString("username");
                    String s4 = rs.getString("password_encrypted");
                    String s5 = rs.getString("tags");


                    result = new String[]{s1, s2, s3, s4, s5};


                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }


        return result;


    }

    public static void set_secure_notes(int user_id, String notes_name, String notes_path, String tags,String loginusername) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "insert into secure_notes(user_id,notes_name,tags,notes_path,loginusername) values(?,?,?,?,?)";

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setInt(1, user_id);
                pst.setString(2,notes_name);
                pst.setString(3, tags);
                pst.setString(4, notes_path);
                pst.setString(5, loginusername);


                pst.executeUpdate();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());


        }
    }

    public static List<String[]> get_secure_notes(String username) {
        String url = "jdbc:mysql://localhost:3306/vz295";
        List<String[]> result = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "SELECT * FROM secure_notes WHERE loginusername = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, username);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String s1 = rs.getString("notes_name");
                    String s2 = rs.getString("tags");
                    String s3 = rs.getString("notes_path");
                    String s4 = Integer.toString(rs.getInt("user_id"));
                    String s5 = Integer.toString(rs.getInt("notes_id"));

                    result.add(new String[]{s1, s2, s3, s4,s5});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return result;
    }

    public static String[] get_secure_notes_notes_id(int notes_id) {
        String url = "jdbc:mysql://localhost:3306/vz295";
        String[] result=null;

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "SELECT * FROM secure_notes WHERE notes_id= ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1,notes_id);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String s1 = rs.getString("notes_name");
                    String s2 = rs.getString("tags");
                    String s3 = rs.getString("notes_path");
                    String s4 = rs.getString("user_id");


                    result = new String[]{s1, s2, s3, s4,};
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return result;
    }


    public static boolean isEmailMatch(int user_id, String currentEmail) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "SELECT email FROM users WHERE user_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, user_id);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String emailFromDB = rs.getString("email");
                        return emailFromDB != null && emailFromDB.equalsIgnoreCase(currentEmail.trim());
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error checking email: " + e.getMessage());
        }
        return false;
    }

    public static void update_password(int user_id, String newHash, String newSalt) {
        String url = "jdbc:mysql://localhost:3306/vz295";

        try (Connection con = DriverManager.getConnection(url, "", "")) {
            String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, newHash);
                pst.setString(2, newSalt);
                pst.setInt(3, user_id);

                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("✅ Password updated successfully for user ID: " + user_id);
                } else {
                    System.out.println("⚠️ No user found with user ID: " + user_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ Failed to update password: " + e.getMessage());
        }
    }




    public static void main(String[] args) {




    }
}

