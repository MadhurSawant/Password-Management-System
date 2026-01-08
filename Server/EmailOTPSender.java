package Server;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class EmailOTPSender{

    public static boolean sendOTP(String to, String otp) {
        String smtpServer = "smtp.gmail.com";
        int port = 465; // SSL port
        String from = "";
        String password = ""; // Use Gmail app password if 2FA enabled

        try {
            Socket socket = SSLSocketFactory.getDefault().createSocket(smtpServer, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            readResponse(reader); // server greeting

            sendCommand(writer, "EHLO localhost");
            readResponse(reader);

            sendCommand(writer, "AUTH LOGIN");
            readResponse(reader);

            sendCommand(writer, Base64.getEncoder().encodeToString(from.getBytes())); // username
            readResponse(reader);

            sendCommand(writer, Base64.getEncoder().encodeToString(password.getBytes())); // password
            readResponse(reader);

            sendCommand(writer, "MAIL FROM:<" + from + ">");
            readResponse(reader);

            sendCommand(writer, "RCPT TO:<" + to + ">");
            readResponse(reader);

            sendCommand(writer, "DATA");
            readResponse(reader);

            // Compose plain text email
            writer.write("Subject: Your OTP Code\r\n");
            writer.write("From: " + from + "\r\n");
            writer.write("To: " + to + "\r\n");
            writer.write("\r\n");
            writer.write("Your OTP is: " + otp + "\r\n");
            writer.write(".\r\n");
            writer.flush();
            readResponse(reader);

            sendCommand(writer, "QUIT");
            readResponse(reader);

            writer.close();
            reader.close();
            socket.close();

            System.out.println("Email sent successfully");
            return true;


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
        }
        return false; 
    }

    private static void sendCommand(BufferedWriter writer, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
    }

    private static void readResponse(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("SERVER: " + line);
            if (!line.startsWith("250-") && line.matches("^[0-9]{3} .*")) break;
        }
    }

    public static void main(String[] args) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        sendOTP("", otp);
    }
}
