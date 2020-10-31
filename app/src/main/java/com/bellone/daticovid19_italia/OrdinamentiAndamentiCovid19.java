package com.bellone.daticovid19_italia;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Classe che mette a disposizione diversi metodi per diverse modalita' di ordinamento
 * degli andamenti.
 */
public class OrdinamentiAndamentiCovid19 implements Comparator<AndamentoCovid19> {

    public static final String[] ORDINAMENTI = new String[]{
            "DATA",                         //[0]
            "PER REGIONE",                  //[1]
            "NUOVI POSITIVI",               //[2]
            "DECEDUTI",                     //[3]
            "TERAPIA INTENSIVA",            //[4]
            "GUARITI",                      //[5]
            "TAMPONI FATTI"                 //[6]
    };
    public static final String[] TIPO_ORDINAMENTI = new String[]{
            "ASCENDENTE",
            "DISCENDENTE"
    };

    private final String tipoOrdinamento;
    private final String ordinamento;


    public OrdinamentiAndamentiCovid19(String ordinamento, String tipoOrdinamento) {
        this.ordinamento = ordinamento;
        this.tipoOrdinamento = tipoOrdinamento;
    }

    @Override
    public int compare(AndamentoCovid19 and1, AndamentoCovid19 and2) {
        /* Tra due elementi la loro posizione reciproca e' la seguente:
            se return 1     il primo andamento va dopo il secondo andamento
            se return -1    il primo andamento va prima il secondo andamento
            se return 0     restano nello stesso ordine
        */

        /*In caso il tipoOrdinamento = "DISCENDENTE" lascia il ritorno normale (cioe' a 1)
            In caso invece il tipoOrdinamento = "ASCENDENTE" inverte il ritorno, invertendo cosi'
            l'ordinamento */
        int ritorno_positivo = 1;
        if(tipoOrdinamento.equals(TIPO_ORDINAMENTI[0])){
            ritorno_positivo = -ritorno_positivo;
        }

        if(ordinamento.equals(ORDINAMENTI[0])){               //DATA

            return ordinaPerData(and1, and2, ritorno_positivo);

        }else if(ordinamento.equals(ORDINAMENTI[1])){        //PER REGIONE

            return ordinaPerRegione(and1, and2, ritorno_positivo);

        }else if(ordinamento.equals(ORDINAMENTI[2])){       //NUOVI POSITIVI

            return ordinaPerNuoviPositivi(and1, and2, ritorno_positivo);

        }else if(ordinamento.equals(ORDINAMENTI[3])){       //DECEDUTI

            return ordinaPerDeceduti(and1, and2, ritorno_positivo);

        }else if(ordinamento.equals(ORDINAMENTI[4])){       //TERAPIA INTENSIVA

            return ordinaPerTerapIntensiva(and1, and2, ritorno_positivo);

        }else if(ordinamento.equals(ORDINAMENTI[5])){       //GUARITI

            return ordinaPerGuariti(and1, and2, ritorno_positivo);

        }else if(ordinamento.equals(ORDINAMENTI[6])) {       //TAMPONI FATTI

            return ordinaPerTamponiFatti(and1, and2, ritorno_positivo);

        }else{
             /*In verita' qui non ci entrera' mai, dato che non ci sono
                altri possibili ORDINAMENTI  */

            return 0;
        }

    }


    public static int ordinaPerData(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        String data1 = and1.getData();
        String data2 = and2.getData();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d1 = dateFormat.parse(data1);
            Date d2 = dateFormat.parse(data2);

            if(d1 != null && d2 != null){
                if(d1.before(d2)){
                    return ritorno_positivo;
                }else if(d1.after(d2)){
                    return -ritorno_positivo;
                }else{
                    /* In caso le date siano uguali ordino per ordineAlfabetico delle regioniù
                        (non puo' capitare per gli andamenti nazionali ma cmq faccio un controllo */
                    if(!and1.getNazione_o_regione().equals("ITA")){
                        return ordinaPerRegione(and1, and2, 1);
                    }else{ return 0; }
                }
            }
        } catch (ParseException e) { e.printStackTrace(); }

        return 0;
    }

    public static int ordinaPerRegione(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        String regione1 = and1.getNazione_o_regione();
        String regione2 = and2.getNazione_o_regione();

        if(regione1.compareTo(regione2) > 0){
            return ritorno_positivo;
        }else if(regione1.compareTo(regione2) < 0){
            return -ritorno_positivo;
        }else{
            /* In caso vi siano due regioni uguali ordino, in modo DISCENDENTE, le due
                    regioni per data */
            return ordinaPerData(and1, and2, 1);
        }
    }

    public static int ordinaPerNuoviPositivi(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        int positiviGG1 = and1.getNuovi_positivi_oggi();
        int positiviGG2 = and2.getNuovi_positivi_oggi();

        if(positiviGG1 < positiviGG2){
            return ritorno_positivo;
        }else if(positiviGG1 > positiviGG2){
            return -ritorno_positivo;
        }else{
            /* In caso vi siano due valori uguali ordino in base alla data */
            return ordinaPerData(and1, and2, ritorno_positivo);
        }
    }

    public static int ordinaPerDeceduti(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        int decedutiGG1 = and1.getDecedutiOggi();
        int decedutiGG2 = and2.getDecedutiOggi();

        if(decedutiGG1 < decedutiGG2){
            return ritorno_positivo;
        }else if(decedutiGG1 > decedutiGG2){
            return -ritorno_positivo;
        }else{
            /* In caso vi siano due valori uguali ordino in base alla data */
            return ordinaPerData(and1, and2, ritorno_positivo);
        }
    }

    public static int ordinaPerTerapIntensiva(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        int terapIntensivaGG1 = and1.getTerapia_intensiva_oggi();
        int terapIntensivaGG2 = and2.getTerapia_intensiva_oggi();

        if(terapIntensivaGG1 < terapIntensivaGG2){
            return ritorno_positivo;
        }else if(terapIntensivaGG1 > terapIntensivaGG2){
            return -ritorno_positivo;
        }else{
            /* In caso vi siano due valori uguali ordino in base alla data */
            return ordinaPerData(and1, and2, ritorno_positivo);
        }
    }
    public static int ordinaPerGuariti(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        int guaritiGG1 = and1.getGuaritiOggi();
        int guaritiGG2 = and2.getGuaritiOggi();

        if(guaritiGG1 < guaritiGG2){
            return ritorno_positivo;
        }else if(guaritiGG1 > guaritiGG2){
            return -ritorno_positivo;
        }else{
            /* In caso vi siano due valori uguali ordino in base alla data */
            return ordinaPerData(and1, and2, ritorno_positivo);
        }
    }

    public static int ordinaPerTamponiFatti(AndamentoCovid19 and1, AndamentoCovid19 and2, int ritorno_positivo){
        int tamponiGG1 = and1.getTamponiOggi();
        int tamponiGG2 = and2.getTamponiOggi();

        if(tamponiGG1 < tamponiGG2){
            return ritorno_positivo;
        }else if(tamponiGG1 > tamponiGG2){
            return -ritorno_positivo;
        }else{
            /* In caso vi siano due valori uguali ordino in base alla data */
            return ordinaPerData(and1, and2, ritorno_positivo);
        }
    }
}
