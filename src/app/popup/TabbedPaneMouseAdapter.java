package app.popup;

import app.browser.ExtendedWebBrowser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;

public class TabbedPaneMouseAdapter extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {

        int index = getRealTab(e);

        if (index != -1) {
            System.out.println("Right mouse button is down. Popup will trigger.");
            showMenu(e, index);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        int index = getRealTab(e);

        if (index != -1) {
            System.out.println("Right mouse button is down. Popup will trigger.");
            showMenu(e, index);
        }
    }

    private void showMenu(MouseEvent e, int index) {

        JTabbedPane tabPaneParent = (JTabbedPane) e.getComponent();
        tabPaneParent.setSelectedIndex(index);

        TabPopupMenu menu = new TabPopupMenu((ExtendedWebBrowser) tabPaneParent.getComponentAt(index));
        menu.setInvoker((ExtendedWebBrowser) tabPaneParent.getComponentAt(index));
        menu.setLocation(e.getXOnScreen(), e.getYOnScreen());
        menu.setVisible(true);
    }

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
