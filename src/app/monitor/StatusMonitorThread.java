package app.monitor;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

//This class is the thread that runs when the status monitor is enabled. It periodically checks each agent and alerts the user is any have been on a status for too long.
public class StatusMonitorThread extends Thread {

    ExtendedWebBrowser webBrowser;

    //By using this boolean we can make sure that not too many exceptions are printed to the console if the web browser has left the real-time agent page.
    boolean errorNoticeGiven = false;

    int ACWTime;
    int AUXTime;
    int WrapupTime;

    ArrayList<String> supervisorList = new ArrayList<>();
    ArrayList<String> alertedUsers = new ArrayList<>();
    ArrayList<String> alertedModes = new ArrayList<>();

    public StatusMonitorThread(ExtendedWebBrowser webBrowser1) {
        webBrowser = webBrowser1;
        loadOptions();
    }

    //Reads the Monitors_Settings.txt file and parses them into the threads settings. If no file exists a default one is created.
    public final void loadOptions() {
        File file = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Monitor_Settings.txt");
        
        if (file.exists()) {
            
            try (Scanner read = new Scanner(file)) {

                String temp = read.nextLine();
                String tempArray[] = temp.split(":");
                ACWTime = Integer.parseInt(tempArray[0]) * 60 + Integer.parseInt(tempArray[1]);

                temp = read.nextLine();
                tempArray = temp.split(":");
                AUXTime = Integer.parseInt(tempArray[0]) * 60 + Integer.parseInt(tempArray[1]);

                temp = read.nextLine();
                tempArray = temp.split(":");
                WrapupTime = Integer.parseInt(tempArray[0]) * 60 + Integer.parseInt(tempArray[1]);

                //In case of multiple calls to loadOptions we clear the list each time before we fill it.
                supervisorList.clear();

                while (read.hasNextLine()) {
                    supervisorList.add(read.nextLine());
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(StatusMonitorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else {
            
            try (PrintWriter write = new PrintWriter(file)) {
                write.println("01:00");
                write.println("15:00");
                write.println("01:00");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StatusMonitorThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            loadOptions();
        }
    }

    //This function returns a String containing the chosen agent's mode. AUX is a special exception because it is contained in a different column than all the others, so we check for it first.
    private String getUserMode(int row) {
        if ("AUX".equals((String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + row + "].children[7].innerHTML"))) {
            return "AUX";
        } else {
            return (String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + row + "].children[3].innerHTML");
        }
    }

    //Returns the time the chosen agent has been in his/her current status. 
    private int getUserTime(int row) {
        String tempTime = (String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + row + "].children[4].innerHTML");
        String[] parsedTime = tempTime.split(":");
        
        int time = Integer.parseInt(parsedTime[0]) * 3600 + Integer.parseInt(parsedTime[1]) * 60 + Integer.parseInt(parsedTime[2]);
        return time;
    }

    //Will display a StatusMonitorAlertDialog for a chosen agent. Will also keep a list of users that alerts have been given for so no agents are alearted on more than once.
    //The status the alerted agent was on is also kept track of, so that they can be alerted on if they change statuses.
    private void giveAlert(String name, String mode) {
        System.out.println("Giving status alert for " + name + " for " + mode);

        StatusMonitorAlertDialog statusAlertDialog = new StatusMonitorAlertDialog(main.frame, false, name, mode, webBrowser);
        statusAlertDialog.setLocationRelativeTo(main.frame);
        statusAlertDialog.setVisible(true);
        statusAlertDialog.setAlwaysOnTop(true);
        statusAlertDialog.setAlwaysOnTop(false);

        alertedUsers.add(name);
        alertedModes.add(mode);
    }

    @Override
    public void run() {
        
        while (!Thread.interrupted()) {
            
            Platform.runLater(() -> {
                //If the web browser has left the real-time agent page an exception will be thrown. 
                try {
                    int tutorCount = ((Integer) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows.length"));
                    
                    //Check to see if any alerted user has changed statuses.
                    for (int i = 0; i < alertedUsers.size(); i++) {
                        
                        for (int i2 = 1; i2 < tutorCount; i2++) {
                            //The name kept in the real-time agent page has some fluff that we need to remove.
                            String tempName = (String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i2 + "].children[0].innerHTML");
                            String listedName = tempName.substring(tempName.lastIndexOf("&nbsp;") + 6, tempName.length());
                            
                            if (listedName.equals(alertedUsers.get(i))) {
                                
                                //If an agent has be alerted on but has now changed statuses we remove them from out list of alerted users. 
                                //We need to reduce our index by one because we have removed an element of the alertedModes and alertedUsers ArrayLists.
                                if (!alertedModes.get(i).equals(getUserMode(i2))) {
                                    System.out.println("Removing " + listedName + " from list of alerted users");
                                    
                                    alertedModes.remove(i);
                                    alertedUsers.remove(i);
                                    i--;
                                    
                                    break;
                                }
                            }
                        }
                    }
                    
                    //Checks each user to see if they have been been on their status for too long. Skips already alerted users and supervisor.
                    for (int i = 1; i < tutorCount; i++) {
                        //The name kept in the real-time agent page has some fluff that we need to remove.
                        String tempName = (String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].innerHTML");
                        String listedName = tempName.substring(tempName.lastIndexOf("&nbsp;") + 6, tempName.length());
                        String tempMode = getUserMode(i);
                        
                        if (!supervisorList.contains(listedName)) {
                            
                            if (!alertedUsers.contains(listedName)) {
                                
                                switch (getUserMode(i)) {
                                    case "AUX":
                                        if (getUserTime(i) > AUXTime) {
                                            giveAlert(listedName, tempMode);
                                        }
                                        break;
                                    case "ACW":
                                        if (getUserTime(i) > ACWTime) {
                                            giveAlert(listedName, tempMode);
                                        }
                                        break;
                                    case "Wrapup":
                                        if (getUserTime(i) > WrapupTime) {
                                            giveAlert(listedName, tempMode);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    errorNoticeGiven = false;

                } catch (netscape.javascript.JSException e) {
                    if (!errorNoticeGiven) {
                        System.out.println("Failed to get number of tutors for status monitor. Perhaps not on the right webpage?");
                        errorNoticeGiven = true;
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
