package app.timer;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javax.swing.JDialog;

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

        ChangeListener<Number> refreshListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            
            if (newValue.intValue() == 100) {
                System.out.println("A timer has seen a change of page");
                lastRefreshTime = Calendar.getInstance().getTimeInMillis();
            }

        };

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener(refreshListener);
        });

        long timeDifference;
        TimerWarningDialog timerDialog = null;
        lastRefreshTime = Calendar.getInstance().getTimeInMillis();
        
        while (!terminated) {
            
            timeDifference = Calendar.getInstance().getTimeInMillis() - lastRefreshTime;
            
            if (timeDifference > timerDuration * 35000) {
                
                if (timeDifference < timerDuration * 65000) {
                    
                    if (messageDialog != null && messageDialog.isVisible() && timerDialog != null) {
                        timerDialog.updateMessage("Your timer for " + webBrowser.getName() + " is at " + limitDoublePercision((double) timeDifference / (60000), 2) + " of " + timerDuration + " minutes");
                        messageDialog.pack();
                        
                    } else {
                        
                        timerDialog = new TimerWarningDialog(main.frame, false, "Your timer for " + webBrowser.getName() + " is at " + limitDoublePercision((double) timeDifference / (60000), 2) + " of " + timerDuration + " minutes", webBrowser);                       
                        messageDialog.setLocationRelativeTo(main.frame);                        
                        messageDialog.setVisible(true);
                        messageDialog.setAlwaysOnTop(true);
                        messageDialog.setAlwaysOnTop(false);
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

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener(refreshListener);
        });

    }

    public double limitDoublePercision(double d, int decimals) {
        int d1 = (int) (d * Math.pow(10, decimals));
        double d2 = d1 / Math.pow(10, decimals);
        return d2;
    }
}
