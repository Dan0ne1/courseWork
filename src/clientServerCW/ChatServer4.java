package clientServerCW;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.lang.System;


public class ChatServer4 {

    private static Set<String> ids = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static List<String> ips1 = new ArrayList<>();
    private static List<String> ips2 = new ArrayList<>();
    private static List<String> ports1 = new ArrayList<>();
    private static List<String> ports2 = new ArrayList<>();


    // initialize and start our thread:
    public static void main(String[] args) throws Exception {

        System.out.println("The chat server is running...");

        ExecutorService pool = Executors.newFixedThreadPool(500);

        try (ServerSocket listener = new ServerSocket(59008)) {

            while (true) {
                pool.execute(new Handler(listener.accept()));

                Thread t = new Thread(new Handler(null));
                t.start();
            }
        }
    }


    private static class Handler implements Runnable {
        private String id;
        private String ip1;
        private String ip2;
        private String port1;
        private String port2;

        private Socket socket;
        private Scanner in;
        private PrintWriter out;


        public Handler(Socket socket) {
            this.socket = socket;
        }


        public void run() {

            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                //  Requesting an ID until we get a unique one:
                while (true) {
                    out.println("SUBMITID");
                    id = in.nextLine();
                    if (id == null) {
                        return;
                    }
                    synchronized (ids) {
                        if (!id.isEmpty() && !ids.contains(id)) {
                            ids.add(id);
                            break;
                        }
                    }
                }


                out.println("IDACCEPTED " + id);
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + id + " has joined");
                }
                writers.add(out);


                // member should be informed about being 1-st at start-up (something wrong, needs to be revised:
                while (true) {
                    if (ids.size() == 1) {
                        out.println("COORDINATOR" + id);
                        break;
                    }
                    synchronized (ids) {
                        if (!id.isEmpty()) {
                            ids.add(id);
                            break;
                        }
                    }
                }


                // I added separate while loop to every request:
                while (true) {
                    out.println("SUBMITIP1");   // client ip
                    ip1 = in.nextLine();
                    if (ip1 == null) {
                        return;
                    }
                    synchronized (ips1) {
                        if (!ip1.isEmpty()) {
                            ips1.add(ip1);
                            break;
                        }
                    }
                }


                //requesting clients port:
                while (true) {
                    out.println("SUBMITPORT1");
                    port1 = in.nextLine();
                    if (port1 == null) {
                        return;
                    }
                    synchronized (ports1) {
                        if (!port1.isEmpty()) {
                            ports1.add(port1);
                            break;
                        }
                    }
                }

                // asking for servers ip:
                while (true) {
                    out.println("SUBMITIP2");
                    ip2 = in.nextLine();
                    if (ip2 == null) {
                        return;
                    }
                    synchronized (ips2) {
                        if (!ip2.isEmpty()) {
                            ips2.add(ip2);
                            break;
                        }
                    }
                }

                // asking for servers port:
                while (true) {
                    out.println("SUBMITPORT2");
                    port2 = in.nextLine();
                    if (port2 == null) {
                        return;
                    }
                    synchronized (ports2) {
                        if (!port2.isEmpty()) {
                            ports2.add(port2);
                            break;
                        }
                    }
                }

                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    if (input.toLowerCase().startsWith("/info")) {
                        System.out.println("Group members IDs are: " + ids);
                        System.out.println("Group members IPs are: " + ips1);
                        System.out.println("Servers IPs are: " + ips2);
                        System.out.println("Group members PORTs are: " + ports1);
                        System.out.println("Servers PORTs are: " + ports2);
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + id + ": " + input);
                    }
                }


            } catch (Exception e) {
                System.out.println(e);

            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (id != null) {
                    System.out.println(id + " is leaving");    // console output
                    ids.remove(id);
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + id + " has left");    // windows output 
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
