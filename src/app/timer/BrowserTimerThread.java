package app.timer;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class BrowserTimerThread extends Thread {

    int timerDuration;
    long lastRefreshTime = System.currentTimeMillis();

    ExtendedWebBrowser webBrowser;
    TimerWarningDialog timerDialog = null;
    Timer alertTimer = new Timer();
    TimerAlertTask timerAlertTask = new TimerAlertTask();
    ChangeListener<Number> refreshListener = null;

    public BrowserTimerThread(int minutes, ExtendedWebBrowser webBrowser2) {
        timerDuration = minutes;
        webBrowser = webBrowser2;
    }

    public void terminate() {

        alertTimer.cancel();

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().removeListener(refreshListener);
        });
    }

    @Override
    public void run() {

        refreshListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

            if (newValue.intValue() == 100) {
                System.out.println("A timer has seen a change of page");
                lastRefreshTime = System.currentTimeMillis();

                timerAlertTask.cancel();
                timerAlertTask = new TimerAlertTask();
                alertTimer.scheduleAtFixedRate(timerAlertTask, timerDuration * 40000, timerDuration * 10000);
            }

        };

        alertTimer.scheduleAtFixedRate(timerAlertTask, timerDuration * 40000, timerDuration * 10000);

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().addListener(refreshListener);
        });
    }

    public double limitDoublePercision(double d, int decimals) {
        int d1 = (int) (d * Math.pow(10, decimals));
        return (double) d1 / Math.pow(10, decimals);
    }

    private class TimerAlertTask extends TimerTask {

        @Override
        public void run() {

            long timeDifference = System.currentTimeMillis() - lastRefreshTime;

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

            if (timeDifference >= timerDuration * 60000) {
                this.cancel();
            }
        }
    }

}
