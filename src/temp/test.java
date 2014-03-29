/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package temp;

import java.util.TimerTask;
import java.util.Timer;
//import java.util.timer;
/**
 *
 * @author home
 */


public class test extends TimerTask {
    public void run() {
      System.out.println(" Hello World!");
    }
    public static void main(String[] args){
       Timer t = new Timer();
       t.schedule(new test(), 0,1000);
    }
  }



