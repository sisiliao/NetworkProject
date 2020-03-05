package socs.network.node;

import socs.network.message.LSA;
import socs.network.message.SOSPFPacket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
                router1.addAttach(srcProcessIP, srcProcessPort, srcIP, (short)0);
                System.out.println("set "+ srcIP +" state to "+RouterStatus.INIT+";\n");
                router1.ports[0].rd1.status = RouterStatus.INIT;
                router1.ports[0].rd2.status = RouterStatus.INIT;

                SOSPFPacket packetResponse = new SOSPFPacket(router1.rd.processIPAddress,router1.rd.processPortNumber,router1.rd.simulatedIPAddress,srcIP,(short)0);
                out.writeObject(packetResponse);

                packetResponse = (SOSPFPacket)in.readObject();
                if(packetResponse.sospfType==(short)0){
                    System.out.println("Received HELLO from "+packetResponse.srcIP+";\n");
                    System.out.println("set "+ srcIP +" state to "+RouterStatus.TWO_WAY+";\n");
                    router1.ports[0].rd1.status = RouterStatus.TWO_WAY;
                    router1.ports[0].rd2.status = RouterStatus.TWO_WAY;
                }

                router1.broadcasting(null);
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
                    router1.lsd._store.put(packet.srcIP, newLSA);
                    router1.forwarding(packet);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
