package Socket_Network;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket = null;
    private Element server = null;
    private Element server_basics = null;
    private Element server_option = null;
    private Element server_option_PerformancePreferences = null;
    private Element server_option_Threading = null;
    private InetAddress ip = InetAddress.getByName("127.0.0.1");
    private boolean isOpen = false;
    private int port = 0;
    private int backlog = 1;
    private int timeout = 3000;
    private boolean resuseAddress = false;
    private int receiveBufferSize = 1024;
    private int connectionTime = 1;
    private int latency = 1;
    private int bandwidth = 1;
    private boolean threading_open = false;
    private int threading_count = 1;
    private ExecutorService executorService = null;

    public void getDocument() throws DocumentException {
        Document document = null;
        File directory = new File("");
        String absolutePath = directory.getAbsolutePath();
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(new File(absolutePath+"/Server.xml"));
        this.server = document.getRootElement();
    }

    public void getAttribute() throws UnknownHostException {
        this.server_basics = this.server.element("Basics");
        this.server_option = this.server.element("Option");
        this.server_option_PerformancePreferences = this.server_option.element("PerformancePreferences");
        this.server_option_Threading = this.server_option.element("Threading");
        String args = null;
        if ((args = server_basics.elementText("IP")) != "") this.ip = InetAddress.getByName(args);
        if ((args = server_basics.elementText("Port")) != "") this.port = Integer.valueOf(args);
        if ((args = server_basics.elementText("Backlog")) != "") this.backlog = Integer.valueOf(args);
        if ((args = server_option.elementText("Timeout")) != "") this.timeout = Integer.valueOf(args);
        if ((args = server_option.elementText("ResuseAddress")).equals("true")) this.resuseAddress = true;
        if ((args = server_option.elementText("ReceiveBufferSize")) != "") this.receiveBufferSize = Integer.valueOf(args);
        if ((args = server_option_PerformancePreferences.elementText("ConnectionTime")) != "") this.connectionTime = Integer.valueOf(args);
        if ((args = server_option_PerformancePreferences.elementText("Latency")) != "") this.latency = Integer.valueOf(args);
        if ((args = server_option_PerformancePreferences.elementText("Bandwidth")) != "") this.bandwidth = Integer.valueOf(args);
        if ((args = server_option_Threading.elementText("Open")).equals("true")) this.threading_open = true;
        if ((args = server_option_Threading.elementText("Count")) != "") this.threading_count = Integer.valueOf(args);
    }

    public void Set_Xml() throws IOException {
        this.server.element("isOpen").setText(String.valueOf(this.isOpen));
        this.server.element("Basics").element("IP").setText(String.valueOf(this.serverSocket.getInetAddress()).substring(1));
        this.server.element("Basics").element("Port").setText(String.valueOf(this.serverSocket.getLocalPort()));
        this.server.element("Basics").element("Backlog").setText(String.valueOf(this.backlog));
        this.server.element("Option").element("Timeout").setText(String.valueOf(this.serverSocket.getSoTimeout()));
        this.server.element("Option").element("ResuseAddress").setText(String.valueOf(this.serverSocket.getReuseAddress()));
        this.server.element("Option").element("ReceiveBufferSize").setText(String.valueOf(this.serverSocket.getReceiveBufferSize()));
        this.server.element("Option").element("PerformancePreferences").element("ConnectionTime").setText(String.valueOf(this.connectionTime));
        this.server.element("Option").element("PerformancePreferences").element("Latency").setText(String.valueOf(this.latency));
        this.server.element("Option").element("PerformancePreferences").element("Bandwidth").setText(String.valueOf(this.bandwidth));
        this.server.element("Option").element("Threading").element("Open").setText(String.valueOf(this.threading_open));
        this.server.element("Option").element("Threading").element("Count").setText(String.valueOf(this.threading_count));
    }

    public void Update_xml() throws IOException {
        OutputFormat outputFormat = new OutputFormat().createCompactFormat();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("src/Socket_Network/Server.xml"));
        XMLWriter xmlWriter = new XMLWriter(outputStreamWriter,outputFormat);
        xmlWriter.write(this.server);
        xmlWriter.close();
    }

    public Server() throws IOException, DocumentException {
        this.getDocument();
        this.getAttribute();
        this.serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(this.resuseAddress);
        serverSocket.setSoTimeout(this.timeout);
        serverSocket.setReceiveBufferSize(this.receiveBufferSize);
        serverSocket.setPerformancePreferences(this.connectionTime,this.latency,this.bandwidth);
        serverSocket.bind(new InetSocketAddress(this.ip,this.port),this.backlog);
        if (threading_open) this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * this.threading_count);
        System.out.println("server up");
        this.isOpen = true;
        this.Set_Xml();
        this.Update_xml();
    }

    public void service(){
        while (true){
            Socket socket = null;
            try{
                socket = this.serverSocket.accept();
                if (this.executorService != null){
                    executorService.execute(new Handle(socket));
                }else {
                    System.out.println("New Connection "+socket.getInetAddress()+":"+socket.getPort());
                    In_or_Out in_or_out = new In_or_Out(socket);
                    PrintWriter pw = in_or_out.getWriter();
                    BufferedReader br = in_or_out.getReader();
                    String msg = null;
                    while ((msg = br.readLine()) != null){
                        System.out.println(socket.getInetAddress()+":"+socket.getPort()+"："+msg);
                        pw.println("Echo："+msg);
                        if (msg.equals("exit")){
                            break;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException, DocumentException {
        new Server().service();
    }
}
