import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Client-Klasse zum Debuggen. Wird in IDE mit Parameter "Adriane", dem Admin-Namen, gestartet.
 */
public class Client2 {
    static boolean shutdown = false;
    static String userName = null;
    static Socket clientSocket = null;
    static Scanner scanner;

    public static void main (String[] args) {

        if (args[0]!=null) {
            if (args[0].startsWith("userName=")) {
                userName = args[0].substring(9);
                if (!userName.equals("")) {
                    try {
                        clientSocket = new Socket(InetAddress.getLocalHost(), 666);
                        System.out.println("Connected!");
                        Thread clientSenderThread = new Thread(){
                            public void run() {
                                clientSender(clientSocket);
                            }
                        };
                        clientSenderThread.start();
                        Thread clientReceiverThread = new Thread(){
                            public void run() {
                                clientReceiver(clientSocket);
                            }
                        };
                        clientReceiverThread.start();
                        while (!shutdown) {
                            Thread.sleep(1000);
                        }
                        clientSenderThread.stop();
                        clientReceiverThread.stop();
                        clientSocket.close();
                        scanner.ioException();
                        System.out.println("Drücke Enter zum Beenden.");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void clientSender(Socket clientSocket) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(userName);
        scanner = new Scanner(System.in);
        String userInput;
        while(!shutdown){
            userInput = scanner.nextLine();
            out.println(userInput);
            if (userInput.equals("PWRDWNSYS")) {
                shutdown=true;
            }
        }
    }
    public static void clientReceiver(Socket clientSocket) {
        try {
            String receivedMessage;
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(!shutdown){
                if (in.ready()) {
                    receivedMessage = in.readLine();
                    System.out.println(receivedMessage);
                    if (receivedMessage.equals("PWRDWNSYS")) {
                        shutdown=true;
                    }
                }
            }
            System.out.println("Server fährt herunter!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
