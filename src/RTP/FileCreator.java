/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package RTP;


import VideoStore.VideoDetails;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 *
 * @author home
 */
public class FileCreator {
    
    public static void Filegen(String srcFilePath, String destFilePath,int rtpStreamPort) 
    {
        int bytesRecieved = 0;
        int fileIndex=0;
        FileOutputStream fo=null;
        DataOutputStream dOut=null;
        long videoDuration = 0;
        File sourceVideo=new File(srcFilePath);
        VideoDetails video = Globals.GlobalData.videoLibrary.getVideoDetails(sourceVideo.getName());
        File destFolder=new File(destFilePath); 
        try
        {
            int counter=0;

            System.out.println(destFolder.getCanonicalPath());
            if(!destFolder.exists())
            {
              destFolder.mkdirs();
            }
            File temp = new File(destFolder+"/temp"+fileIndex);
            fo = new FileOutputStream(temp);
             dOut = new DataOutputStream(fo);

            DatagramSocket clientSocket = new DatagramSocket(rtpStreamPort);
            byte[] receiveData = new byte[10000];    
        
            DatagramPacket dp=new DatagramPacket(receiveData,receiveData.length,InetAddress.getByName("localhost"),rtpStreamPort);
            clientSocket.setReceiveBufferSize(5000000);
            clientSocket.setSoTimeout(10000);
            ByteBuffer wrapped = ByteBuffer.wrap(receiveData);
            Short timeStamp=0;
            short prevTimeStamp = -1;
            long time=System.currentTimeMillis()/1000;
            long bytesInSegment = 0;
            while(true)
            {  
                  
                clientSocket.receive(dp);
                bytesRecieved+=dp.getLength();
                bytesInSegment+=dp.getLength();
                dOut.writeInt(dp.getLength());  
                timeStamp=wrapped.getShort(4);
                dOut.writeShort(timeStamp);
                dOut.write(receiveData,0,dp.getLength());  
                
                if(timeStamp!=prevTimeStamp)
                {
                    long newtime=System.currentTimeMillis()/1000;
                    if(newtime-time>=RTPFileGenerator.videoSegmentLength)
                    {
                        videoDuration+= newtime-time;
                        dOut.close();
                        fo.close();
                        video.currentStreamingChunk = fileIndex;
                        File actualFile = new File(destFolder+"/chunk"+fileIndex);
                        temp.renameTo(actualFile);
                        fileIndex++;
                        counter=0;
                        fo = new FileOutputStream(temp);
                        dOut = new DataOutputStream(fo);
                        Globals.log.message("Converting "+sourceVideo.getName()+" with SegmentDataRate : "+
                                (bytesInSegment/1024/5)+"KBps"+" Duration:"+videoDuration+" timeGap "+(newtime-time));
                        time=newtime;
                        bytesInSegment = 0;
                    }
                    prevTimeStamp = timeStamp;
            
                }    
            }
        } 
        catch (Exception e) {
          e.printStackTrace();
        }
        
        Globals.log.message("RTP file generation complete for "+srcFilePath);
        if(fo!=null&&dOut!=null)
        {
            try{
                fo.close();
                dOut.close();
                File temp = new File(destFolder+"/temp"+fileIndex);
                File actualFile = new File(destFolder+"/chunk"+fileIndex);
                temp.renameTo(actualFile);
            }catch(Exception e){e.printStackTrace();}
        }
        
        try{
            
            int averageDataRate = bytesRecieved/((fileIndex+1)*RTPFileGenerator.videoSegmentLength);
            fo = new FileOutputStream(destFolder+"/rtp.log");
            dOut = new DataOutputStream(fo);
            PrintStream ps = new PrintStream(dOut);
            ps.print("AverageDataRate\r\n");
            ps.print(averageDataRate+"\r\n");
            ps.print("VideoDuration\r\n");
            ps.print(videoDuration+"\r\n");
            ps.print("NumberOfChunks\r\n");
            ps.print(fileIndex+"\r\n");
            ps.close();
            dOut.close();
            fo.close();
            video.RTPEncodingAvaliable = true;
            video.avgBitRate = averageDataRate;
            video.numberOfChunks = fileIndex;
            video.streamingLive = false;
            video.currentStreamingChunk = -1;
            video.duration = (int) videoDuration;
            Globals.GlobalData.peerController.broadcastStreamDead(sourceVideo.getName());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        
    }
}
