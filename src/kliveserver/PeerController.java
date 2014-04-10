/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import Tracker.PeerDetails;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
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
        Globals.log.message("Broadcasting new video available "+videoFileName);
        String messages[] = {"NewVideoAvailable",videoFileName};
        broadcastMessages(messages);
    }
    
    public void broadcastNewStreamLive(String videoFileName)
    {
        Globals.log.message("Broadcasting new video stream live "+videoFileName);
        String messages[] = {"NewStreamLive",videoFileName};
        broadcastMessages(messages);
    }
    
    public void broadcastStreamDead(String videoFileName)
    {
        Globals.log.message("Broadcasting video stream dead "+videoFileName);
        String messages[] = {"StreamDead",videoFileName};
        broadcastMessages(messages);
    }
    
    public void broadcastPeerList()
    {
        Globals.log.message("Broadcasting connected peer list ");
        ArrayList<String> msg = new ArrayList<String>();
        msg.add("clearPeerList");
        if(Globals.GlobalData.peerTracker.peerList.size()==0)
            Globals.log.message("No connected peers to Broadcast");
        for(int i=0;i<Globals.GlobalData.peerTracker.peerList.size();i++)
        {
            PeerDetails detail = Globals.GlobalData.peerTracker.peerList.get(i);
            msg.add("peerDetail");
            msg.add(detail.userName);
            msg.add(detail.ip);
            msg.add(java.lang.Integer.toString(detail.port));
            Globals.log.message("peerDetail "+detail.userName+":"+detail.ip+":"+detail.port);
        }
        String array[] = new String[msg.size()];
        for(int i=0;i<msg.size();i++)
            array[i]= msg.get(i);
        broadcastMessages(array);
    }
}
