/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author home
 */
public class ChunkSender extends Thread{

    Socket sock;
    String fileName;
    String chunkNumber;
    
    public ChunkSender(Socket sock, String fileName,String chunkNumber) {
        this.sock = sock;
        this.fileName = fileName;
        this.chunkNumber = chunkNumber;
    }

    @Override
    public void run() {
        super.run(); //To change body of generated methods, choose Tools | Templates.
        try 
        {
            DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            File chunkFile = new File(Globals.GlobalData.RTPVideoStorePath+"/"+fileName+"/"+chunkNumber);
            if(!chunkFile.exists())
            {
                Globals.log.error(chunkNumber+" of "+fileName+" does not exist");
                dout.writeBytes("NoChunk\r\n");
            }
            else
            {
                Globals.log.error("sending "+chunkNumber+" of "+fileName);
                dout.writeBytes("ChunkSize\r\n");
                dout.writeBytes(java.lang.Long.toString(chunkFile.length())+"\r\n");
                dout.writeBytes("data\r\n");
                sendChunk(chunkFile,dout);
            }
            dout.close();
            sock.close();
        } 
        catch (Exception ex) 
        {
            Globals.log.error("error while uploading "+chunkNumber+ " of "+fileName);
            ex.printStackTrace();
        }
    }
    
    public void sendChunk(File chunkFile,DataOutputStream dout)
    {
        try
        {
            long fileSize=chunkFile.length();
            int bytesRead = 0,b;
            byte[] data = new byte[8196];
            bytesRead = 0;
            FileInputStream fin=new FileInputStream(chunkFile);
            while(bytesRead<fileSize)
            {
                b = fin.read(data);
                bytesRead+=b;
                dout.write(data, 0, b);
            }
            Globals.log.message("chunk Transfer complete: "+chunkFile.getCanonicalPath()+ " of size "+bytesRead);
            fin.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
