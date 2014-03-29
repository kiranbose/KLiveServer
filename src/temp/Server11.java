/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package temp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.*;
import javax.swing.Timer;

/**
 *
 * @author home
 */
public class Server11 {
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        //Timer timer=new Timer(1000);
        
        File file = new File("OUTFILE.txt");
       
        ServerSocket welcomeSocket = new ServerSocket(1234);
       
     
        try
        {
           
    DataInputStream dis = new DataInputStream(new FileInputStream(file));
    int i=0;
    byte[] fileData = new byte[8196];
    //DatagramPacket sendPacket=new DatagramPacket(fileData, 0,InetAddress.getByName("localhost"),9999);
    short prevTimeStamp = -1;
    Socket clientsocket= welcomeSocket.accept();
  
    while(true)
    {
        
        DataOutputStream outToServer = new DataOutputStream(clientsocket.getOutputStream());
        
        
        int toRead = dis.readInt();
        short timeStamp = dis.readShort();
        System.out.println(toRead+":"+i++);
        dis.read(fileData, 0, toRead);
       
        outToServer.writeInt(toRead);
        outToServer.writeShort(timeStamp);
               
        outToServer.write(fileData,0,toRead);
        
       
       
    }
    
    //dis.close();
        
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    }
  
    
}
