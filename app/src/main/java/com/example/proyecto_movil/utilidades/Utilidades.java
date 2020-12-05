package com.example.proyecto_movil.utilidades;

public class Utilidades {

    // Constantes campos tabla
    public static final String TABLA = "items";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_UID = "uid";
    public static final String CAMPO_DESCRIP = "nombre";
    public static final String CAMPO_NUBE = "nube";

    public static final String CREAR_TABLA = "CREATE TABLE " + TABLA + " (" +
            CAMPO_ID + " integer primary key autoincrement, " +
            CAMPO_UID + " text, " +
            CAMPO_DESCRIP + " text, " +
            CAMPO_NUBE + " integer)";
}
