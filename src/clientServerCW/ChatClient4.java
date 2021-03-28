package clientServerCW;

import java.awt.event.ActionEvent;


import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatClient4 {

    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("GaduGadu");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);


    public ChatClient4(String serverAddress) {
        this.serverAddress = serverAddress;


// creating chatterbox:
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }


    private String getId() {
        return JOptionPane.showInputDialog(
                frame,                               // window
                "What is your ID?",
                "ID",                             // window title
                JOptionPane.PLAIN_MESSAGE);     // type of window
    }

    private String getClientIP() {
        return JOptionPane.showInputDialog(frame, "What is your IP number?");
    }

    private String getClientPort() {
        return JOptionPane.showInputDialog(frame, "Which port would you like to use today?");
    }

    private String getServerIP() {
        return JOptionPane.showInputDialog(frame, "What is the IP of the server that you wish to connect to?");
    }

    private String getServerPort() {
        return JOptionPane.showInputDialog(frame, "What is the port number of the server?");
    }


    private void run() throws IOException {
        try {
            Socket socket = new Socket(serverAddress, 59008);

            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);


            while (in.hasNextLine()) {

                String line = in.nextLine();

                if (line.startsWith("SUBMITID")) {
                    out.println(getId());
                } else if (line.startsWith("SUBMITIP1")) {
                    out.println(getClientIP());
                } else if (line.startsWith("SUBMITPORT1")) {
                    out.println(getClientPort());
                } else if (line.startsWith("SUBMITIP2")) {
                    out.println(getServerIP());
                } else if (line.startsWith("SUBMITPORT2")) {
                    out.println(getServerPort());
                } else if (line.startsWith("IDACCEPTED")) {
                    this.frame.setTitle("ChatWindow - " + line.substring(11));  // windows title will be "ChatWindow - ID of the user"
                    textField.setEditable(true);
                }

                // member should be informed about being 1-st at start-up (something wrong, needs to be revised:
                else if (line.startsWith("COORDINATOR")) {
                    this.frame.setTitle("ChatWindow - " + line.substring(11) + " - COORDINATOR");
                    JOptionPane.showMessageDialog(frame, "You are the Coordinator of this chat!");
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }

        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }

        ChatClient4 client = new ChatClient4(args[0]);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
} 
