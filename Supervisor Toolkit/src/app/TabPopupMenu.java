/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

/**
 *
 * @author haywoosd
 */
public class TabPopupMenu extends JPopupMenu {

    JCheckBoxMenuItem timerItem;
    JCheckBoxMenuItem backupItem;
    JMenuItem refreshItem;
    ExtendedWebBrowser webBrowser;
    boolean hasTimer = false;
    BrowserTimerAdapter timerListener;

    public TabPopupMenu(ExtendedWebBrowser webBrowser2) {
        webBrowser = webBrowser2;
        timerItem = new JCheckBoxMenuItem("Add timer");
        if (webBrowser.isTimerEnabled()) {
            timerItem.setSelected(true);
        } else {
            timerItem.setSelected(false);
        }
        timerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (webBrowser.isTimerEnabled()) {
                    ModifyOptions(true, "t", null);
                    webBrowser.removeBrowserTimer();
                    System.out.println("A timer has been removed from " + webBrowser.getPageTitle());
                } else {
                    int minutes = GetTimerMinutes(webBrowser);
                    if (minutes == -1) {
                        return;
                    }
                    System.out.println("A " + minutes + " minute timer has been added to " + webBrowser.getPageTitle());
                    ModifyOptions(false, null, "-t:" + minutes);
                    timerListener = new BrowserTimerAdapter(minutes, webBrowser);
                    webBrowser.addBrowserTimer(timerListener);
                }
            }
        });
        backupItem = new JCheckBoxMenuItem("Enable auto backup");
        backupItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                webBrowser.reloadPage();
            }
        });
        add(timerItem);
        add(backupItem);
        add(refreshItem);
    }

    private int GetTimerMinutes(JWebBrowser webBrowser) {
        int minutes;
        String timerDurationStr = JOptionPane.showInputDialog(webBrowser, "How many minutes should the timer be for?", "Timer Duration", JOptionPane.PLAIN_MESSAGE);
        if (timerDurationStr == null) {
            return -1;
        }
        try {
            minutes = Integer.parseInt(timerDurationStr);
            if (minutes < 1) {
                minutes = GetTimerMinutes(webBrowser);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for timer minutes. Prompting for new input");
            minutes = GetTimerMinutes(webBrowser);
        }
        return minutes;
    }

    private void ModifyOptions(boolean removing, String prefix, String fullOption) {
        int index = 0;
        JTabbedPane tabbedPane = (JTabbedPane) webBrowser.getParent();
        int componentCount = tabbedPane.getTabCount();
        for (int i = 0; i < componentCount; i++) {
            if (tabbedPane.getComponentAt(i).equals(webBrowser)) {
                index = i;
                break;
            }
        }
        String[] optionsText = main.optionsEdit.getOptionsText();
        if (removing) {
            System.out.println("Removing timer option switch from tab " + index);
            optionsText[index * 2] = optionsText[index * 2].replaceAll("-" + prefix + "[^-]*", "");
        } else {
            System.out.println("Adding timer option switch to tab " + index);
            optionsText[index * 2] = optionsText[index * 2].trim() + " " + fullOption;
        }
        main.optionsEdit.setOptionsText(optionsText);
        main.writeOptions(main.file, optionsText);
    }
}
