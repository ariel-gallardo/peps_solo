/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frm.oce.peps.controlador;

import frm.oce.peps.modelo.Producto;
import frm.oce.peps.modelo.Stock;
import frm.oce.peps.modelo.Valor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ariel
 */
public class ControladorProducto implements ActionListener {

    private ControladorPrincipal controladorPrincipal;
    private List<Producto> productos;

    public ControladorProducto(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
        productos = new ArrayList<>();
        productos.add(new Producto(0, "Memoria Ram", new TreeMap<>(
                Map.of(
                        new Date(118, 8, 5), new Stock(50, new Valor(200, 10)),
                        new Date(119, 10, 14), new Stock(200, new Date(119, 10, 10), new Valor(500, 10)),
                        new Date(120, 5, 6), new Stock(200, new Valor(1000, 10)),
                        new Date(120, 10, 8), new Stock(200, new Valor(2000, 10)),
                        new Date(120, 10, 20), new Stock(80, null),
                        new Date(120, 11, 14), new Stock(200, new Valor(4000, 10))
                ))
        ));
        cargarProductos();
        cargarDatos(productos.size() - 1, null);
        agregarControladores();
    }

    private void agregarControladores() {
        controladorPrincipal.getVistaPEPS().getjB_Crear().addActionListener(this);
        controladorPrincipal.getVistaPEPS().getjB_Cargar().addActionListener(this);
        controladorPrincipal.getVistaPEPS().getjB_CargarFecha().addActionListener(this);
    }

    private void crearProducto(String nombre) {
        productos.add(new Producto(productos.size(), nombre));
        controladorPrincipal.getVistaPEPS().getjCB_Productos().addItem(productos.get(productos.size() - 1).getNombre());
    }

    private void cargarProductos() {
        controladorPrincipal.getVistaPEPS().getjCB_Productos().removeAllItems();
        for (Producto producto : productos) {
            controladorPrincipal.getVistaPEPS().getjCB_Productos().addItem(producto.getNombre());
        }
    }

    private void cargarDatos(int id, String fecha) {
        controladorPrincipal.getVistaPEPS().getjCB_Fecha().removeAllItems();
        controladorPrincipal.getVistaPEPS().getjL_FechaValor().setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        int seleccion = -1;
        long disponibilidad = 0, vendido = 0, vendidoMes = 0;
        double mayorPrecio = 0.0;
        Date anterior = null, personalizada = null;
        if (fecha != null) {
            try {
                personalizada = new SimpleDateFormat("dd-MM-yyyy").parse(fecha);
            } catch (ParseException ex) {

            }
        }
        for (Map.Entry<Date, Stock> entry : productos.get(id).getStock().entrySet()) {
            if (personalizada != null) {
                if (entry.getValue().getFecha_baja() != null) {
                    if(entry.getValue().getFecha_baja().compareTo(personalizada) == -1 && entry.getValue().getFecha_baja().compareTo(personalizada) == 0){
                        disponibilidad += entry.getValue().getCantidad();
                    }else if(!(entry.getValue().getFecha_baja().compareTo(personalizada) == 1)){
                        vendido += entry.getValue().getCantidad();
                        if(entry.getKey().getMonth() == personalizada.getMonth() && entry.getKey().getYear() == personalizada.getYear()){
                            vendidoMes += entry.getValue().getCantidad();
                        }
                    }
                } else if (entry.getKey().compareTo(personalizada) == -1 || entry.getKey().compareTo(personalizada) == 0) {
                    disponibilidad += entry.getValue().getCantidad();
                }
            }else if (entry.getValue().getFecha_baja() == null) {
                disponibilidad += entry.getValue().getCantidad();
            }else{
                vendido += entry.getValue().getCantidad();
                
            }
            
            if (anterior == null) {
                anterior = entry.getKey();
                controladorPrincipal.getVistaPEPS().getjCB_Fecha().addItem(new SimpleDateFormat("dd-MM-yyyy").format(entry.getKey()));
                if (personalizada == null) {
                    seleccion++;
                } else if (entry.getKey().compareTo(personalizada) == -1 || entry.getKey().compareTo(personalizada) == 0) {
                    seleccion++;
                }
            }
            if (entry.getKey().getTime() != anterior.getTime()) {
                controladorPrincipal.getVistaPEPS().getjCB_Fecha().addItem(new SimpleDateFormat("dd-MM-yyyy").format(entry.getKey()));
                anterior = entry.getKey();
                if (personalizada == null) {
                    seleccion++;
                } else if (entry.getKey().compareTo(personalizada) == -1 || entry.getKey().compareTo(personalizada) == 0) {
                    seleccion++;
                }
            }
            if (entry.getValue().getValor() != null) {
                if (entry.getValue().getValor().getNumero() > mayorPrecio) {
                    if (personalizada != null) {
                        if (entry.getKey().compareTo(personalizada) == -1 || entry.getKey().compareTo(personalizada) == 0) {
                            mayorPrecio = entry.getValue().getValor().getNumero();
                        }
                    } else {
                        mayorPrecio = entry.getValue().getValor().getNumero();
                    }
                }
            }
            
        }
        controladorPrincipal.getVistaPEPS().getjTF_VentaMes().setText(String.valueOf(vendidoMes));
        controladorPrincipal.getVistaPEPS().getjTF_VentaTotal().setText(String.valueOf(vendido));
        controladorPrincipal.getVistaPEPS().getjTF_Stock().setText(String.valueOf(disponibilidad) + " U");
        controladorPrincipal.getVistaPEPS().getjTF_Precio().setText(String.valueOf(mayorPrecio) + " $");
        controladorPrincipal.getVistaPEPS().getjCB_Fecha().setSelectedIndex(seleccion);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Crear":
                if (!controladorPrincipal.getVistaPEPS().getjTF_NuevoProducto().getText().isEmpty()) {
                    crearProducto(controladorPrincipal.getVistaPEPS().getjTF_NuevoProducto().getText());
                }
                break;
            case "Cargar":
                if (!productos.isEmpty()) {
                    cargarDatos(controladorPrincipal.getVistaPEPS().getjCB_Productos().getSelectedIndex(), null);
                }
                break;
            case "Ver":
                if (!productos.isEmpty()) {
                    cargarDatos(controladorPrincipal.getVistaPEPS().getjCB_Productos().getSelectedIndex(), controladorPrincipal.getVistaPEPS().getjCB_Fecha().getSelectedItem().toString());
                }
                break;
        }
    }

}
