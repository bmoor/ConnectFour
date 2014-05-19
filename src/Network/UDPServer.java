package Network;


import Gui.Lobby;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * as host: waits for other players
 * as client: Search for available Games, in hole netowrk or by broadcast
 * 
 */
public class UDPServer implements Runnable {
    public static int udpServerPortNb = 4444;
    public static int responseWaitTime = 3000;
    public static String initCode = "Connectfour";
    public boolean running;
    public String gameName;
    public Lobby lobbyPtr;
    private Timer responseListenTimer;
    private DatagramSocket socket;
    private Thread thread;
    
    
    /*
     * Constructor
     * set sockets
     */
    public UDPServer(Lobby lobbyPtr, boolean broadcastServer) {
        this.lobbyPtr = lobbyPtr;
        try {
            if(broadcastServer) {
                socket = new DatagramSocket(udpServerPortNb);
            }
            else {
                socket = new DatagramSocket();
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
            
    }
    
    /*
     * start to listen on broadcast
     */
    public void startServer(String gameName) {
        if(running) {
            return;
        }
        
        running = true;
        this.gameName = gameName;
        start(); 
    }
    
    /*
     * sends a broadcast and start listen for response
     */
    public void startServerBroadcast() {        
        if(running) {
            return;
        }        
        running = true;
        byte[] buffer = initCode.getBytes();
        try {
            start();
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, udpServerPortNb);
            socket.send(packet);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        responseListenTimer = new Timer(responseWaitTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();                
            }
        });
        responseListenTimer.setRepeats(false);
        responseListenTimer.start();
    }
    
    /*
     * search for hosts in network
     */
    public void startServerIPTest() {        
        if(running) 
            return;
        
        running = true;
        byte[] buffer = initCode.getBytes();
        
        try {
            start();
            
            InetAddress   in  = InetAddress.getLocalHost();  
            InetAddress[] all = InetAddress.getAllByName(in.getHostName());  
            //get ip
            InetAddress ret = (InetAddress)JOptionPane.showInputDialog(null, "Select own IP", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, all, initCode);
            byte[] ownIPBuf = ret.getAddress();
            int ownIP = 0;
            for (byte b: ownIPBuf)  {  
                ownIP = ownIP << 8 | (b & 0xFF);  
            }

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ret);
            //get correct interface
            InterfaceAddress netI = (InterfaceAddress)JOptionPane.showInputDialog(null, "Select Interface", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, networkInterface.getInterfaceAddresses().toArray(), null);
            int subnetPrefix = netI.getNetworkPrefixLength();
            int subnetMask = 0;
            int networkAdr;            
            if(subnetPrefix < 0) {
                String str = JOptionPane.showInputDialog("Enter Network Mask Prefix"); //For Hslu network it should be 19, iguess?
                Integer ii = new Integer(str);
                subnetPrefix = ii.intValue();
            }
            //calculate neighbour hosts
            int nbHosts = (int)Math.pow(2, (32-subnetPrefix)) -2;
            for(int i = 0; i < 32; i++) {
                subnetMask = subnetMask << 1;
                if(i<subnetPrefix) {
                    subnetMask += 1;
                }
            }
            networkAdr = ownIP & subnetMask;
            //ask every ip in range for available games
            for(int i = 1; i <= nbHosts; i++) {
                int newAdr = networkAdr+i;
                byte[] adrBuf = BigInteger.valueOf(newAdr).toByteArray();
                InetAddress adr = InetAddress.getByAddress(adrBuf);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, adr, udpServerPortNb);
                socket.send(packet);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        responseListenTimer = new Timer(responseWaitTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
                
            }
        });
        responseListenTimer.setRepeats(false);
        responseListenTimer.start();
    }
    
    public void stopServer() {
        running = false;
    }
    
    public void start() 
    { 
        if (thread == null) { 
            thread = new Thread(this); 
            thread.start(); 
        } 
    }
    /*
     * listen for incoming datagram packets
     */
    @Override
    public void run() {
        try {
            while (running) {
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                byte[] data = packet.getData();
                String infoString = new String(data);
                if(infoString.startsWith(initCode)) {
                    // return Ready
                    String dataStr = gameName;
                    data = dataStr.getBytes();
                    packet = new DatagramPacket(data, data.length, address, port);
                    socket.send(packet);
                    System.out.println(address + " asked for available games");
                }
                else {
               
                    lobbyPtr.HandleUDPResponse(address, infoString);
                    System.out.println(infoString + " found");
                }
            }
            socket.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
