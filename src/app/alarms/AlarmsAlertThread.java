/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.alarms;

import app.main;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author haywoosd
 */
public class AlarmsAlertThread extends Thread {

    public static ArrayList<Integer> timerHours = new ArrayList<>();
    public static ArrayList<Integer> timerMinutes = new ArrayList<>();
    public static ArrayList<String> timerNames = new ArrayList<>();
    JPanel parentPanel;
    boolean running = true;

    @Override
    public void run() {
        while (running) {
            for (int i = 0; i < timerHours.size(); i++) {
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == timerHours.get(i)) {
                    if (Calendar.getInstance().get(Calendar.MINUTE) == timerMinutes.get(i)) {
                        final JDialog messageDialog = new JDialog(main.frame, "Alarm Warning");
                        AlarmAlertPanel messagePanel = new AlarmAlertPanel(timerNames.get(i), i);
                        messageDialog.add(messagePanel, BorderLayout.CENTER);
                        messageDialog.pack();
                        messageDialog.setLocationRelativeTo(main.frame);
                        messageDialog.setResizable(false);
                        messageDialog.setVisible(true);
                        messageDialog.setAlwaysOnTop(true);
                        messageDialog.setAlwaysOnTop(false);
                    }
                }
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AlarmsAlertThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void terminate(){
        running = false;
    }
}
