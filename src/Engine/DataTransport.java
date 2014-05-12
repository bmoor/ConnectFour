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
    private int xSize;

    public DataTransport(final int x)
    {
        this.x = x;
    }

    public DataTransport(final String text)
    {
        this.chat = text;
    }

    public int getX()
    {
        return x;
    }

    public String getChat()
    {
        return chat;
    }

    public void setChat(final String chat)
    {
        this.chat = chat;
    }

    public int getxSize()
    {
        return xSize;
    }

    public void setxSize(final int xSize)
    {
        this.xSize = xSize;
    }

    public int getySize()
    {
        return ySize;
    }

    public void setySize(final int ySize)
    {
        this.ySize = ySize;
    }

    private int ySize;

    @Override
    public String toString()
    {
        return "DataTransport: " + x;
    }

}
