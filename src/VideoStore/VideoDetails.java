/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package VideoStore;

/**
 * Class to store details about a video and its rtp files.
 * @author Kiran
 */
public class VideoDetails {
    public String fileName;
    public boolean RTPEncodingAvaliable;
    public int duration;
    public int avgBitRate;//in bits per sec
    public int numberOfChunks;
    public boolean streamingLive;

    public VideoDetails() {
        fileName = null;
        RTPEncodingAvaliable = false;
        duration = 0;
        avgBitRate = 0;
        numberOfChunks = 0;
        streamingLive = false;
    }
    
}
