package app.backup;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

//The thread that runs when backups are enabled. It sleeps 30 seconds each loop. Each loop stores the contents of the Nightly Log page into the Backup_Log.txt file.
public class AutoBackupThread extends Thread {

    private static final File backupFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backup_Log.txt");
    private static final File longTermBackups = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Backups_Old");

    private final ExtendedWebBrowser webBrowser;

    private boolean hasStored = false;

    //The line seperator is different on different operating systems. This helps keep it standard. The custome split string is an assumption that !!split!! won't be typed normally.
    //It is how we seperate different elements of the saved log because there can be line breaks in certain fields of the Nightly Log.
    public static final String newLine = System.getProperty("line.separator");
    public static final String customSplitString = "!!split!!";

    //The maximum number of old backups to store.
    private static final int maxBackups = 30;

    //This is hard coded to tell if nothing has be inputed into the page. The default year value is always the current year. Therefore we get the current year to use as a default.
    public static final String[] emptyBackupStrings = {"", "", String.valueOf(GregorianCalendar.getInstance().get(Calendar.YEAR)) , "", "", "", "", "", "false", "false", "false", "false", "false", "false", ""};

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

            //We need to make sure that the pages is fully loaded before we try to save any information. Listeners can only be added on the JavaFX thread. THis is why we use 
            //Platform.runLater. To keep things synchronized we use a simple CountDownLatch.
            Platform.runLater(() -> {
                
                if (webBrowser.getEngine().getLoadWorker().workDoneProperty().intValue() == 100) {
                    latch.countDown();
                    
                } else {    
                     webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {

                        @Override
                        public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                            if (newValue.intValue() == 100) {
                                latch.countDown();
                                webBrowser.getEngine().getLoadWorker().workDoneProperty().removeListener(this);
                            }
                        }
                    });  
                }
            });

            latch.await();

            while (!Thread.interrupted()) {
                Thread.sleep(30000);
                saveBackup();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(AutoBackupThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //This function saves all the needed information from the Nightly Log page and stores in in Backup_Log.txt. The fileds to get data from are hard coded and must be updated
    //if the page changes format.
    private void saveBackup() {
        Platform.runLater(() -> {
            //If some page other than the Nightly Log is loaded there is no need to save a backup.
            if (webBrowser.getEngine().getLocation().equals(main.Default[1])) {
                //As a backup incase something happend and the page isn't loaded corretly or has changed locations midway through.
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

                    //Don't save if the page is blank and hasn't been changed.
                    if (!Arrays.equals(backupEntries, emptyBackupStrings)) {
                        
                        if (!hasStored) {
                            storeLatestBackup();
                            hasStored = true;
                        }

                        System.out.println("Saving a backup of the Nightly log");

                        try (PrintWriter backupWriter = new PrintWriter(backupFile)) {

                            for (String backupEntry : backupEntries) {
                                //If the field was blank on the Nightly Log a null value would have be returned. We make that a blank again.
                                if (backupEntry == null) {
                                    backupWriter.print("" + newLine + customSplitString + newLine);
                                } else {
                                    //A \n represents a newline to Javascript and Java, but not necessarly to the operating system. Therefore we replace \n with the newLine constant.
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
    
    //This is used to move the current Backup_Log.txt to Backups_Old. It will also delete the oldest backups until there are only maxBackups.
    public static void storeLatestBackup() {
        if (backupFile.exists()) {

            System.out.println("Moving backup file to long term storage with new file name: " + "Backup_Log_" + +backupFile.lastModified() + ".txt");

            //By adjusting the name of the backup file to include the last time modified we can keep a more accurate log of how old the backup is.
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
