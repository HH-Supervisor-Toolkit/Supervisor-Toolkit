/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author haywoosd
 */
public class RightClickListener extends MouseAdapter{
    
    @Override
    public void mousePressed (MouseEvent e){
        if (e.isPopupTrigger()){
            System.out.println("Right mouse button is down. Popup will trigger.");
            showMenu(e);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger()){
            System.out.println("Right mouse button is released. Popup will trigger.");
            showMenu(e);
        }
    }
    
    private void showMenu(MouseEvent e){
        RightClickMenu menu = new RightClickMenu();
            menu.setInvoker(e.getComponent());
            menu.setLocation(e.getXOnScreen(),e.getYOnScreen());
            menu.setVisible(true);
    }
}
