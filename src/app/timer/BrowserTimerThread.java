package app.timer;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class BrowserTimerThread extends Thread {

    int timerDuration;
    ExtendedWebBrowser webBrowser;
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
                lastRefreshTime = System.currentTimeMillis();
            }

        };

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener(refreshListener);
        });

        long timeDifference;
        TimerWarningDialog timerDialog = null;
        lastRefreshTime = System.currentTimeMillis();

        while (!terminated) {

            timeDifference = System.currentTimeMillis() - lastRefreshTime;

            if (timeDifference > timerDuration * 35000 && timeDifference < timerDuration * 65000) {

                if (timerDialog != null && timerDialog.isVisible()) {
                    timerDialog.updateMessage("Your timer for " + webBrowser.getName() + " is at " + limitDoublePercision((double) timeDifference / (60000), 2) + " of " + timerDuration + " minutes");
                    timerDialog.pack();

                } else {

                    timerDialog = new TimerWarningDialog(main.frame, false, "Your timer for " + webBrowser.getName() + " is at " + limitDoublePercision((double) timeDifference / (60000), 2) + " of " + timerDuration + " minutes", webBrowser);
                    timerDialog.setLocationRelativeTo(main.frame);
                    timerDialog.setVisible(true);
                    timerDialog.setAlwaysOnTop(true);
                    timerDialog.setAlwaysOnTop(false);
                }
                
                System.out.println("A notice about the timer for " + webBrowser.getName() + " has been given.");
            }

            try {
                Thread.sleep(timerDuration * 10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BrowserTimerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().removeListener(refreshListener);
        });

    }

    public double limitDoublePercision(double d, int decimals) {
        int d1 = (int) (d * Math.pow(10, decimals));
        double d2 = d1 / Math.pow(10, decimals);
        return d2;
    }
}
