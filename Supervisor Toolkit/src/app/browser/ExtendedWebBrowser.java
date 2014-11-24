/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.browser;

import app.backup.AutoBackupThread;
import app.monitor.StatusMonitorThread;
import app.timer.BrowserTimerThread;
import app.watcher.AgentWatcherThread;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 *
 * @author Sloan
 */
public class ExtendedWebBrowser extends JWebBrowser {

    private boolean hasTimer = false;
    private boolean hasBackup = false;
    private boolean hasMonitor = false;
    private AutoBackupThread backupThread;
    private BrowserTimerThread browserTimer;
    private AgentWatcherThread watcherThread;
    private StatusMonitorThread statusMonitor;

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

    public void addBrowserTimer(BrowserTimerThread bta) {
        hasTimer = true;
        browserTimer = bta;
        browserTimer.start();
    }

    public void removeBrowserTimer() {
        hasTimer = false;
        browserTimer.terminate();
    }

    public void disableBackup() {
        backupThread.terminate();
        hasBackup = false;
    }

    public boolean isWatcherEnabled() {
        if (watcherThread != null) {
            return watcherThread.isAlive();
        } else {
            return false;
        }
    }

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
        statusMonitor.terminate();
    }

}
