/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Sloan
 */
public class main {

    final static String[] Default = {"[Nightly Log]", "https://docs.google.com/forms/d/172-Elqzog2MgLSMe9WvCHkuxHsJAb5IaFJZKq74KxPw/viewform",
        "[Equipment Problem Report]", "https://docs.google.com/forms/d/1X8K1XeWBykPRnnxn5TWaLGUcc68Yn3JiejvpSgwiJTc/viewform",
        "[Incident Report]", "https://docs.google.com/forms/d/1Zy4Hd4FxPlpSAOZMigRfUVywnL78-pBm5HP5E69TasE/viewform",
        "[Textbook Request Form]", "https://docs.google.com/forms/d/1wW0GEoEqkOlpTIPP__2kRSWbD1RskTBo4wtBaO738BM/viewform",
        "[Real-Time Agent]", "http://geomantce-cra.rose-hulman.edu/ACEAdmin/Admin/login.asp?entire=yes&returnpage=http://geomantce-cra.rose-hulman.edu:8080/ACEReport/",
        "[Phone Surveys]", "https://prod11gbss8.rose-hulman.edu/BanSS/rhit_hwhl.P_QuestionPage",
        "[Attendance Page]", "http://askrose.org/askrose-login"
    };
    static File file;
    static FileReader read;
    public static OptionsEdit optionsEdit;
    
    public static JComponent createContent(String[] address) {
        JWebBrowser[] webBrowser = new JWebBrowser[address.length / 2];
        JPanel contentPane = new JPanel(new BorderLayout());
        JTabbedPane webBrowserPane = new JTabbedPane();
        System.out.println("Begining to load pages");
        try {
            for (int i = 1; i < address.length; i = i + 2) {
                System.out.println("Constructing web browser " + (i - 1) / 2 + " in tab: " + address[i - 1]);
                webBrowser[(i - 1) / 2] = new JWebBrowser();
                System.out.println("Navagating to " + address[i]);
                webBrowser[(i - 1) / 2].navigate(address[i]);
                webBrowser[(i - 1) / 2].setBarsVisible(false);
                webBrowser[(i - 1) / 2].setName("Web Browser " + (i - 1) / 2);
            }
            for (int i = 0; i < address.length - 1; i += 2) {
                webBrowserPane.addTab(address[i].substring(1, address[i].length() - 1), webBrowser[i / 2]);
            }
        } catch (StringIndexOutOfBoundsException ex1) {
            RepairOptions();
            return createContent(ReadOptions());
        }
        optionsEdit = new OptionsEdit(false);
        webBrowserPane.addTab("Options", optionsEdit);
        webBrowserPane.addMouseListener(new RightClickListener(webBrowserPane));
        webBrowserPane.setName("Tabbed Pane");
        contentPane.add(webBrowserPane, BorderLayout.CENTER);
        return contentPane;
    }

    /* Standard main method to try that test as a standalone application. */
    public static void main(final String[] args) {
        NativeInterface.open();
        UIUtils.setPreferredLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Supervisor Reports");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(createContent(ReadOptions()), BorderLayout.CENTER);
                frame.setSize(900, 600);
                frame.setLocationByPlatform(true);
                try {
                    String iconPath;
                    if (args.length > 0) {
                        if (args[0].equals("don'tfeedthebears")) {
                            iconPath = "img/bear.png";
                        } else {
                            iconPath = "img/icon.png";
                        }
                    } else {
                        iconPath = "img/icon.png";
                    }
                    InputStream iconStream = main.class.getResourceAsStream(iconPath);
                    BufferedImage icon = ImageIO.read(iconStream);
                    frame.setIconImage(icon);
                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        String ObjButtons[] = {"Yes", "No"};
                        int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Nightly Log", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                        if (PromptResult == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                });
                frame.setVisible(true);
            }
        });
        NativeInterface.runEventPump();
    }

    static String[] ReadOptions() {
        ArrayList<String> LineList = new ArrayList();
        String workingLine;
        try {
            System.out.println("Attempting to read options file");
            file = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Options.txt");
            if (!file.exists()) {
                System.out.println("Options file not found attemping to create default");
                if (!file.getParentFile().exists()) {
                    System.out.println("SuperToolkit directory not found attempting to create it");
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                writeOptions(file, Default);
            }
            read = new FileReader(file);
            BufferedReader bufRead = new BufferedReader(read);
            workingLine = bufRead.readLine();
            if (workingLine == null || workingLine.length() <= 0) {
                RepairOptions();
                return ReadOptions();
            }
            while (workingLine != null) {
                LineList.add(workingLine);
                workingLine = bufRead.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return LineList.toArray(new String[1]);
    }

    public static void infoBox(String infoMessage, String location) {
        JOptionPane.showMessageDialog(null, infoMessage, location, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void writeOptions(File file, String[] writeContent) {
        PrintWriter write;
        try {
            write = new PrintWriter(file);
            for (int i = 0; i < writeContent.length; i++) {
                write.println(writeContent[i]);
            }
            write.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void RepairOptions() {
        System.out.println("Options file not formatted correctly. Opening repair window");
        infoBox("Options file is not formatted correctly please repair it manually.", "Bad Startup");
        JDialog diag = new JDialog();
        diag.setTitle("Options Manual Repair");
        diag.add(new OptionsEdit(true), BorderLayout.CENTER);
        diag.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        diag.setSize(900, 600);
        diag.setLocationByPlatform(true);
        diag.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        diag.setVisible(true);
        System.out.println("Options revision submitted");
    }
}
