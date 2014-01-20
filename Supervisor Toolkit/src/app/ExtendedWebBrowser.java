/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

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
}
