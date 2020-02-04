package socs.network.node;

import socs.network.message.SOSPFPacket;
import socs.network.util.Configuration;

import java.io.*;
import java.net.Socket;


public class Router {

  protected LinkStateDatabase lsd;

  RouterDescription rd = new RouterDescription();

  //assuming that all routers are with 4 ports
  Link[] ports = new Link[4];

  public Router(Configuration config) {
    rd.simulatedIPAddress = config.getString("socs.network.router.ip");
    rd.processPortNumber = config.getShort("socs.network.router.port");
    lsd = new LinkStateDatabase(rd);
  }

  public void setRouterStatus(RouterStatus rs){
    System.out.println("set "+rd.simulatedIPAddress+" state to "+rs.toString()+";\n");
    rd.status = rs;
  }

  public void addAttach(String processIP, short processPort,
                        String simulatedIP){
    processAttach(processIP,processPort,simulatedIP,(short)0);
  }

  public AttachStatus isAttached(String simulatedIP){
    for (int i=0; i<4; i++){
      if(ports[i]!=null){
        if(ports[i].rd2.simulatedIPAddress.equals(simulatedIP)) return AttachStatus.ATTACHED; // Attachment Already exists
      }
    }
    return AttachStatus.NOT_ATTACHED;
  }

  public int findPort(){
    for(int i=0; i<4;i++){
      if(ports[i]==null){
        return i;
      }
    }
    return -1;
  }


  /**
   * output the shortest path to the given destination ip
   * <p/>
   * format: source ip address  -> ip address -> ... -> destination ip
   *
   * @param destinationIP the ip adderss of the destination simulated router
   */
  private void processDetect(String destinationIP) {

  }

  /**
   * disconnect with the router identified by the given destination ip address
   * Notice: this command should trigger the synchronization of database
   *
   * @param portNumber the port number which the link attaches at
   */
  private void processDisconnect(short portNumber) {

  }

  /**
   * attach the link to the remote router, which is identified by the given simulated ip;
   * to establish the connection via socket, you need to indentify the process IP and process Port;
   * additionally, weight is the cost to transmitting data through the link
   * <p/>
   * NOTE: this command should not trigger link database synchronization
   */
  private void processAttach(String processIP, short processPort,
                             String simulatedIP, short weight) {
    //check if its connected and find a free port, return full if it has no availability
    if(isAttached(simulatedIP)==AttachStatus.ATTACHED) {
      System.out.println(simulatedIP + " is already attached.");
      return;
    }
    int availablePort = findPort();
    if(availablePort==100){
      System.out.println("All ports are occupied, no available port currently.");
      return;
    }else{
      RouterDescription guestRD = new RouterDescription();
      guestRD.processIPAddress = processIP;
      guestRD.processPortNumber = processPort;
      guestRD.simulatedIPAddress = simulatedIP;
      Link templink = new Link(this.rd, guestRD);
      ports[availablePort] = templink;
    }

  }

  /**
   * broadcast Hello to neighbors
   */
  private void processStart() {

    try {
      for (int i=0; i<4; i++){

        if (ports[i]==null) return;
        //loop neighbors
        Socket outSocket = new Socket(ports[i].rd2.processIPAddress,ports[i].rd2.processPortNumber);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        SOSPFPacket packet = new SOSPFPacket(rd.processIPAddress,rd.processPortNumber,rd.simulatedIPAddress,ports[i].rd2.simulatedIPAddress,(short)0);
        out.writeObject(packet);

        SOSPFPacket packetIn = (SOSPFPacket)in.readObject();

        //set to two way
        if(packetIn.sospfType==(short)0) {
          System.out.println("Received HELLO from "+packetIn.srcIP+";\n");
          System.out.println("set " + ports[i].rd2.simulatedIPAddress + " state to " + RouterStatus.TWO_WAY + ";\n");
          ports[i].rd1.status = RouterStatus.TWO_WAY;
          ports[i].rd2.status = RouterStatus.TWO_WAY;
        }

        //send hello back
        out.writeObject(packet);

        outSocket.close();
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e){
      System.out.println("Error detected during processStart");
      e.printStackTrace();
    }
  }

  /**
   * attach the link to the remote router, which is identified by the given simulated ip;
   * to establish the connection via socket, you need to indentify the process IP and process Port;
   * additionally, weight is the cost to transmitting data through the link
   * <p/>
   * This command does trigger the link database synchronization
   */
  private void processConnect(String processIP, short processPort,
                              String simulatedIP, short weight) {

  }

  /**
   * output the neighbors of the routers
   */
  private void processNeighbors() {
    System.out.println("IP address of neighbors: ");
    for(int i=0; i<4; i++){
      if(ports[i]!=null){
        System.out.println("Neighbor "+i+": "+ports[i].rd2.simulatedIPAddress);
      }
    }
    System.out.println();
  }

  /**
   * disconnect with all neighbors and quit the program
   */
  private void processQuit() {

  }

  public void terminal() {
    try {
      InputStreamReader isReader = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isReader);
      System.out.print(">> ");
      String command = br.readLine();
      while (true) {
        if (command.startsWith("detect ")) {
          String[] cmdLine = command.split(" ");
          processDetect(cmdLine[1]);
        } else if (command.startsWith("disconnect ")) {
          String[] cmdLine = command.split(" ");
          processDisconnect(Short.parseShort(cmdLine[1]));
        } else if (command.startsWith("quit")) {
          processQuit();
        } else if (command.startsWith("attach ")) {
          String[] cmdLine = command.split(" ");
          processAttach(cmdLine[1], Short.parseShort(cmdLine[2]),
                  cmdLine[3], Short.parseShort(cmdLine[4]));
        } else if (command.equals("start")) {
          processStart();
        } else if (command.equals("connect ")) {
          String[] cmdLine = command.split(" ");
          processConnect(cmdLine[1], Short.parseShort(cmdLine[2]),
                  cmdLine[3], Short.parseShort(cmdLine[4]));
        } else if (command.equals("neighbors")) {
          //output neighbors
          processNeighbors();
        } else {
          //invalid command
          break;
        }
        System.out.print(">> ");
        command = br.readLine();
      }
      isReader.close();
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
