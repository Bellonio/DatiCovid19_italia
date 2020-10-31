package com.bellone.daticovid19_italia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * ArrayAdapter per visionare un andamento.
 */
public class PersonalizedArrayAdapter extends ArrayAdapter<AndamentoCovid19> {

    private final int resource;
    private final LayoutInflater layoutInflater;

    private boolean segnaDifferenzaValori;

    private final String url_usato;
    private final String ordinamento_scelto;
    private final String tipoOrdinamento_scelto;
    private final boolean flagOrdinePerGiornata;
    private final String data_di_oggi;


    public PersonalizedArrayAdapter(@NonNull Context context, int resource, @NonNull List<AndamentoCovid19> objects
                , String url_usato, String ordinamento_scelto, String tipoOrdinamento_scelto
                , boolean flagOrdinePerGiornata, String data_di_oggi) {

        super(context, resource, objects);
        this.resource = resource;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.url_usato = url_usato;
        this.ordinamento_scelto = ordinamento_scelto;
        this.tipoOrdinamento_scelto = tipoOrdinamento_scelto;
        this.flagOrdinePerGiornata = flagOrdinePerGiornata;
        this.data_di_oggi = data_di_oggi;
    }

    @SuppressLint({"DefaultLocale", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(resource, parent, false);

        AndamentoCovid19 andCorrente = getItem(position);
        AndamentoCovid19 andPrecedente = null;

            //Controllo se dovro' andare a segnare la differenza di valore dal giorno precedente
        segnaDifferenzaValori = false;
        if(tipoOrdinamento_scelto.equals(OrdinamentiAndamentiCovid19.TIPO_ORDINAMENTI[1])){
            if( url_usato.equals(MainActivity.URL[0]) ){
                andPrecedente = trovaAndamentoPrecedente(position, true
                        , ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0])
                        , false);
            }else{
                andPrecedente = trovaAndamentoPrecedente(position, true
                        , false, ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0]));
            }
        }else{
            if( url_usato.equals(MainActivity.URL[0]) ){
                if(position > 0){
                    andPrecedente = trovaAndamentoPrecedente(position, false
                            , ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0])
                            , false);
                }
            }else{
                andPrecedente = trovaAndamentoPrecedente(position, false
                        , false
                        , ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0]));
                if(position <= 20 && !ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[1])){
                   andPrecedente = null;
                }
            }
        }
        if(segnaDifferenzaValori && !ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0])
                && !ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[1])){

            segnaDifferenzaValori = false;
        }


            //Do un certo colore per separare visivamente due andamenti
        LinearLayout layoutSeparatore = convertView.findViewById(R.id.layoutSeparatore_Andamento);
        layoutSeparatore.setBackgroundColor(Color.BLACK);
        if(!andCorrente.getNazione_o_regione().equals("ITA")){
            if(ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0])
                    || flagOrdinePerGiornata){
                if(position < getCount()-1) {
                    if((position+1)%21 == 0){
                        layoutSeparatore.setBackgroundColor(Color.BLUE);
                    }
                }
            }
        }


        String stringa;
        int valoreCorrente;
        int valorePrecedente = 0;
        TextView lbl;

        lbl = convertView.findViewById(R.id.lblData_Andamento);
        String data = andCorrente.getData();
        if(data_di_oggi.equals( andCorrente.getData() )){
            data += "\t\t( OGGI )";
        }else{
            if(Integer.parseInt(data_di_oggi.split("-")[0])-1
                    == Integer.parseInt(andCorrente.getData().split("-")[0])){

                data += "\t\t( IERI )";
            }
        }
        lbl.setText(data);
        rendiPiuVisibile(lbl, 0);


        LinearLayout bigLayout = convertView.findViewById(R.id.bigLayout_Default);

        if(andCorrente.getNazione_o_regione().equals("ITA")){
                //sarebbe per tutti "ITA", quindi sarebbe inutile, percio' la rimuovo
            bigLayout.removeView( convertView.findViewById(R.id.layoutNazioneRegione_Andamento) );
        }else{
            lbl = convertView.findViewById(R.id.lblNazioneRegione_Andamento);
            lbl.setText(andCorrente.getNazione_o_regione());
            rendiPiuVisibile(lbl, 1);
            lbl = convertView.findViewById(R.id.lblNazioneRegione_Andamento);
            rendiPiuVisibile(lbl, 1);
        }

        valoreCorrente = andCorrente.getNuovi_positivi_oggi();
        if(andPrecedente != null){
            valorePrecedente = andPrecedente.getNuovi_positivi_oggi();
        }
        stringa = creaStringaPerTextView(valoreCorrente, valorePrecedente);

        lbl = convertView.findViewById(R.id.lblNuoviPositivi_Andamento);
        lbl.setText( stringa );
        rendiPiuVisibile(lbl, 2);
        lbl = convertView.findViewById(R.id.lblInfoNuoviPositivi_Andamento);
        rendiPiuVisibile(lbl, 2);


        valoreCorrente = andCorrente.getTamponiOggi();
        if(andPrecedente != null){
            valorePrecedente = andPrecedente.getTamponiOggi();
        }
        stringa = creaStringaPerTextView(valoreCorrente, valorePrecedente);

        lbl = convertView.findViewById(R.id.lblTamponiEffettuati_Andamento);
        lbl.setText( stringa );
        rendiPiuVisibile(lbl, 6);


        valoreCorrente = andCorrente.getTotale_ospedalizzati_oggi();
        if(andPrecedente != null){
            valorePrecedente = andPrecedente.getTotale_ospedalizzati_oggi();
        }
        stringa = creaStringaPerTextView(valoreCorrente, valorePrecedente);

        lbl = convertView.findViewById(R.id.lblPositiviOspedale_Andamento);
        lbl.setText( stringa );


        valoreCorrente = andCorrente.getTerapia_intensiva_oggi();
        if(andPrecedente != null){
            valorePrecedente = andPrecedente.getTerapia_intensiva_oggi();
        }
        stringa = creaStringaPerTextView(valoreCorrente, valorePrecedente);

        lbl = convertView.findViewById(R.id.lblTerIntensiva_Andamento);
        lbl.setText( stringa );
        rendiPiuVisibile(lbl, 4);
        lbl = convertView.findViewById(R.id.lblInfoTerIntensiva_Andamento);
        rendiPiuVisibile(lbl, 4);


        valoreCorrente = andCorrente.getGuaritiOggi();
        if(andPrecedente != null){
            valorePrecedente = andPrecedente.getGuaritiOggi();
        }
        stringa = creaStringaPerTextView(valoreCorrente, valorePrecedente);

        lbl = convertView.findViewById(R.id.lblGuariti_Andamento);
        lbl.setText( stringa );
        rendiPiuVisibile(lbl, 5);
        lbl = convertView.findViewById(R.id.lblInfoGuariti_Andamento);
        rendiPiuVisibile(lbl, 5);


        valoreCorrente = andCorrente.getDecedutiOggi();
        if(andPrecedente != null){
            valorePrecedente = andPrecedente.getDecedutiOggi();
        }
        stringa = creaStringaPerTextView(valoreCorrente, valorePrecedente);

        lbl = convertView.findViewById(R.id.lblDeceduti_Andamento);
        lbl.setText( stringa );
        rendiPiuVisibile(lbl, 3);
        lbl = convertView.findViewById(R.id.lblInfoDeceduti_Andamento);
        rendiPiuVisibile(lbl, 3);


        return convertView;
    }


    /**
     * Metodo che trova la posizione dell'andamento del giorno precedente (non e' per forza
     * l'andamento precedente)
     */
    private AndamentoCovid19 trovaAndamentoPrecedente(int itemPosition
            , boolean flagTipoOrdinamentoDisc, boolean flagOrdinaDataNaz, boolean flagOrdinaDataReg){

        if(flagTipoOrdinamentoDisc){
            if(flagOrdinaDataNaz){
                if(itemPosition < getCount()-1) {
                    segnaDifferenzaValori = true;
                    return getItem(itemPosition+1);
                }
            }else{
                if(flagOrdinaDataReg){
                    if(itemPosition < getCount()-21) {
                        segnaDifferenzaValori = true;
                        return getItem(itemPosition+21);
                    }
                }else{
                    if(itemPosition < getCount()-1) {
                        segnaDifferenzaValori = true;
                        return getItem(itemPosition+1);
                    }
                }
            }
        }else{
            if(flagOrdinaDataNaz){
                if(itemPosition > 0) {
                    segnaDifferenzaValori = true;
                    return getItem(itemPosition-1);
                }
            }else{
                if(flagOrdinaDataReg){
                    if(itemPosition > 20) {
                        segnaDifferenzaValori = true;
                        return getItem(itemPosition-21);
                    }
                }else{
                    if(itemPosition < getCount()-1) {
                        segnaDifferenzaValori = true;
                        return getItem(itemPosition+1);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Metodo che cambia alcune proprieta' di una TextView per rendere piu' visibile
     * il suo contenuto, se l'utente ha scelto di ordinare gli andamenti per il dato
     * contenuto nella TextView
     */
    private void rendiPiuVisibile(TextView lbl, int pos_ordinamento){
        if( pos_ordinamento == 0 && flagOrdinePerGiornata ||
            ordinamento_scelto.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[pos_ordinamento]) ){

            lbl.setTextSize(21);
            lbl.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    /**
     * Metodo che formatta la stringa per la TextView dati i valori dell'
     * andamento corrente e precedente
     */
    @SuppressLint("DefaultLocale")
    private String creaStringaPerTextView(int valoreCorrente, int valorePrecedente){
        String stringa = String.format("%,d", valoreCorrente);

        if(segnaDifferenzaValori){
            int differenzaValorePrec = valoreCorrente - valorePrecedente;

            stringa += "\t(";
            if(differenzaValorePrec >= 0){
                stringa += "+ ";
            }else{
                stringa += "- ";
            }
            stringa += String.format("%,d", Math.abs(differenzaValorePrec))+")";
        }

        return stringa;
    }

}
