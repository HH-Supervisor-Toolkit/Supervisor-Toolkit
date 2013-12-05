/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author haywoosd
 */
public class RightClickMenu extends JPopupMenu{
    JMenuItem timerItem;
    public RightClickMenu(){
        timerItem = new JMenuItem("Add Timer to Tab");
        add(timerItem);
    }
}
