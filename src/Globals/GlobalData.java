/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Globals;

import Tracker.PeerTracker;
import VideoStore.VideoLibrary;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import kliveserver.PeerController;

/**
 * All global data will be stored here.
 * @author Kiran
 */
public class GlobalData {
    public static String VideoStorePath = "./KLiveServer/Videos";
    public static String RTPVideoStorePath = "./KLiveServer/RTPVideos";
    public static int serverPort = 8080;
    public static boolean logEnabled = true;
    public static VideoLibrary videoLibrary;
    public static PeerController peerController;
    public static PeerTracker peerTracker;
    public static void init()
    {
        File settings = new File("settings.txt");
        if(!settings.exists())
        {
            Globals.log.error("Error: Settings file doesnt exist "+settings.getAbsolutePath());
            Globals.log.error("proceeding with default settings");
        }
        else
        {
            try {
                FileInputStream fis = new FileInputStream(settings);
                DataInputStream dis = new DataInputStream(fis);
                dis.readLine();//VideoFolder
                VideoStorePath = dis.readLine();
                Globals.log.message("load setting video store path: "+VideoStorePath);
                dis.readLine();//RTPFolder
                RTPVideoStorePath = dis.readLine();
                Globals.log.message("load setting RTP Video path: "+RTPVideoStorePath);
                dis.readLine();//server port
                serverPort = Integer.parseInt(dis.readLine());
                Globals.log.message("load setting kLiveServer Port: "+serverPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        videoLibrary = new VideoLibrary();
        peerController = new PeerController();
        peerTracker = new PeerTracker();
    }
}
