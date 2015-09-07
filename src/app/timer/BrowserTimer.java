package app.timer;

import app.browser.ExtendedWebBrowser;
import app.main;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

//This class is used to add a refresh timer to a web browser. This class creates a TimerTask and Timer to manage when to alert the user. 
//This class also adds a listener to the ExtendedWebBrowser that will reset the TimerTask if the page is reloaded or navigated to a different page.
public class BrowserTimer {

    int timerDuration;
    long lastRefreshTime = System.currentTimeMillis();

    private final ExtendedWebBrowser webBrowser;

    private TimerWarningDialog timerDialog = null;
    private ChangeListener<Number> refreshListener = null;

    private final Timer alertTimer = new Timer();
    private TimerAlertTask timerAlertTask = new TimerAlertTask();

    public BrowserTimer(int minutes, ExtendedWebBrowser webBrowser2) {
        timerDuration = minutes;
        webBrowser = webBrowser2;
    }

    //This function can be called from outside of the class to shutdown the Timer and to remove the listener from the web browser.
    public void terminate() {

        alertTimer.cancel();

        Platform.runLater(() -> {
            webBrowser.getEngine().getLoadWorker().workDoneProperty().removeListener(refreshListener);
        });
    }

    //This class started as a thread and didn't use Timer/TimerTask, so this function name is an artifact of that.
    //This function starts the TimerTask and adds the refresh listener.
    public void start() {

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

    //This subcalss defines the TimerTask that will alert the user when the page hasn't been refreshed for a user set amount of time.
    private class TimerAlertTask extends TimerTask {

        @Override
        public void run() {

            long timeDifference = System.currentTimeMillis() - lastRefreshTime;

            //If the dialog is visibile we won't open another we'll just update the current message in place.
            if (timerDialog != null && timerDialog.isVisible()) {
                timerDialog.updateMessage(timerDuration, timeDifference);
                timerDialog.pack();

            } else {
                timerDialog = new TimerWarningDialog(main.frame, false, timerDuration, timeDifference, webBrowser);
                timerDialog.pack();
                timerDialog.setLocationRelativeTo(main.frame);
                timerDialog.setVisible(true);
                timerDialog.setAlwaysOnTop(true);
                timerDialog.setAlwaysOnTop(false);
            }

            System.out.println("A notice about the timer for " + webBrowser.getName() + " has been given.");

            //If the the maximum duration of the timer has been passed we stop alerting the user.
            if (timeDifference >= timerDuration * 60000) {
                this.cancel();
            }
        }
    }

}
