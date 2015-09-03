package app.popup;

import app.browser.ExtendedWebBrowser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;

//This class is used to watch for right-clicks. It will display the TabPopupMenu if the right-click was on a valid tab.
public class TabbedPaneMouseAdapter extends MouseAdapter {

    //When the right click is released this function is called. It will check to see if a tab was right-clicked and if a valid one was it will show the popup menu.
    @Override
    public void mouseReleased(MouseEvent e) {

        int index = getRealTab(e);

        if (index != -1) {
            System.out.println("Right mouse button is released. Popup will trigger.");
            showMenu(e, index);
        }
    }

    //A shortcut function to show the TabPopupMenu.
    private void showMenu(MouseEvent e, int index) {
        JTabbedPane tabPaneParent = (JTabbedPane) e.getComponent();
        tabPaneParent.setSelectedIndex(index);

        TabPopupMenu menu = new TabPopupMenu((ExtendedWebBrowser) tabPaneParent.getComponentAt(index));
        menu.setInvoker((ExtendedWebBrowser) tabPaneParent.getComponentAt(index));
        
        menu.setLocation(e.getXOnScreen(), e.getYOnScreen());
        menu.setVisible(true);
    }

    //First checks to makesure the click was a popup triggering click. Then checks to see if a tab was clicked. Returns the index of the tab if one was clicked. Else it returns -1.
    private int getRealTab(MouseEvent e) {

        if (e.isPopupTrigger()) {

            JTabbedPane tabPaneParent = (JTabbedPane) e.getComponent();

            int index = tabPaneParent.indexAtLocation(e.getX(), e.getY());

            if (index != -1 && index < tabPaneParent.getTabCount() - 2) {
                System.out.println("The tab right clicked is: " + tabPaneParent.getTitleAt(index));
                return index;
            } else {
                return -1;
            }
            
        } else {
            return -1;
        }
    }
}
