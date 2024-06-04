package com.example.candycrush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Menu extends AppCompatActivity {
    static String selectedTheme, selectedBlock, selectedTemps,nomJoueur;
    DatabaseHelper db;

    EditText nom;

    Button buttonCommencer ,buttonAfficher;


    Spinner spinnerTheme, spinnerBlock, spinnerTemps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = new DatabaseHelper(this);

        nom = findViewById(R.id.nom);

        buttonCommencer = findViewById(R.id.commencerBTN);
        buttonAfficher = findViewById(R.id.afficherBTN);

        spinnerTheme = findViewById(R.id.theme);
        String[] themes = {"Classic", "Halloween", "Special"};

        ArrayAdapter<String> adapterTheme = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themes);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(adapterTheme);

        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedTheme = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        spinnerTemps = findViewById(R.id.temps);
        String[] temps = {"10 s", "30 s", "60 s", "120 s"};

        ArrayAdapter<String> adapterTemps = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, temps);
        adapterTemps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemps.setAdapter(adapterTemps);

        spinnerTemps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedTemps = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        buttonCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomJoueur = nom.getText().toString();
                if(nomJoueur.isEmpty()){
                    Toast.makeText(Menu.this, "Veuillez entrer votre nom!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        buttonAfficher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, ScoreActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MediaPlayerService.class));
    }
}