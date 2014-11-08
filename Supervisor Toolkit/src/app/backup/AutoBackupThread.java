/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.backup;

import app.browser.ExtendedWebBrowser;
import app.main;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author haywoosd
 */
public class AutoBackupThread extends Thread {

    private final File backupFile;
    private final ExtendedWebBrowser webBrowser;
    private boolean terminated = false;
    private final String newLine;
    private final String customSplitString;
    private PrintWriter backupWriter;
    private final Object syncObject = new Object();

    public AutoBackupThread(ExtendedWebBrowser ewb) {
        newLine = System.getProperty("line.separator");
        customSplitString = "!!split!!";
        backupFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backup_Log.txt");
        webBrowser = ewb;
    }

    public void terminate() {
        terminated = true;
    }

    @Override
    public void run() {
        try {
            webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
                @Override
                public void loadingProgressChanged(WebBrowserEvent e) {
                    if (e.getWebBrowser().getLoadingProgress() == 100) {
                        if (backupFile.length() > 0 && webBrowser.getResourceLocation().equals(main.Default[1])) {
                            String[] ObjButtons = {"Yes", "No"};
                            int choice = JOptionPane.showOptionDialog(main.frame, "There is an backup available. Would you like to load it?", "Load Backup?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, ObjButtons, ObjButtons[1]);
                            if (choice == JOptionPane.YES_OPTION) {
                                loadBackup();
                            }
                        }
                        e.getWebBrowser().removeWebBrowserListener(this);
                        Thread syncRelease = new Thread() {
                            @Override
                            public void run() {
                                synchronized (syncObject) {
                                    System.out.println("Giving notice that prompt has been responsed to.");
                                    syncObject.notify();
                                }
                            }
                        };
                        syncRelease.start();
                    }
                }
            });
            synchronized (syncObject) {
                Thread.sleep(30000);
                System.out.println("Waiting to start thread until response to prompt is given.");
                syncObject.wait();
            }
            while (!terminated) {
                saveBackup();
                Thread.sleep(30000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadBackup() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fileInputStream = new FileInputStream(backupFile);
                    byte[] data = new byte[(int) backupFile.length()];
                    fileInputStream.read(data);
                    fileInputStream.close();
                    String content = new String(data, "UTF-8");
                    String[] backupEntries = content.split(newLine + customSplitString + newLine, -1);
                    for (int i = 0; i < backupEntries.length; i++) {
                        backupEntries[i] = backupEntries[i].replace(newLine, "\\n");
                        backupEntries[i] = backupEntries[i].replace("\"", "\\\"");
                    }
                    webBrowser.executeJavascript("document.getElementById(\"entry.50106969_month\").value = " + backupEntries[0]);
                    webBrowser.executeJavascript("document.getElementById(\"entry.50106969_day\").value = " + backupEntries[1]);
                    webBrowser.executeJavascript("document.getElementById(\"entry.50106969_year\").value = " + backupEntries[2]);
                    webBrowser.executeJavascript("document.getElementById(\"entry_1877084581\").value = \"" + backupEntries[3] + "\"");
                    webBrowser.executeJavascript("document.getElementById(\"entry_1290029990\").value = \"" + backupEntries[4] + "\"");
                    webBrowser.executeJavascript("document.getElementById(\"entry_1758939265\").value = \"" + backupEntries[5] + "\"");
                    webBrowser.executeJavascript("document.getElementById(\"entry_1705517941\").value = \"" + backupEntries[6] + "\"");
                    webBrowser.executeJavascript("document.getElementById(\"entry_1600669098\").value = \"" + backupEntries[7] + "\"");
                    for (int i = 0; i < 2; i++) {
                        webBrowser.executeJavascript("document.getElementsByName(\"entry.392665480\")[" + i + "].checked = " + backupEntries[8 + i]);
                    }
                    for (int i = 0; i < 2; i++) {
                        webBrowser.executeJavascript("document.getElementsByName(\"entry.659717484\")[" + i + "].checked = " + backupEntries[10 + i]);
                    }
                    for (int i = 0; i < 2; i++) {
                        webBrowser.executeJavascript("document.getElementsByName(\"entry.1657790510\")[" + i + "].checked = " + backupEntries[12 + i]);
                    }
                    for (int i = 0; i < 6; i++) {
                        webBrowser.executeJavascript("document.getElementsByName(\"entry.398759739\")[" + i + "].checked = " + backupEntries[14 + i]);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    private void saveBackup() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (webBrowser.getResourceLocation().equals(main.Default[1])) {
                    System.out.println("Saving a backup of the Nightly log");
                    try {
                        String[] backupEntries = new String[20];
                        backupEntries[0] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry.50106969_month\").value");
                        backupEntries[1] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry.50106969_day\").value");
                        backupEntries[2] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry.50106969_year\").value");
                        backupEntries[3] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry_1877084581\").value");
                        backupEntries[4] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry_1290029990\").value");
                        backupEntries[5] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry_1758939265\").value");
                        backupEntries[6] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry_1705517941\").value");
                        backupEntries[7] = (String) webBrowser.executeJavascriptWithResult("return document.getElementById(\"entry_1600669098\").value");
                        for (int i = 0; i < 2; i++) {
                            backupEntries[8 + i] = webBrowser.executeJavascriptWithResult("return document.getElementsByName(\"entry.392665480\")[" + i + "].checked;").toString();
                        }
                        for (int i = 0; i < 2; i++) {
                            backupEntries[10 + i] = webBrowser.executeJavascriptWithResult("return document.getElementsByName(\"entry.659717484\")[" + i + "].checked").toString();
                        }
                        for (int i = 0; i < 2; i++) {
                            backupEntries[12 + i] = webBrowser.executeJavascriptWithResult("return document.getElementsByName(\"entry.1657790510\")[" + i + "].checked").toString();
                        }
                        for (int i = 0; i < 6; i++) {
                            backupEntries[14 + i] = webBrowser.executeJavascriptWithResult("return document.getElementsByName(\"entry.398759739\")[" + i + "].checked").toString();
                        }
                        backupWriter = new PrintWriter(backupFile);
                        for (String backupEntry : backupEntries) {
                            if (backupEntry == null) {
                                backupWriter.print("" + newLine + customSplitString + newLine);
                            } else {
                                backupWriter.print(backupEntry.replace("\n", newLine) + newLine + customSplitString + newLine);
                            }
                        }
                        backupWriter.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NullPointerException ex){
                        System.out.println("Failed to aquire data from nightly log site. Perhaps the page isn't loaded correctly");
                    }
                }
            }
        });
    }
}
