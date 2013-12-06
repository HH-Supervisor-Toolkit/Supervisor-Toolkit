/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author haywoosd
 */
public class RightClickMenu extends JPopupMenu {

    JCheckBoxMenuItem timerItem;

    public RightClickMenu() {
        timerItem = new JCheckBoxMenuItem("Add Timer to Tab");
        timerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
        add(timerItem);
    }
}
