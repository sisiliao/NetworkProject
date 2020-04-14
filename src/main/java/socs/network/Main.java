package socs.network;

import socs.network.node.Router;
import socs.network.node.ServerThread;
import socs.network.util.Configuration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Main {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("usage: program conf_path");
      System.exit(1);
    }
    Router r = new Router(new Configuration(args[0]));
    ServerThread serverThreadInstance = new ServerThread(r);
    Thread server = new Thread(serverThreadInstance);
    server.start();
    r.setServerThread(server);
    r.setServerThreadInstance(serverThreadInstance);

//    Socket testClient = new Socket("0.0.0.0",20000);
//    OutputStream outToServer = testClient.getOutputStream();
//    DataOutputStream out = new DataOutputStream(outToServer);
//    out.writeUTF("Hello from "+testClient.getLocalSocketAddress());
//    testClient.close();

    r.terminal();
  }
}
