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
import javax.swing.BoxLayout;
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
        lstGames.setSize(250, 250);
        pnlLobby.add(lstGames, BorderLayout.WEST);
        
        //ButtonPanel
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.Y_AXIS));
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
        pnlButtons.add(btnCreateGame, BorderLayout.EAST);
        
        //Button JoinGame
        btnJoinGame = new JButton("Join Game");
        btnJoinGame.setSize(50, 250);
        btnJoinGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = lstGames.getSelectedIndex();
                if(index<0)
                    return;
                try {
                    JoinGame(gameList.get(index).getAddress());
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        pnlButtons.add(btnJoinGame, BorderLayout.EAST);
        
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
        pnlButtons.add(btnJoinGameIP, BorderLayout.EAST);
        
        // Button Start Game with AI
        btnJoinGameAI = new JButton("Start Game with AI");
        btnJoinGameAI.setSize(50, 250);
        btnJoinGameAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game g= new Game();
                setVisible(false);
            }
        });
        pnlButtons.add(btnJoinGameAI, BorderLayout.EAST);
        
        // Button Load Game
        btnLoadGame = new JButton("Load Game");
        btnLoadGame.setSize(50, 250);
        btnLoadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               LoadGame();      
            }
        });
        pnlButtons.add(btnLoadGame, BorderLayout.EAST);
        
        // Button Refresh List
        btnRefresh = new JButton("Refresh List using Broadcast");
        btnRefresh.setSize(50, 250);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PerformBroadcast();
            }
        });
        pnlButtons.add(btnRefresh, BorderLayout.SOUTH);
        
        // Button Refresh List IP
        btnRefreshIP = new JButton("Refresh List using IP Test");
        btnRefreshIP.setSize(50, 250);
        btnRefreshIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PerformIPTest();
            }
        });
        pnlButtons.add(btnRefreshIP, BorderLayout.SOUTH);
        
                
        //Set visibility
        setVisible(true);
        
    }
    public void LoadGame()
    {
        JFileChooser chooser = new JFileChooser(); 
        chooser.setFileFilter(new FileNameExtensionFilter("Connect four game (*.cofo)", "cofo"));
        int r = chooser.showOpenDialog(this); 
        String s = "no File!"; 
        if (r == JFileChooser.APPROVE_OPTION) 
        {          
            s= chooser.getSelectedFile().getPath();
            System.out.println(s);
            Game g= new Game();
            g.restoreGame(s);
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
    
    public void StartGameServer(String gameName)
    {
        if(broadcastServer.running)
            broadcastServer.stopServer();
        
        broadcastServer.startServer(gameName);
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
        Player player = new Player();
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
     * create Player / start game
     */
    public void StartGame(Player player)
    {        
        Game game = new Game(player, this);
    }
    
    public void HostGame()
    {
        String gameName = JOptionPane.showInputDialog("New Game Name:");
        StartGameServer(gameName);
    }
    
    public void SetGUIMode(boolean enable)
    {
        waitMode = !enable;
        if(waitMode)
            btnCreateGame.setText("Cancel");
        else
            btnCreateGame.setText("Create Game");
        btnJoinGame.setEnabled(enable);
        btnRefresh.setEnabled(enable);
        btnJoinGameAI.setEnabled(enable);
        btnJoinGameIP.setEnabled(enable);
        btnRefreshIP.setEnabled(enable);
    }
}
