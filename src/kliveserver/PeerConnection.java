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
import java.io.PrintStream;
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
                    Globals.log.message(userID+": request stream "+requestedFileName);
                    startVideoStream(requestedFileName);
                }
                else if(request.equalsIgnoreCase("getCurrentStreamingChunk"))
                {
                    final String requestedFileName=dis.readLine();
                    Globals.log.message(userID+": request current playing time of stream"+requestedFileName);
                    getCurrentStreamingChunk(requestedFileName);
                }
                else if(request.equalsIgnoreCase("close"))
                {
                    Globals.log.message(userID+": closed ");
                    dis.close();
                    sock.close();
                    break;
                }
                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Globals.log.message(userID+": disconnected ");
        Globals.GlobalData.peerController.removePeer(userID);
    }
    
    public void sendChannelDetails()
    {
        try {
            DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            PrintStream ps = new PrintStream(dout);
            for(int i=0;i<Globals.GlobalData.videoLibrary.videoList.size();i++)
            {
                VideoDetails video = Globals.GlobalData.videoLibrary.videoList.elementAt(i);
                if(video.streamingLive)
                {
                    ps.print("streaming\r\n");
                    ps.print(video.fileName+"\r\n");
                }
                else
                {
                    ps.print("Video\r\n");
                    ps.print(video.fileName+"\r\n");
                }
            }
            ps.flush();
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
        Globals.GlobalData.peerController.broadcastNewStreamLive(fileName);
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
                   Globals.GlobalData.peerController.broadcastStreamDead(fname);
               }
           },video.duration*1000);
        }
    }
    
    public void getCurrentStreamingChunk(String fileName)
    {
        VideoDetails video = Globals.GlobalData.videoLibrary.getVideoDetails(fileName);
        if(video==null)
        {
            Globals.log.error(userID+" getCurrentStreamingChunk requested invalid stream "+fileName);
            return;
        }
        try {
            DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            PrintStream ps = new PrintStream(dout);

            if(!video.streamingLive)
            {
                Globals.log.error(userID+" getCurrentStreamingChunk stream not live "+fileName);
                ps.print("NotStreaming\r\n");
                ps.print(fileName+"\r\n");
            }
            else if(video.currentStreamingChunk !=-1 )//vlc encoding is going on
            {
                Globals.log.message(userID+" getCurrentStreamingChunk  "+fileName+ "chunk "+video.currentStreamingChunk);
                ps.print("currentStreamingChunk\r\n");
                ps.print(fileName+"\r\n");
                ps.print(video.currentStreamingChunk+"\r\n");
            }
            else //ftream from rtp files
            {
                long time = System.currentTimeMillis();
                long currentChunk = (time - video.videoStreamStartTime)/(RTPFileGenerator.videoSegmentLength*1000);
                Globals.log.message(userID+" getCurrentStreamingChunk  "+fileName+ "chunk "+currentChunk);
                ps.print("currentStreamingChunk\r\n");
                ps.print(fileName+"\r\n");
                ps.print(currentChunk+"\r\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
