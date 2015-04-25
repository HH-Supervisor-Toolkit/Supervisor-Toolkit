package app.popup;

import app.backup.AutoBackupDialog;
import app.browser.ExtendedWebBrowser;
import app.main;
import app.monitor.StatusMonitorOptionsDialog;
import app.timer.BrowserTimerThread;
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

public class TabPopupMenu extends JPopupMenu {

    JCheckBoxMenuItem timerItem;
    JCheckBoxMenuItem backupItem;
    JCheckBoxMenuItem monitorItem;
    JMenuItem watcherItem;
    JMenuItem refreshItem;
    ExtendedWebBrowser webBrowser;

    public TabPopupMenu(ExtendedWebBrowser webBrowser) {
        this.webBrowser = webBrowser;

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {

            if (webBrowser.isTimerEnabled()) {
                timerItem = new JCheckBoxMenuItem("Disable Timer");
                timerItem.setSelected(true);
            } else {
                timerItem = new JCheckBoxMenuItem("Enable Timer");
            }

            timerItem.addActionListener((ActionEvent e) -> {
                
                if (webBrowser.isTimerEnabled()) {
                    main.ModifyOptions(true, "t", null, webBrowser);
                    webBrowser.removeBrowserTimer();
                    System.out.println("A timer has been removed from " + webBrowser.getName());
                } else {
                    int minutes = getTimerMinutes(webBrowser);
                    
                    if (minutes == -1) {
                        return;
                    }
                    
                    System.out.println("A " + minutes + " minute timer has been added to " + webBrowser.getName());
                    main.ModifyOptions(false, "t", "t:" + minutes, webBrowser);
                    BrowserTimerThread timerListener = new BrowserTimerThread(minutes, webBrowser);
                    webBrowser.addBrowserTimer(timerListener);
                }

            });

            add(timerItem);

            if (webBrowser.isBackupEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[1])) {

                backupItem = new JCheckBoxMenuItem("Backups");

                if (webBrowser.isBackupEnabled()) {
                    backupItem.setSelected(true);
                }

                backupItem.addActionListener((ActionEvent e) -> {
                    AutoBackupDialog backupDialog = new AutoBackupDialog(main.frame, false, webBrowser);
                    backupDialog.setLocationRelativeTo(main.frame);
                    backupDialog.setVisible(true);
                });

                add(backupItem);
            }

            if (webBrowser.isMonitorEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[9])) {
                monitorItem = new JCheckBoxMenuItem("Status Monitor");

                if (webBrowser.isMonitorEnabled()) {
                    monitorItem.setSelected(true);
                }

                monitorItem.addActionListener((ActionEvent e) -> {
                    StatusMonitorOptionsDialog monitorDialog = new StatusMonitorOptionsDialog(main.frame, false, webBrowser);
                    monitorDialog.setLocationRelativeTo(main.frame);
                    monitorDialog.setVisible(true);
                });

                add(monitorItem);
            }

            if (webBrowser.isWatcherEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[9])) {
                watcherItem = new JMenuItem("Watcher");

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

            latch.countDown();

        });

        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(TabPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

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
