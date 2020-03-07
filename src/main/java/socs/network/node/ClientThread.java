package socs.network.node;

import socs.network.message.LSA;
import socs.network.message.LinkDescription;
import socs.network.message.SOSPFPacket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

public class ClientThread implements Runnable {
    Router router1;
    Socket clientSocket;


    public ClientThread(Router router1, Socket clientSocket) {
        this.router1 = router1;
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            SOSPFPacket packet = (SOSPFPacket)in.readObject();
            String srcProcessIP = packet.srcProcessIP;
            short srcProcessPort = packet.srcProcessPort;
            String srcIP = packet.srcIP;


            //Receive Hello
            if (packet.sospfType==0){
                System.out.println("Received HELLO from "+packet.srcIP+";\n");
                //add link
                int availablePort = router1.addAttach(srcProcessIP, srcProcessPort, srcIP, Short.MIN_VALUE);

                System.out.println("set "+ srcIP +" state to "+RouterStatus.INIT+";\n");

                int newPort = router1.findPort();
                router1.ports[availablePort].rd1.status = RouterStatus.INIT;
                router1.ports[availablePort].rd2.status = RouterStatus.INIT;

                SOSPFPacket packetResponse = new SOSPFPacket(router1.rd.processIPAddress,router1.rd.processPortNumber,router1.rd.simulatedIPAddress,srcIP,(short)0);
                out.writeObject(packetResponse);

                packetResponse = (SOSPFPacket)in.readObject();
                if(packetResponse.sospfType==(short)0){
                    System.out.println("Received HELLO from "+packetResponse.srcIP+";\n");
                    System.out.println("set "+ srcIP +" state to "+RouterStatus.TWO_WAY+";\n");
                    router1.ports[availablePort].rd1.status = RouterStatus.TWO_WAY;
                    router1.ports[availablePort].rd2.status = RouterStatus.TWO_WAY;
                }

                clientSocket.close();
                System.out.print(">>");

            //Receive a LSAUPDATE
            } else if (packet.sospfType==1){

                Vector<LSA> lsaArray = packet.lsaArray;
                LSA newLSA = lsaArray.lastElement();
                String newLSA_linkStateID = newLSA.linkStateID;
                int new_seq_no = newLSA.lsaSeqNumber;

                LSA currentLSA = router1.lsd._store.get(newLSA_linkStateID);

                //only update when seq number is greater than current max
                if(currentLSA == null || new_seq_no > currentLSA.lsaSeqNumber){

                    //if these 2 routers are neighbors, add the LSA to the receiver's lsd
                    for(Link l: router1.ports){
                        if(l!=null && l.rd2.simulatedIPAddress.equals(packet.srcIP)){
                            LinkedList<LinkDescription> linkList = newLSA.links;
                            for(LinkDescription ld : linkList){
                                if (ld.linkID.equals(router1.rd.simulatedIPAddress) && l.weight !=  (short) ld.tosMetrics) {
                                    l.weight = (short) ld.tosMetrics;

//                                    System.out.println("Print Link information for "+ router1.rd.simulatedIPAddress);
//                                    for(Link p : router1.ports){
//                                        if(p!=null)System.out.println(p.toString());
//                                    }

                                    LSA myOwnLSA = router1.lsd._store.get(router1.rd.simulatedIPAddress);
                                    myOwnLSA.links = router1.getLinkDescriptionList();

                                    router1.lsd._store.put(router1.rd.simulatedIPAddress, myOwnLSA);

                                    router1.broadcasting(null);
                                    break;
                                }
                            }


                        }
                    }

                    router1.lsd._store.put(packet.srcIP, newLSA);
                    router1.forwarding(packet);


                }


//                LSA newLSA = packet.lsaArray.lastElement();
//                LSA currentLSA = router1.lsd._store.get(packet.srcIP);
//                boolean newRouter = false;
//
//                if(currentLSA==null || newLSA.lsaSeqNumber > currentLSA.lsaSeqNumber){
//                    //Check if the srcIP is a direct neighbor with this router
//                    boolean directNeighbor = false;
//                    int linkPort = -1000;
//                    newRouter = currentLSA == null;
//
//                    for(int i=0;i<4;i++){
//                        if(router1.ports[i]!=null && router1.ports[i].rd2.simulatedIPAddress.equals(packet.srcIP)){
//                            directNeighbor = true;
//                            linkPort = i;
//                        }
//                    }
//
//                    //update link in ports
//                    if(directNeighbor){
//                        LinkedList<LinkDescription> ld_list = packet.lsaArray.lastElement().links;
//                        LinkDescription ld_updating = null;
//                        for(LinkDescription ld : ld_list){
//                            if(ld.linkID.equals(router1.rd.simulatedIPAddress)){
//                                ld_updating = ld;
//                                break;
//                            }
//                        }
//
//                        if(ld_updating!=null){
//                            if(ld_updating.tosMetrics!=(short)router1.ports[linkPort].weight && ld_updating.tosMetrics>-1){
//                                router1.ports[linkPort].weight = (short)ld_updating.tosMetrics;
//
//                                LSA myOwnLSA = router1.lsd._store.get(router1.rd.simulatedIPAddress);
//                                myOwnLSA.links = router1.getLinkDescriptionList();
//                                router1.lsd._store.put(router1.rd.simulatedIPAddress, myOwnLSA);
//                                router1.broadcasting(null);
//
//                            }
//                        }
//                    }
//
//                    router1.lsd._store.put(packet.srcIP, newLSA);
//                    for(int i=0; i<4; i++){
//                        if(router1.ports[i]!=null && !router1.ports[i].rd2.simulatedIPAddress.equals(packet.srcIP)){
//                            router1.broadcasting(packet);
//                        }
//
//                        if(newRouter){
//                            router1.broadcasting(null);
//                        }
//                    }
//                }




            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
