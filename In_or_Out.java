package Socket_Network;

import java.io.*;
import java.net.Socket;

public class In_or_Out {
    private Socket socket = null;

    public In_or_Out(Socket socket){
        this.socket = socket;
    }
    public PrintWriter getWriter() throws IOException {
        OutputStream socketOut = this.socket.getOutputStream();
        return new PrintWriter(socketOut,true);
    }

    public BufferedReader getReader() throws IOException {
        InputStreamReader socketIn = new InputStreamReader(this.socket.getInputStream());
        return new BufferedReader(socketIn);
    }
}
