/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author home
 */
public class VideoUpload extends Thread{

    Socket sock;
    String fileName;
    int fileSize;
    
    public VideoUpload(Socket sock, String fileName,String fileSize) {
        this.sock = sock;
        this.fileName = fileName;
        this.fileSize = java.lang.Integer.parseInt(fileSize);
    }

    @Override
    public void run() {
        super.run(); //To change body of generated methods, choose Tools | Templates.
        try {
            DataInputStream dis = new DataInputStream(sock.getInputStream());
            FileOutputStream fout = new FileOutputStream(Globals.GlobalData.VideoStorePath+"/"+fileName);
            int bytesRead = 0,b;
            byte[] data = new byte[8196];
            bytesRead = 0;
            while(bytesRead<fileSize)
            {
                b = dis.read(data);
                bytesRead+=b;
                fout.write(data, 0, b);
            }
            fout.close();
            Globals.log.message("upload complete: "+Globals.GlobalData.VideoStorePath+"/"+fileName+ " of size "+bytesRead);
            dis.close();
            sock.close();
        } catch (Exception ex) {
            Logger.getLogger(VideoUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
