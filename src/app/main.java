package app;

import app.JNI.EnumAllWindowNames;
import app.alarms.AlarmsEditPanel;
import app.browser.ConfURLHandlerClass;
import app.browser.ExtendedWebBrowser;
import app.popup.TabbedPaneMouseAdapter;
import app.options.OptionsEditPanel;
import app.timer.BrowserTimer;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class main {

    //Contains all default tab names, links, and option switches. There must be a tab name for each link or else an exception will be thrown when reading the options.
    public final static String[] Default = {"[Nightly Log] -B", "https://docs.google.com/forms/d/172-Elqzog2MgLSMe9WvCHkuxHsJAb5IaFJZKq74KxPw/viewform",
        "[Equipment Problem Report]", "https://docs.google.com/forms/d/1X8K1XeWBykPRnnxn5TWaLGUcc68Yn3JiejvpSgwiJTc/viewform",
        "[Incident Report]", "https://docs.google.com/forms/d/1Zy4Hd4FxPlpSAOZMigRfUVywnL78-pBm5HP5E69TasE/viewform",
        "[Textbook Request Form]", "https://docs.google.com/forms/d/1wW0GEoEqkOlpTIPP__2kRSWbD1RskTBo4wtBaO738BM/viewform",
        "[Real-Time Agent] -S", "http://geomantce-hv.rose-hulman.edu:8080/ACEReport/",
        "[Phone Surveys] -t:30", "https://prod11gbss8.rose-hulman.edu/BanSS/rhit_hwhl.P_QuestionPage",
        "[Attendance Page]", "http://dev2.askrose.org/askrose-login",
        "[JotForms]", "http://www.jotform.com/login"
    };

    static ExtendedWebBrowser[] webBrowsers;
    public static JFrame frame;
    static public BufferedImage icon;
    static public OptionsEditPanel optionsEdit;
    static JTabbedPane webBrowserPane;

    //This method take care of creating all the content that is displayed in the main frame. It is passed a string array read from the options file or form the Default string array if no options exist.
    @SuppressWarnings("InfiniteRecursion")
    public static JComponent createContent(String[] address) {

        webBrowsers = new ExtendedWebBrowser[address.length / 2];
        webBrowserPane = new JTabbedPane();

        //If the options file is corrupt or incorrectly formatted this will fail and the repair window will be shown. Once the repair window is closed createContent is tried again.
        try {

            for (int i = 1; i < address.length; i = i + 2) {
                webBrowsers[(i - 1) / 2] = new ExtendedWebBrowser();
                System.out.println("Navagating to " + address[i]);

                webBrowsers[(i - 1) / 2].loadURL(address[i]);
                addTabWithOptions(webBrowserPane, webBrowsers[(i - 1) / 2], address[i - 1]);
            }

        } catch (StringIndexOutOfBoundsException ex1) {

            RepairOptions();
            return createContent(ReadOptions());

        }

        //optionsEdit is a class variable because it will be reference again when options switches are changed. These changes come from any tab and not just the OptionsEditPanel itself.
        optionsEdit = new OptionsEditPanel(false);
        webBrowserPane.addTab("Options", optionsEdit);
        webBrowserPane.addTab("Alarms", new AlarmsEditPanel());
        webBrowserPane.addMouseListener(new TabbedPaneMouseAdapter());

        return webBrowserPane;
    }

    public static void main(final String[] args) {

        //Both SSLv3 and TLSv1 must be allowed because Rose's sites are out of date.
        System.setProperty("https.protocols", "SSLv3,TLSv1");

        //This is how we add special handeling to urls prefixed with conf. Conf is what lync uses to initiate group calls e.g. observations.
        URL.setURLStreamHandlerFactory((String protocol) -> {
            if (protocol.equals("conf")) {
                return new ConfURLHandlerClass();
            } else {
                return null;
            }
        });

        //How we detect if the user wants bears.
        String iconPath = "img/icon.png";
        for (String arg : args) {
            if (arg.equals("don'tfeedthebears")
                    || arg.equals("don'tfeedthebear")
                    || arg.equals("bear")
                    || arg.equals("bears")
                    || arg.equals("dontfeedthebears")
                    || arg.equals("dontfeedthebear")) {
                iconPath = "img/bear.png";
                break;
            }
        }

        //Make the toolkit fit Window's default theme.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }

        frame = new JFrame("Supervisor Toolkit");
        frame.setLocationByPlatform(true);

        //Load the chosen icon. Icons should be located in app.img.
        try {
            InputStream iconStream = main.class.getResourceAsStream(iconPath);
            icon = ImageIO.read(iconStream);

            frame.setIconImage(icon);

        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }

        frame.add(createContent(ReadOptions()), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //We're handeling the main frame closing in a custom way. This way we can prompt the user if they click close by accident.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {

                String ObjButtons[] = {"Yes", "No"};
                int PromptResult = JOptionPane.showOptionDialog(frame, "Are you sure you want to exit?", "Supervisor Toolkit", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);

                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    //This funciton will read from the options file and return its contents in the form of a String[]. If no file/directory is found one will be created and the default options written to it.
    static String[] ReadOptions() {
        try {
            System.out.println("Attempting to read options file");
            File optionsFile = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Options.txt");

            if (!optionsFile.exists() || optionsFile.length() == 0) {

                System.out.println("Options file not found attemping to create default");

                if (!optionsFile.getParentFile().exists()) {
                    System.out.println("SuperToolkit directory not found attempting to create it");
                    optionsFile.getParentFile().mkdirs();
                }

                optionsFile.createNewFile();
                writeOptions(Default);
            }

            return Files.readAllLines(optionsFile.toPath()).toArray(new String[0]);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //If the options file couldn't be read for some reason we'll offer the user to repair the options themself.
        RepairOptions();
        return ReadOptions();
    }

    //Write the given String[] to the Options file. Each array element is given a seperate line.
    public static void writeOptions(String[] writeContents) {
        File file = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\SuperToolkit\\Options.txt");

        try (PrintWriter write = new PrintWriter(file)) {

            for (String writeContent : writeContents) {
                write.println(writeContent);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //If the options are broken a dialog containing an OptionsEditPanel will be shown. This is a modal Window blocking until closed. The exit button does nothing so the user has to make changes or cancel.
    public static void RepairOptions() {
        System.out.println("Options file not formatted correctly. Opening repair window");
        JOptionPane.showMessageDialog(null, "Options file is not formatted correctly please repair it manually.", "Bad Startup", JOptionPane.INFORMATION_MESSAGE);

        JDialog diag = new JDialog();

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

    //Used to parse the switches assigned to the given tab and apply them to the ExtendedWebBrowser element.
    public static void addTabWithOptions(JTabbedPane webBrowserPane, ExtendedWebBrowser webBrowser, String title) {

        int tabTitleEndPos = title.lastIndexOf("]");
        String[] parsedOptions = title.substring(tabTitleEndPos + 1).trim().split("-", -1);
        String tabTitle = title.substring(1, tabTitleEndPos);

        System.out.println("Constructing web browser in tab: " + tabTitle);

        webBrowser.setName(tabTitle);
        webBrowserPane.add(tabTitle, webBrowser);

        for (String parsedOption : parsedOptions) {

            switch (parsedOption) {
                case "t:":
                    System.out.println("Adding a timer to " + tabTitle + " from options for " + parsedOption.substring(2).trim() + " minute(s)");
                    BrowserTimer browserTimer = new BrowserTimer(Integer.parseInt(parsedOption.substring(2).trim()), webBrowser);
                    webBrowser.addBrowserTimer(browserTimer);
                    break;
                case "B":
                    System.out.println("Enabling auto backup for " + tabTitle);
                    webBrowser.enableBackup();
                    break;
                case "S":
                    System.out.println("Enabling status monitor for " + tabTitle);
                    webBrowser.enableMonitor();
                    break;
            }
        }
    }

    //Used to edit the add/remove option switches from the OptionsEditPanel and the options file. All future option switches should use "prefix:extra_data".
    public static void ModifyOptions(boolean removing, String option, ExtendedWebBrowser webBrowser) {

        int index = 0;
        int componentCount = webBrowserPane.getTabCount();

        //We can determine which lines in the options file and OptionsEditPanel to modify based on the index of the ExtendedWebBrowser contained in the JTabbedPane
        for (int i = 0; i < componentCount; i++) {
            if (webBrowserPane.getComponentAt(i).equals(webBrowser)) {
                index = i;
                break;
            }
        }

        String[] optionsText = optionsEdit.getOptionsText();

        int prefixEndPos = option.indexOf(":");
        
        //If the option switch doesn't have a colon then we should consider the whole switch to be a prefix.
        if (prefixEndPos == -1) {
            prefixEndPos = option.length();
        }

        String prefix = option.substring(0, prefixEndPos);

        //Regix expressions are beautiful monsters.
        if (removing) {
            System.out.println("Removing " + prefix + " option switch from tab " + webBrowser.getName());
            optionsText[index * 2] = optionsText[index * 2].replaceAll("-" + prefix + "[^-]*", "");
        } else {
            System.out.println("Adding " + prefix + " option switch from tab " + webBrowser.getName());
            optionsText[index * 2] = optionsText[index * 2].trim() + " -" + option;
        }

        optionsEdit.setOptionsText(optionsText);
        writeOptions(optionsText);
    }
}
