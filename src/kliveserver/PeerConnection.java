/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import VideoStore.VideoDetails;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    public OutputStream getOutputStream() throws IOException
    {
        return sock.getOutputStream();
    }
    
    @Override
    public void run() {
        super.run(); 
        //send stream details to peer
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            while(true)
            {
                String request = dis.readLine();
                if(request.equalsIgnoreCase("getChannels"))
                {
                    Globals.log.message(userID+": getChannels ");
                    sendChannelDetails();
                }
                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.GlobalData.peerController.removePeer(userID);
    }
    
    public void sendChannelDetails()
    {
        try {
            DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            for(int i=0;i<Globals.GlobalData.videoLibrary.videoList.size();i++)
            {
                VideoDetails video = Globals.GlobalData.videoLibrary.videoList.elementAt(i);
                if(video.streamingLive)
                {
                    dout.writeBytes("streaming\r\n");
                    dout.writeBytes(video.fileName+"\r\n");
                }
                else
                {
                    dout.writeBytes("Video\r\n");
                    dout.writeBytes(video.fileName+"\r\n");
                }
            }
            dout.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
