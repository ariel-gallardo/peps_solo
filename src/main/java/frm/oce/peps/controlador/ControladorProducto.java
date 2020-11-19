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
                        new Date(120, 5, 6), new Stock(200, new Valor(1000, 10)),
                        new Date(120, 10, 8), new Stock(200, new Valor(2000, 10))
                //new Date(120, 10, 20), new Stock(80, null)
                //new Date(120, 11, 14), new Stock(200, new Valor(4000, 10)),
                //new Date(120, 11, 18), new Stock(200, new Date(120, 11, 18), null)
                ))
        ));
        productos.get(0).getStock().get(new Date(120, 10, 8)).agregarSalida(new Date(120, 10, 8), new Stock(20L));
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

            getVistaPEPS().getjL_Seleccion().setText("Producto Actual: " + getVistaPEPS().getjCB_Productos().getItemAt(productos.size() - 1));
            productoActual = productos.size() - 1;
            cargarDatos(productoActual, null);
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

    private void cargarFechas(int id) {
        getVistaPEPS().getjCB_Fecha().removeAllItems();
        String ultimaFecha = "";
        for (Map.Entry<Date, Stock> entry : productos.get(id).getStock().entrySet()) {
            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(entry.getKey());
            if (getVistaPEPS().getjCB_Fecha().getItemCount() == 0) {
                getVistaPEPS().getjCB_Fecha().addItem(fecha);
            } else if (!getVistaPEPS().getjCB_Fecha().getItemAt(getVistaPEPS().getjCB_Fecha().getItemCount() - 1).equals(fecha)) {
                getVistaPEPS().getjCB_Fecha().addItem(fecha);
            }
            ultimaFecha = fecha;
        }
        String fechaHoy = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        if (!fechaHoy.equals(ultimaFecha)) {
            getVistaPEPS().getjCB_Fecha().addItem(fechaHoy);
        }
    }

    private boolean controlarFecha(String dia, String mes, String anio, int diaPersonalizado, int mesPersonalizado, int anioPersonalizado) {

        int diaActual = Integer.parseInt(dia),
                mesActual = Integer.parseInt(mes),
                anioActual = Integer.parseInt(anio);
        return diaActual == diaPersonalizado && mesActual == mesPersonalizado && anioActual == anioPersonalizado;
    }

    private void cargarStock(int id, String fechaSeleccionada) {
        long disponibilidad = 0;
        double precio = 0.0, precioAnterior = 0.0;
        String mesAnterior = "", anioAnterior = "";
        int diaPersonalizado = 0, mesPersonalizado = 0, anioPersonalizado = 0;
        double ventaMes = 0.0;
        double ventaTotal = 0.0;

        if (fechaSeleccionada != null) {
            diaPersonalizado = Integer.parseInt(fechaSeleccionada.substring(0, 2));
            mesPersonalizado = Integer.parseInt(fechaSeleccionada.substring(3, 5));
            anioPersonalizado = Integer.parseInt(fechaSeleccionada.substring(6, 10));
        }

        if (productos.get(id).getStock() != null) {
            for (Map.Entry<Date, Stock> entry : productos.get(id).getStock().entrySet()) {

                var stock = entry.getValue();
                var fecha = entry.getKey();
                var precioActual = stock.getValor() != null ? stock.getValor().getNumero() : precioAnterior;

                String fechaActualTexto = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
                String dia = fechaActualTexto.substring(0, 2);
                String mes = fechaActualTexto.substring(3, 5);
                String anio = fechaActualTexto.substring(6, 10);

                if (mesAnterior.isEmpty() && anioAnterior.isEmpty()) {
                    mesAnterior = mes;
                    anioAnterior = anio;
                }

                if (stock.getFecha_baja() == null) {
                    if (!stock.hasSalidas()) {
                        disponibilidad += stock.getCantidad();
                    } else {
                        long cuenta = stock.getCantidad() - stock.isDisponible();
                        if (cuenta > 0) {
                            disponibilidad += cuenta;
                        }
                    }
                }

                if ((precioActual > precio || precioActual < precio) && precioActual != 0.0) {
                    precio = precioActual;
                    if (precioAnterior != precioActual && precioActual != 0.0) {
                        precioAnterior = precioActual;
                    }
                }

                for (Map.Entry<Date, Stock> salida : stock.getSalidas().entrySet()) {
                    if(salida.getKey().getMonth()+1 == Integer.parseInt(mes) && salida.getKey().getYear()+1900 == Integer.parseInt(anio)){
                        ventaMes += salida.getValue().getCantidad();
                    }
                    

                    if (salida.getValue().getFecha_baja() != null) {
                        ventaTotal += salida.getValue().getCantidad();
                    }

                }

                if (fechaSeleccionada != null) {
                    if (controlarFecha(dia, mes, anio, diaPersonalizado, mesPersonalizado, anioPersonalizado)) {
                        break;
                    }
                }
                mesAnterior = mes;
                anioAnterior = anio;
            }
        }

        getVistaPEPS().getjTF_Stock().setText(String.valueOf(disponibilidad));
        getVistaPEPS().getjTF_Precio().setText(String.valueOf(precio));
        getVistaPEPS().getjTF_VentaMes().setText(String.valueOf(ventaMes));
        getVistaPEPS().getjTF_VentaTotal().setText(String.valueOf(ventaTotal));
        if (fechaSeleccionada != null) {
            getVistaPEPS().getjCB_Fecha().setSelectedIndex(seleccionado);
        }
    }

    private void cargarDatos(int id, String fecha) {
        if (!productos.isEmpty()) {
            cargarProductos();
            cargarFechas(id);
            cargarStock(id, fecha);
            getVistaPEPS().getjCB_Productos().setSelectedIndex(productoActual);
            getVistaPEPS().getjL_Seleccion().setText("Producto actual: " + getVistaPEPS().getjCB_Productos().getItemAt(productoActual));
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
                    productoActual = getVistaPEPS().getjCB_Productos().getSelectedIndex();
                    cargarDatos(getVistaPEPS().getjCB_Productos().getSelectedIndex(), null);
                    getVistaPEPS().getjCB_Fecha().setSelectedIndex(getVistaPEPS().getjCB_Fecha().getItemCount() - 1);
                    getVistaPEPS().getjCB_Productos().setSelectedIndex(productoActual);
                } else {
                    JOptionPane.showMessageDialog(getVistaPEPS(), "Debe seleccionar algun producto", "PEPS", 0);
                }
                break;
            case "Ver":
                if (getVistaPEPS().getjCB_Fecha().getItemCount() > 0) {
                    seleccionado = getVistaPEPS().getjCB_Fecha().getSelectedIndex();
                    cargarDatos(productoActual, getVistaPEPS().getjCB_Fecha().getItemAt(seleccionado));
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
                        getVistaPEPS().getjTF_PrecioUR().setText("");
                        getVistaPEPS().getjTF_UnidadesR().setText("");
                    } else {
                        if (productos.get(productoActual).getStock().isEmpty() && getVistaPEPS().getjTF_PrecioUR().getText().isEmpty()) {
                            JOptionPane.showMessageDialog(getVistaPEPS(), "La primera recarga debe tener un valor", "PEPS", 0);
                            getVistaPEPS().getjTF_PrecioUR().setText("");
                            return;
                        }
                        productos.get(productoActual).addStock(tDate, new Stock(Long.parseLong(getVistaPEPS().getjTF_UnidadesR().getText()), getVistaPEPS().getjTF_PrecioUR().getText().equals("") ? null : new Valor(Long.parseLong(getVistaPEPS().getjTF_PrecioUR().getText()))));
                        getVistaPEPS().getjTF_PrecioUR().setText("");
                        getVistaPEPS().getjTF_UnidadesR().setText("");
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
                if (e.getSource().equals(getVistaPEPS().getjB_Salida())) {
                    if (!getVistaPEPS().getjTF_UnidadesS().getText().isEmpty() && Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText()) != 0) {
                        if (Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText()) <= Long.parseLong(getVistaPEPS().getjTF_Stock().getText())) {
                            long cantidad = Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText());
                            Date d = new Date();
                            d.setHours(0);
                            d.setMinutes(0);
                            d.setSeconds(0);
                            for (Map.Entry<Date, Stock> entry : productos.get(productoActual).getStock().entrySet()) {
                                /*if (cantidad == 0) {
                                JOptionPane.showMessageDialog(getVistaPEPS(), "Carga completa");
                                break;
                            }*/
                                cantidad = entry.getValue().agregarSalida(d, new Stock(cantidad));
                            }
                            getVistaPEPS().getjTF_UnidadesS().setText("");
                            cargarDatos(productoActual, null);
                            getVistaPEPS().getjCB_Fecha().setSelectedIndex(getVistaPEPS().getjCB_Fecha().getItemCount() - 1);
                        } else {
                            JOptionPane.showMessageDialog(getVistaPEPS(), "La cantidad supera al disponible", "PEPS", 0);
                        }
                    } else {
                        if (getVistaPEPS().getjTF_UnidadesS().getText().isEmpty()) {
                            JOptionPane.showMessageDialog(getVistaPEPS(), "Campos vacios.", "PEPS", 0);
                        } else if (Long.parseLong(getVistaPEPS().getjTF_UnidadesS().getText()) == 0) {
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
