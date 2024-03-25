package com.example.ejericicio23;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ejericicio23.config.DB;
import com.example.ejericicio23.config.SQLiteConexion;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION =  100;
    Button nueva_foto;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    ImageView foto;
    Button guardar, lista_fotos, drop;
    TextView descripcion_txt;
    String foto_codificada, descripcion;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nueva_foto = findViewById(R.id.agregarfoto_btn);
        foto = findViewById(R.id.imageView);
        guardar = findViewById(R.id.Salvar);
        lista_fotos = findViewById(R.id.fotografias_btn);
        descripcion_txt = findViewById(R.id.editText);

        nueva_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisoCamara();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDB();
            }
        });

        lista_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listaActivity = new Intent(getApplicationContext(), ListaFotosActivity.class);
                startActivity(listaActivity);
            }
        });

    }
    private void guardarDB() {
        if(foto_codificada != ""){
            if(descripcion_txt !=null){

                SQLiteConexion con = new SQLiteConexion(this, DB.DBnombre, null, 1);
                SQLiteDatabase db = con.getWritableDatabase();

                descripcion = descripcion_txt.getText().toString();

                ContentValues datos = new ContentValues();
                datos.put(DB.imagen, foto_codificada);
                datos.put(DB.descripcion, descripcion);

                long resultado = db.insert(DB.TablaFotos, DB.id_foto, datos);

                if(resultado != 1){
                    Toast.makeText(getApplicationContext(),"Item registrado correctamente - ID"+resultado, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Erro al registrar el nuevo registro", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(),"Debe Colocar una descripcion", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Debe Tomar una Foto Primero", Toast.LENGTH_LONG).show();
        }

    }
    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }else{
            Toast.makeText(getApplicationContext(),"Error al abrir la Camara", Toast.LENGTH_LONG).show();
        }
    }

    private void permisoCamara() {

        String[] permissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (allPermissionsGranted) {
            tomarFoto();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(getApplicationContext(),"Se requiere permiso de la Camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                foto.setImageBitmap(imageBitmap);

                Bitmap foto_bitmpa = ((BitmapDrawable) foto.getDrawable()).getBitmap();
                foto_codificada = convertirImagen(foto_bitmpa);
            }
        }
    }

    private String convertirImagen(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}