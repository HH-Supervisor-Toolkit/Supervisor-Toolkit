/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.Component;
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
public class RightClickMenu extends JPopupMenu {

    JCheckBoxMenuItem timerItem;
    JMenuItem refreshItem;
    JWebBrowser webBrowser;

    public RightClickMenu(final JWebBrowser webBrowser2) {
        webBrowser = webBrowser2;
        timerItem = new JCheckBoxMenuItem("Add Timer to Tab");
        if (webBrowser.getWebBrowserListeners().length != 0) {
            timerItem.setState(true);
        } else {
            timerItem.setState(false);
        }
        timerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (webBrowser.getWebBrowserListeners().length != 0) {
                    ModifyTimerOptions(true, 0);
                    webBrowser.removeWebBrowserListener(webBrowser.getWebBrowserListeners()[0]);
                    System.out.println("A timer has been removed from " + webBrowser.getPageTitle());
                } else {
                    int minutes = GetTimerMinutes(webBrowser);
                    System.out.println("A " + minutes + " minute timer has been added to " + webBrowser.getPageTitle());
                    ModifyTimerOptions(false, minutes);
                    webBrowser.addWebBrowserListener(new BrowserTimerListener(minutes, webBrowser));
                }
            }
        });
        refreshItem = new JMenuItem("Refresh Tab");
        refreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                webBrowser.reloadPage();
            }
        });
        add(timerItem);
        add(refreshItem);
    }

    private int GetTimerMinutes(JWebBrowser webBrowser) {
        int minutes;
        String timerdurationstr = JOptionPane.showInputDialog(webBrowser, "How many minutes should the timer be for?", "Timer Duration", JOptionPane.PLAIN_MESSAGE);
        try {
            minutes = Integer.parseInt(timerdurationstr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for timer minutes. Prompting for new input");
            minutes = GetTimerMinutes(webBrowser);
        }
        return minutes;
    }

    private void ModifyTimerOptions(boolean removing, int minutes) {
        int index = 0;
        Component[] components = ((JTabbedPane) webBrowser.getParent()).getComponents();
        int componentCount = components.length;
        for (int i = 0; i < componentCount; i++) {
            if (components[i].equals(webBrowser)) {
                index = i;
                break;
            }
        }
        System.out.println("Modifying timer options for tab " + index);
        String[] optionsText = main.optionsEdit.getOptionsText();
        if (removing) {
            System.out.println("Removing timer option switch from tab " + index);
            optionsText[index * 2] = optionsText[index * 2].replaceAll("-t[^-]*", "");
        } else {
            System.out.println("Adding timer option switch to tab" + index);
            optionsText[index * 2] = optionsText[index * 2].trim() + " -t:" + minutes;
        }
        main.optionsEdit.setOptionsText(optionsText);
        main.writeOptions(main.file, optionsText);
    }
}
