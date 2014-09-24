/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.watcher;

import app.browser.ExtendedWebBrowser;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author haywoosd
 */
public class AgentWatcherThread extends Thread {

    private final ArrayList<String> watchedAgents = new ArrayList<String>();
    private final ExtendedWebBrowser webBrowser;
    private boolean running = true;

    public AgentWatcherThread(ExtendedWebBrowser webBrowser1) {
        webBrowser = webBrowser1;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        int TutorCount = ((Double) webBrowser.executeJavascriptWithResult("return frames[0].document.getElementById(\"tagents\").rows.length")).intValue();
                        for (int i = 1; i < TutorCount; i++) {
                            String tempName = (String) webBrowser.executeJavascriptWithResult("return frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].innerHTML");
                            String listedName = tempName.substring(tempName.lastIndexOf("&nbsp;") + 6, tempName.length());
                            if (isWatched(listedName)) {
                                System.out.println("Checking to see if " + listedName + " is on a call.");
                                System.out.println(webBrowser.executeJavascriptWithResult("return frames[0].document.getElementById(\"tagents\").rows[2].children[0].children[2].onclick"));
                                if (webBrowser.executeJavascriptWithResult("return frames[0].document.getElementById(\"tagents\").rows[2].children[0].children[2].onclick") != null) {
                                    System.out.println("Watcher is activating to start listening to " + listedName);
                                    webBrowser.executeJavascript("frames[0].document.getElementById(\"tagents\").rows[2].children[0].children[2].click()");
                                    watchedAgents.clear();
                                    running = false;
                                    break;
                                }
                            }
                        }
                    }
                });

            } catch (NullPointerException e) {
                System.out.println("The tab with the watcher has left the real-time agent page. The watcher thread will now shutdown.");
                running = false;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AgentWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addWatchedAgent(String watch) {
        if (!isWatched(watch)) {
            watchedAgents.add(watch);
            System.out.println("Adding " + watch + " to the watcher thread.");
        } else {
            System.out.println(watch + " is already being watched. He/She will not be added to the watch thread.");
        }
    }

    public void removeWatchedAgent(String watch) {
        int arrayLength = watchedAgents.size();
        for (int i = 0; i < arrayLength; i++) {
            if (watchedAgents.get(i).equals(watch)) {
                watchedAgents.remove(i);
            }
        }
    }

    private boolean isWatched(String watch) {
        int arrayLength = watchedAgents.size();
        for (int i = 0; i < arrayLength; i++) {
            if (watchedAgents.get(i).equals(watch)) {
                return true;
            }
        }
        return false;
    }
}
