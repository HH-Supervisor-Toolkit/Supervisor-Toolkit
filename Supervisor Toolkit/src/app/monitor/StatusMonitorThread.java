/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.monitor;

import app.browser.ExtendedWebBrowser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author haywoosd
 */
public class StatusMonitorThread extends Thread {

    ExtendedWebBrowser webBrowser;
    boolean enabled = true;
    int ACWTime;
    int AUXTime;
    int WrapupTime;
    ArrayList<String> supervisorList = new ArrayList<String>();
    ArrayList<String> alertedUsers = new ArrayList<String>();
    ArrayList<String> alertedModes = new ArrayList<String>();
    boolean errorNoticeGiven = false;

    public StatusMonitorThread(ExtendedWebBrowser webBrowser1) {
        webBrowser = webBrowser1;
        loadOptions();
    }

    public void terminate() {
        enabled = false;
    }

    public final void loadOptions() {
        File file = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Monitor_Settings.txt");
        if (file.exists()) {
            try {
                Scanner read = new Scanner(file);
                String temp = read.nextLine();
                String tempArray[] = temp.split(":");
                ACWTime = Integer.parseInt(tempArray[0]) * 60 + Integer.parseInt(tempArray[1]);
                temp = read.nextLine();
                tempArray = temp.split(":");
                AUXTime = Integer.parseInt(tempArray[0]) * 60 + Integer.parseInt(tempArray[1]);
                temp = read.nextLine();
                tempArray = temp.split(":");
                WrapupTime = Integer.parseInt(tempArray[0]) * 60 + Integer.parseInt(tempArray[1]);
                supervisorList.clear();
                while (read.hasNextLine()) {
                    supervisorList.add(read.nextLine());
                }
                read.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StatusMonitorOptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                PrintWriter write = new PrintWriter(file);
                write.println("01:00");
                write.println("15:00");
                write.println("01:00");
                write.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StatusMonitorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadOptions();
        }
    }

    @Override
    public void run() {
        while (enabled) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        int tutorCount = ((Double) webBrowser.executeJavascriptWithResult("return frames[0].document.getElementById(\"tagents\").rows.length")).intValue();
                        // Add code that check to see if a tutor whom an alert was given for has changed modes and therefore can recieve new notices
                        for (String alertedUser : alertedUsers) {
                            for (int i = 0; i < tutorCount; i++) {

                            }
                        }
                        //Add code that checks to see if a user has been on a mode too long
                        for (int i = 0; i < tutorCount; i++) {

                        }
                        errorNoticeGiven = false;
                    } catch (NullPointerException e) {
                        if (!errorNoticeGiven) {
                            System.out.println("Failed to get number of tutors for status monitor. Perhaps not on the right webpage?");
                            errorNoticeGiven = true;
                        }
                    }
                }
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(StatusMonitorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
