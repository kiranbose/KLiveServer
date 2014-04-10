/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Tracker;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Arun_
 */
public class CachedVideoDetails {
    public ArrayList<String> videos;
    public ArrayList<ArrayList<Integer>> cachedChunkList;

    public CachedVideoDetails() {
        videos = new ArrayList<>();
        cachedChunkList = new ArrayList<>();
    }
    
    public void parseChunkList(String filename,String chunkList)
    {
        int index = findVideoIndex(filename);
        if(index == -1) // not found
        {
            videos.add(filename);
            cachedChunkList.add(new ArrayList<Integer>());
            index = findVideoIndex(filename);
        }
        ArrayList chunks = cachedChunkList.get(index);
        StringTokenizer tokens = new StringTokenizer(chunkList);
        while(tokens.hasMoreTokens())
        {
            String chunkName = tokens.nextToken();
            if(chunkName.toLowerCase().startsWith("chunk"))
                chunkName = chunkName.replaceFirst("chunk", "").trim();
            chunks.add(java.lang.Integer.parseInt(chunkName));
        }
    }
    
    public boolean isChunkPresent(String filename,int chunk)
    {
        int index = findVideoIndex(filename);
        if(index != -1) // not found
        {
            ArrayList chunks = cachedChunkList.get(index);
            if(chunks.contains(chunk))
                return true;
        }
        return false;
    }
    
    int findVideoIndex(String fileName)
    {
        for(int i=0;i<videos.size();i++)
        {
            if(videos.get(i).equalsIgnoreCase(fileName))
                return i;
        }
        return -1;
    }
}
