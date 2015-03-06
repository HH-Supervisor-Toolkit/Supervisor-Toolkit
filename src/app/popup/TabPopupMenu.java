/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.popup;

import app.browser.ExtendedWebBrowser;
import app.main;
import app.monitor.StatusMonitorOptionsPanel;
import app.timer.BrowserTimerThread;
import app.watcher.WatcherSelectPanel;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author haywoosd
 */
public class TabPopupMenu extends JPopupMenu {

    JCheckBoxMenuItem timerItem;
    JCheckBoxMenuItem backupItem;
    JCheckBoxMenuItem monitorItem;
    JMenuItem watcherItem;
    JMenuItem refreshItem;
    ExtendedWebBrowser webBrowser;

    public TabPopupMenu(ExtendedWebBrowser webBrowser) {
        this.webBrowser = webBrowser;
        Platform.runLater(() -> {
            synchronized (this.webBrowser) {
                if (webBrowser.isTimerEnabled()) {
                    timerItem = new JCheckBoxMenuItem("Disable timer");
                    timerItem.setSelected(true);
                } else {
                    timerItem = new JCheckBoxMenuItem("Enable timer");
                }
                timerItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (webBrowser.isTimerEnabled()) {
                            main.ModifyOptions(true, "t:", null, webBrowser);
                            webBrowser.removeBrowserTimer();
                            System.out.println("A timer has been removed from " + webBrowser.getName());
                        } else {
                            int minutes = getTimerMinutes(webBrowser);
                            if (minutes == -1) {
                                return;
                            }
                            System.out.println("A " + minutes + " minute timer has been added to " + webBrowser.getName());
                            main.ModifyOptions(false, null, "t:" + minutes, webBrowser);
                            BrowserTimerThread timerListener = new BrowserTimerThread(minutes, webBrowser);
                            webBrowser.addBrowserTimer(timerListener);
                        }
                    }
                });
                add(timerItem);
                if (webBrowser.isBackupEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[1])) {
                    if (webBrowser.isBackupEnabled()) {
                        backupItem = new JCheckBoxMenuItem("Disable auto backup");
                        backupItem.setSelected(true);
                    } else {
                        backupItem = new JCheckBoxMenuItem("Enable auto backup");
                    }
                    backupItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (webBrowser.isBackupEnabled()) {
                                main.ModifyOptions(true, "B", null, webBrowser);
                                webBrowser.disableBackup();
                                System.out.println("Auto backup has been disabled for " + webBrowser.getName());
                            } else {
                                main.ModifyOptions(false, null, "B", webBrowser);
                                webBrowser.enableBackup();
                                System.out.println("Auto backup has been enabled for " + webBrowser.getName());
                            }
                        }
                    });
                    add(backupItem);
                }
                if (webBrowser.isMonitorEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[9])) {
                    monitorItem = new JCheckBoxMenuItem("Status Monitor");
                    if (webBrowser.isMonitorEnabled()) {
                        monitorItem.setSelected(true);
                    }
                    monitorItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JDialog monitorDialog = new JDialog(main.frame, "Status Monitor");
                            monitorDialog.add(new StatusMonitorOptionsPanel(webBrowser));
                            monitorDialog.pack();
                            monitorDialog.setLocationRelativeTo(main.frame);
                            monitorDialog.setResizable(false);
                            monitorDialog.setVisible(true);
                        }
                    });
                    add(monitorItem);
                }
                if (webBrowser.isWatcherEnabled() || webBrowser.getEngine().getLocation().equals(main.Default[9])) {
                    watcherItem = new JMenuItem("Watcher");
                    watcherItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JDialog watcherFrame = new JDialog(main.frame, "Watcher Select", ModalityType.APPLICATION_MODAL);
                            watcherFrame.add(new WatcherSelectPanel(webBrowser));
                            watcherFrame.pack();
                            watcherFrame.setLocationRelativeTo(main.frame);
                            watcherFrame.setVisible(true);
                        }
                    });
                    add(watcherItem);
                }
                refreshItem = new JMenuItem("Refresh");
                refreshItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Platform.runLater(() -> {
                            webBrowser.getEngine().reload();
                        });
                    }
                });
                add(refreshItem);

                webBrowser.notify();

            }
        });
        synchronized (webBrowser) {
            
            try {
                webBrowser.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(TabPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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
