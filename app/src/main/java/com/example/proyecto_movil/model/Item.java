package com.example.proyecto_movil.model;

public class Item {

    private int id;
    private String uid;
    private String descripcion;
    private int nube;

    public Item() {
    }

    public Item(int id, String uid, String descripcion, int nube) {
        this.id = id;
        this.uid = uid;
        this.descripcion = descripcion;
        this.nube = nube;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getNube() {
        return nube;
    }

    public void setNube(int nube) {
        this.nube = nube;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
