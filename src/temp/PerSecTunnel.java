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
import java.util.LinkedList;
import javax.swing.Timer;
import java.util.TimerTask;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author home
 */
public class PerSecTunnel {
    
   public static BlockingQueue<byte[]> q = new LinkedBlockingQueue<>();
   
   public static Queue<Short> timeQ = new LinkedList<>();
   

   
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        //Timer timer=new Timer(1000);
        
       
       
        
        try
        {
           
            Socket clientSocket = new Socket("localhost", 1234);
           
    int i=0;
    byte[] fileData = new byte[8196];
    
    
    ByteBuffer wrapped = ByteBuffer.wrap(fileData);
    short prevTimeStamp = -1;
       
    
    while(true)
    {
        
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        
        int toRead = dis.readInt();
        short timeStamp = dis.readShort();
        
       dis.read(fileData,0,toRead);
        
      
       timeQ.add(timeStamp);
       q.add(fileData);
       
        if(prevTimeStamp==-1)
        {
            
          sendsocket ss= new sendsocket(toRead);
          ss.run();
            
        }
        System.out.println(toRead+":"+i++);
        //dis.read(fileData, 0, toRead);
       
        
       
        
        if(timeStamp!=prevTimeStamp)
        {
            prevTimeStamp = timeStamp;
            
    }
            
        }
    }
    
    //dis.close();
        
    
    catch(Exception e)
    {
      e.printStackTrace();
    }
    }
    
    
    
    
}


class sendsocket extends PerSecTunnel implements Runnable 
{

    int length;
    short timeStamp;
    public sendsocket(int len) {
        
        length=len;
        
        
    }
    
    public void run()
    {
        
        try
        {
            
       
        MulticastSocket sendSocket = new MulticastSocket(9999);
        sendSocket.joinGroup(InetAddress.getByName("224.1.1.2"));
        sendSocket.setTimeToLive(0);
        sendSocket.setSendBufferSize(5000000);
        byte[] sendData=new byte[length];
        
        short prevTimeStamp=-1;
        long tStart = System.currentTimeMillis();
        while(!q.isEmpty()&&!timeQ.isEmpty())
        {
            
            timeStamp=timeQ.poll();
            sendData=q.poll();
            DatagramPacket sendPacket=new DatagramPacket(sendData, 0,InetAddress.getByName("224.1.1.2"),9999);
            sendPacket.setData(sendData,0,length);
            sendSocket.send(sendPacket);
            System.err.println("packet sent "+ sendData);
            
             if(timeStamp!=prevTimeStamp)
        {
            
            prevTimeStamp = timeStamp;
            long tEnd=System.currentTimeMillis();
            Thread.sleep(1000-(tEnd-tStart));
            tStart=System.currentTimeMillis();
        }
            
        }
        
        
        
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    
    
}


