/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Kiran
 */
public class SocketListener {
    public void StartServerOn(int port)
    {
        try{
            ServerSocket server = new ServerSocket(port);
            Globals.log.message("kLiveSever started on port "+port);
            Socket clientSocket;
            DataInputStream dis;
            while(true)
            {
                clientSocket = server.accept();
                try{
                    dis = new DataInputStream(clientSocket.getInputStream());
                    String request = dis.readLine();
                    if(request.equalsIgnoreCase("control"))
                    {
                        String userID = dis.readLine();
                        String peerIP = dis.readLine();
                        int peerPort = java.lang.Integer.parseInt(dis.readLine());
                        Globals.log.message(userID+": Connected for control peer-ip: "+peerIP+":"+peerPort);
                        Globals.GlobalData.peerController.addPeer(clientSocket,userID);
                        Globals.GlobalData.peerTracker.addPeer(userID, peerIP, peerPort);
                        Globals.GlobalData.peerController.broadcastPeerList();
                    }
                    else if(request.equalsIgnoreCase("upload"))
                    {
                        String userID = dis.readLine();
                        String fileName = dis.readLine();
                        String fileSize = dis.readLine();
                        Globals.log.message(userID+": Reciever upload: "+fileName+ " of size "+fileSize);
                        VideoUpload uploader = new VideoUpload(clientSocket,fileName,fileSize);
                        uploader.start();
                    }
                    else if(request.equalsIgnoreCase("download"))
                    {
                        String userID = dis.readLine();
                        String fileName = dis.readLine();
                        String chunkNumber = dis.readLine();
                        Globals.log.message(userID+": Recieved chunk download: "+fileName+ " " +chunkNumber);
                        ChunkSender chunkSender = new ChunkSender(clientSocket,fileName,chunkNumber);
                        chunkSender.start();
                    }
                    else
                    {
                        clientSocket.close();
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }   
    }
}
