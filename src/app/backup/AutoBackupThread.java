package app.backup;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AutoBackupThread extends Thread {

    private final File backupFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backup_Log.txt");
    private final File longTermBackups = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backups_Old");

    private final ExtendedWebBrowser webBrowser;

    private boolean terminated = false;
    private boolean hasStored = false;

    public static final String newLine = System.getProperty("line.separator");
    public static final String customSplitString = "!!split!!";

    private final int maxBackups = 30;

    public static final String[] emptyBackupStrings = {"", "", "2015", "", "", "", "", "", "false", "false", "false", "false", "false", "false", ""};

    public AutoBackupThread(ExtendedWebBrowser ewb) {

        if (!longTermBackups.isDirectory()) {
            longTermBackups.mkdir();
        }

        webBrowser = ewb;
    }

    @Override
    public void run() {
        try {

            CountDownLatch latch = new CountDownLatch(1);

            Platform.runLater(() -> {

                if (webBrowser.getEngine().getLoadWorker().workDoneProperty().intValue() == 100) {
                    latch.countDown();
                } else {
                    webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
                        if (newValue.intValue() == 100) {
                            latch.countDown();
                        }
                    });
                }

            });

            latch.await();

            while (!terminated) {
                Thread.sleep(30000);
                saveBackup();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
        }
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

                        if (!hasStored) {
                            storeLatestBackup();
                            hasStored = true;
                        }

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
}
