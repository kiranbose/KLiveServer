/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import java.io.DataInputStream;
import java.net.Socket;

/**
 *
 * @author home
 */
public class PeerConnection extends Thread{
    Socket sock;
    String userID;

    public PeerConnection(Socket sock, String userID) {
        this.sock = sock;
        this.userID = userID;
    }

    @Override
    public void run() {
        super.run(); 
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            while(true)
            {
                String request = dis.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.GlobalData.peerController.removePeer(userID);
    }
    
    
}
