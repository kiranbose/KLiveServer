/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kliveserver;

import java.net.ServerSocket;
import java.net.Socket;


/**
 *main class for application
 * @author Kiran
 */
public class KliveServer { 
  
    public static void main(String args[])
    {
        Globals.GlobalData.init();
        SocketListener server = new SocketListener();
        server.StartServerOn(Globals.GlobalData.serverPort);
    }
}

 