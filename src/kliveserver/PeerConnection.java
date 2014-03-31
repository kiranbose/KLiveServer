/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import RTP.RTPFileGenerator;
import VideoStore.VideoDetails;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

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
                else if(request.equalsIgnoreCase("stream"))
                {
                     
                     final String requestedFileName=dis.readLine();
                     startVideoStream(requestedFileName);
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
    
    public void startVideoStream(String fileName)
    {
        final String fname=fileName;
        VideoDetails video = Globals.GlobalData.videoLibrary.getVideoDetails(fileName);
        if(video==null)
        {
            Globals.log.error(userID+" requested invalid stream "+fileName);
            return;
        }

        Globals.log.message(userID+": Streaming requested for "+video.fileName);
        if(!video.RTPEncodingAvaliable && !video.streamingLive)
        {
            video.streamingLive = true;
            video.videoStreamStartTime = System.currentTimeMillis();
            Globals.log.message(userID+": Initiating transcoding "+video.fileName);
            RTPFileGenerator.createRtpFileSegments(Globals.GlobalData.VideoStorePath+"/"+fileName, Globals.GlobalData.RTPVideoStorePath+"/"+fileName);
        }
        else if(!video.streamingLive && video.RTPEncodingAvaliable)
        {
           video.streamingLive = true;
           video.videoStreamStartTime = System.currentTimeMillis();
           Globals.log.message(userID+":  streaming "+video.fileName+" started at time "+video.videoStreamStartTime);
           //start timer to reset streaming flags in library after streaming
           Globals.log.message(userID+": Initiating timer to stop stream "+video.fileName+" after "
                               +video.duration+"Sec");
           Timer timer = new Timer();
           timer.schedule(new TimerTask() {

               @Override
               public void run() {
                   VideoDetails video = Globals.GlobalData.videoLibrary.getVideoDetails(fname);
                   if(video.streamingLive == false)
                       Globals.log.error(userID+": Timer trying to stop non existant stream "+fname);
                   Globals.log.message(userID+": stream stopped by timer "+fname);
                   video.streamingLive = false;
                   video.videoStreamStartTime = 0;
               }
           },video.duration*1000);
        }
    }
}
