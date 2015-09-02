package app.browser;

import app.backup.AutoBackupThread;
import app.monitor.StatusMonitorThread;
import app.timer.BrowserTimer;
import app.watcher.AgentWatcherThread;

//This class adds functionality to the web browser other than the basic functions of any web browser. Any new features will most likely be placed here.
//Most of these methods all act the same and simply represent a different feature.
public class ExtendedWebBrowser extends JWebBrowserPanel {

    private boolean hasTimer = false;
    private boolean hasBackup = false;
    private boolean hasMonitor = false;

    private AutoBackupThread backupThread;
    private BrowserTimer browserTimer;
    private AgentWatcherThread watcherThread;
    private StatusMonitorThread statusMonitor;

    //Typically there should be an enable, disable, and isEnabled function for each feature of the toolkit. Most features are threads so enable/disable starts/interrupts them.
    //The prefered way to stop a thread is with the built-in intterupt feature. Interrupt will also break from Thread.sleep calls.
    
    public boolean isTimerEnabled() {
        return hasTimer;
    }

    public boolean isBackupEnabled() {
        return hasBackup;
    }

    public void enableBackup() {
        backupThread = new AutoBackupThread(this);
        backupThread.start();
        hasBackup = true;
    }

    //The browser timer must be created with a specific time specified to it. Therefore it is created outside of this class and passed to this function as an argument.
    public void addBrowserTimer(BrowserTimer bta) {
        hasTimer = true;
        browserTimer = bta;
        browserTimer.start();
    }

    //The browserTimer isn't a thread so it has a special method for stopping it.
    public void removeBrowserTimer() {
        hasTimer = false;
        browserTimer.terminate();
    }

    public void disableBackup() {
        backupThread.interrupt();
        hasBackup = false;
    }

    //The watcher can disable itself if no one is being watched any longer. Therefore we need to check if the thread is still alive, but we can't do 
    //this if the thread has never been created. We first need to make sure the thread is not null.
    public boolean isWatcherEnabled() {
        if (watcherThread != null) {
            return watcherThread.isAlive();
        } else {
            return false;
        }
    }

    //Returns a String[] of all users currently being watched. If the watcher isn't currently enabled then null should be returned.
    public String[] getWatched() {
        if (isWatcherEnabled()) {
            return watcherThread.getWatched();
        } else {
            return null;
        }
    }

    public void enableWatcher() {
        watcherThread = new AgentWatcherThread(this);
        watcherThread.start();
    }

    public void addWatched(String watch) {
        watcherThread.addWatchedAgent(watch);
    }

    public void removeWatched(String watch) {
        watcherThread.removeWatchedAgent(watch);
    }

    public boolean isMonitorEnabled() {
        return hasMonitor;
    }

    public void enableMonitor() {
        hasMonitor = true;
        statusMonitor = new StatusMonitorThread(this);
        statusMonitor.start();
    }

    public void disableMonitor() {
        hasMonitor = false;
        statusMonitor.interrupt();
    }

    //Used to easily update the monitor's settings for any dialog that can be passed the current ExtendedWebBrowser element as a constructor argument.
    public void updateMonitor() {
        statusMonitor.loadOptions();
    }

}
