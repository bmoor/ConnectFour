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
 *
 * @author HP
 */
public class UDPServer implements Runnable {
    public static int udpServerPortNb = 4444;
    public static int responseWaitTime = 3000;
    public static String initCode = "Battleship";
    public boolean running;
    public String gameName;
    public Lobby lobbyPtr;
    private Timer responseListenTimer;
    private DatagramSocket socket;
    private Thread thread;
    
    public UDPServer(Lobby lobbyPtr, boolean broadcastServer) {
        this.lobbyPtr = lobbyPtr;
        try {
            if(broadcastServer)
                socket = new DatagramSocket(udpServerPortNb);
            else
                socket = new DatagramSocket();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
            
    }
    
    public void startServer(String gameName) {
        // start as broadcast listener
        if(running)
            return;
        
        running = true;
        this.gameName = gameName;
        start(); 
    }
    
    public void startServerBroadcast() {
        // send broadcast and start as response listener
        if(running) 
            return;
        
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
    
    public void startServerIPTest() {
        // send broadcast and start as response listener
        if(running) 
            return;
        
        running = true;
        byte[] buffer = initCode.getBytes();
        
        try {
            start();
            
            InetAddress   in  = InetAddress.getLocalHost();  
            InetAddress[] all = InetAddress.getAllByName(in.getHostName());  
            InetAddress ret = (InetAddress)JOptionPane.showInputDialog(null, "Select own IP", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, all, initCode);
            byte[] ownIPBuf = ret.getAddress();
            int ownIP = 0;
            for (byte b: ownIPBuf)  {  
                ownIP = ownIP << 8 | (b & 0xFF);  
            }

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ret);
            InterfaceAddress netI = (InterfaceAddress)JOptionPane.showInputDialog(null, "Select Interface", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, networkInterface.getInterfaceAddresses().toArray(), null);
            int subnetPrefix = netI.getNetworkPrefixLength();
            int subnetMask = 0;
            int networkAdr;
            if(subnetPrefix < 0) {
                String str = JOptionPane.showInputDialog("Enter Network Mask Prefix");
                Integer ii = new Integer(str);
                subnetPrefix = ii.intValue();
            }
            int nbHosts = (int)Math.pow(2, (32-subnetPrefix)) -2;
            for(int i = 0; i < 32; i++) {
                subnetMask = subnetMask << 1;
                if(i<subnetPrefix)
                    subnetMask += 1;
            }
            networkAdr = ownIP & subnetMask;
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
