/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.io.File;

/**
 *
 * @author Sloan
 */
public class ExtendedWebBrowser extends JWebBrowser {

    private boolean hasTimer = false;
    private BrowserTimerAdapter browserTimer = null;

    public boolean isTimerEnabled() {
        return hasTimer;
    }
    
    public void addBrowserTimer(BrowserTimerAdapter bta){
        super.addWebBrowserListener(bta);
        hasTimer = true;
        browserTimer = bta;
    }
    
    public void removeBrowserTimer(){
        super.removeWebBrowserListener(browserTimer);
        hasTimer = false;
        browserTimer.terminate();
    }

    public class AutoBackupThread {

        File backupFile;
        ExtendedWebBrowser webBrowser;
        
        AutoBackupThread(ExtendedWebBrowser ewb) {
            backupFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Log_Backup.txt");
            webBrowser = ewb;
        }

        public void run() {
            while (true) {
                String str = "return document.getElementById('entry_1877084581').value;";
            }
        }
    }

}
