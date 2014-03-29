/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package temp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static temp.timerSec.packet;

/**
 *
 * @author home
 */
public class StreamTimer {
    public static void main(String args[])
    {
        try {
            RTPProcessor rTPProcessor = new RTPProcessor("OUTFILE.txt");
            MulticastSocket sendSocket = new MulticastSocket(9999);
            sendSocket.setTimeToLive(0);
            sendSocket.setSendBufferSize(5000000);
            sendSocket.joinGroup(InetAddress.getByName("224.1.1.2"));
            timerSec.sendPacket=new DatagramPacket(packet.dataBuffer, 0,InetAddress.getByName("224.1.1.2"),9999);
            
            new java.util.Timer().scheduleAtFixedRate(new timerSec(rTPProcessor,sendSocket),0, 800 );
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}

class timerSec extends TimerTask{
    RTPProcessor rtpProcessor;
    public static RTPPacket packet = new RTPPacket();
    public static DatagramPacket sendPacket;
    MulticastSocket sendSocket;
    public timerSec(RTPProcessor pro, MulticastSocket sock)
    {
        rtpProcessor = pro;
        sendSocket = sock;
    }
    @Override
    public void run() {
        int savedTimeStamp=0;
        int packetsSent = 0;
        int dataRate = 0;
        try{
            if(rtpProcessor.getNextPacket(packet))
            savedTimeStamp = packet.timestamp;
            else 
                return;
            while( savedTimeStamp == packet.timestamp )
            {
                rtpProcessor.getNextPacket(packet);
                sendPacket.setData(packet.dataBuffer, 0, packet.dataBufferSize);
                sendSocket.send(sendPacket);
                packetsSent++;
                dataRate += packet.dataBufferSize;
               
            }
            System.out.println("pakcets sent: " + packetsSent + " data rate KBPS: "+dataRate/1024);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}

class RTPPacket
{
    int dataBufferSize;
    public byte[]dataBuffer;
    public int timestamp;
    public int seq;

    public RTPPacket() {
        dataBuffer = new byte[2048];
        dataBufferSize = timestamp = seq = 0;
    }
    
}

class RTPProcessor
{
    File file;
    DataInputStream dis;
    public RTPProcessor(String RTPDumpFileName) 
    {
        file = new File("OUTFILE.txt");
        try
        {
            dis = new DataInputStream(new FileInputStream(file));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean getNextPacket(RTPPacket outPacket)
    {
        try{
            outPacket.dataBufferSize = dis.readInt();
            outPacket.timestamp = dis.readShort();
            if(outPacket.dataBuffer.length<outPacket.dataBufferSize)
                outPacket.dataBuffer = new byte[outPacket.dataBufferSize];
            dis.read(outPacket.dataBuffer,0,outPacket.dataBufferSize);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
