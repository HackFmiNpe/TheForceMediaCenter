package realServer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
  public static void main(String[] args) throws Exception {
//    String str = "{\"gesture\":\"down\",\"full_name\":\"NFS\"}";
    String str = "{\"gesture\":\"down\"}";

    Socket socket = null;
    OutputStreamWriter osw;
    try {
        socket = new Socket("192.168.0.100", 1234);
        osw =new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
        osw.write(str, 0, str.length());
        osw.flush();
    } catch (IOException e) {
        System.err.print(e);
    } finally {
        socket.close();
    }
    
  }

}
