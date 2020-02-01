package socs;

import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class tester {

    @Test
    public void testSocket() throws IOException {
        Socket testClient = new Socket("0.0.0.0",2001);
        OutputStream outToServer = testClient.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF("Hello from "+testClient.getLocalSocketAddress());
        testClient.close();
    }

}
