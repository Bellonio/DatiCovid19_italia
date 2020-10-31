package com.bellone.daticovid19_italia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Activity per gestire le impostazioni dell'app.
 */
public class ImpostazioniActivity extends AppCompatActivity implements View.OnClickListener {

        //Possibili quantita' giorni da leggere
    private final String[] QUANTITA_NAZ_REG = new String[]{
        "Tutti",
        "1 mese",
        "2 mesi",
        "3 mesi",
        "4 mesi",
        "5 mesi",
        "6 mesi",
    };
    public static final String[] VAL_ANDAMENTO_DEFAULT = new String[]{
        "Nazionale",
        "Regionale"
    };

    private RadioGroup rdGroupNazReg = null;
    private Spinner spnNaz = null;
    private Spinner spnReg = null;
    private Button btnTornaIndietro = null;
    private TextView lblEmail_di_contatto = null;
    private TextView lblUrl_accountGitHub = null;


    private String valAndamentoDefault_scelto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        rdGroupNazReg = findViewById(R.id.rdGroupNazReg_Impostazioni);
        spnNaz = findViewById(R.id.spnQtaNaz_Impostazioni);
        spnReg = findViewById(R.id.spnQtaReg_Impostazioni);
        btnTornaIndietro = findViewById(R.id.btnTornaIndietro_Impostazioni);
        lblEmail_di_contatto = findViewById(R.id.lblEmail_di_contatto_Impostazioni);
        lblUrl_accountGitHub = findViewById(R.id.lblUrl_accountGitHub_Impostazioni);


        ArrayList<String> arrayList_valori = new ArrayList<>();
        arrayList_valori.addAll(Arrays.asList(QUANTITA_NAZ_REG));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext()
                    , android.R.layout.simple_spinner_dropdown_item, arrayList_valori);


        spnNaz.setAdapter(adapter);
        spnReg.setAdapter(adapter);

        caricaImpostazioni();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,1, 1, "Licenza");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == 1){
            Intent intent = new Intent(getApplicationContext(), LicenzaActivity.class);
            startActivity(intent);
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        rdGroupNazReg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rdBtnNazionale_Impostazioni){
                    valAndamentoDefault_scelto = VAL_ANDAMENTO_DEFAULT[0];
                }else if(checkedId == R.id.rdBtnRegionale_Impostazioni){
                    valAndamentoDefault_scelto = VAL_ANDAMENTO_DEFAULT[1];
                }
            }
        });
        btnTornaIndietro.setOnClickListener(this);

        lblEmail_di_contatto.setOnClickListener(this);
        lblUrl_accountGitHub.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        salvaImpostazioni();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        String clipID = "", clipMsg = "";
        switch (v.getId()){
            case R.id.lblEmail_di_contatto_Impostazioni:
                clipID = "email_di_contatto";
                clipMsg = ((TextView)v).getText().toString();
                break;
            case R.id.lblUrl_accountGitHub_Impostazioni:
                clipID = "url_accountGitHub";
                clipMsg = ((TextView)v).getText().toString();
                break;
            case R.id.btnTornaIndietro_Impostazioni:
                onBackPressed();
                break;
        }
        if(clipID.length() > 0 && clipMsg.length() > 0){
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(clipID, clipMsg);
            clipboardManager.setPrimaryClip(clip);

            Toast.makeText(getApplicationContext(), "INDIRIZZO COPIATO\nNEGLI APPUNTI.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo che va a leggere le impostazioni e ne carica i valori nelle varie View.
     */
    private void caricaImpostazioni(){
        Impostazioni impostazioni = MainActivity.getGestoreFile().readImpostazioni();

        if(impostazioni != null){
            if(impostazioni.getAndamentoDefault().equals("null") ||
                    impostazioni.getAndamentoDefault().equals(VAL_ANDAMENTO_DEFAULT[0])){

                ((RadioButton)rdGroupNazReg.getChildAt(0)).setChecked(true);
            }else{
                ((RadioButton)rdGroupNazReg.getChildAt(1)).setChecked(true);
            }

            setQtaGiorni(impostazioni);
        }else{
            Toast.makeText(getApplicationContext()
                    , "Si e' verificato un errore nella\nlettura delle impostazioni", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Metodo che prende i vari valori scelti, crea l'impostazione e
     * richiama il metodo del gestore del file per salvare le impostazioni.
     */
    private void salvaImpostazioni(){
        String qtaNaz = (String)spnNaz.getSelectedItem();
        String qtaReg = (String)spnReg.getSelectedItem();

        int qtaAndamentiNaz = getQtaGiorni(qtaNaz);
        int qtaAndamentiReg = getQtaGiorni(qtaReg);

        Impostazioni impostazioni = new Impostazioni(valAndamentoDefault_scelto, qtaAndamentiNaz, qtaAndamentiReg);
        MainActivity.setImpostazioni(impostazioni);

        MainActivity.getGestoreFile().salvaImpostazioni(impostazioni);
        Toast.makeText(getApplicationContext(), "IMPOSTAZIONI SALVATE", Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo che leggendo i valori delle impostazioni seleziona la voce
     * corretta degli spinner relativi alla qta di giorni da leggere
     */
    private void setQtaGiorni(Impostazioni impostazioni){
            /*Metodo richiamato all'interno di un if che controlla
                che le impostazioni non siano null. Percio'
                nessun controllo da fare*/

        int qtaMesi;
        String mesi;

        if(impostazioni.getQtaAndamentiNaz() != -1) {
            qtaMesi = impostazioni.getQtaAndamentiNaz()/30;
            mesi = QUANTITA_NAZ_REG[qtaMesi];

            if(mesi.equals(QUANTITA_NAZ_REG[1])){
                spnNaz.setSelection(1);
            }else if(mesi.equals(QUANTITA_NAZ_REG[2])){
                spnNaz.setSelection(2);
            }else if(mesi.equals(QUANTITA_NAZ_REG[3])){
                spnNaz.setSelection(3);
            }else if(mesi.equals(QUANTITA_NAZ_REG[4])) {
                spnNaz.setSelection(4);
            }else if(mesi.equals(QUANTITA_NAZ_REG[5])){
                spnNaz.setSelection(5);
            }else if(mesi.equals(QUANTITA_NAZ_REG[6])){
                spnNaz.setSelection(6);
            }
        }else{
            spnNaz.setSelection(0);
        }

        if(impostazioni.getQtaAndamentiReg() != -1) {
            qtaMesi = impostazioni.getQtaAndamentiReg()/30;
            mesi = QUANTITA_NAZ_REG[qtaMesi];

            if(mesi.equals(QUANTITA_NAZ_REG[1])){
                spnReg.setSelection(1);
            }else if(mesi.equals(QUANTITA_NAZ_REG[2])){
                spnReg.setSelection(2);
            }else if(mesi.equals(QUANTITA_NAZ_REG[3])){
                spnReg.setSelection(3);
            }else if(mesi.equals(QUANTITA_NAZ_REG[4])) {
                spnReg.setSelection(4);
            }else if(mesi.equals(QUANTITA_NAZ_REG[5])){
                spnReg.setSelection(5);
            }else if(mesi.equals(QUANTITA_NAZ_REG[6])){
                spnReg.setSelection(6);
            }
        }else{
            spnReg.setSelection(0);
        }
    }

    /**
     * Metodo che preso uno dei valori presenti nella lista costante QUANTITA_NAZ_REG,
     * restituisce il corrispondente numero di giorni per la stringa data.
     */
    private int getQtaGiorni(String qtaNazReg){
        if(qtaNazReg.equals(QUANTITA_NAZ_REG[0])){
            return -1;
        }else if(qtaNazReg.equals(QUANTITA_NAZ_REG[1])){
            return 30;
        }else if(qtaNazReg.equals(QUANTITA_NAZ_REG[2])){
            return 60;
        }else if(qtaNazReg.equals(QUANTITA_NAZ_REG[3])){
            return 90;
        }else if(qtaNazReg.equals(QUANTITA_NAZ_REG[4])){
            return 120;
        }else if(qtaNazReg.equals(QUANTITA_NAZ_REG[5])){
            return 150;
        }else{
            return 180;
        }
    }
}