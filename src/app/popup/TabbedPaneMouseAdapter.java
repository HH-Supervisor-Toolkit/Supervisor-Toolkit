/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app.popup;

import app.browser.ExtendedWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;

/**
 *
 * @author haywoosd
 */
public class TabbedPaneMouseAdapter extends MouseAdapter {

    JTabbedPane tabPaneParent;
    int index;

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger() && isTabReal(e)) {
            System.out.println("Right mouse button is down. Popup will trigger.");
            showMenu(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() && isTabReal(e)) {
            System.out.println("Right mouse button is released. Popup will trigger.");
            showMenu(e);
        }
    }

    private void showMenu(MouseEvent e) {
        tabPaneParent.setSelectedIndex(index);
        TabPopupMenu menu = new TabPopupMenu((ExtendedWebBrowser) tabPaneParent.getComponentAt(index));
        menu.setInvoker((JWebBrowser) tabPaneParent.getComponentAt(index));
        menu.setLocation(e.getXOnScreen(), e.getYOnScreen());
        menu.setVisible(true);
    }

    private boolean isTabReal(MouseEvent e) {
        index = tabPaneParent.indexAtLocation(e.getX(), e.getY());
        if (index != -1 && index < tabPaneParent.getTabCount() - 2) {
            System.out.println("The tab right clicked is: " + tabPaneParent.getTitleAt(index));
            return true;
        } else {
            return false;
        }

    }

    public TabbedPaneMouseAdapter(JTabbedPane parent) {
        tabPaneParent = parent;
    }
}
