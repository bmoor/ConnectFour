/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import Engine.DataTransport;
import Gui.Lobby;
import Engine.Game;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles network player
 * setup TcpRead, send messages
 * 
 * @author bmoor
 */
public class Player extends Thread {
    
    private boolean running, playing, waitforclient;    
    private boolean ishost = false;
    private boolean isClient = false;
    private int port = 45678;
    private Thread thread;           

    private ServerSocket serverSocket;
    private Socket connectionSocket;
    private Socket clientSocket;

    private ObjectOutputStream objectWriter;    
    private TcpRead tcpRead;

    private Game game;        
    private Lobby lobby;
    
    
    
    public Player (){
        start();
    }
    
    @Override
    public void start() {
        thread = new Thread(this);
        thread.start();        
        running = true;
        playing = false;
        System.out.println("TCP Thread started");
    }
    
    
    /*
     * Setup a game as host
     */
    public boolean host(Lobby aLobby) {        
        waitforclient = true;
        ishost = true;
        lobby = aLobby;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        System.out.println("Host");
        playing = true;
        return true;
    }
    
    /*
     * Connect to a host
     */
    public synchronized boolean connect(InetAddress ipadr) {
        ishost = false;
        try {
            clientSocket = new Socket(ipadr, port);
            isClient = true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        System.out.println("Client, verbunden mit: "+clientSocket.getInetAddress());
        playing = true;
        return true;
    }
    
    
    /*
     * close open connection
     * server or client
     */
    public void disconnect() throws IOException {
        running = false;
        if(ishost == true) {
            if (serverSocket != null){
                serverSocket.close();
            }
        } else {
            if (clientSocket != null){
                clientSocket.close();
            }
        }
        ishost = false;
        playing = false;        
    }
    
     public void run() {
        try {
            while(running) {
                Thread.sleep(10);
                if(playing == true) {
                    if (isClient == true){                            
                            if (game != null){
                                tcpRead = new TcpRead(clientSocket, game);
                                isClient = false;
                                System.out.println("is Client");   
                                running = false;
                            }
                    }
                    
                    if(ishost == true) {
                        if(waitforclient == true) {
                 
                            connectionSocket = serverSocket.accept();
                            
                            //Startgame
                            lobby.StartGame(this);

                            tcpRead = new TcpRead(connectionSocket, game);
                                                                         
                            System.out.println("Client connection accepted from: "+connectionSocket.getInetAddress());
                            waitforclient = false;
                            running = false;
                        }
                    }
                }
            }
        } catch(InterruptedException | IOException ex) {
            System.out.println("Player run() Exception: "+ex.getMessage());
        }
    }
     
    /*
     * Close wait thread
     */ 
    public void close() {
        if(running == true) {
            running = false;
            System.out.println("TCP wait Thread closed");
        }
    }
    
    /*
     * Send dataTransport message over tcp
     */
    public void sendMessage(DataTransport message) {
        try {
            if (ishost){
                objectWriter = new ObjectOutputStream(connectionSocket.getOutputStream());
            } else {
                objectWriter = new ObjectOutputStream(clientSocket.getOutputStream());
            }
            objectWriter.writeObject(message);
            objectWriter.flush();            
        }
        catch (Exception e){
            System.out.println("Exception: sendMessage " + e.getMessage());
        }                
    }
    
    /*
     * Getter isHost
     */
    public boolean isHost(){
        return this.ishost;
    }
    
    public void registerGame(Game game){
        this.game = game;
    }
}
