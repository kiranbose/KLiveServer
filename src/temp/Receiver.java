 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package temp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.swing.Timer;

/**
 *
 * @author home
 */
public class Receiver {
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        //Timer timer=new Timer(1000);
        
       
        MulticastSocket sendSocket = new MulticastSocket(9999);
        sendSocket.joinGroup(InetAddress.getByName("224.1.1.2"));
        sendSocket.setTimeToLive(0);
        
        
        try
        {
            Socket clientSocket = new Socket("localhost", 1234);

         
    int i=0;
    byte[] fileData = new byte[8196];
    String response;
    DatagramPacket sendPacket=new DatagramPacket(fileData, 0,InetAddress.getByName("224.1.1.2"),9999);
    sendSocket.setSendBufferSize(5000000);
    ByteBuffer wrapped = ByteBuffer.wrap(fileData);
    long tStart = System.currentTimeMillis();
    long tEnd;
    short prevTimeStamp = -1;
        while(true)
    {
        
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        
        int toRead = dis.readInt();
        short timeStamp = dis.readShort();
        
       dis.read(fileData,0,toRead);
        
        
        
        System.out.println(toRead+":"+i++);
        //dis.read(fileData, 0, toRead);
       sendPacket.setData(fileData,0,toRead);
        
        
        sendSocket.send(sendPacket);
        if(timeStamp!=prevTimeStamp)
        {
            prevTimeStamp = timeStamp;
            tEnd=System.currentTimeMillis();
            Thread.sleep(1000-(tEnd-tStart)-300);
            tStart=System.currentTimeMillis();
        }
    }
    
    //dis.close();
        
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    }
    void nextSecond()
    {
        
    }
    
}
