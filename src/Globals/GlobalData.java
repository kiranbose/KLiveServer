/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Globals;

import VideoStore.VideoLibrary;
import kliveserver.PeerController;

/**
 * All global data will be stored here.
 * @author Kiran
 */
public class GlobalData {
    public static String VideoStorePath = "c://KLiveServer/Videos";
    public static String RTPVideoStorePath = "c://KLiveServer/RTPVideos";
    public static boolean logEnabled = true;
    public static VideoLibrary videoLibrary;
    public static PeerController peerController;
    public static void init()
    {
        videoLibrary = new VideoLibrary();
        peerController = new PeerController();
    }
}
