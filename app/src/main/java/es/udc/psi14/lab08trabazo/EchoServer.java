package es.udc.psi14.lab08trabazo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class EchoServer {

    private final static String banner = "[EchoServer] ";
    private volatile boolean running = true;

    ServerSocket serverSocket;
    Handler handler;

    public EchoServer(Handler handler) {
        this.handler = handler;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while(running){
                Thread clientThread = new Thread(new ClientHandler(serverSocket.accept(), handler));
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
    Handler handler;

    private void sendDataToUI (String key, String data) {

        Bundle b = new Bundle();
        Message m = new Message();

        b.putString(key, data);
        m.setData(b);
        handler.sendMessage(m);

        Log.d(NetActiv.TAG, banner + "sendDataToUI: " + data);
    }

    public ClientHandler(Socket s, Handler handler) throws SocketException {
        connectionId = numConnections++;
        Log.d(NetActiv.TAG, banner + "handling connection, #" + connectionId);

        clientSocket = s;
        clientSocket.setSoTimeout(10000); // Client socket ends after 10 seconds
        this.handler = handler;
    }

    public void run() {

        sendDataToUI(ServerActiv.KEY_CLIENT_THREAD_IP, clientSocket.getInetAddress().getHostAddress());

        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            while((inputLine = in.readLine()) != null){
                outputLine = inputLine;
                Log.d(NetActiv.TAG, banner + "received: " + outputLine);
                sendDataToUI(ServerActiv.KEY_CLIENT_THREAD_LINE, outputLine);
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

                String endMsg = "closing connection, #" + connectionId;
                sendDataToUI(ServerActiv.KEY_CLIENT_THREAD_ENDLINE, endMsg);
                Log.d(NetActiv.TAG, banner + endMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
