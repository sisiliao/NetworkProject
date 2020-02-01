package socs.network.node;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerThread implements Runnable{
    private Router router;

    public ServerThread(Router router) {
        this.router = router;
    }

    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(router.rd.processPortNumber);
            while(true){
                System.out.println("Waiting for Client to connect on port" + serverSocket.getLocalPort()+"...");
                Socket server = serverSocket.accept();
                System.out.println("Just connected to "+server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                System.out.println(in.readUTF());

            }
        }catch(SocketTimeoutException e){
            System.out.println("Socket timed out");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
