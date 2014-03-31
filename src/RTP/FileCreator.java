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
        int segmentLength=5;
        try
        {
            int counter=0;

            File destFolder=new File(destFilePath); 
            System.out.println(destFolder.getCanonicalPath());
            if(!destFolder.exists())
            {
              destFolder.mkdirs();
            }
            fo = new FileOutputStream(destFolder+"/chunk"+fileIndex);
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
                    videoDuration+= newtime-time;
                    if(newtime-time>=segmentLength)
                    {
                        dOut.close();
                        fo.close();
                        fileIndex++;
                        counter=0;
                        fo = new FileOutputStream(destFolder+"/chunk"+fileIndex);
                        dOut = new DataOutputStream(fo);
                        File fname=new File(srcFilePath);
                        Globals.log.message("Converting "+fname.getName()+" with SegmentDataRate : "+
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
            }catch(Exception e){e.printStackTrace();}
        }
        
        try{
            
            int averageDataRate = bytesRecieved/((fileIndex+1)*segmentLength);
            File destFolder=new File(destFilePath); 
            fo = new FileOutputStream(destFolder+"/rtp.log");
            dOut = new DataOutputStream(fo);
            dOut.writeBytes("AverageDataRate\r\n");
            dOut.writeBytes(averageDataRate+"\r\n");
            dOut.writeBytes("VideoDuration\r\n");
            dOut.writeBytes(videoDuration+"\r\n");
            dOut.writeBytes("NumberOfChunks\r\n");
            dOut.writeBytes(fileIndex+"\r\n");
            dOut.close();
            fo.close();
            File fname=new File(srcFilePath);
            VideoDetails video = Globals.GlobalData.videoLibrary.getVideoDetails(fname.getName());
            video.RTPEncodingAvaliable = true;
            video.avgBitRate = averageDataRate;
            video.numberOfChunks = fileIndex;
            video.streamingLive = false;
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        
    }
}
