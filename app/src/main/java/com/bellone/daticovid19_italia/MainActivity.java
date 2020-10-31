package com.bellone.daticovid19_italia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
                                                        , AdapterView.OnItemSelectedListener {


    public static final String[] URL = new String[]{
            "https://github.com/pcm-dpc/COVID-19/raw/master/dati-json/dpc-covid19-ita-andamento-nazionale.json",
            "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json"
    };

    private ListView listViewAndamenti = null;
    private RadioGroup rdGroupNazReg = null;
    private RadioGroup rdGroupTipoOrdine = null;
    private Spinner spnOrdinamenti = null;
    private CheckBox checkBoxAlfab = null;
    private TextView lblTotaleCasi = null;
    private TextView lblTotaleDecessi = null;
    private Spinner spnRegioni = null;


    private Andamenti_nazionali_Covid19 andamentiNazionaliCovid19;
    private Andamenti_regionali_Covid19 andamentiRegionaliCovid19;


    private static GestoreFile gestoreFile = null;
    private static Impostazioni impostazioni = null;


    private RequestQueue queue;
    private String url_usato = null;


    private String data_di_oggi = null;
    private int qtaGiorniTraDate = 0;


    private String tipoOrdinamento = null;
    private String ordinamento = null;
    private boolean ordine_perGiornata = false;


    public static GestoreFile getGestoreFile(){ return gestoreFile; }
    public static void setImpostazioni(Impostazioni imp){ impostazioni = imp; }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdFormatter = new SimpleDateFormat("dd-MM-yyyy");
        data_di_oggi = sdFormatter.format(Calendar.getInstance().getTime());


        listViewAndamenti = findViewById(R.id.listViewAndamenti_Main);
        rdGroupNazReg = findViewById(R.id.rdGroupNazReg_Main);
        rdGroupTipoOrdine = findViewById(R.id.rdGroupTipoOrdine_Main);
        spnOrdinamenti = findViewById(R.id.spnOrdinamento_Main);
        checkBoxAlfab = findViewById(R.id.checkBoxAlfab_Main);
        lblTotaleCasi = findViewById(R.id.lblTotaleCasi_Main);
        lblTotaleDecessi = findViewById(R.id.lblTotaleDecessi_Main);
        spnRegioni = findViewById(R.id.spnSceltaRegione_Main);


        queue = Volley.newRequestQueue(this);
        gestoreFile = new GestoreFile(getFilesDir().getPath()+"/");


        impostazioni = gestoreFile.readImpostazioni();
        if(impostazioni.getAndamentoDefault().equals("null") || impostazioni.getAndamentoDefault()
                .equals(ImpostazioniActivity.VAL_ANDAMENTO_DEFAULT[0])){

            url_usato = URL[0];
            ((RadioButton)rdGroupNazReg.getChildAt(0)).setChecked(true);
        }else{
            modificaTestoRadioBtn_e_checkBox(getApplicationContext().getString(R.string.rdBtnAsc_Main)
                    , getApplicationContext().getString(R.string.rdBtnDisc_Main), false);

            url_usato = URL[1];
            ((RadioButton)rdGroupNazReg.getChildAt(1)).setChecked(true);
        }


        ordine_perGiornata = true;
        tipoOrdinamento = OrdinamentiAndamentiCovid19.TIPO_ORDINAMENTI[1];      //DISCENDENTE

        modificaArrayPerLoSpinner(false);

        request_queque();
        ordina_andamenti(0, tipoOrdinamento);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, 1, "Impostazioni");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == 1){
            Intent intent = new Intent(this, ImpostazioniActivity.class);
            startActivity(intent);
            return true;
        }else{ return false; }
    }

    @Override
    protected void onResume() {
        super.onResume();

        rdGroupNazReg.setOnCheckedChangeListener(this);
        rdGroupTipoOrdine.setOnCheckedChangeListener(this);

        spnOrdinamenti.setOnItemSelectedListener(this);
        spnRegioni.setOnItemSelectedListener(this);

        checkBoxAlfab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ordine_perGiornata = isChecked;
                ordina_andamenti(spnOrdinamenti.getSelectedItemPosition(), tipoOrdinamento);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
            //Rimuovo tutti gli elementi dalla listview rimuovendo l'adapter
        listViewAndamenti.setAdapter(null);

        switch (group.getId()){
            case R.id.rdGroupNazReg_Main:

                if (checkedId == R.id.rdBtnNazionale_Main) {
                    modificaTestoRadioBtn_e_checkBox(getApplicationContext().getString(R.string.rdBtnAscendente_Main)
                            , getApplicationContext().getString(R.string.rdBtnDiscendente_Main), false);

                    url_usato = URL[0];
                } else {
                    /* "spnOrdinamenti.getSelectedItemPosition() != 0", consigliato dalla IDE, e' una
                            condizione che da true o false percio' proprio quello che serve al metodo */
                    modificaTestoRadioBtn_e_checkBox(getApplicationContext().getString(R.string.rdBtnAsc_Main)
                            , getApplicationContext().getString(R.string.rdBtnDisc_Main),
                            spnOrdinamenti.getSelectedItemPosition() != 0);

                    url_usato = URL[1];
                }

                request_queque();

                break;
            case R.id.rdGroupTipoOrdine_Main:

                if(checkedId == R.id.rdBtnAscendente_Main){
                    tipoOrdinamento = OrdinamentiAndamentiCovid19.TIPO_ORDINAMENTI[0];
                }else{
                    tipoOrdinamento = OrdinamentiAndamentiCovid19.TIPO_ORDINAMENTI[1];
                }
                modificaArrayPerLoSpinner(false);

                ordina_andamenti(spnOrdinamenti.getSelectedItemPosition(), tipoOrdinamento);

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spnOrdinamento_Main){
            if(url_usato.equals(URL[1])){
                spnRegioni.setVisibility(View.INVISIBLE);
                ordine_perGiornata = false;
                checkBoxAlfab.setChecked(false);

                if(position == 0 || position == 1){
                    checkBoxAlfab.setVisibility(View.INVISIBLE);

                    if(position == 1){
                        spnRegioni.setVisibility(View.VISIBLE);
                    }
                }else{
                    checkBoxAlfab.setVisibility(View.VISIBLE);
                }
            }else{
                if(position == 1){
                    if(spnRegioni.getVisibility() == View.INVISIBLE) {
                        spnRegioni.setVisibility(View.VISIBLE);
                    }
                    spnRegioni.setSelection(0);
                }
                if(spnRegioni.getVisibility() == View.VISIBLE){
                    spnRegioni.setVisibility(View.INVISIBLE);
                }
            }
            ordina_andamenti(position, tipoOrdinamento);
        }else{
            String regione_scelta;
            if(ordinamento != null) {
                if(url_usato.equals(URL[1]) && ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[1])){
                    spnRegioni.setVisibility(View.VISIBLE);
                    regione_scelta = String.valueOf(spnRegioni.getSelectedItem());

                    listViewAndamenti.setSelection( andamentiRegionaliCovid19.trovaPosizioneRegione(regione_scelta
                            , qtaGiorniTraDate-1) );
                }
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    /**
     * Metodo per ordinare la lista di andamenti, nazionali o regionali, in base all'ordinamento
     * scelto
     */
    private void ordina_andamenti(int position, String tipoOrdinamento){
        PersonalizedArrayAdapter personalizedArrayAdapter = null;

        if(andamentiNazionaliCovid19 != null || andamentiRegionaliCovid19 != null){
                //Lo spinner per gli andamentiNazionali ha una voce in meno
            if(url_usato.equals(URL[0]) && position >= 1){ position++; }

            ordinamento = OrdinamentiAndamentiCovid19.ORDINAMENTI[position];

            if(url_usato.equals(URL[0]) && andamentiNazionaliCovid19 != null){

                andamentiNazionaliCovid19.ordinamentoPer(ordinamento, tipoOrdinamento);

                personalizedArrayAdapter = new PersonalizedArrayAdapter(this,
                        R.layout.andamento_covid19_layout, andamentiNazionaliCovid19.getAndamentiGiornalieriCovid19()
                        , url_usato, ordinamento, tipoOrdinamento, ordine_perGiornata, data_di_oggi);

            }else if(url_usato.equals(URL[1]) && andamentiRegionaliCovid19 != null){

                andamentiRegionaliCovid19.ordinaAndamenti(ordinamento, tipoOrdinamento, ordine_perGiornata);

                personalizedArrayAdapter = new PersonalizedArrayAdapter(this,
                        R.layout.andamento_covid19_layout, andamentiRegionaliCovid19.getTuttiAndamentiRegionali()
                        , url_usato, ordinamento, tipoOrdinamento, ordine_perGiornata, data_di_oggi);
            }
        }
        listViewAndamenti.setAdapter(personalizedArrayAdapter);
    }


    /**
     * Metodo per cambiare le voci, o i loro ordine, di uno spinner.
     * In caso dello spinner degli ordinamenti aggiunge i valori possibili di ordinamento e
     * poi, in caso si stiano visionando gli andamenti nazionali, rimuove la voce "PER REGIONE"
     * (la seconda). Nel caso invece si stiano visionando gli andamenti delle regioni, in caso si
     * stia ordinando "PER REGIONE", aggiunge le denominazioni delle varie regioni. Inoltre, in caso
     * di ordinamento in modo "ASCENDENTE" inverte la lista delle denominazioni delle regioni.
     */
    private void modificaArrayPerLoSpinner(boolean spinnerOrdinamenti){
        if(spinnerOrdinamenti){
            ArrayList<String> ordinamenti = new ArrayList<>();
            ordinamenti.addAll( Arrays.asList(OrdinamentiAndamentiCovid19.ORDINAMENTI) );

            if(url_usato.equals(URL[0])){ ordinamenti.remove(1); }

            spnOrdinamenti.setAdapter(new ArrayAdapter<>(getApplicationContext()
                    , android.R.layout.simple_spinner_dropdown_item, ordinamenti));
        }else{
            ArrayList<String> denominazioni_regione = new ArrayList<>();
            denominazioni_regione.addAll(Arrays.asList(Andamenti_regionali_Covid19.DENOMINAZIONE_REGIONI));

            if(tipoOrdinamento != null && tipoOrdinamento.equals(OrdinamentiAndamentiCovid19.TIPO_ORDINAMENTI[0])){
                Collections.reverse(denominazioni_regione);
            }

            spnRegioni.setAdapter(new ArrayAdapter<>(getApplicationContext()
                    , android.R.layout.simple_spinner_dropdown_item, denominazioni_regione));
        }
    }

    /**
     * Metodo che modifica il testo dei radio button per l'ordinamento ascendente e discendente
     * e la visibilita della checkbox
     * @param txtAscendente         testo da mostrare per il radio button per l'ordinamento ascendente
     * @param txtDiscendente        testo da mostrare per il radio button per l'ordinamento discendente
     * @param checkBoxVisibile      true/false, se la checkbox dev'essere visibile o meno
     */
    private void modificaTestoRadioBtn_e_checkBox(String txtAscendente, String txtDiscendente
            , boolean checkBoxVisibile){

        ((RadioButton)rdGroupTipoOrdine.getChildAt(0)).setText(txtAscendente);
        ((RadioButton)rdGroupTipoOrdine.getChildAt(1)).setText(txtDiscendente);

        if(checkBoxVisibile){
            checkBoxAlfab.setVisibility(View.VISIBLE);
        }else{
            checkBoxAlfab.setChecked(false);
            checkBoxAlfab.setVisibility(View.INVISIBLE);

            spnRegioni.setVisibility(View.INVISIBLE);
        }
    }

    /* Avevo fatto una classe a parte per la gestione della coda di richieste ma c'era un problema che non
            sto qui a scrivere xke' sarebbe un po' complicato da capire. Vi basti sapere che il problema nasceva
            dal fatto che il metodo "request_queque" avviava la richiesta (e quindi eseguiva l"onResponse"
            MA poi andava avanti lasciando, immagino, l'esecuzione dell"onResponse" in background. */


    /*private void stringRequest(){
        //       SEMPLICE STRING REQUEST:
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //    ...
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }*/

    @SuppressLint("DefaultLocale")
    public void request_queque(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url_usato, null, new Response.Listener<JSONArray>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(JSONArray response) {
                try {

                    JSONObject andamento_regione_nazione;

                    if(url_usato.equals(URL[0])) {

                        andamentiNazionaliCovid19 = new Andamenti_nazionali_Covid19();
                        andamentiRegionaliCovid19 = null;

                        int qtaGiorni = 0;
                        if(impostazioni != null && impostazioni.getQtaAndamentiNaz() != -1){
                                /*Dato che per ragioni spiegate nella classe "Andamenti_nazionali_Covid19"
                                    e "Andamenti_regionali_Covid19", rimuovero' il primo elemento, per rispettare
                                    il numero di giorni scelto dall'utente, aumento di 1. */
                            qtaGiorni = response.length() - impostazioni.getQtaAndamentiNaz() +1;
                        }

                            //Ad ogni ciclo andamento_regione_nazione corrispondera' all'andamento nazionale di un giorno
                        for(int i=qtaGiorni; i<response.length(); i++){
                            andamento_regione_nazione = response.getJSONObject(i);

                                /*Creazione dell'AndamentoCovid19 con tutti i valori
                                    Per l'utilizzo de-commentare anche il costruttore e tutti gli attributi in AndamentoCovid19*/
                            /*AndamentoCovid19 andamento
                                    = new AndamentoCovid19(
                                    valore_perLa_chiave(andamento_regione_nazione, "data"),
                                    valore_perLa_chiave(andamento_regione_nazione, "stato"),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "terapia_intensiva")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_ospedalizzati")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "isolamento_domiciliare")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_positivi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "variazione_totale_positivi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "nuovi_positivi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "dimessi_guariti")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "deceduti")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "casi_da_sospetto_diagnostico")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "casi_da_screening")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_casi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "tamponi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "casi_testati")),
                                    valore_perLa_chiave(andamento_regione_nazione, "note")
                            );*/
                            AndamentoCovid19 andamento
                                    = new AndamentoCovid19(
                                    valore_perLa_chiave(andamento_regione_nazione, "data"),
                                    valore_perLa_chiave(andamento_regione_nazione, "stato"),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "terapia_intensiva")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_ospedalizzati")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "nuovi_positivi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "dimessi_guariti")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "deceduti")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_casi")),
                                    Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "tamponi"))
                            );

                                /*Se e' stato letto almeno un giorno (ha terminato almeno un ciclo),
                                    assegna all'andamento attuale alcuni valori dell'andamento precedente*/
                            if(i > qtaGiorni){
                                /* "- qtaGiorni" perche' la "i" parte da "qtaGiorni", ma gli indici dell'array partono da 0
                                        ("-1" perche' devo prendere l'andamento precedente)*/
                                AndamentoCovid19 andPrecedente = andamentiNazionaliCovid19
                                        .getAndamentiGiornalieriCovid19().get(i - qtaGiorni -1);

                                assegnaValoriAndamentoPrec(andamento, andPrecedente);
                            }
                            andamentiNazionaliCovid19.addAndamento(andamento);
                        }
                            //Rimuovo il primo elemento (ragioni spiegate nella JavaDoc del metodo
                        andamentiNazionaliCovid19.removePrimoAndamento();

                    }else{
                        andamentiRegionaliCovid19 = new Andamenti_regionali_Covid19();
                        andamentiNazionaliCovid19 = null;

                        AndamentiGiornalieroRegioni_Covid19 andamentiGiornalieroRegioni_covid19;

                        int pos_elem;
                        if(impostazioni != null && impostazioni.getQtaAndamentiReg() != -1) {
                            qtaGiorniTraDate = impostazioni.getQtaAndamentiReg();
                            pos_elem = response.length() - qtaGiorniTraDate*21;
                        }else{
                                //la qta di giorni registrati sul file JSON (elementi / numRegioni)
                            qtaGiorniTraDate = (int)response.length()/21;
                            pos_elem = 0;
                        }

                        /*Ciclo per il numero di giorni da leggere, all'interno di questo ciclo
                            ciclo ulteriormente per ogni regione (ogni giorno possiede 21 AndamentoCovid19
                            uno per ogni regione */
                        for(int x=0; x<qtaGiorniTraDate; x++){
                            andamentiGiornalieroRegioni_covid19 = new AndamentiGiornalieroRegioni_Covid19();

                            /*Ad ogni ciclo andamento_regione_nazione corrispondera'
                                all'andamento giornaliero di una regione differente */
                            for(int i=0; i<21; i++){
                                andamento_regione_nazione = response.getJSONObject(pos_elem+i);

                                /*Creazione dell'AndamentoCovid19 con tutti i valori
                                        Per l'utilizzo de-commentare anche il costruttore
                                        e tutti gli attributi in AndamentoCovid19*/
                                /*AndamentoCovid19 andamento
                                        = new AndamentoCovid19(
                                        valore_perLa_chiave(andamento_regione_nazione, "data"),
                                        valore_perLa_chiave(andamento_regione_nazione, "stato"),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "terapia_intensiva")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_ospedalizzati")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "isolamento_domiciliare")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_positivi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "variazione_totale_positivi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "nuovi_positivi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "dimessi_guariti")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "deceduti")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "casi_da_sospetto_diagnostico")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "casi_da_screening")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_casi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "tamponi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "casi_testati")),
                                        valore_perLa_chiave(andamento_regione_nazione, "note")
                                );*/
                                AndamentoCovid19 andamento
                                        = new AndamentoCovid19(
                                        valore_perLa_chiave(andamento_regione_nazione, "data"),
                                        valore_perLa_chiave(andamento_regione_nazione, "denominazione_regione"),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "terapia_intensiva")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_ospedalizzati")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "nuovi_positivi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "dimessi_guariti")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "deceduti")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "totale_casi")),
                                        Integer.parseInt(valore_perLa_chiave(andamento_regione_nazione, "tamponi"))
                                );

                                     /*Se e' stato letto almeno un giorno (ha terminato almeno un ciclo),
                                        assegna all'andamento attuale alcuni valori dell'andamento precedente*/
                                if(x > 0){
                                    AndamentoCovid19 andPrecedente = andamentiRegionaliCovid19
                                            .getAndamentoRegionalePrecedente(x, i);

                                    assegnaValoriAndamentoPrec(andamento, andPrecedente);
                                }
                                    //Aggiungo l'andamento di una regione alla lista
                                andamentiGiornalieroRegioni_covid19.addAndGiornalieroRegione(andamento);
                            }
                                //Passo alla prossima giornata
                            pos_elem += 21;

                                //Aggiungo gli andamenti di una giornata alla lista
                            andamentiRegionaliCovid19.addAndamentoGiornalieroRegioni(andamentiGiornalieroRegioni_covid19);
                        }
                            //Rimuovo il primo elemento (ragioni spiegate nella JavaDoc del metodo
                        andamentiRegionaliCovid19.removePrimoAndamentoGiornaliero();
                    }

                    String totale_casi = "";
                    String totale_decessi = "";

                    if(andamentiNazionaliCovid19 != null){
                        totale_casi = String.format("%,d", andamentiNazionaliCovid19.getUltimoAndamentiGiornalieriCovid19()
                                .getTotale_casi());
                        totale_decessi = String.format("%,d", (andamentiNazionaliCovid19.getUltimoAndamentiGiornalieriCovid19()
                                .getTotale_deceduti()));
                    }

                    /*In caso abbia letto gli andamenti regionali e' un po' piu' complicato, ogni andamento letto
                        e' di una regione, e il totale_casi e totale_decessi sono di quella regione, non nazionali.
                        Percio' devo ciclare per ogni andamento, dell'ultimo giorno letto (il piu' recente) e fare la somma*/
                    if(andamentiRegionaliCovid19 != null){
                        int tot_casi = 0;
                        int tot_decessi = 0;
                        for(AndamentoCovid19 and:
                                andamentiRegionaliCovid19.getAndamentiRegioni()
                                        .get(andamentiRegionaliCovid19.getAndamentiRegioni().size()-1)
                                        .getAndamentiGiornalieroRegioni()){

                            tot_casi += and.getTotale_casi();
                            tot_decessi += and.getTotale_deceduti();
                        }
                        totale_casi = String.format("%,d", tot_casi);
                        totale_decessi = String.format("%,d", tot_decessi);
                    }

                    lblTotaleCasi.setText( String.valueOf(totale_casi) );
                    lblTotaleDecessi.setText( String.valueOf(totale_decessi) );


                    modificaArrayPerLoSpinner(true);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "ERRORE: QUALCOSA E' ANDATO STORTO\nNELLA LETTURA DEL FILE.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERRORE: NON E' STATO POSSIBILE\nLEGGERE IL FILE.\nControlla la connessione.", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        queue.add(jsonArrayRequest);
    }

    /**
     * Metodo per il recupero del valore di una certa chiave data del file JSON (in STRINGA).
     * In caso il valore di una chiave dovrebbe essere un numero ma equivale a "null" ritorna "0".
     */
    private String valore_perLa_chiave(JSONObject jsonObject, String chiave){
        try {
            if(jsonObject.getString(chiave).equals("null") &&
                    !chiave.equals("data") && !chiave.equals("stato") && !chiave.equals("denominazione_regione") ){

                return "0";
            }else{
                    //Non ce' bisono di nessuna trasformazione
                if(chiave.equals("data") || chiave.equals("stato") || chiave.equals("denominazione_regione")){
                    return jsonObject.getString(chiave);
                }else{
                    /*Ho bisogno di un numero intero (in stringa), percio' controllo, se il numero
                        contiene un "." allora e' decimale, allora lo trasformo in un numero intero */
                    String number_str = jsonObject.getString(chiave);
                    if(number_str.contains(".")){
                        return number_str.substring(0, number_str.lastIndexOf("."));
                    }else{ return number_str; }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "null";
        }
    }

    private void assegnaValoriAndamentoPrec(AndamentoCovid19 andCorrente, AndamentoCovid19 andPrecedente){
        andCorrente.setTotale_tamponiAndamentoPrec(andPrecedente.getTotale_tamponi());
        andCorrente.setTotale_decedutiAndamentoPrec(andPrecedente.getTotale_deceduti());
        andCorrente.setTotale_guaritiAndamentoPrec(andPrecedente.getTotale_guariti());
    }
}