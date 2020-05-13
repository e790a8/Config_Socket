package Socket_Network;

import java.io.*;
import java.net.Socket;

public class Handle implements Runnable {
    private Socket socket = null;
    private In_or_Out in_or_out = null;

    public Handle(Socket socket) throws IOException {
        this.socket = socket;
        this.in_or_out = new In_or_Out(this.socket);
    }
    public String Echo(String msg) {
        return "Echo:" + msg;
    }
    @Override
    public void run() {
        try{
            System.out.println("New Connection "+socket.getInetAddress()+":"+socket.getPort());
            BufferedReader br = in_or_out.getReader();
            PrintWriter pw = in_or_out.getWriter();
            String msg = null;
            while ((msg = br.readLine()) != null){
                System.out.println(socket.getInetAddress()+":"+socket.getPort()+"："+msg);
                pw.println(Echo(msg));
                if (msg.equals("exit")){
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (socket != null){
                    socket.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
