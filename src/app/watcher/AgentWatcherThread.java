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

//This class is the thread that runs to check if someone of the watched list is on class. If they are on call it will connect the user to their call.
//The watcher will now connect the user to another call until after the user is off call. A prompt will be given to continue the watcher once a call ends.
//The dialog to resume the watcher can be helpful if the user stops listening to a call in order to take a survey or other call. 
//It can also relieve issues seen with race conditions seen with connect to Skype for Business. 
public class AgentWatcherThread extends Thread {
    
    public final ArrayList<String> watchedAgents = new ArrayList<>();
    private final ExtendedWebBrowser webBrowser;
    
    private boolean hasActivated = false;
    
    public AgentWatcherThread(ExtendedWebBrowser webBrowser1) {
        webBrowser = webBrowser1;
    }
    
    @Override
    public void run() {
        while (!Thread.interrupted()) {

            //If the user is in a call we do nothing, but we will make sure to propmt the user to resume the watcher once he/she leaves the call.
            if (!inCall()) {

                //If we know that the user was previously in a call we ask him/her if he/she wants to resume the watcher.
                if (hasActivated) {
                    System.out.println("Asking user if he/she wants to resume the watcher");
                    
                    AgentWatcherResumeDialog resumeDialog = new AgentWatcherResumeDialog(main.frame, false);
                    resumeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    resumeDialog.setLocationRelativeTo(main.frame);
                    
                    boolean dialogResult = resumeDialog.showDialog();

                    //If dialog result is false the user chose to stop the watcher. If true they chose to continue.
                    if (!dialogResult) {
                        watchedAgents.clear();
                        hasActivated = false;
                        
                        break;
                    } else {
                        hasActivated = false;

                        //In case the user has gone on a call again before responding to the dialog we'll check for that. If they have we just restart the while loop.
                        if (inCall()) {
                            continue;
                        }
                    }
                }

                //Because we must execute the Javascript on the JavaFX thread we must use a CountDownLatch to synchronize.
                final CountDownLatch latch = new CountDownLatch(1);

                //Execute the Javascript on the JavaFX thread.
                Platform.runLater(() -> {
                    //If the web browser is no longer on the real-time agent page then an exception will be thrown. We plan for that here.
                    try {
                        int TutorCount = (Integer) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows.length");
                        
                        for (int i = 1; i < TutorCount; i++) {

                            //The names stored for agents on real-time agent have some fluff. We remove that here.
                            String tempName = (String) webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].innerHTML");
                            String listedName = tempName.substring(tempName.lastIndexOf("&nbsp;") + 6, tempName.length());

                            //We can check to see if an agent is on call by checking to see if there is an action assigned to their start listening button.
                            if (watchedAgents.contains(listedName) && webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].children[2].onclick") != null) {

                                //We simply click their start listening button.
                                webBrowser.getEngine().executeScript("frames[0].document.getElementById(\"tagents\").rows[" + i + "].children[0].children[2].click()");
                                System.out.println("Watcher is activating to start listening to " + listedName);
                                
                                hasActivated = true;

                                //Once we have started listening to someone we need to stop checking if others are on call.
                                break;
                            }
                        }
                    } catch (netscape.javascript.JSException e) {
                        System.out.println("The tab with the watcher has left the real-time agent page. The watcher thread will now shutdown.");
                        Thread.currentThread().interrupt();
                    }
                    
                    latch.countDown();
                });

                //Waiting of synchronization from the CountDownLatch.
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AgentWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else {
                //If the user was on call we keep track of that with hasActivated.
                hasActivated = true;
            }
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AgentWatcherThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("The watcher thread is shuting down.");
    }

    //This function allows new agents to be added to the list of watched agents. This should be used by other classes to add agents.
    public void addWatchedAgent(String watch) {
        
        if (!watchedAgents.contains(watch)) {
            watchedAgents.add(watch);
            
            System.out.println("Adding " + watch + " to the watcher thread.");            
        } else {            
            System.out.println(watch + " is already being watched. He/She will not be added to the watch thread.");
        }
    }

    //This will function will remove the provided agent from the list of watched agents. If the list of watched agents is now empty this thread will be stopped.
    public void removeWatchedAgent(String watch) {
        watchedAgents.remove(watch);
        
        if (watchedAgents.isEmpty()) {
            Thread.currentThread().interrupt();
        }
    }
    
    //This function returns the list of all agents who are being watched. Returns as a String[] as opposed to an ArrayList.
    public String[] getWatched() {
        return watchedAgents.toArray(new String[0]);
    }
    
    //This is used to check if the user is currently in call. This uses the cLib.dll
    private boolean inCall() {
        String[] windowNames = EnumAllWindowNames.getWindowTitles();
        
        //If getWindowTitles() fails then the return value is null. We check for that here. We may want to add in extra code to handle that return being null.
        if (windowNames != null) {
     
            for (String name : windowNames) {
                
                //If any of these phrases appears in a window name we know the user is in a call.
                if (name.contains("[CE]") || name.contains("+1")) {
                    return true;
                }
            }
        }
        return false;
    }
}
