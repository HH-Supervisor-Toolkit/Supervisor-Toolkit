package app.alarms;

import app.main;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

//This class extends TimerTask and will display the AlarmAlertDialog at the chosen time. It can have dismiss or snooze called to reschedule the task again.
//Rescheduling a TimerTask actually requires that a new one be created and scheduled.
public class AlarmTask extends TimerTask {

    String AlarmName;
    int entryNumber;
    
    //taskList and alarmTimer contain all schedules AlarmTasks. The taskList is needed to be able to get the individual AlarmTasks and cancel them.
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

    //Reschedules the Alarm for the same time the next day. The GregorianCalendar object is recommended over Date now, but TimerTasks can only be scheduled via Date objects.
    //This is the reason for such odd date handeling.
    public void dismissAlarm() {
        AlarmTask newAlarm = new AlarmTask(AlarmName, entryNumber);

        GregorianCalendar newTime = new GregorianCalendar();
        newTime.setTime(new Date(this.scheduledExecutionTime()));
        newTime.add(Calendar.DATE, 1);
        
        System.out.println(AlarmName + " will occur at " + newTime.getTime().toString());

        alarmTimer.schedule(newAlarm, newTime.getTime());
        taskList.set(entryNumber, newAlarm);
    }

    //Reschedules the alarm for the set minutes in the future.
    public void snoozeAlarm(int minutes) {
        AlarmTask newAlarm = new AlarmTask(AlarmName, entryNumber);

        GregorianCalendar newTime = new GregorianCalendar();
        newTime.setTime(new Date(this.scheduledExecutionTime()));
        newTime.add(Calendar.MINUTE, minutes);

        System.out.println(AlarmName + " will occur at " + newTime.getTime().toString());
        
        alarmTimer.schedule(newAlarm, newTime.getTime());
        taskList.set(entryNumber, newAlarm);
    }
    
    //This is used to simplify scheduling an AlarmTask.
    public static void schedule(AlarmTask task, Date time){
        alarmTimer.schedule(task, time);
        taskList.add(task);
    }
    
    //This is used to simplify unscheduling an AlarmTask.
    public static void unschedule(int entryNumber){
        AlarmTask.taskList.get(entryNumber).cancel();
        AlarmTask.taskList.remove(entryNumber);
    }
}
