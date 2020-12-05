package com.example.proyecto_movil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_movil.model.ConexionSQLiteHelper;
import com.example.proyecto_movil.model.Item;
import com.example.proyecto_movil.utilidades.Utilidades;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListadoLocal extends AppCompatActivity {

    ConexionSQLiteHelper conn;
    ArrayAdapter<Item> arrayAdapterItem;
    ListView lv_datos;

    Button btn_exportarNube;
    TextView tv_menureturn;

    // Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_listado_local );

        lv_datos = findViewById( R.id.lv_datos );
        btn_exportarNube = findViewById( R.id.btn_exportarNube );
        tv_menureturn = findViewById( R.id.tv_menureturn );

        tv_menureturn.setPaintFlags( tv_menureturn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG );

        conn = new ConexionSQLiteHelper( getApplicationContext(), "bd_items", null, 1 );

        inicializarFirebase();

        // procedimiento listar
        listarDatos();

        // Evento exportar
        btn_exportarNube.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportarNube();
            }
        } );

        tv_menureturn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass( ListadoLocal.this, MainActivity.class );
                startActivity( intent );
                finish();
            }
        } );


    }

    private void listarDatos() {
        // Creo array de tipo item
        final ArrayList<Item> listado = new ArrayList<Item>();
        // crear variable de conexion
        SQLiteDatabase db = conn.getReadableDatabase();

        // ejecutar sentencia TRANSACT-SQL
        Cursor cursor = db.rawQuery( "SELECT * FROM " + Utilidades.TABLA, null );

        // ciclo
        while (cursor.moveToNext()) {

            // Colocar datos del cursor en un objeto de tipo item
            Item itm = new Item();
            itm.setId( cursor.getInt( 0 ) );
            itm.setUid( cursor.getString( 1 ) );
            itm.setDescripcion( cursor.getString( 2 ) );
            itm.setNube( cursor.getInt( 3 ) );

            // agregar objeto al listado
            listado.add( itm );
        }

        // llenar datos de mi lista al list view
        arrayAdapterItem = new ArrayAdapter<Item>( this, android.R.layout.simple_list_item_1, listado );
        lv_datos.setAdapter( arrayAdapterItem );

        // Se ejecuta cuando damos click a un item del list view
        lv_datos.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Obtener objero seleccionado en la lista del listview
                final Item it = listado.get( position );

                // Crear un dialogo o modal
                new AlertDialog.Builder( ListadoLocal.this )
                        .setIcon( android.R.drawable.ic_delete )
                        .setTitle( "Está seguro!" )
                        .setMessage( "¿Desea eliminar a " + it.getDescripcion() + " de la lista?" )
                        .setPositiveButton( "Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Elimar localmente
                                eliminarDatos( it.getId() );
                            }
                        } )
                        .setNegativeButton( "No", null )
                        .show();

            }
        } );

    }

    // método para exporar a firebase
    private void exportarNube() {

        SQLiteDatabase db = conn.getReadableDatabase();
        String[] parametros = {String.valueOf( 0 )};

        // ejecuto sentencia para obtener registros con nube de estado false o 0
        Cursor cursor = db.rawQuery( "SELECT * FROM " + Utilidades.TABLA + " WHERE " + Utilidades.CAMPO_NUBE + "=? ", parametros );

        // Crear ciclo
        while (cursor.moveToNext()) {

            // Crear objeto item
            Item itm = new Item();
            itm.setId( cursor.getInt( 0 ) );
            itm.setUid( cursor.getString( 1 ) );
            itm.setDescripcion( cursor.getString( 2 ) );
            itm.setNube( 1 );

            // Guardar en firebase
            databaseReference.child( "Item" ).child( itm.getUid() ).setValue( itm );

            // Actualizar estado nube de mis items local
            actualizarEstadoNube( itm.getId() );

        }

        Toast.makeText( getApplicationContext(), "Exportación exitosa", Toast.LENGTH_SHORT ).show();
        listarDatos();

    }

    private void actualizarEstadoNube(int id) {
        SQLiteDatabase db = conn.getWritableDatabase();
        String[] parametros = {String.valueOf( id )};
        ContentValues values = new ContentValues();
        values.put( Utilidades.CAMPO_NUBE, 1 );

        // actualizo en mi db local
        db.update( Utilidades.TABLA, values, Utilidades.CAMPO_ID + "=?", parametros );
        db.close();


    }

    private void eliminarDatos(int id) {

        SQLiteDatabase db = conn.getWritableDatabase();
        String[] parametros = {String.valueOf( id )};

        // Ejecutar sentencia para eliminar
        db.delete( Utilidades.TABLA, Utilidades.CAMPO_ID + "=?", parametros );
        // Listar datos
        listarDatos();
        // Confirmo eliminacion
        Toast.makeText( getApplicationContext(), "Item eliminado", Toast.LENGTH_SHORT ).show();
        db.close();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp( this );
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


}