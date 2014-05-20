/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.Game;
import Network.GameInfo;
import Network.Player;
import Network.UDPServer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * 
 */
public class Lobby extends JFrame
{
    private JPanel pnlLobby;
    private JPanel pnlButtons;
    private List lstGames;
    private JButton btnCreateGame;
    private JButton btnJoinGame;
    private JButton btnRefresh;
    private JButton btnRefreshIP;
    private JButton btnJoinGameAI;
    private JButton btnLoadGame;
    private JButton btnJoinGameIP;
    private ArrayList<GameInfo> gameList;
    private UDPServer broadcastServer;
    private UDPServer responseServer;
    private boolean waitMode;
    private Player player;
    private Game game;
    
    
    public Lobby()
    {
        //Window Settings
        super("Lobby - ConnectFour");
        
        //Initialize Components
        InitializeComponents();
        
        // UDP servers
        broadcastServer = new UDPServer(this, true);
        responseServer = new UDPServer(this, false);
        gameList = new ArrayList<>();
    }

    private void InitializeComponents() 
    {
        //Window Settings
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //Main Panel
        pnlLobby = new JPanel(new GridLayout(1, 1));
        pnlLobby.setBackground(Color.WHITE);  
        this.add(pnlLobby);
        
        //List Games
        lstGames = new List();
        lstGames.setSize(200, 200);
        pnlLobby.add(lstGames, BorderLayout.WEST);
        
        //ButtonPanel
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new GridLayout(7,1,0,3));  
        pnlLobby.add(pnlButtons, BorderLayout.EAST);
        
        //Button CreateGame / Cancel
        btnCreateGame = new JButton("Create Game");                
        btnCreateGame.setSize(50, 250);
        btnCreateGame.addActionListener(new ActionListener(){ 
            @Override
            public void actionPerformed(ActionEvent e){                
                if(waitMode) {
                    try {                        
                        broadcastServer.stopServer();                                                
                        destroyOldGame();
                    } catch(Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    SetGUIMode(true);
                }
                else {
                    //Host a new game
                    HostGame();
                }
            }
        });
        pnlButtons.add(btnCreateGame);
        
        //Button JoinGame
        btnJoinGame = new JButton("Join Game");        
        btnJoinGame.setSize(50, 250);
        btnJoinGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = lstGames.getSelectedIndex();
                if(index<0) {
                    return;
                }
                try {
                    JoinGame(gameList.get(index).getAddress());
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        pnlButtons.add(btnJoinGame);
        
        // Button Join Game Through IP
        btnJoinGameIP = new JButton("Join Game Through IP");
        btnJoinGameIP.setSize(50, 250);
        btnJoinGameIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = JOptionPane.showInputDialog("Enter remote IP:");
                if (ip != null){                
                    try {
                        InetAddress adr = InetAddress.getByName(ip);
                        JoinGame(adr);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
        pnlButtons.add(btnJoinGameIP);
        
        // Button Start Game with AI
        btnJoinGameAI = new JButton("Start Game with AI");
        btnJoinGameAI.setSize(50, 250);
        btnJoinGameAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                                
                startAi();                
            }
        });
        pnlButtons.add(btnJoinGameAI);
        
        // Button Load Game
        btnLoadGame = new JButton("Load Game");
        btnLoadGame.setSize(50, 250);
        btnLoadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               LoadGame();      
            }
        });
        pnlButtons.add(btnLoadGame);
        
        // Button Refresh List
        btnRefresh = new JButton("List games by broadcast");
        btnRefresh.setSize(50, 250);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PerformBroadcast();
            }
        });
        pnlButtons.add(btnRefresh);
        
        // Button Refresh List IP
        btnRefreshIP = new JButton("List games in network");
        btnRefreshIP.setSize(50, 250);
        btnRefreshIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PerformIPTest();
            }
        });
        pnlButtons.add(btnRefreshIP);
        
                
        //Set visibility
        setVisible(true);
        
    }
    public void LoadGame()
    {
        JFileChooser chooser = new JFileChooser(); 
        chooser.setFileFilter(new FileNameExtensionFilter("Connect four game (*.c4)", "c4"));
        int r = chooser.showOpenDialog(this); 
        String s = "no File!"; 
        if (r == JFileChooser.APPROVE_OPTION) 
        {          
            s= chooser.getSelectedFile().getPath();
            System.out.println(s);
            game = new Game(this);
            game.restoreGame(s);
            setVisible(false);
        } 
    }
    
    public void HandleUDPResponse(InetAddress address, String gameName)
    {
        gameList.add(new GameInfo(address, gameName));
        UpdateGameList();
    }
    
    public void UpdateGameList() {
        lstGames.removeAll();
        for(GameInfo i:gameList) {
           lstGames.add(i.toString());
        }
        
    }
       
    public void PerformBroadcast()
    {
        if(!responseServer.running) {
            gameList.clear();
            UpdateGameList();
        }
        responseServer.startServerBroadcast(); 
    }
    
    public void PerformIPTest()
    {
        if(!responseServer.running) {
            gameList.clear();
            UpdateGameList();
        }
        responseServer.startServerIPTest();
    }
    
    public void JoinGame(InetAddress adr)
    {
        player = new Player();
        if(player.connect(adr)){
            StartGame(player);
        } else {
            JOptionPane.showMessageDialog(null, "Could not connect to: "+adr, "IP conflict", WIDTH);
            try{
                player.disconnect();
            }
            catch (Exception e) {
                System.out.println("Exception Lobby player.disconnect(): "+e.getMessage());
            }
        }
    }
    
    /*
     * create game
     * 
     */
    public void StartGame(Player player)
    {        
        game = new Game(player, this);
        player.registerGame(game);
        setVisible(false);
    }
    
    public void HostGame()
    {
        String gameName = JOptionPane.showInputDialog("New Game Name:");
        StartGameServer(gameName);
        player = new Player();
        player.host(this);
        SetGUIMode(false);
    }
    
    public void StartGameServer(String gameName)
    {
        if(broadcastServer.running) {
            broadcastServer.stopServer();
        }
        
        broadcastServer.startServer(gameName);
    }
    
    public void SetGUIMode(boolean enable)
    {
        waitMode = !enable;
        if(waitMode) {
            btnCreateGame.setText("Cancel");
        }
        else {
            btnCreateGame.setText("Create Game");
        }
        btnJoinGame.setEnabled(enable);
        btnRefresh.setEnabled(enable);
        btnJoinGameAI.setEnabled(enable);
        btnJoinGameIP.setEnabled(enable);
        btnRefreshIP.setEnabled(enable);
        btnLoadGame.setEnabled(enable);
    }
    
    public void destroyOldGame(){
        try {
            player.disconnect();
        } catch(Exception e){
            System.out.println("Exception destroyOldGame: " +e);
        }
        game = null;
        player = null;
        if (waitMode){
            this.SetGUIMode(true);            
        }
        setVisible(true);
        System.gc();
    }
    
    public void startAi(){
        game = new Game(this);                
        setVisible(false);
    }
}
