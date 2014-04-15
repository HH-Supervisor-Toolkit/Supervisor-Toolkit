/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.alarms.AlarmsEditPanel;
import app.browser.ExtendedWebBrowser;
import app.popup.TabbedPaneMouseAdapter;
import app.options.OptionsEditPanel;
import app.timer.BrowserTimerAdapter;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Sloan
 */
public class main {

    public final static String[] Default = {"[Nightly Log] -B", "https://docs.google.com/forms/d/172-Elqzog2MgLSMe9WvCHkuxHsJAb5IaFJZKq74KxPw/viewform",
        "[Equipment Problem Report]", "https://docs.google.com/forms/d/1X8K1XeWBykPRnnxn5TWaLGUcc68Yn3JiejvpSgwiJTc/viewform",
        "[Incident Report]", "https://docs.google.com/forms/d/1Zy4Hd4FxPlpSAOZMigRfUVywnL78-pBm5HP5E69TasE/viewform",
        "[Textbook Request Form]", "https://docs.google.com/forms/d/1wW0GEoEqkOlpTIPP__2kRSWbD1RskTBo4wtBaO738BM/viewform",
        "[Real-Time Agent]", "http://geomantce-cra.rose-hulman.edu/ACEAdmin/Admin/login.asp?entire=yes&returnpage=http://geomantce-cra.rose-hulman.edu:8080/ACEReport/",
        "[Phone Surveys] -t:30", "https://prod11gbss8.rose-hulman.edu/BanSS/rhit_hwhl.P_QuestionPage",
        "[Attendance Page]", "http://askrose.org/askrose-login"
    };
    public final static String[] bearsLinks = {"http://www.firstpeople.us/pictures/bear/1600x1200/Feeling_Grizzly-1600x1200.jpg",
        "http://www.firstpeople.us/pictures/bear/1600x1200/Grin_and_Bear_It-1600x1200.jpg",
        "http://cdn.zmescience.com/wp-content/uploads/2011/11/grizzly-bear.jpg",
        "http://images.nationalgeographic.com/wpf/media-live/photos/000/095/cache/bald-bears_9572_600x450.jpg",
        "http://www.nationalgeographic.com/adventure/images/05_06/bears-2.jpg",
        "http://upload.wikimedia.org/wikipedia/commons/a/a0/Sun_Bear_7.jpg",
        "http://media.247sports.com/Uploads/Avatars/248/293481.jpg"};
    public static File file;
    public static OptionsEditPanel optionsEdit;
    public static JFrame frame;
    public static int addressCount;
    public static boolean bears = false;
    static BufferedImage icon;

    public static JComponent createContent(String[] address) {
        ExtendedWebBrowser[] webBrowser = new ExtendedWebBrowser[address.length / 2];
        JTabbedPane webBrowserPane = new JTabbedPane();
        try {
            for (int i = 1; i < address.length; i = i + 2) {
                webBrowser[(i - 1) / 2] = new ExtendedWebBrowser();
                System.out.println("Navagating to " + address[i]);
                if (bears) {
                    webBrowser[(i - 1) / 2].navigate(bearsLinks[i / 2 - i / (2 * bearsLinks.length)]);
                } else {
                    webBrowser[(i - 1) / 2].navigate(address[i]);
                }
                webBrowser[(i - 1) / 2].setBarsVisible(false);
                webBrowser[(i - 1) / 2].setButtonBarVisible(true);
                addTabWithOptions(webBrowserPane, webBrowser[(i - 1) / 2], address[i - 1]);

            }
        } catch (StringIndexOutOfBoundsException ex1) {
            RepairOptions();
            return createContent(ReadOptions());
        }
        optionsEdit = new OptionsEditPanel(false);
        webBrowserPane.addTab("Options", optionsEdit);
        if (bears) {
            JPanel bearPanel = new JPanel();
            JLabel bearLabel = new JLabel();
            bearLabel.setFont(new Font(bearLabel.getFont().getName(),Font.PLAIN,86));
            bearLabel.setText("BEARS!");
            bearPanel.setLayout(new GridBagLayout());
            bearPanel.add(bearLabel);
            webBrowserPane.addTab("Alarms",bearPanel);
        } else {
            webBrowserPane.addTab("Alarms", new AlarmsEditPanel());
        }
        webBrowserPane.addMouseListener(new TabbedPaneMouseAdapter(webBrowserPane));
        addressCount = address.length / 2;
        return webBrowserPane;
    }

    /* Standard main method to try that test as a standalone application. */
    public static void main(final String[] args) {
        for (String arg : args) {
            if (arg.equals("don'tfeedthebears")) {
                bears = true;
            }
        }
        NativeInterface.open();
        UIUtils.setPreferredLookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame("Supervisor Reports");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationByPlatform(true);
                try {
                    String iconPath;
                    if (bears) {
                        iconPath = "img/bear.png";
                    } else {
                        iconPath = "img/icon.png";
                    }
                    InputStream iconStream = main.class.getResourceAsStream(iconPath);
                    icon = ImageIO.read(iconStream);
                    frame.setIconImage(icon);
                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
                frame.add(createContent(ReadOptions()), BorderLayout.CENTER);
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        String ObjButtons[] = {"Yes", "No"};
                        int PromptResult = JOptionPane.showOptionDialog(frame, "Are you sure you want to exit?", "Nightly Log", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
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
            if (!file.exists() || file.length() == 0) {
                System.out.println("Options file not found attemping to create default");
                if (!file.getParentFile().exists()) {
                    System.out.println("SuperToolkit directory not found attempting to create it");
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                writeOptions(file, Default);
            }
            FileReader read = new FileReader(file);
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

    public static void writeOptions(File file, String[] writeContents) {
        PrintWriter write;
        try {
            write = new PrintWriter(file);
            for (String writeContent : writeContents) {
                write.println(writeContent);
            }
            write.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void RepairOptions() {
        System.out.println("Options file not formatted correctly. Opening repair window");
        infoBox("Options file is not formatted correctly please repair it manually.", "Bad Startup");
        JDialog diag = new JDialog((Window)null);
        diag.setTitle("Options Manual Repair");
        diag.add(new OptionsEditPanel(true), BorderLayout.CENTER);
        diag.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        diag.setSize(900, 600);
        diag.setLocationByPlatform(true);
        diag.setIconImage(icon);
        diag.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        diag.setVisible(true);
        System.out.println("Options revision submitted");
    }

    public static void addTabWithOptions(JTabbedPane webBrowserPane, ExtendedWebBrowser webBrowser, String title) {
        int tabTitleEndPos = title.lastIndexOf("]");
        String[] parsedOptions = title.substring(tabTitleEndPos + 1).trim().split("-", -1);
        String tabTitle = title.substring(1, tabTitleEndPos);
        System.out.println("Constructing web browser in tab: " + tabTitle);
        webBrowser.setName(tabTitle);
        webBrowserPane.add(tabTitle, webBrowser);
        for (String parsedOption : parsedOptions) {
            if (parsedOption.contains("t:")) {
                System.out.println("Adding a timer to " + tabTitle + " from options for " + parsedOption.substring(2).trim() + " minute(s)");
                BrowserTimerAdapter browserTimer = new BrowserTimerAdapter(Integer.parseInt(parsedOption.substring(2).trim()), webBrowser);
                webBrowser.addBrowserTimer(browserTimer);
            } else if (parsedOption.contains("B")) {
                System.out.println("Enabling auto backup for " + tabTitle);
                webBrowser.enableBackup();
            }
        }
    }
}
