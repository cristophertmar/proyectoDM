package com.example.proyecto_movil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_movil.model.ConexionSQLiteHelper;
import com.example.proyecto_movil.model.Item;
import com.example.proyecto_movil.utilidades.Utilidades;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class MostrarInformacion extends AppCompatActivity {

    Bundle datos;
    Button btn_guardarLocal, btn_guardarNube;
    String item_guardar;

    // Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView tv_mostrardato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mostrar_informacion );

        tv_mostrardato = findViewById(R.id.tv_mostrardato);
        btn_guardarLocal = findViewById(R.id.btn_guardarLocal);
        btn_guardarNube = findViewById(R.id.btn_guardarNube);

        datos = getIntent().getExtras();
        item_guardar = datos.getString("dato_capturado");

        inicializarFirebase();

        tv_mostrardato.setText( item_guardar );

        btn_guardarLocal.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item item = new Item();
                item.setUid( UUID.randomUUID().toString());
                item.setDescripcion( item_guardar );
                item.setNube( 0 );

                ConexionSQLiteHelper conn = new ConexionSQLiteHelper( getApplicationContext(), "bd_items", null, 1 );
                SQLiteDatabase db = conn.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put( Utilidades.CAMPO_UID, item.getUid());
                values.put( Utilidades.CAMPO_DESCRIP, item.getDescripcion());
                values.put( Utilidades.CAMPO_NUBE, item.getNube());

                db.insert( Utilidades.TABLA, Utilidades.CAMPO_ID, values );

                Toast.makeText(getApplicationContext(), "Guardado localmente ", Toast.LENGTH_SHORT).show();
                db.close();

                Intent intent = new Intent().setClass(MostrarInformacion.this, ListadoLocal.class);
                startActivity(intent);
                finish();


            }
        } );

        btn_guardarNube.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item item = new Item();
                item.setUid( UUID.randomUUID().toString());
                item.setDescripcion( item_guardar );
                item.setNube( 1 );

                databaseReference.child("Item").child(item.getUid()).setValue(item);

                Toast.makeText(getApplicationContext(), "Guardado en la nube", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent().setClass(MostrarInformacion.this, ListadoNube.class);
                startActivity(intent);
                finish();

            }
        } );





    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
}