package app.backup;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javax.swing.JOptionPane;

public class AutoBackupThread extends Thread {

    private final File backupFile;
    private final File longTermBackups;
    private final ExtendedWebBrowser webBrowser;
    private boolean terminated = false;
    private final String newLine = System.getProperty("line.separator");
    private final String customSplitString = "!!split!!";
    private final int maxBackups = 30;
    public static final String[] emptyBackupStrings = {"", "", "2015", "", "", "", "", "", "false", "false", "false", "false", "false", "false", ""};

    public AutoBackupThread(ExtendedWebBrowser ewb) {
        
        backupFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backup_Log.txt");
        longTermBackups = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backups_Old");
        
        if (!longTermBackups.isDirectory()) {
            longTermBackups.mkdir();
        }
        
        webBrowser = ewb;
    }

    public void terminate() {
        terminated = true;
    }

    public void storeLatestBackup() {
        if (backupFile.exists()) {
            System.out.println("Moving backup file to long term storage with new file name: " + "Backup_Log_" + +backupFile.lastModified() + ".txt");
            backupFile.renameTo(new File(longTermBackups.getAbsolutePath() + "\\Backup_Log_" + backupFile.lastModified() + ".txt"));
            File[] longTermList = longTermBackups.listFiles();
            if (longTermList.length > maxBackups) {
                Arrays.sort(longTermList);
                for (int i = 0; i < longTermList.length - maxBackups; i++) {
                    System.out.println("Too many backup files. Deleting file: " + longTermList[i].getName());
                    longTermList[i].delete();
                }
            }
        }
    }

    @Override
    public void run() {
        try {

            CountDownLatch latch = new CountDownLatch(1);

            Platform.runLater(() -> {
                webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                        if (newValue.intValue() == 100) {

                            new Thread() {
                                @Override
                                public void run() {
                                    if ((backupFile.exists() || longTermBackups.list().length > 0) && webBrowser.getEngine().getLocation().equals(main.Default[1])) {

                                        String[] ObjButtons = {"Yes", "No"};
                                        int choice = JOptionPane.showOptionDialog(main.frame, "There is an backup available. Would you like to load it?", "Load Backup?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, ObjButtons, ObjButtons[1]);

                                        if (choice == JOptionPane.YES_OPTION) {

                                            CountDownLatch latch = new CountDownLatch(1);
                                            AutoBackupSelectDialog selectDialog = new AutoBackupSelectDialog(main.frame, false, backupFile, longTermBackups, latch);
                                            selectDialog.setLocationRelativeTo(main.frame);
                                            selectDialog.setVisible(true);

                                            try {
                                                latch.await();
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                            if (selectDialog.getSelectedFile() == null) {

                                                storeLatestBackup();

                                            } else {

                                                if (backupFile.equals(selectDialog.getSelectedFile())) {

                                                    System.out.println("Loading latest backup.");
                                                    loadBackup(getFileStringContent(selectDialog.getSelectedFile()));

                                                } else {

                                                    System.out.println("Loading old backup. Will store latest backup if possible.");
                                                    loadBackup(getFileStringContent(selectDialog.getSelectedFile()));
                                                    storeLatestBackup();

                                                }
                                            }

                                        } else {

                                            storeLatestBackup();

                                        }
                                    }

                                    System.out.println("Giving notice that prompt has been responsed to.");
                                    latch.countDown();

                                }
                            }.start();

                            webBrowser.getEngine().getLoadWorker().workDoneProperty().removeListener(this);
                        }
                    }

                });
            });

            System.out.println("Waiting to start backup thread until response to prompt is given.");
            Thread.sleep(30000);
            latch.await();

            while (!terminated) {
                saveBackup();
                Thread.sleep(30000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getFileStringContent(File file) {
        String content = null;

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            content = new String(data, "UTF-8");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return content;

    }

    public void loadBackup(String content) {

        Platform.runLater(() -> {

            String[] backupEntries = content.split(newLine + customSplitString + newLine, -1);

            for (int i = 0; i < backupEntries.length; i++) {
                backupEntries[i] = backupEntries[i].replace(newLine, "\\n");
                backupEntries[i] = backupEntries[i].replace("\"", "\\\"");
            }

            webBrowser.getEngine().executeScript("document.getElementById(\"entry.50106969_month\").value = \"" + backupEntries[0] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry.50106969_day\").value = \"" + backupEntries[1] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry.50106969_year\").value = \"" + backupEntries[2] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry_1877084581\").value = \"" + backupEntries[3] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry_1290029990\").value = \"" + backupEntries[4] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry_1758939265\").value = \"" + backupEntries[5] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry_1705517941\").value = \"" + backupEntries[6] + "\"");
            webBrowser.getEngine().executeScript("document.getElementById(\"entry_1600669098\").value = \"" + backupEntries[7] + "\"");

            for (int i = 0; i < 2; i++) {
                webBrowser.getEngine().executeScript("document.getElementsByName(\"entry.392665480\")[" + i + "].checked = " + backupEntries[8 + i]);
            }

            for (int i = 0; i < 2; i++) {
                webBrowser.getEngine().executeScript("document.getElementsByName(\"entry.659717484\")[" + i + "].checked = " + backupEntries[10 + i]);
            }

            for (int i = 0; i < 2; i++) {
                webBrowser.getEngine().executeScript("document.getElementsByName(\"entry.1657790510\")[" + i + "].checked = " + backupEntries[12 + i]);
            }

            webBrowser.getEngine().executeScript("document.getElementById(\"entry_398759739\").value = \"" + backupEntries[14] + "\"");

        });

    }

    private void saveBackup() {
        Platform.runLater(() -> {
            if (webBrowser.getEngine().getLocation().equals(main.Default[1])) {

                try {
                    String[] backupEntries = new String[15];
                    backupEntries[0] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry.50106969_month\").value");
                    backupEntries[1] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry.50106969_day\").value");
                    backupEntries[2] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry.50106969_year\").value");
                    backupEntries[3] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry_1877084581\").value");
                    backupEntries[4] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry_1290029990\").value");
                    backupEntries[5] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry_1758939265\").value");
                    backupEntries[6] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry_1705517941\").value");
                    backupEntries[7] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry_1600669098\").value");
                    for (int i = 0; i < 2; i++) {
                        backupEntries[8 + i] = webBrowser.getEngine().executeScript("document.getElementsByName(\"entry.392665480\")[" + i + "].checked;").toString();
                    }
                    for (int i = 0; i < 2; i++) {
                        backupEntries[10 + i] = webBrowser.getEngine().executeScript("document.getElementsByName(\"entry.659717484\")[" + i + "].checked").toString();
                    }
                    for (int i = 0; i < 2; i++) {
                        backupEntries[12 + i] = webBrowser.getEngine().executeScript("document.getElementsByName(\"entry.1657790510\")[" + i + "].checked").toString();
                    }
                    backupEntries[14] = (String) webBrowser.getEngine().executeScript("document.getElementById(\"entry_398759739\").value");

                    if (!Arrays.equals(backupEntries, emptyBackupStrings)) {

                        System.out.println("Saving a backup of the Nightly log");

                        try (PrintWriter backupWriter = new PrintWriter(backupFile)) {

                            for (String backupEntry : backupEntries) {

                                if (backupEntry == null) {
                                    backupWriter.print("" + newLine + customSplitString + newLine);
                                } else {
                                    backupWriter.print(backupEntry.replace("\n", newLine) + newLine + customSplitString + newLine);
                                }
                            }
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (NullPointerException ex) {
                    System.out.println("Failed to aquire data from nightly log site. Perhaps the page isn't loaded correctly");
                }
            }
        });
    }
}
