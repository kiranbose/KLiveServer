package temp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
/**
 *
 * @author home
 */
public class FileWriter {
    public static void main(String args[]) throws Exception
{

    try
      {
          FileOutputStream fo = new FileOutputStream("OUTFILE.txt");
          DataOutputStream dOut = new DataOutputStream(fo);
          PrintStream ps = new PrintStream(fo);  
           MulticastSocket clientSocket = new MulticastSocket(1234);
           clientSocket.joinGroup(InetAddress.getByName("224.1.1.1"));
          byte[] receiveData = new byte[10000];    
        
DatagramPacket dp=new DatagramPacket(receiveData,receiveData.length);
clientSocket.setReceiveBufferSize(5000000);
ByteBuffer wrapped = ByteBuffer.wrap(receiveData);
          while(true)
         {  
               
             
             clientSocket.receive(dp);

                
                System.out.println( dp.getLength());
                dOut.writeInt(dp.getLength());  
                dOut.writeShort(wrapped.getShort(4));
                ps.write(receiveData,0,dp.getLength());
                //ps.println();
                

         }
       /*         ps.close();
                fo.close();
          File file = new File("OUTFILE.txt");
          FileInputStream fis = new FileInputStream(file);
          byte[] fsize = new byte[(int) file.length()];
          int size = fis.read(fsize);
          System.out.println("Received Size = " + size);
          ps.close();
                fo.close();*/

      } 
        catch (Exception e) {
          System.err.println(e);
        }



}
    
}
