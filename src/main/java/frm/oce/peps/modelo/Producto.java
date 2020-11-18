/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frm.oce.peps.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;
/**
 *
 * @author ariel
 */

public class Producto implements Serializable {

    private int id;
    private String nombre;
    private TreeMap<Date,Stock> stock;

    public Producto() {
    }

    public Producto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    

    public Producto(String nombre) {
        this.nombre = nombre;
    }

    public Producto(String nombre, TreeMap<Date, Stock> stock) {
        this.nombre = nombre;
        this.stock = stock;
    }

    public Producto(int id, String nombre, TreeMap<Date, Stock> stock) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TreeMap<Date, Stock> getStock() {
        return stock;
    }

    public void setStock(TreeMap<Date, Stock> stock) {
        this.stock = stock;
    }
    
    public void addStock(Date date, Stock stock){
        getStock().put(date, stock);
    }


}
