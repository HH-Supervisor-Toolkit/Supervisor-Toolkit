/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.browser;

import app.backup.AutoBackupThread;
import app.timer.BrowserTimerAdapter;
import app.watcher.AgentWatcherThread;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 *
 * @author Sloan
 */
public class ExtendedWebBrowser extends JWebBrowser {

    private boolean hasTimer = false;
    private boolean hasBackup = false;
    private AutoBackupThread backupThread;
    private BrowserTimerAdapter browserTimer;
    private AgentWatcherThread watcherThread;

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

    public void addBrowserTimer(BrowserTimerAdapter bta) {
        super.addWebBrowserListener(bta);
        hasTimer = true;
        browserTimer = bta;
    }

    public void removeBrowserTimer() {
        super.removeWebBrowserListener(browserTimer);
        hasTimer = false;
        browserTimer.terminate();
    }

    public void disableBackup() {
        backupThread.terminate();
        hasBackup = false;
    }

    public boolean isWatcherEnabled() {
        return watcherThread.isAlive();
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
}
