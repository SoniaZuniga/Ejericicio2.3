package com.example.ejericicio23;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.ejericicio23.Model.Fotografias;
import com.example.ejericicio23.config.DB;
import com.example.ejericicio23.config.SQLiteConexion;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListadodeFotos extends AppCompatActivity {
    SQLiteConexion con;
    Button refresh, nuevo_item;
    List<Fotografias> fotos_list;
    List<String> Arreglo;
    ListView listFotos;
    Integer id_foto_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listadode_fotos);
        con = new SQLiteConexion(this, DB.DBnombre, null,1);

        refresh = (Button) findViewById(R.id.btnRefresh);
        nuevo_item = (Button) findViewById(R.id.btnNuevo);
        listFotos = (ListView) findViewById(R.id.list_fotos);

        ObtenerFotos();

        nuevo_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObtenerFotos();
            }
        });

        listFotos.setOnItemClickListener((parent, view, position, id) -> {
        });
    }
    private void ObtenerFotos() {
        SQLiteDatabase db = con.getReadableDatabase();
        Fotografias foto;
        fotos_list = new ArrayList<>();

        Cursor cursor = db.rawQuery(DB.SelectAllFotos, null);

        while (cursor.moveToNext()) {
            foto = new Fotografias();
            foto.setId_foto(cursor.getInt(0));
            foto.setImagen(cursor.getString(1));
            foto.setDescripcion(cursor.getString(2));

            fotos_list.add(foto);
        }
        cursor.close();
        LlenarDatos();
    }

    private void LlenarDatos() {
        Arreglo = new ArrayList<String>();

        for(int x = 0; x < fotos_list.size(); x++){
            Arreglo.add(fotos_list.get(x).getId_foto() + " | " +
                    fotos_list.get(x).getDescripcion());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arreglo);
        listFotos.setAdapter(adapter);
    }

}