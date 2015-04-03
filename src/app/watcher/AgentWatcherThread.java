/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.watcher;

import app.JNI.EnumAllWindowNames;
import app.browser.ExtendedWebBrowser;
import app.main;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.JDialog;

/**
 *
 * @author haywoosd
 */
public class AgentWatcherThread extends Thread {

    public final ArrayList<String> watchedAgents = new ArrayList<>();
    private final ExtendedWebBrowser webBrowser;
    private boolean running = true;
    private boolean hasActivated = false;

    public AgentWatcherThread(ExtendedWebBrowser webBrowser1) {
        webBrowser = webBrowser1;
    }

    @Override
    public void run() {
        while (running) {

            boolean inCall = false;
            String[] windowNames = EnumAllWindowNames.getWindowTitles();

            if (windowNames != null) {
                for (String name : windowNames) {
                    if (name.contains("[CE]")) {
                        inCall = true;
                        break;
                    }
                }
            }

            if (!inCall) {

                if (hasActivated) {

                    final CountDownLatch latch = new CountDownLatch(1);

                    System.out.println("Asking user if he/she wants to resume the watcher");

                    JDialog diag = new JDialog(main.frame);
                    
                    AgentWatcherResumePanel panel = new AgentWatcherResumePanel(latch);
                    diag.add(panel);
                    
                    diag.setTitle("Resume Watcher?");
                    diag.pack();
                    
                    diag.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    diag.setResizable(false);
                    
                    diag.setLocationRelativeTo(main.frame);
                    diag.setVisible(true);
                    
                    diag.setAlwaysOnTop(true);
                    diag.setAlwaysOnTop(false);

                    try {
                        
                        latch.await();
                        
                    } catch (InterruptedException ex) {
                        
                        Logger.getLogger(AgentWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
                        
                    }

                    boolean resumeWatcher = panel.getResult();

                    if (!resumeWatcher) {

                        watchedAgents.clear();

                        running = false;
                        hasActivated = false;

                        break;

                    } else {

                        hasActivated = false;

                    }

                }

                final CountDownLatch latch = new CountDownLatch(1);

                Platform.runLater(() -> {
                    try {

                        int TutorCount = ((Integer) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows.length"));

                        for (int i = 1; i < TutorCount; i++) {

                            String tempName = (String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].innerHTML");

                            String listedName = tempName.substring(tempName.lastIndexOf("&nbsp;") + 6, tempName.length());

                            if (watchedAgents.contains(listedName) && webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].children[2].onclick") != null) {

                                webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].children[2].click()");
                                System.out.println("Watcher is activating to start listening to " + listedName);

                                hasActivated = true;

                                break;
                            }
                        }

                    } catch (netscape.javascript.JSException e) {

                        System.out.println("The tab with the watcher has left the real-time agent page. The watcher thread will now shutdown.");
                        running = false;

                    } finally {

                        latch.countDown();

                    }

                });

                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AgentWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AgentWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println("The watcher thread is shuting down.");
    }

    public void addWatchedAgent(String watch) {

        if (!watchedAgents.contains(watch)) {

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
                break;
            }

        }

        if (watchedAgents.isEmpty()) {
            running = false;
        }

    }

    public String[] getWatched() {
        return watchedAgents.toArray(new String[1]);
    }
}
