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

    public static ArrayList<Integer> timerHours;
    public static ArrayList<Integer> timerMinutes;
    public static ArrayList<String> timerNames;
    JPanel parentPanel;

    public AlarmsAlertThread(JPanel parent) {
        parentPanel = parent;
        timerHours = new ArrayList();
        timerMinutes = new ArrayList();
        timerNames = new ArrayList();
    }

    @Override
    public void run() {
        while (true) {
            for (int i = 0; i < timerHours.size(); i++) {
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == timerHours.get(i)) {
                    if (Calendar.getInstance().get(Calendar.MINUTE) == timerMinutes.get(i)) {
                        final JDialog messageDialog = new JDialog(main.frame, "Alarm Warning");
                        AlarmAlertPanel messagePanel = new AlarmAlertPanel(timerNames.get(i), i);
                        messageDialog.add(messagePanel, BorderLayout.CENTER);
                        messageDialog.pack();
                        messageDialog.setLocationRelativeTo(parentPanel.getParent());
                        messageDialog.setResizable(false);
                        messageDialog.setVisible(true);
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
}
