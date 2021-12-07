import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static boolean shutdown = false;
    static String adminName;
    static ArrayList<Socket> activeClients = new ArrayList<>();
    static ArrayList<PrintWriter> printWritersOfActiveClients = new ArrayList<>();
    static ArrayList<Thread> clientHandlerThreads = new ArrayList<>();
    static ArrayList<Thread> activeSenderThreads;
    static ArrayList<Thread> activeReceiverThreads;
    static Thread clientAcceptThread;

    public static void main(String[] args) {
        ServerSocket mainServer;
        try {

            mainServer = new ServerSocket(666);
            System.out.println("Server gestartet...");
            System.out.println("Admin: "+args[0]);
            adminName = args[0];
            clientAcceptThread = new Thread() {
                public void run() {
                    while (!shutdown) {
                        Socket remote = null;
                        try {
                            remote = mainServer.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        activeClients.add(remote);
                        System.out.println("Remote Client accepted: "+remote.getInetAddress());
                        Socket finalRemote = remote;
                        Thread clientThread = new Thread() {
                            public void run() {
                                handleClient(finalRemote);
                            }
                        };
                        clientThread.start();
                    }
                }
            };
            clientAcceptThread.start();
            while (!shutdown) {
                Thread.sleep(1000);
            }
            for (int i = 0; i < clientHandlerThreads.size(); i++) {
                clientHandlerThreads.get(i).stop();

            }
            clientAcceptThread.stop();
            Thread.sleep(1000);
            mainServer.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    static public void handleClient(Socket clientSocket) {
        String clientName = "";
        BufferedReader in = null;
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            printWritersOfActiveClients.add(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!shutdown) {
            try {
                if (in.ready() &&!shutdown) {

                    String incomingMessage = in.readLine();
                    if (clientName.equals("")) {
                        clientName = incomingMessage;
                    } else {
                        System.out.println(clientName+": "+incomingMessage);
                        for (int i = 0; i < printWritersOfActiveClients.size(); i++) {
                            printWritersOfActiveClients.get(i).println(clientName+": "+incomingMessage);
                            //System.out.println("sende Nachricht an client:" +incomingMessage);
                        }
                        if (clientName.equals(adminName)) {
                            if (incomingMessage.equals("PWRDWNSYS")) {
                                shutdown=true;
                                System.out.println("fahre Server herunter...");
                            }

                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
