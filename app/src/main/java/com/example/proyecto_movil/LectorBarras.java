package com.example.proyecto_movil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class LectorBarras extends AppCompatActivity {

    Button btn_leerCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_lector_barras );

        // Inicializar el botón
        btn_leerCodigo = findViewById( R.id.btn_leerCodigo );

        // Evento click del botón
        btn_leerCodigo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ejecutar procedimiento de escaneo
                scanCode();

            }
        } );


    }

    // Procedimiento de escaneo
    private void scanCode() {

        IntentIntegrator integrator = new IntentIntegrator( this );
        // Abrir Activity
        integrator.setCaptureActivity( Scanner.class );
        integrator.setOrientationLocked( false );
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt( "Escaneando código" );
        integrator.initiateScan();

    }

    // Procedimiento que recepciona
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult( requestCode, resultCode, data );
        if (result != null) {
            if (result != null) {
                if (result.getContents() != null) {
                    // Crear un dialogo o modal
                    AlertDialog.Builder builder = new AlertDialog.Builder( this );
                    // Mostrar mensaje de respuesta de escaneo
                    builder.setMessage( result.getContents() );
                    builder.setTitle( "Resultado del Escaneo" );
                    builder.setPositiveButton( "Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent().setClass( LectorBarras.this, MostrarInformacion.class );
                            intent.putExtra( "dato_capturado", result.getContents() );
                            startActivity( intent );

                        }
                    } ).setNegativeButton( "Escanear de nuevo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            scanCode();
                        }
                    } );

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText( this, "No se encontró resultados", Toast.LENGTH_LONG ).show();
                }
            } else {
                super.onActivityResult( requestCode, resultCode, data );
            }
        }
    }


}