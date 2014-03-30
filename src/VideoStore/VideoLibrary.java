/*@author kiran bose
 * create and search for video store files
 * create a library. add files to library.
 * call rtpfilegenerator class, where it calls vlc and stream.
 * Populates Vector<videolist> and initialze videolist elements.
 */

package VideoStore;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
           
            if(videoFolder.list().length>0)
            {
                for(final File fileEntry : videoFolder.listFiles())
                {
                    addVideo(fileEntry.getCanonicalPath());
                }
            }
            else
            {
                Globals.log.error(videoFolder.getCanonicalPath()+ " folder is empty. no video to stream ");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void addVideo(String filePath)
    {
        File fileEntry = new File(filePath);
        VideoDetails obj=new VideoDetails();
        obj.fileName=fileEntry.getName();
        String logFilePath=Globals.GlobalData.RTPVideoStorePath+"/"+fileEntry.getName()+"/rtp.log";
        File logFile = new File(logFilePath);
        if(logFile.exists()&& !logFile.isDirectory())
        {
            obj.RTPEncodingAvaliable=true;
            Globals.log.message("RTP encoding avilable "+obj.fileName);
            try{
                FileInputStream fin = new FileInputStream(logFile);
                DataInputStream dis = new DataInputStream(fin);
                String message = dis.readLine();
                obj.avgBitRate = java.lang.Integer.parseInt(dis.readLine());
                message = dis.readLine();
                obj.duration = java.lang.Integer.parseInt(dis.readLine());
                message = dis.readLine();
                obj.avgBitRate = java.lang.Integer.parseInt(dis.readLine());
                message = dis.readLine();
                obj.numberOfChunks = java.lang.Integer.parseInt(dis.readLine());
                dis.close();
                fin.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }
        else
            Globals.log.message("RTP encoding not avilable for "+obj.fileName+". Convertion will be initiated at stream time.");
        videoList.add(obj);
        Globals.log.message("added video "+obj.fileName+" to library");
    }
  
    
}
