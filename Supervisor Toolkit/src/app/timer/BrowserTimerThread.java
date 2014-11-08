/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.timer;

import app.browser.ExtendedWebBrowser;
import app.main;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 *
 * @author haywoosd
 */
public class BrowserTimerThread extends Thread {

    int timerDuration;
    ExtendedWebBrowser webBrowser;
    JDialog messageDialog = null;
    boolean terminated = false;
    long lastRefreshTime;

    public BrowserTimerThread(int minutes, ExtendedWebBrowser webBrowser2) {
        timerDuration = minutes;
        webBrowser = webBrowser2;
    }

    public void terminate() {
        terminated = true;
    }

    @Override
    public void run() {
        WebBrowserAdapter refreshAdapter = new WebBrowserAdapter() {
            @Override
            public void titleChanged(WebBrowserEvent wbe) {
                lastRefreshTime = Calendar.getInstance().getTimeInMillis();
            }
        };
        webBrowser.addWebBrowserListener(refreshAdapter);
        long timeDifference;
        long timeNoticeDifference;
        TimerWarningPanel timerPanel = null;
        lastRefreshTime = Calendar.getInstance().getTimeInMillis();
        while (!terminated) {
            timeDifference = Calendar.getInstance().getTimeInMillis() - lastRefreshTime;
            if (timeDifference > timerDuration * 35000) {
                if (timeDifference < timerDuration * 65000) {
                    if (messageDialog != null && messageDialog.isVisible() && timerPanel != null) {
                        timerPanel.updateMessage("Your timer for " + webBrowser.getName() + " is at " + limitDoublePercision((double) timeDifference / (60000), 2) + " of " + timerDuration + " minutes");
                        messageDialog.pack();
                        messageDialog.setAlwaysOnTop(true);
                        messageDialog.setAlwaysOnTop(false);
                    } else {
                        messageDialog = new JDialog(main.frame,true);
                        timerPanel = new TimerWarningPanel("Your timer for " + webBrowser.getName() + " is at " + limitDoublePercision((double) timeDifference / (60000), 2) + " of " + timerDuration + " minutes", webBrowser);
                        messageDialog.add(timerPanel, BorderLayout.CENTER);
                        messageDialog.setTitle("Timer Warning");
                        messageDialog.setLocationRelativeTo(webBrowser.getParent());
                        messageDialog.pack();
                        messageDialog.setResizable(false);
                        messageDialog.setVisible(true);
                    }
                    System.out.println("A notice about the timer for " + webBrowser.getName() + " has been given.");
                }
            }
            try {
                Thread.sleep(timerDuration * 10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BrowserTimerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        webBrowser.removeWebBrowserListener(refreshAdapter);
    }

    public double limitDoublePercision(double d, int decimals) {
        int d1 = (int) (d * Math.pow(10, decimals));
        double d2 = d1 / Math.pow(10, decimals);
        return d2;
    }
}
