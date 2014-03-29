/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package VideoStore;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * maintains a list of video details
 * @author Kiran
 */
public class VideoLibrary {
    public Vector<VideoDetails> videoList;
    public int numberOfVideos;
    
    public VideoLibrary() {
        videoList = new Vector(50);
        numberOfVideos = 0;
        createIndex();
    }
    
    public void createIndex()
    {
        try {
            File videoFolder= new File(Globals.GlobalData.VideoStorePath);
            File rtpVideoFolder= new File(Globals.GlobalData.RTPVideoStorePath);
            if(!videoFolder.exists())
            {
                videoFolder.mkdir();
                Globals.log.message("created Video Store at : "+videoFolder.getCanonicalPath());
            }
            if(!rtpVideoFolder.exists())
            {
                rtpVideoFolder.mkdir();
                Globals.log.message("created RTPVideo Store at : "+rtpVideoFolder.getCanonicalPath());
                       
            }
           
            if(videoFolder.list().length>0){
            
                for(final File fileEntry : videoFolder.listFiles())
            {
                VideoDetails obj=new VideoDetails();
                obj.fileName=fileEntry.getName();
                String logFilePath=fileEntry.getName()+"//rtp.log";
                File logFile = new File(logFilePath);
                if(logFile.exists()&& !logFile.isDirectory())
                {
                    obj.RTPEncodingAvaliable=true;
                }
              
                videoList.add(obj);
                Globals.log.message("adding video "+obj.fileName+" to library");
            }
                
                //test RTPFileGenerator. remove after done
                RTP.RTPFileGenerator.createRtpFileSegments(Globals.GlobalData.VideoStorePath+"/test3.mp4", 
                        Globals.GlobalData.RTPVideoStorePath+"/test3.mp4");
                
            }
            else
            {
                Globals.log.error(videoFolder.getCanonicalPath()+ " folder is empty. no video to sttream ");
            }
            
            
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
  
    
}
