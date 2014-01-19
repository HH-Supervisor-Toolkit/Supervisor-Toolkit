/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.File;

/**
 *
 * @author haywoosd
 */
public class AutoBackupThread extends Thread {

    private File backupFile;
    private ExtendedWebBrowser webBrowser;
    private boolean terminated = false;

    AutoBackupThread(ExtendedWebBrowser ewb) {
        backupFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backup_Log.txt");
        webBrowser = ewb;
    }
    
    public void terminate(){
        terminated = true;
    }

    @Override
    public void run() {
        while (!terminated) {
            String str = "return document.getElementById('entry_1877084581').value;";
        }
    }
}
