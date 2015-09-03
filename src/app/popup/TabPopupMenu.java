package app.popup;

import app.backup.AutoBackupDialog;
import app.browser.ExtendedWebBrowser;
import app.main;
import app.monitor.StatusMonitorOptionsDialog;
import app.timer.BrowserTimer;
import app.watcher.WatcherSelectDialog;
import java.awt.event.ActionEvent;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

//This class is the popup menu that displays when the user right clicks a tab. The menu's contents vary depending on what page the tab is displaying or what options are already enabled.
public class TabPopupMenu extends JPopupMenu {

    JCheckBoxMenuItem timerItem;
    JCheckBoxMenuItem backupItem;
    JCheckBoxMenuItem monitorItem;
    JCheckBoxMenuItem watcherItem;

    JMenuItem refreshItem;

    ExtendedWebBrowser webBrowser;

    public TabPopupMenu(ExtendedWebBrowser webBrowser) {
        this.webBrowser = webBrowser;

        CountDownLatch latch = new CountDownLatch(1);

        //Because requesting the location of the web browser must be done on the JavaFX thread we do most of our work through Platform.runLater and use the CountDownLatch to synchronize.
        Platform.runLater(() -> {

            //The convention is to show a checkmark if the feature is enabled, but the text will tell what action clicking will cause.
            //Some features aren't enabled/disabled so they don't have checkboxes.
            if (webBrowser.isTimerEnabled()) {
                timerItem = new JCheckBoxMenuItem("Disable Timer", true);
            } else {
                timerItem = new JCheckBoxMenuItem("Enable Timer", false);
            }

            timerItem.addActionListener((ActionEvent e) -> {

                if (webBrowser.isTimerEnabled()) {
                    main.ModifyOptions(true, "t", webBrowser);
                    webBrowser.removeBrowserTimer();

                    System.out.println("A timer has been removed from " + webBrowser.getName());
                } else {
                    int minutes = getTimerMinutes(webBrowser);

                    //getTimerMinutes returns -1 if the user chose to cancel the dialog.
                    if (minutes == -1) {
                        return;
                    }

                    System.out.println("A " + minutes + " minute timer has been added to " + webBrowser.getName());
                    main.ModifyOptions(false, "t:" + minutes, webBrowser);
                    
                    BrowserTimer timerListener = new BrowserTimer(minutes, webBrowser);
                    webBrowser.addBrowserTimer(timerListener);
                }

            });

            add(timerItem);

            //If a feature is already enabled the user should be able to disable it even if the web browser is on the relavent page. Therefore we an or condition.
            //Another convention is to use the Default site list as a reference when checking if the browser is on the correct page.
            if (webBrowser.isBackupEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[1])) {

                backupItem = new JCheckBoxMenuItem("Backups", webBrowser.isBackupEnabled());

                backupItem.addActionListener((ActionEvent e) -> {
                    AutoBackupDialog backupDialog = new AutoBackupDialog(main.frame, false, webBrowser);
                    backupDialog.setLocationRelativeTo(main.frame);
                    backupDialog.setVisible(true);
                });

                add(backupItem);
            }

            if (webBrowser.isMonitorEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[9])) {
                monitorItem = new JCheckBoxMenuItem("Status Monitor", webBrowser.isMonitorEnabled());

                monitorItem.addActionListener((ActionEvent e) -> {
                    StatusMonitorOptionsDialog monitorDialog = new StatusMonitorOptionsDialog(main.frame, false, webBrowser);
                    monitorDialog.setLocationRelativeTo(main.frame);
                    monitorDialog.setVisible(true);
                });

                add(monitorItem);
            }

            if (webBrowser.isWatcherEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[9])) {
                watcherItem = new JCheckBoxMenuItem("Watcher", webBrowser.isWatcherEnabled());

                watcherItem.addActionListener((ActionEvent e) -> {
                    WatcherSelectDialog watcherDialog = new WatcherSelectDialog(main.frame, true, webBrowser);
                    watcherDialog.setLocationRelativeTo(main.frame);
                    watcherDialog.setVisible(true);
                });

                add(watcherItem);
            }

            refreshItem = new JMenuItem("Refresh");

            refreshItem.addActionListener((ActionEvent e) -> {
                Platform.runLater(() -> {
                    webBrowser.getEngine().reload();
                });
            });

            add(refreshItem);

            //Now that everything is created we can allow the constructor to return.
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(TabPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //This function is simply a shortcut to ask the user how long the timer should last. It returns the time in minutes or -1 if the dialog was canceled.
    private int getTimerMinutes(ExtendedWebBrowser webBrowser) {

        int minutes;

        String timerDurationStr = JOptionPane.showInputDialog(webBrowser, "How many minutes should the timer be for?", "Timer Duration", JOptionPane.PLAIN_MESSAGE);

        if (timerDurationStr == null) {
            return -1;
        }

        try {
            minutes = Integer.parseInt(timerDurationStr);
            if (minutes < 1) {
                minutes = getTimerMinutes(webBrowser);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for timer minutes. Prompting for new input");
            minutes = getTimerMinutes(webBrowser);
        }
        
        return minutes;
    }

}
