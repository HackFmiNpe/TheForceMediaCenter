package realServer;

import java.io.IOException;

import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
  public static void main(String[] args) throws Exception {
    String str = "{\"gesture\":\"open\"}";
    
    // implement go back
    
//    String str = "{\"gesture\":\"down\"}";
//    String str = "{\"gesture\":\"up\"}";
//    String str = "{\"gesture\":\"right\"}";
//    String str = "{\"gesture\":\"left\"}";
//    
//    String str = "{\"gesture\":\"open\",\"full_name\":\"avatarm.mp4\"}";
//    
//    String str = "{\"gesture\":\"vol_up\"}";
//    String str = "{\"gesture\":\"vol_down\"}";
//    
//    String str = "{\"gesture\":\"forward\"}";
//    String str = "{\"gesture\":\"backward\"}";
//    
//    String str = "{\"gesture\":\"close\"}";

    Socket socket = null;
    OutputStreamWriter osw;
    try {
        socket = new Socket("192.168.0.123", 1234);
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
