/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package RTP;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author home
 */
public class FileCreator {
    
    public static void Filegen(String srcFilePath, String destFilePath) throws FileNotFoundException, IOException
    {
        try
        {
            int segmentLength=5;
            int counter=0;
            int fileIndex=0;

            File destFolder=new File(destFilePath); 
            System.out.println(destFolder.getCanonicalPath());
            if(!destFolder.exists())
            {
              destFolder.mkdir();
            }
            FileOutputStream fo = new FileOutputStream(destFolder+"/chunk"+fileIndex);
            DataOutputStream dOut = new DataOutputStream(fo);

            DatagramSocket clientSocket = new DatagramSocket(1234);
            byte[] receiveData = new byte[10000];    
        
            DatagramPacket dp=new DatagramPacket(receiveData,receiveData.length,InetAddress.getByName("localhost"),1234);
            clientSocket.setReceiveBufferSize(5000000);
            //clientSocket.setSoTimeout(5000);
            ByteBuffer wrapped = ByteBuffer.wrap(receiveData);
            Short timeStamp=0;
            short prevTimeStamp = -1;
            while(true)
            {  
                  
                clientSocket.receive(dp);
                System.out.println( dp.getLength());
                dOut.writeInt(dp.getLength());  
                timeStamp=wrapped.getShort(4);
                dOut.writeShort(timeStamp);
                dOut.write(receiveData,0,dp.getLength());  
                
                if(timeStamp!=prevTimeStamp)
                {
                    counter++;
                if(counter==segmentLength)
                {
                    dOut.close();
                    fo.close();
                    fileIndex++;
                    counter=0;
                    fo = new FileOutputStream(destFolder+"/chunk"+fileIndex);
                    dOut = new DataOutputStream(fo);
                }
                prevTimeStamp = timeStamp;
            
            }    
         }
      } 
      catch (Exception e) {
        e.printStackTrace();
      }
        //create log file
    }
}
