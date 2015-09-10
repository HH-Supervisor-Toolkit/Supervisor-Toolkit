package app.alarms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.FocusManager;

//This class is the panel that displays all active alarms and allows for new alarms to be created.
public class AlarmsEditPanel extends javax.swing.JPanel {

    //entryNumber is used to keep track of how many alarms exist. It is passed to each AlarmEntryPanel created so they can return the value to AlarmsEditPanel if they are removed.
    private int entryNumber = 0;
    String alarmsFile = System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Alarms.txt";

    public AlarmsEditPanel() {
        initComponents();
        loadAlarms();
    }

    //Used to get all alarms from the alarms file and then schedule their respective AlarmTasks.
    private void loadAlarms() {

        try {
            if (!Files.exists(Paths.get(alarmsFile))) {

                System.out.println("Alarms file not found attemping to create default");
                Files.createFile(Paths.get(alarmsFile));

            } else {
                List<String> fileLines = Files.readAllLines(Paths.get(alarmsFile));

                for (int i = 0; i < fileLines.size() - 1; i += 4) {
                    int hour = Integer.parseInt(fileLines.get(i));
                    int minute = Integer.parseInt(fileLines.get(i + 1));
                    int period = Integer.parseInt(fileLines.get(i + 2));
                    String name = fileLines.get(i + 3);

                    System.out.println("Adding new alarm from file. " + name);
                    addEntry(hour, minute, period, name);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AlarmsEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AlarmsEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlarmsEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Save a newly created alarm to the alarms file. All alarms should be created with the hour, minute, period (AM/PM), and name each getting their own line.
    private void writeAlarm(int hour, int minute, int period, String name) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(alarmsFile, true))) {

            writer.println(Integer.toString(hour));
            writer.println(Integer.toString(minute));
            writer.println(Integer.toString(period));
            writer.println(name);

        } catch (IOException ex) {
            Logger.getLogger(AlarmsEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Adds an entry displaying the new alarm and schedules the associated AlarmTask.
    private void addEntry(int hour, int minute, int period, String name) {

        //A new instance of GregorianCalendar will initially be set for the time of its creation.
        GregorianCalendar alarmTime = new GregorianCalendar();

        alarmTime.set(Calendar.HOUR, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.AM_PM, period);

        //If the alarm's time has already happened today reschedule it for tomorrow
        if (alarmTime.before(GregorianCalendar.getInstance())) {
            alarmTime.add(Calendar.DATE, 1);
        }

        AlarmTask alarmTask = new AlarmTask(name, entryNumber);
        AlarmTask.schedule(alarmTask, alarmTime.getTime());

        AlarmsEntryPanel alarmEntry = new AlarmsEntryPanel(hour, minute, period, name, entryNumber);
        System.out.println("Adding new alarm entry: " + name);

        //Validate calls are required to correctly adjust the scroll sliders.
        entryContainerPanel.add(alarmEntry);
        entryContainerPanel.validate();
        entryContainerScrollPane.validate();

        entryNumber++;
    }

    //Used to remove any existing alarms and unschedule their respective AlarmTasks.
    public void removeEntry(int number) {

        System.out.println("Removing alarm entry: " + number);
        AlarmTask.unschedule(number);

        //We need to rewrite the AlarmsFile to not include the removed alarm.
        try {
            List<String> fileLines = Files.readAllLines(Paths.get(alarmsFile));

            try (PrintWriter writer = new PrintWriter(new FileWriter(alarmsFile))) {

                for (int i = 0; i < (fileLines.size() - 1) / 4; i++) {

                    if (i != number) {

                        writer.println(fileLines.get(i * 4));
                        writer.println(fileLines.get(i * 4 + 1));
                        writer.println(fileLines.get(i * 4 + 2));
                        writer.println(fileLines.get(i * 4 + 3));
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AlarmsEntryPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AlarmsEntryPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlarmsEntryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        entryContainerPanel.remove(number);

        //Each component after the removed one needs their number decremented by one.
        for (int i = number; i < entryContainerPanel.getComponentCount(); i++) {
            System.out.println("Setting entry " + (i + 1) + " to be " + i);
            ((AlarmsEntryPanel) entryContainerPanel.getComponent(i)).setEntryNumber(i);
        }

        entryNumber--;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        hourSelect = new javax.swing.JComboBox();
        minuteSelect = new javax.swing.JComboBox();
        nameSelect = new javax.swing.JTextField();
        periodSelect = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        entryContainerScrollPane = new javax.swing.JScrollPane();
        entryContainerPanel = new javax.swing.JPanel();

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new CustomFocusTraversalPolicy());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Alarms");

        hourSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        minuteSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));

        nameSelect.setForeground(java.awt.Color.lightGray);
        nameSelect.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nameSelect.setText("Alarm Name");
        nameSelect.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameSelectFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameSelectFocusLost(evt);
            }
        });

        periodSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AM", "PM" }));

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jLabel2.setText(":");

        entryContainerScrollPane.setBorder(null);

        entryContainerPanel.setLayout(new javax.swing.BoxLayout(entryContainerPanel, javax.swing.BoxLayout.PAGE_AXIS));
        entryContainerScrollPane.setViewportView(entryContainerPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(entryContainerScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(nameSelect, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hourSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minuteSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(periodSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(addButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hourSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minuteSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(periodSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton)
                    .addComponent(jLabel2)
                    .addComponent(nameSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(entryContainerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    //Called when the add button is clicked. Used to add a new alarm to AlarmEditPanel and schedule a new AlarmTask.
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        System.out.println("Manually adding an alarm entry");

        addEntry(hourSelect.getSelectedIndex() + 1, minuteSelect.getSelectedIndex(), periodSelect.getSelectedIndex(), nameSelect.getText());
        writeAlarm(hourSelect.getSelectedIndex() + 1, minuteSelect.getSelectedIndex(), periodSelect.getSelectedIndex(), nameSelect.getText());

        nameSelect.setText("Alarm Name");
        nameSelect.setForeground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_addButtonActionPerformed

    //Called when focus is gained by the name text box. Deletes the default text.
    private void nameSelectFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameSelectFocusGained
        if (nameSelect.getText().equals("Alarm Name") && nameSelect.getForeground() == Color.LIGHT_GRAY) {
            nameSelect.setText("");
            nameSelect.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_nameSelectFocusGained

    //Called when focus is lost by the name text box. Replaces the name text box with the default text if it is blank.
    private void nameSelectFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameSelectFocusLost
        if (nameSelect.getText().trim().equals("") && nameSelect.getForeground() == Color.BLACK) {
            nameSelect.setForeground(Color.LIGHT_GRAY);
            nameSelect.setText("Alarm Name");
                        
            if(evt.isTemporary()){
                hourSelect.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_nameSelectFocusLost

    //This class is used to control the order in which focus is passed within this panel. 
    //This is used to keep nameSelect from being first and then clearing the default text when the alarm tab is switched to.
    private class CustomFocusTraversalPolicy extends FocusTraversalPolicy {

        private final Component[] compList = {hourSelect, minuteSelect, periodSelect, addButton, nameSelect};

        //All overridden methods are simply implications of the FocusTraversalPolicy interface.
        @Override
        public Component getComponentAfter(Container cntnr, Component cmpnt) {
            return compList[(findComp(cmpnt) + 1) % compList.length];
        }

        @Override
        public Component getComponentBefore(Container cntnr, Component cmpnt) {
            return compList[(findComp(cmpnt) - 1) % compList.length];
        }

        @Override
        public Component getFirstComponent(Container cntnr) {
            return compList[0];
        }

        @Override
        public Component getLastComponent(Container cntnr) {
            return compList[compList.length - 1];
        }

        @Override
        public Component getDefaultComponent(Container cntnr) {
            return compList[0];
        }

        //A shortcut method to find index of a component within compList. If the provided component isn't within compList then 0 is returned.
        private int findComp(Component cmpnt) {

            for (int i = 0; i < compList.length; i++) {
                if (compList[i].equals(cmpnt)) {
                    return i;
                }
            }

            return 0;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel entryContainerPanel;
    private javax.swing.JScrollPane entryContainerScrollPane;
    private javax.swing.JComboBox hourSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox minuteSelect;
    private javax.swing.JTextField nameSelect;
    private javax.swing.JComboBox periodSelect;
    // End of variables declaration//GEN-END:variables
}
