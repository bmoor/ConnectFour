/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

/**
 *
 * @author Yves
 */
public class DataTransport implements java.io.Serializable
{

    private int x;
    private String chat;
    private int xSize = 0;
    private int ySize = 0;
    
    public DataTransport(int x)
    {
        this.x = x;
    }
    
    public DataTransport(String text)
    {
        chat = text;
    }

    public int getX()
    {
        return x;
    }
    
    public String getChat()
    {
        return chat;
    }

    public void setChat(String chat)
    {
        this.chat = chat;
    }

    public int getxSize()
    {
        return xSize;
    }

    public void setxSize(int xSize)
    {
        this.xSize = xSize;
    }

    public int getySize()
    {
        return ySize;
    }

    public void setySize(int ySize)
    {
        this.ySize = ySize;
    }    

    @Override
    public String toString()
    {
        return "DataTransport: " + x;
    }

}
