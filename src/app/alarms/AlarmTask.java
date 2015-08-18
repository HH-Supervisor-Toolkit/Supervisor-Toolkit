package app.alarms;

import app.main;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmTask extends TimerTask {

    String AlarmName;
    int entryNumber;
    
    static ArrayList<AlarmTask> taskList = new ArrayList<>();
    static Timer alarmTimer = new Timer();

    AlarmTask(String name, int number) {
        AlarmName = name;
        entryNumber = number;
    }

    @Override
    public void run() {

        AlarmAlertDialog messageDialog = new AlarmAlertDialog(main.frame, false, AlarmName, this);

        messageDialog.setLocationRelativeTo(main.frame);
        messageDialog.setVisible(true);
        messageDialog.setAlwaysOnTop(true);
        messageDialog.setAlwaysOnTop(false);
    }

    public void dismissAlarm() {
        AlarmTask newAlarm = new AlarmTask(AlarmName, entryNumber);

        GregorianCalendar newTime = new GregorianCalendar();
        newTime.setTime(new Date(this.scheduledExecutionTime()));
        newTime.add(Calendar.DATE, 1);
        
        System.out.println(AlarmName + " will occur at " + newTime.getTime().toString());

        alarmTimer.schedule(newAlarm, newTime.getTime());
        taskList.set(entryNumber, newAlarm);
    }

    public void snoozeAlarm(int minutes) {
        AlarmTask newAlarm = new AlarmTask(AlarmName, entryNumber);

        GregorianCalendar newTime = new GregorianCalendar();
        newTime.setTime(new Date(this.scheduledExecutionTime()));
        newTime.add(Calendar.MINUTE, minutes);

        System.out.println(AlarmName + " will occur at " + newTime.getTime().toString());
        
        alarmTimer.schedule(newAlarm, newTime.getTime());
        taskList.set(entryNumber, newAlarm);
    }
    
    public static void schedule(AlarmTask task, Date time){
        alarmTimer.schedule(task, time);
        taskList.add(task);
    }
    
    public static void unschedule(int entryNumber){
        AlarmTask.taskList.get(entryNumber).cancel();
        AlarmTask.taskList.remove(entryNumber);
    }
}
