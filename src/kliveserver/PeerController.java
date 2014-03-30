/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * maintains a list of connections to all live pears to control them
 * @author Kiran
 */
public class PeerController {
    Vector<PeerConnection> ConnectedPeers;

    public PeerController() {
        ConnectedPeers = new Vector<>(20);
    }
    
    public void addPeer(Socket peerSock,String userID)
    {
        PeerConnection peerConnection = new PeerConnection(peerSock,userID);
        ConnectedPeers.add(peerConnection);
        peerConnection.start();
    }
    
    public void removePeer(String userID)
    {
        try{
            for(int i=0;i<ConnectedPeers.size();i++)
            {
                if(userID.equalsIgnoreCase(ConnectedPeers.get(i).userID))
                {
                    PeerConnection removed = ConnectedPeers.remove(i);
                    if(removed.sock.isConnected())
                        removed.sock.close();
                    return;
                }
            }
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public void broadcastMessages(String[] message)
    {
        try{
            for(int i=0;i<ConnectedPeers.size();i++)
            {
                PeerConnection peer = ConnectedPeers.elementAt(i);
                if(peer.sock.isConnected())
                {
                    DataOutputStream dout = new DataOutputStream(peer.getOutputStream());
                    for(int j=0;j<message.length;j++)
                        dout.writeBytes(message[j]+"\r\n");
                    dout.flush();
                }
            }
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public void broadcastNewVideoAvaliable(String videoFileName)
    {
        String messages[] = {"NewVideoAvaliable",videoFileName};
        broadcastMessages(messages);
    }
    
    public void broadcastNewStreamLive(String videoFileName)
    {
        String messages[] = {"NewStreamLive",videoFileName};
        broadcastMessages(messages);
    }
    
    public void broadcastStreamDead(String videoFileName)
    {
        String messages[] = {"StreamDead",videoFileName};
        broadcastMessages(messages);
    }
}
