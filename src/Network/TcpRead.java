/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import Engine.Game;
import Engine.DataTransport;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Listen for incoming Datatransport objects
 * on accepted ip
 * 
 * @author bmoor
 */
public class TcpRead extends Thread {
        Thread thread;    
    private ObjectInputStream objectReader;
    private Socket socket;
    private Object tmp;
    private Game game;
    private boolean running = true;

    public TcpRead(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;
        thread = new Thread(this);
        thread.start();
    }
            
    @Override
    public void run(){                
        try {
            while(running){
                objectReader = new ObjectInputStream(socket.getInputStream());
                tmp = objectReader.readObject();
                if (tmp != null && tmp instanceof DataTransport){
                     game.TcpTurnPreformed((DataTransport)tmp);                     
                }                
            }
        } catch(Exception e) {
            System.out.println("TcpCommunication Exception ReadObject: "+e.getMessage());
        }
    }        
}
