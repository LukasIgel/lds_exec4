import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    static boolean shutdown = false;
    static String userName = null;
    static Socket clientSocket = null;
    public static void main (String[] args) {


        if (args[0].startsWith("userName=")) {
            userName = args[0].substring(9);
            //System.out.println(userName);
        }
        try {
            clientSocket = new Socket(InetAddress.getLocalHost(), 666);
            while (!clientSocket.isConnected()) {
                Thread.sleep(100);
            }
            System.out.println("Connected!");
            Thread.sleep(1000);
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
            /*
            while (!shutdown) {

            }
            */

            //clientSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        Scanner scanner = new Scanner(System.in);
        String userInput;
        while(!shutdown){
            userInput = scanner.nextLine();
            out.println(userInput);
            shutdown = userInput.equals("PWRDWNSYS") ? true :false;
        }
    }
    public static void clientReceiver(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(!shutdown){
                if (in.ready()) {
                    System.out.println(in.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
