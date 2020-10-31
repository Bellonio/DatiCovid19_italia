package com.bellone.daticovid19_italia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LicenzaActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblRepGitHub = null;
    private TextView lblUrlLicenza = null;
    private Button btnTornaIndietro = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenza);

        lblRepGitHub = findViewById(R.id.lblUrlRepGitHub_Licenza);
        lblUrlLicenza = findViewById(R.id.lblUrlLicenza_Licenza);
        btnTornaIndietro = findViewById(R.id.btnTornaIndietro_Licenza);
    }

    @Override
    protected void onResume() {
        super.onResume();

        lblRepGitHub.setOnClickListener(this);
        lblUrlLicenza.setOnClickListener(this);
        btnTornaIndietro.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lblUrlRepGitHub_Licenza:
            case R.id.lblUrlLicenza_Licenza:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", ((TextView)v).getText().toString());
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "INDIRIZZO COPIATO\nNEGLI APPUNTI.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnTornaIndietro_Licenza:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}