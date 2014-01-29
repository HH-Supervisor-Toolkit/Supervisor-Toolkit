/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 *
 * @author haywoosd
 */
public class BrowserTimerAdapter extends WebBrowserAdapter {

    int timerDuration;
    long lastRefreshTime;
    JWebBrowser webBrowser;
    TimerCounterThread timerThread;
    boolean terminated = false;

    public BrowserTimerAdapter(int minutes, JWebBrowser webBrowser2) {
        timerDuration = minutes;
        webBrowser = webBrowser2;
        lastRefreshTime = Calendar.getInstance().getTimeInMillis();
        timerThread = new TimerCounterThread();
        timerThread.start();
    }

    public void terminate() {
        terminated = true;
    }

    @Override
    public void titleChanged(WebBrowserEvent wbe) {
        lastRefreshTime = Calendar.getInstance().getTimeInMillis();
    }

    private class TimerCounterThread extends Thread {

        long lastNoticeTime;
        JDialog messageDialog = null;
        TimerWarningPanel timerPanel;

        TimerCounterThread() {
            lastNoticeTime = Calendar.getInstance().getTimeInMillis();
        }

        @Override
        public void run() {
            long timeDifference;
            long timeNoticeDifference;
            while (!terminated) {
                timeDifference = Calendar.getInstance().getTimeInMillis() - lastRefreshTime;
                timeNoticeDifference = Calendar.getInstance().getTimeInMillis() - lastNoticeTime;
                if (timeDifference > timerDuration * 40000 && timeNoticeDifference > timerDuration * 10000 && timeDifference < timerDuration * 70000) {
                    lastNoticeTime = Calendar.getInstance().getTimeInMillis();
                    if (messageDialog != null && messageDialog.isVisible()) {
                        timerPanel.updateMessage("Your timer for " + webBrowser.getName() + " is at " + (double) timeDifference / (60000) + " of " + timerDuration + " minutes");
                        messageDialog.setAlwaysOnTop(true);
                        messageDialog.setAlwaysOnTop(false);
                    } else {
                        timerPanel = new TimerWarningPanel("Your timer for " + webBrowser.getName() + " is at " + (double) timeDifference / (60000) + " of " + timerDuration + " minutes");
                        messageDialog = new JDialog();
                        messageDialog.add(timerPanel, BorderLayout.CENTER);
                        messageDialog.pack();
                        messageDialog.setTitle("Timer Warning");
                        messageDialog.setLocationRelativeTo(webBrowser.getParent());
                        messageDialog.setResizable(false);
                        messageDialog.setVisible(true);
                        messageDialog.setAlwaysOnTop(true);
                        messageDialog.setAlwaysOnTop(false);
                        main.frame.addWindowFocusListener(new WindowFocusListener() {
                            @Override
                            public void windowGainedFocus(WindowEvent e) {
                                messageDialog.setAlwaysOnTop(true);
                                messageDialog.setAlwaysOnTop(false);
                            }

                            @Override
                            public void windowLostFocus(WindowEvent e) {
                                messageDialog.toBack();
                            }
                        });
                    }
                    System.out.println("A notice about the timer for " + webBrowser.getName() + " has been given.");
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BrowserTimerAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }
}
