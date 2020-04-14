package socs.network.node;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable {
    private Router router;
    private ServerSocket serverSocket;
    private Map<Socket, Thread> allThreads;

    public ServerThread(Router router) {
        this.router = router;
        this.allThreads = new HashMap<Socket, Thread>();
    }

    public void run() {
        try{
            serverSocket = new ServerSocket(router.rd.processPortNumber);
            while(true){
//                System.out.println("Waiting for Client to connect on port " + serverSocket.getLocalPort()+"...");
                Socket newSocket = serverSocket.accept();
//                System.out.println("Just connected to "+newSocket.getRemoteSocketAddress());

                Thread client = new Thread(new ClientThread(router, newSocket));
                allThreads.put(newSocket, client);
                client.start();
            }
        }catch(SocketTimeoutException e){
            System.out.println("Socket timed out");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void closeAll() {
        try {
            serverSocket.close();
            serverSocket = null;

            for (Thread channelThread: allThreads.values()) {
                channelThread.interrupt();
            }

            allThreads = new HashMap<Socket, Thread>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
