package Socket_Network;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket = null;
    private In_or_Out in_or_out = null;
    private Element client = null;
    private InetAddress target_IP = InetAddress.getByName("127.0.0.1");
    private int target_Port = 8000;
    private InetAddress local_IP = InetAddress.getByName("127.0.0.1");
    private int local_Port = 0;
    private boolean proxy_isOpen = false;
    private Proxy proxy = null;
    private Proxy.Type type = Proxy.Type.HTTP;
    private InetAddress proxy_IP = InetAddress.getByName("127.0.0.1");
    private int proxy_Port = 0;
    private boolean tcpNoDelay = false;
    private boolean resuseAddress = false;
    private int timeout = 3000;
    private boolean linger_isOpen = false;
    private int linger_Timout = 1000;
    private int sendBufferSize = 1024;
    private int receiveBufferSize = 1024;
    private boolean keepAlive = false;
    private boolean oOBInline = false;
    private int trafficClass = 0x02;
    private int connectionTime = 1;
    private int latency = 1;
    private int bandwidth = 1;

    public void getDocument() throws DocumentException {
        Document document = null;
        File directory = new File("");
        String absolutePath = directory.getAbsolutePath();
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(new File(absolutePath+"/Client.xml"));
        this.client = document.getRootElement();
    }

    public void getAttribute() throws UnknownHostException {
        Element client_basics = this.client.element("Basics");
        Element client_basics_Proxy = client_basics.element("Proxy");
        Element client_option = this.client.element("Option");
        Element client_option_PerformancePreferences = client_option.element("PerformancePreferences");
        String args = null;
        if ((args = client_basics.elementText("Target_IP")) != "") this.target_IP = InetAddress.getByName(args);
        if ((args = client_basics.elementText("Target_Port")) != "") this.target_Port = Integer.valueOf(args);
        if ((args = client_basics.elementText("Local_IP")) != "") this.local_IP = InetAddress.getByName(args);
        if ((args = client_basics.elementText("Local_Port")) != "") this.local_Port = Integer.valueOf(args);
        if ((args = client_basics_Proxy.elementText("isOpen")).equals("true")) this.proxy_isOpen = true;
        if ((args = client_basics_Proxy.elementText("Proxy_IP")) != "") this.proxy_IP = InetAddress.getByName(args);
        if ((args = client_basics_Proxy.elementText("Proxy_Port")) != "") this.local_Port = Integer.valueOf(args);
        if ((args = client_option.elementText("TcpNoDelay")).equals("true")) this.tcpNoDelay = true;
        if ((args = client_option.elementText("ResuseAddress")).equals("true")) this.resuseAddress = true;
        if ((args = client_option.elementText("Timeout")) != "") this.timeout = Integer.valueOf(args);
        if ((args = client_option.element("Linger").elementText("isOpen")).equals("true")) this.linger_isOpen = true;
        if ((args = client_option.element("Linger").elementText("Timeout")) != "") this.linger_Timout = Integer.valueOf(args);
        if ((args = client_option.elementText("SendBufferSize")) != "") this.sendBufferSize = Integer.valueOf(args);
        if ((args = client_option.elementText("ReceiveBufferSize")) != "") this.receiveBufferSize = Integer.valueOf(args);
        if ((args = client_option.elementText("KeepAlive")).equals("true")) this.keepAlive = true;
        if ((args = client_option.elementText("OOBInline")).equals("true")) this.oOBInline = true;
        if ((args = client_option.elementText("TrafficClass")) != "") this.trafficClass = Integer.valueOf(args,16);
        if ((args = client_option_PerformancePreferences.elementText("ConnectionTime")) != "") this.connectionTime = Integer.valueOf(args);
        if ((args = client_option_PerformancePreferences.elementText("Latency")) != "") this.latency = Integer.valueOf(args);
        if ((args = client_option_PerformancePreferences.elementText("Bandwidth")) != "") this.bandwidth = Integer.valueOf(args);
        switch (args = client_basics_Proxy.elementText("Type")){
            case "HTTP":
                this.type = Proxy.Type.HTTP;
                break;
            case "SOCKS":
                this.type = Proxy.Type.SOCKS;
                break;
            case "DIRECT":
                this.type = Proxy.Type.DIRECT;
                break;
            default:
        }
    }

    public void Set_Xml(){
        this.client.element("Basics").element("Target_IP").setText(String.valueOf(this.target_IP).substring(1));
        this.client.element("Basics").element("Target_Port").setText(String.valueOf(this.target_Port));
        this.client.element("Basics").element("Local_IP").setText(String.valueOf(this.local_IP).substring(1));
        this.client.element("Basics").element("Local_Port").setText(String.valueOf(this.local_Port));
        this.client.element("Basics").element("Proxy").element("isOpen").setText(String.valueOf(this.proxy_isOpen));
        this.client.element("Basics").element("Proxy").element("Proxy_IP").setText(String.valueOf(this.proxy_IP).substring(1));
        this.client.element("Basics").element("Proxy").element("Proxy_Port").setText(String.valueOf(this.proxy_Port));
        this.client.element("Option").element("TcpNoDelay").setText(String.valueOf(this.tcpNoDelay));
        this.client.element("Option").element("ResuseAddress").setText(String.valueOf(this.resuseAddress));
        this.client.element("Option").element("Timeout").setText(String.valueOf(this.timeout));
        this.client.element("Option").element("Linger").element("isOpen").setText(String.valueOf(this.linger_isOpen));
        this.client.element("Option").element("Linger").element("Timeout").setText(String.valueOf(this.linger_Timout));
        this.client.element("Option").element("SendBufferSize").setText(String.valueOf(this.sendBufferSize));
        this.client.element("Option").element("ReceiveBufferSize").setText(String.valueOf(this.receiveBufferSize));
        this.client.element("Option").element("KeepAlive").setText(String.valueOf(this.keepAlive));
        this.client.element("Option").element("OOBInline").setText(String.valueOf(this.oOBInline));
        this.client.element("Option").element("TrafficClass").setText(Integer.toHexString(this.trafficClass));
        this.client.element("Option").element("PerformancePreferences").element("ConnectionTime").setText(String.valueOf(this.connectionTime));
        this.client.element("Option").element("PerformancePreferences").element("Latency").setText(String.valueOf(this.latency));
        this.client.element("Option").element("PerformancePreferences").element("Bandwidth").setText(String.valueOf(this.bandwidth));
    }

    public void Update_xml() throws IOException {
        OutputFormat outputFormat = new OutputFormat().createCompactFormat();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("src/Socket_Network/Client.xml"));
        XMLWriter xmlWriter = new XMLWriter(outputStreamWriter,outputFormat);
        xmlWriter.write(this.client);
        xmlWriter.close();
    }

    public Client() throws IOException, DocumentException {
        this.getDocument();
        this.getAttribute();
        if (this.proxy_isOpen){
            this.proxy = new Proxy(this.type,new InetSocketAddress(this.proxy_IP,this.proxy_Port));
            this.socket = new Socket(this.proxy);
        }else {
            this.socket = new Socket();
        }
        this.socket.setPerformancePreferences(this.connectionTime,this.latency,this.bandwidth);
        this.socket.setReceiveBufferSize(this.receiveBufferSize);
        this.socket.setSendBufferSize(this.sendBufferSize);
        this.socket.setReuseAddress(this.resuseAddress);
        this.socket.setSoTimeout(this.timeout);
        this.socket.setKeepAlive(this.keepAlive);
        this.socket.setOOBInline(this.oOBInline);
        this.socket.setSoLinger(this.linger_isOpen,this.linger_Timout);
        this.socket.setTcpNoDelay(this.tcpNoDelay);
        this.socket.setTrafficClass(this.trafficClass);
        this.socket.bind(new InetSocketAddress(this.local_IP,this.local_Port));
        this.socket.connect(new InetSocketAddress(this.target_IP,this.target_Port),this.timeout);
        System.out.println("Connection Success!");
        this.in_or_out = new In_or_Out(socket);
        this.Set_Xml();
        this.Update_xml();
    }
    public void Talk(){
        try{
            PrintWriter pw = in_or_out.getWriter();
            BufferedReader br = in_or_out.getReader();
            BufferedReader localbufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = localbufferedReader.readLine()) != null){
                pw.println(msg);
                System.out.println(br.readLine());
                if (msg.equals("exit")){
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (socket != null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, DocumentException {
        new Client().Talk();
    }
}
