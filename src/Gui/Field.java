/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import java.awt.Color;
//import java.awt.Graphics;
import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * @author Mario
 */
public class Field
{
    private JFrame frame;
    private JPanel panel;
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuHelp;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemOpen;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemInfo;
    private JDialog dialogOpen;
    private JDialog dialogInfo;
    private JLabel labelTurn;

    private int[][] board;
    private final int BOARD_WIDTH = 600;
    private final int BOARD_HEIGHT = 500;
    private int rows = 6;
    private int columns = 7;
    public final int BLANK = 0;
    public final int RED = 1;
    public final int YELLOW = 2;

    private boolean isMyTurn;

    
    public Field()
    {

        fillBoard();
        createGUI();
        createBoard(rows, columns);
        setTextTurn();

    }

    /**
     * public Field(....) {
     *
     * }
     */
    private void fillBoard()
    {
        board = new int[rows][columns];
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                board[r][c] = BLANK;
            }
        }
    }

    private void createGUI()
    {
        //Create frame
        frame = new JFrame("Connect four");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);

        //Create menubar
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        //Create menus in the menubar
        menuFile = new JMenu("File");
        menuHelp = new JMenu("Help");
        menuBar.add(menuFile);
        menuBar.add(menuHelp);

        //Create items in menu "File"
        //Open
        menuItemOpen = new JMenuItem("Open");
        menuFile.add(menuItemOpen);
        menuItemOpen.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createOpenDialog();
            }
        });

        //Save
        menuItemSave = new JMenuItem("Save");
        menuFile.add(menuItemSave);

        //Separate exit from the other items
        menuFile.addSeparator();

        //Exit
        menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        menuFile.add(menuItemExit);

        //Create items in menu "Help"
        //Info
        menuItemInfo = new JMenuItem("Info");
        menuHelp.add(menuItemInfo);
        menuItemInfo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createInfoDialog();
            }
        });

        //Create the label, that indicates who has to play
        labelTurn = new JLabel();
        labelTurn.setBounds(680, 50, 300, 35);
        Font schrift = new Font("Serif", Font.BOLD + Font.ITALIC, 25);
        labelTurn.setFont(schrift);
        frame.add(labelTurn);

        //Set visibility
        frame.setVisible(true);

    }

    private void setTextTurn()
    {
        if (isMyTurn)
        {
            labelTurn.setForeground(Color.GREEN);
            labelTurn.setText("It's your turn!");
        }
        else
        {
            labelTurn.setForeground(Color.RED);
            labelTurn.setText("Waiting for other player...");
        }

    }

    //Create dialog when "Open" was pressed in "File"
    private void createOpenDialog()
    {
        dialogOpen = new JDialog();
        dialogOpen.setTitle("Open file");
        dialogOpen.setSize(200, 200);
        dialogOpen.setLocationRelativeTo(frame);
        //Set visibility
        dialogOpen.setVisible(true);
    }

    //Create dialog when "Info" was pressed in "Help"
    private void createInfoDialog()
    {
        dialogInfo = new JDialog();
        dialogInfo.setTitle("Information");
        dialogInfo.setSize(200, 200);
        dialogInfo.setLocationRelativeTo(frame);
        //Set visibility
        dialogInfo.setVisible(true);

    }

    private void createBoard(int rows, int columns)
    {
        //create a blue panel
        panel = new JPanel();
        panel.setBounds(50, 50, BOARD_WIDTH, BOARD_HEIGHT);
        panel.setBackground(Color.BLUE);
        panel.setLayout(new GridLayout(rows, columns, 0, 0));

        updateBoard();

        frame.add(panel);

    }

    //Check each entry in "board", create a new circle
    //in the specific color and add it to the board
    private void updateBoard()
    {
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                if (board[r][c] == BLANK)
                {
                    panel.add(new Stone(Color.WHITE));
                }
                else if (board[r][c] == RED)
                {
                    panel.add(new Stone(Color.RED));
                }
                else if (board[r][c] == YELLOW)
                {
                    panel.add(new Stone(Color.YELLOW));
                }

            }
        }
    }

    private class Stone extends JPanel
    {

        private Color color;

        public Stone(Color c)
        {
            color = c;
            this.setBackground(Color.BLUE);
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            g.setColor(color);
            g.fillOval((int) ((BOARD_WIDTH / columns) * 0.1),
                    (int) ((BOARD_HEIGHT / rows) * 0.1),
                    (int) ((BOARD_WIDTH / columns) * 0.75),
                    (int) ((BOARD_WIDTH / columns) * 0.75));
        }
    }
}
