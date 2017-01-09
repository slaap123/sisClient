/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.testwedstrijdrunner.utils;

import static java.lang.Thread.sleep;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.phanos.testwedstrijdrunner.gui.MainWindow;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;

/**
 *
 * @author woutermkievit
 */
public class savingHandler extends Thread{
    
    public static Queue uitslagen = new LinkedList();
    private HibernateSessionHandler sessionHandler;
    
    @Override
    public void run() {
        sessionHandler = HibernateSessionHandler.get();
        while (true) {
            SaveNewRecords();
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(savingHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void SaveNewRecords() {
        while (!uitslagen.isEmpty()) {
            if (!MainWindow.mainObj.test) {
                try {
                    System.out.println("saving: " + uitslagen.peek().getClass().getName());
                    sessionHandler.save(uitslagen.poll());
                } catch (HibernateException he) {
                    he.printStackTrace();
                } catch (AssertionFailure he) {
                    he.printStackTrace();
                }
            }
        }
    }
}
