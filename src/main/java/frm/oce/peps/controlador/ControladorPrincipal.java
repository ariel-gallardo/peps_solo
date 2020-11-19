/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frm.oce.peps.controlador;

import frm.oce.peps.vista.VistaPEPS;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author ariel
 */
public class ControladorPrincipal  implements MouseMotionListener, MouseListener {

    private VistaPEPS vistaPEPS;
    private int X, Y;
    private ControladorProducto controladorProducto;

    public ControladorPrincipal() {
        vistaPEPS = new VistaPEPS();
        vistaPEPS.getjP_Menu().addMouseListener(this);
        vistaPEPS.getjP_Menu().addMouseMotionListener(this);
        vistaPEPS.setVisible(true);
    }

    public static void main(String[] args) {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        ControladorPrincipal controladorPrincipal = new ControladorPrincipal();

        controladorPrincipal.controladorProducto = new ControladorProducto(controladorPrincipal);
        

    }

    public VistaPEPS getVistaPEPS() {
        return vistaPEPS;
    }

    public void setVistaPEPS(VistaPEPS vistaPEPS) {
        this.vistaPEPS = vistaPEPS;
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        int _X = e.getXOnScreen();
        int _Y = e.getYOnScreen();
        vistaPEPS.setLocation(_X - X, _Y - Y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
}
