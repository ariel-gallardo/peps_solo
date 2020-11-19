/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frm.oce.peps.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

public final class Stock implements Serializable {

    private long id;
    private long cantidad;
    private Date fecha_baja;
    private Valor valor;
    private TreeMap<Date, Stock> salidas;

    public Stock() {
        salidas = new TreeMap<>();
    }

    public Stock(long cantidad) {
        this.cantidad = cantidad;
        salidas = new TreeMap<>();
    }

    public Stock(long cantidad, Valor valor) {
        this.cantidad = cantidad;
        this.valor = valor;
        salidas = new TreeMap<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }

    public Valor getValor() {
        return valor;
    }

    public void setValor(Valor valor) {
        this.valor = valor;
    }

    public Date getFecha_baja() {
        return fecha_baja;
    }

    public void setFecha_baja(Date fecha_baja) {
        this.fecha_baja = fecha_baja;
    }

    public boolean hasSalidas() {
        return salidas.size() > 0;
    }

    public long isDisponible() {
        long _Cantidad = 0L;
        if (getFecha_baja() == null) {
            if (salidas.values().size() > 0) {
                for (Stock value : salidas.values()) {
                    _Cantidad += value.getCantidad();
                }
            }
        }
        return _Cantidad;
    }

    public long agregarSalida(Date date, Stock stock) {
        if (getFecha_baja() == null) {
            long cuenta = 0L;
            stock.setValor(valor);
            stock.setFecha_baja(new Date());   
            
            if (stock.getCantidad() > getCantidad() && isDisponible() >= 0 && isDisponible() <= getCantidad()) {   //stock.getCantidad() > getCantidad() && 
                cuenta = stock.getCantidad() >= getCantidad() ? stock.getCantidad() - getCantidad() : getCantidad() - stock.getCantidad(); //stock.getCantidad() - getCantidad();           
                stock.setCantidad(isDisponible() > 0 && isDisponible() < getCantidad() ? isDisponible() : getCantidad());
            }
            salidas.put(date, stock);           
            if ((isDisponible() == getCantidad())) {
                setFecha_baja(new Date());
                if (cuenta > 0L) {
                    return cuenta;
                }
            }
            return 0L;
        }
        /*
        long agregacion = _cantidad + stock.getCantidad();
        if (stock.getCantidad() <= getCantidad()) {
            if (_cantidad <= getCantidad()) {
                
                if (agregacion <= getCantidad()) {
                    stock.setId(getId());
                    stock.setFecha_baja(date);
                    stock.setValor(getValor());
                    salidas.put(date, stock);
                    if (_cantidad + stock.getCantidad() == getCantidad()) {
                        setFecha_baja(date);
                        return 0L;
                    }
                    return getCantidad() - agregacion;
                }
            }
        }*/
        return stock.getCantidad();                                                           //No entra la reserva.
    }

    public TreeMap<Date, Stock> getSalidas() {
        return salidas;
    }

    public void setSalidas(TreeMap<Date, Stock> salidas) {
        this.salidas = salidas;
    }

}
