/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frm.oce.peps.controlador;

import frm.oce.peps.vista.VistaPEPS;

/**
 *
 * @author ariel
 */
public class ControladorPrincipal{
    
    private VistaPEPS vistaPEPS;
    private ControladorProducto controladorProducto;
    
    public ControladorPrincipal(){
        vistaPEPS = new VistaPEPS();
        vistaPEPS.setVisible(true);
    }
    
    public static void main(String[] args) {
        ControladorPrincipal controladorPrincipal = new ControladorPrincipal();
        controladorPrincipal.controladorProducto = new ControladorProducto(controladorPrincipal);
    }

    public VistaPEPS getVistaPEPS() {
        return vistaPEPS;
    }

    public void setVistaPEPS(VistaPEPS vistaPEPS) {
        this.vistaPEPS = vistaPEPS;
    }
    
    
    
}
