/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.browser;

import app.main;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

/**
 *
 * @author haywoosd
 */
public class JWebBrowserPanel extends javax.swing.JPanel {

    private final String[] fileSuffixes = {"doc", "docx", "rft", "txt", "pps",
        "ppt", "pptx", "png", "bmp", "tif", "jpg", "xls", "xlsx", "7z", "rar", "zip"};

    private final JFXPanel fxWebViewPanel = new JFXPanel();
    private WebEngine engine;

    /**
     * Creates new form JWebBrowserPanel
     */
    public JWebBrowserPanel() {
        synchronized (fxWebViewPanel) {

            createScene();
            initComponents();
            webViewContainer.add(fxWebViewPanel);

            try {

                fxWebViewPanel.wait();

            } catch (InterruptedException ex) {

                Logger.getLogger(JWebBrowserPanel.class.getName()).log(Level.SEVERE, null, ex);

            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        webViewContainer = new javax.swing.JPanel();
        printButton = new javax.swing.JButton();

        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/img/backIcon.png"))); // NOI18N
        backButton.setBorder(null);
        backButton.setContentAreaFilled(false);
        backButton.setMaximumSize(new java.awt.Dimension(20, 20));
        backButton.setMinimumSize(new java.awt.Dimension(20, 20));
        backButton.setPreferredSize(new java.awt.Dimension(20, 20));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/img/forwardIcon.png"))); // NOI18N
        forwardButton.setBorder(null);
        forwardButton.setContentAreaFilled(false);
        forwardButton.setMaximumSize(new java.awt.Dimension(20, 20));
        forwardButton.setMinimumSize(new java.awt.Dimension(20, 20));
        forwardButton.setPreferredSize(new java.awt.Dimension(20, 20));
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });

        refreshButton.setForeground(new java.awt.Color(240, 240, 240));
        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/img/refreshIcon.png"))); // NOI18N
        refreshButton.setBorder(null);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setMaximumSize(new java.awt.Dimension(20, 20));
        refreshButton.setMinimumSize(new java.awt.Dimension(20, 20));
        refreshButton.setPreferredSize(new java.awt.Dimension(20, 20));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        webViewContainer.setLayout(new java.awt.BorderLayout());

        printButton.setForeground(new java.awt.Color(240, 240, 240));
        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/app/img/printIcon.png"))); // NOI18N
        printButton.setBorder(null);
        printButton.setContentAreaFilled(false);
        printButton.setMaximumSize(new java.awt.Dimension(20, 20));
        printButton.setMinimumSize(new java.awt.Dimension(20, 20));
        printButton.setPreferredSize(new java.awt.Dimension(20, 20));
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(webViewContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(forwardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 610, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(forwardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webViewContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardButtonActionPerformed
        Platform.runLater(() -> {
            engine.executeScript("history.forward()");
        });
    }//GEN-LAST:event_forwardButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        Platform.runLater(() -> {
            engine.reload();
        });
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Platform.runLater(() -> {
            engine.executeScript("history.back()");
        });
    }//GEN-LAST:event_backButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job.showPrintDialog(null)) {

            System.out.println("Sending pring job to " + job.getPrinter().getName());
            engine.print(job);
            job.endJob();

        }

    }//GEN-LAST:event_printButtonActionPerformed

    private void createScene() {
        
        Platform.runLater(() -> {
            synchronized (fxWebViewPanel) {
                WebView view = new WebView();
                engine = view.getEngine();

                engine.setCreatePopupHandler((PopupFeatures param) -> {

                    Stage stage = new Stage();
                    WebView popupView = new WebView();

                    popupView.getEngine().locationProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                        if (newValue.startsWith("conf:sip")) {
                            stage.close();
                        }
                        if (contains(fileSuffixes, newValue.substring(newValue.lastIndexOf(".") + 1, newValue.length()))) {
                            System.out.println("The browser has detected a file to download");

                            downloadFile(newValue);
                            
                            Platform.runLater(() -> {
                                popupView.getEngine().getLoadWorker().cancel();
                            });

                            stage.close();
                        }
                    });

                    stage.setScene(new Scene(popupView));
                    stage.setTitle("Supervisor Toolkit Popup");
                    stage.getIcons().add(SwingFXUtils.toFXImage(main.icon, null));
                    stage.show();

                    return popupView.getEngine();

                });

                fxWebViewPanel.setScene(new Scene(view));
                fxWebViewPanel.notify();
            }
        });
    }

    public void loadURL(final String url) {
        Platform.runLater(() -> {
            String tmp = toURL(url);

            if (tmp == null) {
                tmp = toURL("http://" + url);
            }

            engine.load(tmp);
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }

    private void downloadFile(String url) {
        SwingUtilities.invokeLater(() -> {

            JFileChooser chooser = new JFileChooser();
            
            chooser.setSelectedFile(new File(url.substring(url.lastIndexOf("/"))));
            int dialogResult = chooser.showSaveDialog(main.frame);
            
            if(dialogResult == JFileChooser.APPROVE_OPTION){
                BufferedInputStream inStream;
                FileOutputStream outStream;
                
            }
            
        });
    }

    private boolean contains(String[] suffixList, String suffix) {

        for (String suffixItem : suffixList) {
            if (suffixItem.equals(suffix)) {
                return true;
            }
        }

        return false;
    }

    public WebEngine getEngine() {
        return engine;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JButton forwardButton;
    private javax.swing.JButton printButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel webViewContainer;
    // End of variables declaration//GEN-END:variables
}
