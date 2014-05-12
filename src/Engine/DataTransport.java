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
    
    public DataTransport(int x)
    {
        this.x=x;
    }
    
    public int getX()
    {
        return x;
    }
    
    @Override
    public String toString(){
        return "DataTransport: "+x;
    }
    
}
