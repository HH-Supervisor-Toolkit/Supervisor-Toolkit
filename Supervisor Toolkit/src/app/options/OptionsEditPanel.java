/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.options;

import app.main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Sloan
 */
public class OptionsEditPanel extends javax.swing.JPanel {

    boolean broken;

    private void FillEditPane() {
        try {
            BufferedReader bufRead = new BufferedReader(new FileReader(main.file));
            System.out.println("Attempting to fill options pane");
            String workingLine;
            workingLine = bufRead.readLine();
            optionsEditorArea.setText(null);
            while (workingLine != null) {
                optionsEditorArea.setText(optionsEditorArea.getText() + workingLine + System.getProperty("line.separator"));
                workingLine = bufRead.readLine();
            }
            System.out.println("Options pane sucessfully filled");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OptionsEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OptionsEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] getOptionsText() {
        return optionsEditorArea.getText().split(System.getProperty("line.separator"));
    }

    public void setOptionsText(String[] optionsText) {
        optionsEditorArea.setText("");
        for (int i = 0; i < optionsText.length; i++) {
            optionsEditorArea.setText(optionsEditorArea.getText() + optionsText[i] + System.getProperty("line.separator"));
        }
    }

    /**
     * Creates new form OptionsEdit
     */
    public OptionsEditPanel(boolean broken2) {
        initComponents();
        broken = broken2;
        FillEditPane();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        optionsEditorArea = new javax.swing.JTextArea();
        resetButton = new javax.swing.JButton();

        applyButton.setText("Apply");
        applyButton.setMaximumSize(new java.awt.Dimension(65, 23));
        applyButton.setMinimumSize(new java.awt.Dimension(65, 23));
        applyButton.setPreferredSize(new java.awt.Dimension(65, 23));
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Pages to Load");

        optionsEditorArea.setColumns(20);
        optionsEditorArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        optionsEditorArea.setRows(5);
        jScrollPane2.setViewportView(optionsEditorArea);

        resetButton.setText("Reset");
        resetButton.setMaximumSize(new java.awt.Dimension(65, 23));
        resetButton.setMinimumSize(new java.awt.Dimension(65, 23));
        resetButton.setPreferredSize(new java.awt.Dimension(65, 23));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(404, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap(405, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(applyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(applyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addGap(18, 18, 18)
                        .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (!broken) {

            int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to revert any changes?", "Confirm cancel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                FillEditPane();
            }
        } else {
            int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want cancel. This will close the program?", "Confirm cancel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want save these changes?", "Confirm apply", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            main.writeOptions(main.file, optionsEditorArea.getText().split(System.getProperty("line.separator")));
            if (broken) {
                SwingUtilities.getRoot(this).setVisible(false);
            }
        }
    }//GEN-LAST:event_applyButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset to default options?", "Confirm reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            main.writeOptions(main.file, main.Default);
            FillEditPane();
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea optionsEditorArea;
    private javax.swing.JButton resetButton;
    // End of variables declaration//GEN-END:variables
}
