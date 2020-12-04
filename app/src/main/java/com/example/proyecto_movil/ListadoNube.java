package com.example.proyecto_movil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_movil.model.Item;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListadoNube extends AppCompatActivity {

    private List<Item> listadoItem = new ArrayList<Item>();
    ArrayAdapter<Item> arrayAdapterItem;
    ListView lv_datos;
    TextView tv_menureturn;

    // Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_listado_nube );

        lv_datos = findViewById(R.id.lv_datos);
        tv_menureturn = findViewById(R.id.tv_menureturn);

        tv_menureturn.setPaintFlags(tv_menureturn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        inicializarFirebase();
        listarDatosNube();

        tv_menureturn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(ListadoNube.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } );

    }

    private void listarDatosNube() {
        databaseReference.child("Item").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listadoItem.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Item item = objSnaptshot.getValue(Item.class);
                    listadoItem.add(item);

                    arrayAdapterItem = new ArrayAdapter<Item>(ListadoNube.this, android.R.layout.simple_list_item_1, listadoItem);
                    lv_datos.setAdapter(arrayAdapterItem);

                    lv_datos.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final Item it = listadoItem.get(position);

                            new AlertDialog.Builder(ListadoNube.this)
                                    .setIcon( android.R.drawable.ic_delete )
                                    .setTitle( "¿Está seguro?")
                                    .setMessage( "¿Desea eliminar a " + it.getDescripcion() + " de la lista?" )
                                    .setPositiveButton( "Sí", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            eliminarDatos(it.getUid());
                                        }
                                    } )
                                    .setNegativeButton( "No", null )
                                    .show();

                        }
                    } );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void eliminarDatos(String uid){
        databaseReference.child("Item").child(uid).removeValue();
        listarDatosNube();
        Toast.makeText(getApplicationContext(), "Item eliminado" , Toast.LENGTH_SHORT).show();

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
}