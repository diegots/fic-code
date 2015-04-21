package es.udc.psi14.lab08trabazo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class EchoServer {

    private final static String banner = "[EchoServer] ";
    private volatile boolean running = true;

    ServerSocket serverSocket;

    public EchoServer(){}

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while(running){
                Thread clientThread = new Thread(new ClientHandler(serverSocket.accept()));
                clientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Log.d(NetActiv.TAG, banner + "closing server socket");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        Log.d(NetActiv.TAG, banner + "terminate");
        running = false;
    }
}

class ClientHandler implements Runnable {

    private final static String banner = "[ClientHandler] ";

    private static int numConnections;
    private int connectionId = 0;
    Socket clientSocket;

    public ClientHandler(Socket s) throws SocketException {
        connectionId = numConnections++;
        Log.d(NetActiv.TAG, banner + "handling connection, #" + connectionId);

        clientSocket = s;
        clientSocket.setSoTimeout(10000); // Client socket ends after 10 seconds
    }

    public void run() {

        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            while((inputLine = in.readLine()) != null){
                outputLine = inputLine;
                Log.d(NetActiv.TAG, banner + "received: " + outputLine);
                out.write(outputLine+"\n");
                out.flush();
                if (outputLine.equals("exit"))
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            try {
                in.close();
                clientSocket.close();
                Log.d(NetActiv.TAG, banner + "closing connection, #" + connectionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
