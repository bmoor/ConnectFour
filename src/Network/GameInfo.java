/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import java.net.InetAddress;
/**
 *
 * @author HP
 */
public class GameInfo
{
    private InetAddress address;
    private String gameName;

    public GameInfo(InetAddress address, String gameName) {
        this.address = address;
        this.gameName = gameName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getGameName() {
        return gameName;
    }

    @Override
    public String toString() {
        return address.toString() + " " +gameName;
    }

    @Override
    public boolean equals(Object obj) {
        return address.equals(obj);
    }
}
