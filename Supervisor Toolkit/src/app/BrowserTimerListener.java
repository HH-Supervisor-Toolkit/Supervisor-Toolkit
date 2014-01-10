/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserListener;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author haywoosd
 */
public class BrowserTimerListener extends WebBrowserAdapter {

    int timerDuration;
    long lastRefreshTime;
    JWebBrowser webBrowser;
    TimerCounterThread timerThread;
    boolean terminated = false;

    public BrowserTimerListener(int minutes, JWebBrowser webBrowser2) {
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
                if (timeDifference > timerDuration * 40000 && timeNoticeDifference > timerDuration * 10000) {
                    lastNoticeTime = Calendar.getInstance().getTimeInMillis();
                    new TimerMessageThread(timeDifference).start();
                    System.out.println("A notice about the timer for " + webBrowser.getPageTitle() + " has been given.");
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BrowserTimerListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class TimerMessageThread extends Thread {

        long timeDifference;

        TimerMessageThread(long timeDifference2) {
            timeDifference = timeDifference2;
        }

        @Override
        public void run() {
            JOptionPane.showMessageDialog(webBrowser, "Your timer is at " + (double) timeDifference / (60000) + " of " + timerDuration + " minutes", "Timer Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
