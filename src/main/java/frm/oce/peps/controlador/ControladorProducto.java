/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frm.oce.peps.controlador;

import frm.oce.peps.modelo.Producto;
import frm.oce.peps.modelo.Stock;
import frm.oce.peps.modelo.Valor;
import frm.oce.peps.vista.VistaPEPS;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author ariel
 */
public class ControladorProducto implements ActionListener {

    private ControladorPrincipal controladorPrincipal;
    private List<Producto> productos;
    private int productoActual;
    private int seleccionado;

    public ControladorProducto(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
        productos = new ArrayList<>();

        productos.add(new Producto(0, "Memoria Ram", new TreeMap<>(
                Map.of(
                        new Date(118, 8, 5), new Stock(50, new Valor(200, 10)),
                        new Date(119, 10, 14), new Stock(200, new Date(119, 10, 15), new Valor(500, 10)),
                        new Date(120, 5, 6), new Stock(200, new Valor(1000, 10)),
                        new Date(120, 10, 8), new Stock(200, new Valor(2000, 10))
                //new Date(120, 10, 20), new Stock(80, null)
                //new Date(120, 11, 14), new Stock(200, new Valor(4000, 10)),
                //new Date(120, 11, 18), new Stock(200, new Date(120, 11, 18), null)
                ))
        ));
        //productos.get(0).getStock().get(new Date(120, 11, 14)).agregarSalida(new Date(120, 11, 15), new Stock(100L));
        cargarProductos();
        cargarDatos(productos.size() - 1, null);
        productoActual = productos.size() - 1;
        agregarControladores();
        getVistaPEPS().getjCB_Fecha().setSelectedIndex(getVistaPEPS().getjCB_Fecha().getItemCount() - 1);
    }

    private void agregarControladores() {
        getVistaPEPS().getjB_Crear().addActionListener(this);
        getVistaPEPS().getjB_Cargar().addActionListener(this);
        getVistaPEPS().getjB_Registrar().addActionListener(this);
        getVistaPEPS().getjB_CargarFecha().addActionListener(this);
        getVistaPEPS().getjC_PrecioUR().addActionListener(this);
        getVistaPEPS().getjCB_Fecha().addActionListener(this);
        getVistaPEPS().getjB_Salida().addActionListener(this);
    }

    private void crearProducto(String nombre) {
        if (!nombre.isEmpty()) {
            for (Producto producto : productos) {
                if (producto.getNombre().equals(nombre)) {
                    JOptionPane.showMessageDialog(getVistaPEPS(), "El producto ya existe", "PEPS", 0);
                    getVistaPEPS().getjTF_NuevoProducto().setText("");
                    return;
                }
            }
            productos.add(new Producto(productos.size(), nombre));
            getVistaPEPS().getjCB_Productos().addItem(productos.get(productos.size() - 1).getNombre());
            getVistaPEPS().getjTF_VentaMes().setText("");
            getVistaPEPS().getjTF_VentaTotal().setText("");
            getVistaPEPS().getjTF_Stock().setText("");
            getVistaPEPS().getjTF_Precio().setText("");
            getVistaPEPS().getjCB_Fecha().removeAllItems();
            getVistaPEPS().getjTF_NuevoProducto().setText("");
            getVistaPEPS().getjCB_Productos().setSelectedIndex(productos.size() - 1);
            getVistaPEPS().getjL_Seleccion().setText("Producto Actual: " + getVistaPEPS().getjCB_Productos().getItemAt(productos.size() - 1));
            productoActual = productos.size() - 1;
        } else {
            JOptionPane.showMessageDialog(getVistaPEPS(), "El producto debe tener un nombre", "PEPS", 1);
        }

    }

    private void cargarProductos() {
        getVistaPEPS().getjCB_Productos().removeAllItems();
        for (Producto producto : productos) {
            getVistaPEPS().getjCB_Productos().addItem(producto.getNombre());
        }
    }

    private void cargarDatos(int id, String fecha) {

        if (!productos.isEmpty()) {
            if (productos.get(id).getStock() != null) {
                int tempSeleccionado = seleccionado;
                getVistaPEPS().getjCB_Fecha().removeAllItems();
                getVistaPEPS().getjL_FechaValor().setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                int seleccion = -1, mesAnterior = 0, anioAnterior = 0;
                long disponibilidad = 0, vendido = 0, vendidoMes = 0;
                double mayorPrecio = 0.0;
                Date anterior = null, personalizada = null;
                Valor valAnterior = null;
                if (fecha != null) {
                    try {
                        personalizada = new SimpleDateFormat("dd-MM-yyyy").parse(fecha);
                        personalizada.setDate(personalizada.getDate() + 1);
                    } catch (ParseException ex) {

                    }
                }

                for (Map.Entry<Date, Stock> entry : productos.get(id).getStock().entrySet()) {
                    if (personalizada != null) {
                        if (entry.getValue().getFecha_baja() != null) {
                            if (entry.getValue().getFecha_baja().compareTo(personalizada) == -1 && entry.getValue().getFecha_baja().compareTo(personalizada) == 0) {
                                disponibilidad += entry.getValue().getCantidad();
                            } else if (!(entry.getValue().getFecha_baja().compareTo(personalizada) == 1)) {
                                vendido += entry.getValue().getCantidad();
                                if (entry.getKey().getMonth() == personalizada.getMonth() && entry.getKey().getYear() == personalizada.getYear()) {
                                    vendidoMes += entry.getValue().getCantidad();
                                }
                            }
                        } else if (entry.getKey().before(personalizada)) {
                            disponibilidad += entry.getValue().getCantidad();
                            if (entry.getValue().hasSalidas()) {
                                for (Map.Entry<Date, Stock> salida : entry.getValue().getSalidas().entrySet()) {
                                    if (salida.getKey().compareTo(entry.getKey()) == -1 || entry.getKey().compareTo(entry.getKey()) == 0) {
                                        vendido += salida.getValue().getCantidad();
                                    }
                                }
                            }
                        }
                    } else if (entry.getValue().getFecha_baja() == null) {
                       
                        if (entry.getValue().hasSalidas()) {
                            for (Map.Entry<Date, Stock> salida : entry.getValue().getSalidas().entrySet()) {
                                if (salida.getKey().compareTo(entry.getKey()) == -1 || entry.getKey().compareTo(entry.getKey()) == 0) {
                                    vendido += salida.getValue().getCantidad();  
                                    if(salida.getValue().getFecha_baja() != null){
                                        if(disponibilidad == 0){
                                            disponibilidad += (entry.getValue().getCantidad()-salida.getValue().getCantidad());
                                        }else{
                                            disponibilidad -=salida.getValue().getCantidad();
                                        }
                                        
                                    }
                                }
                            }
                        }else{
                            disponibilidad += entry.getValue().getCantidad();
                        }
                    } else {
                        vendido += entry.getValue().getCantidad();
                        if ((entry.getKey().getMonth() == mesAnterior && entry.getKey().getYear() == anioAnterior) || mesAnterior == 0) {
                            vendidoMes += entry.getValue().getCantidad();
                        } else {
                            vendidoMes = 0;
                        }
                    }

                    if (anterior == null) {
                        anterior = entry.getKey();
                        getVistaPEPS().getjCB_Fecha().addItem(new SimpleDateFormat("dd-MM-yyyy").format(entry.getKey()));
                    }
                    if (entry.getKey().getTime() != anterior.getTime()) {
                        getVistaPEPS().getjCB_Fecha().addItem(new SimpleDateFormat("dd-MM-yyyy").format(entry.getKey()));
                        anterior = entry.getKey();
                    }
                    if (entry.getValue().getValor() != null) {
                        /*
                        if (entry.getValue().getValor().getNumero() > mayorPrecio) {
                            if (personalizada != null) {
                                if (entry.getKey().compareTo(personalizada) == -1 || entry.getKey().compareTo(personalizada) == 0) {
                                    mayorPrecio = entry.getValue().getValor().getNumero();
                                }
                            } else {
                                mayorPrecio = entry.getValue().getValor().getNumero();
                            }
                        }*/
                        if (personalizada != null) {
                            if (entry.getKey().getMonth() <= personalizada.getMonth() && entry.getKey().getDay() <= personalizada.getDay() && entry.getKey().getYear() <= personalizada.getYear()) {
                                mayorPrecio = entry.getValue().getValor() == null ? (valAnterior != null ? valAnterior.getNumero() : 0) : entry.getValue().getValor().getNumero();
                            }
                        } else {
                            mayorPrecio = entry.getValue().getValor().getNumero();
                        }
                    }
                    valAnterior = entry.getValue().getValor();
                    mesAnterior = entry.getKey().getMonth();
                    anioAnterior = entry.getKey().getYear();
                }
                getVistaPEPS().getjTF_VentaMes().setText(String.valueOf(vendidoMes) + " U");
                getVistaPEPS().getjTF_VentaTotal().setText(String.valueOf(vendido) + " U");
                getVistaPEPS().getjTF_Stock().setText(String.valueOf(disponibilidad) + " U");
                getVistaPEPS().getjTF_Precio().setText(String.valueOf(mayorPrecio) + " $");
                getVistaPEPS().getjCB_Fecha().setSelectedIndex(tempSeleccionado);
                getVistaPEPS().getjL_Seleccion().setText("Producto Actual: " + getVistaPEPS().getjCB_Productos().getItemAt(id));
                productoActual = id;
            } else {
                JOptionPane.showMessageDialog(getVistaPEPS(), "Producto Nuevo", "PEPS", 1);
                getVistaPEPS().getjL_Seleccion().setText("Producto Actual: " + getVistaPEPS().getjCB_Productos().getItemAt(id));
                productoActual = id;
            }
        } else {
            JOptionPane.showMessageDialog(getVistaPEPS(), "Debe existir algun producto", "PEPS", 0);
            getVistaPEPS().getjL_Seleccion().setText("Producto Actual: Ninguno");
            productoActual = -1;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Crear":
                crearProducto(getVistaPEPS().getjTF_NuevoProducto().getText());
                break;
            case "Cargar":
                if (getVistaPEPS().getjCB_Productos().getItemCount() > 0) {
                    cargarDatos(getVistaPEPS().getjCB_Productos().getSelectedIndex(), null);
                } else {
                    JOptionPane.showMessageDialog(getVistaPEPS(), "Debe seleccionar algun producto", "PEPS", 0);
                }
                break;
            case "Ver":
                if (getVistaPEPS().getjCB_Fecha().getItemCount() > 0) {
                    cargarDatos(getVistaPEPS().getjCB_Productos().getSelectedIndex(), getVistaPEPS().getjCB_Fecha().getSelectedItem().toString());
                } else {
                    JOptionPane.showMessageDialog(getVistaPEPS(), "Debe crear algun registro", "PEPS", 0);
                }
                break;
            case "Registrar":
                if ((!getVistaPEPS().getjTF_PrecioUR().getText().equals("") && getVistaPEPS().getjC_PrecioUR().isSelected() && !getVistaPEPS().getjTF_UnidadesR().getText().equals("")) || (!getVistaPEPS().getjTF_UnidadesR().getText().equals("") && !getVistaPEPS().getjC_PrecioUR().isSelected())) {
                    Date tDate = new Date();
                    tDate.setHours(0);
                    tDate.setMinutes(0);
                    tDate.setSeconds(0);
                    boolean existe = false;
                    for (Date date : productos.get(productoActual).getStock().keySet()) {
                        if (date.getDay() == tDate.getDay() && date.getMonth() == tDate.getMonth() && date.getYear() == tDate.getYear()) {
                            existe = true;
                            tDate = date;
                            break;
                        }
                    }
                    if (existe) {
                        productos.get(productoActual).getStock().get(tDate).setCantidad(productos.get(productoActual).getStock().get(tDate).getCantidad() + Long.parseLong(getVistaPEPS().getjTF_UnidadesR().getText()));
                        
                        if (productos.get(productoActual).getStock().get(tDate).getValor() == null) {
                            productos.get(productoActual).getStock().get(tDate).setValor(new Valor());
                        } else {
                            if (!getVistaPEPS().getjTF_PrecioUR().getText().equals("")) {
                                productos.get(productoActual).getStock().get(tDate).getValor().setNumero(Double.parseDouble(getVistaPEPS().getjTF_PrecioUR().getText()));
                            }
                        }

                    } else {
                        productos.get(productoActual).addStock(tDate, new Stock(Long.parseLong(getVistaPEPS().getjTF_UnidadesR().getText()), getVistaPEPS().getjTF_PrecioUR().getText().equals("") ? null : new Valor(Long.parseLong(getVistaPEPS().getjTF_PrecioUR().getText()))));
                    }
                    cargarDatos(productoActual, null);
                    getVistaPEPS().getjCB_Fecha().setSelectedIndex(productos.get(productoActual).getStock().size() - 1);
                } else {
                    JOptionPane.showMessageDialog(getVistaPEPS(), "Completar los campos", "PEPS", 0);
                }
                break;
            default:
                if (e.getSource().equals(getVistaPEPS().getjC_PrecioUR())) {
                    getVistaPEPS().getjTF_PrecioUR().setEditable(getVistaPEPS().getjTF_PrecioUR().isEditable() ? false : true);
                    if (!getVistaPEPS().getjTF_PrecioUR().isEditable()) {
                        getVistaPEPS().getjTF_PrecioUR().setText("");
                    }
                }
                if (e.getSource().equals(getVistaPEPS().getjCB_Fecha())) {
                    seleccionado = getVistaPEPS().getjCB_Fecha().getSelectedIndex();
                }
                if (e.getSource().equals(getVistaPEPS().getjB_Salida())) {
                    if (!getVistaPEPS().getjTF_UnidadesS().getText().isEmpty() && Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText()) != 0) {
                        long cantidad = Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText());
                        Date d = new Date();
                        d.setHours(0);
                        d.setMinutes(0);
                        d.setSeconds(0);
                        for (Map.Entry<Date, Stock> entry : productos.get(productoActual).getStock().entrySet()) {        
                            if(cantidad == 0){
                                JOptionPane.showMessageDialog(getVistaPEPS(), "Carga completa");
                                break;
                            }      
                            cantidad = entry.getValue().agregarSalida(d, new Stock(cantidad));
                        }
                        getVistaPEPS().getjTF_UnidadesS().setText("");
                        cargarDatos(productoActual, null);
                    }else{
                        if(getVistaPEPS().getjTF_UnidadesS().getText().isEmpty()){
                            JOptionPane.showMessageDialog(getVistaPEPS(), "Campos vacios.", "PEPS", 0);
                        }else if(Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText()) == 0){
                            JOptionPane.showMessageDialog(getVistaPEPS(), "La cantidad debe ser superior a 0", "PEPS", 0);
                        }
                    }
                }
                break;
        }
    }

    public VistaPEPS getVistaPEPS() {
        return controladorPrincipal.getVistaPEPS();
    }

}
