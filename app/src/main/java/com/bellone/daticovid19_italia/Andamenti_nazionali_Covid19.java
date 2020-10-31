package com.bellone.daticovid19_italia;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe per la gestione della lista di andamenti nazionali. Ogni elemento dell'array e' un semplice AndamentoCovid19
 */
public class Andamenti_nazionali_Covid19 {

    private final ArrayList<AndamentoCovid19> andamentiGiornalieriCovid19;

    public Andamenti_nazionali_Covid19() {
        this.andamentiGiornalieriCovid19 = new ArrayList<>();
    }

    public ArrayList<AndamentoCovid19> getAndamentiGiornalieriCovid19() { return andamentiGiornalieriCovid19; }
    public AndamentoCovid19 getUltimoAndamentiGiornalieriCovid19(){
        return andamentiGiornalieriCovid19.get(andamentiGiornalieriCovid19.size()-1);
    }


    /*addAndamento con tutti gli attributi di AndamentoCovid19*/
    /*public void addAndamento(String data, String nazione_regione, int terapia_intensiva,
                             int totale_ospedalizzati, int isolamento_domiciliare, int totale_positivi,
                             int variazione_totale_positivi, int nuovi_positivi, int dimessi_guariti,
                             int deceduti, int casi_da_sospetto_diagnostico, int casi_da_screening,
                             int totale_casi, int tamponi, int casi_testati){
        addAndamento(new AndamentoCovid19(data, nazione_regione, terapia_intensiva,
                totale_ospedalizzati, isolamento_domiciliare, totale_positivi, variazione_totale_positivi,
                nuovi_positivi, dimessi_guariti, deceduti, casi_da_sospetto_diagnostico, casi_da_screening,
                totale_casi, tamponi, casi_testati));
    }*/

    /**
     * Metodo, al momento inutile, ma disponibile, per aggiungere un AndamentoCovid19 alla lista, passandogli
     * i vari dati e non gia' l'oggetto AndamentoCovid19
     */
    /*public void addAndamento(String data, String nazione_regione, int terapia_intensiva,
                             int totale_ospedalizzati, int nuovi_positivi,
                             int dimessi_guariti, int deceduti,
                             int totale_casi, int tamponi){
        addAndamento(new AndamentoCovid19(data, nazione_regione, terapia_intensiva,
                totale_ospedalizzati,
                nuovi_positivi, dimessi_guariti, deceduti,
                totale_casi, tamponi));
    }*/
    public void addAndamento(AndamentoCovid19 andamento){
        andamentiGiornalieriCovid19.add(andamento);
    }


        //Non vado a leggere tutti gli andamenti, percio' un andamento non potra' avere i dati
        // dell'andamento precedente. Ad esempio il numero di tamponi effettuati rimarra' un numero
        // molto alto, non potendo togliere al totale_tamponi i totale_tamponiAndamentoPrec.
        // Non avra' quindi i dati corretti, percio' rimuovo il primo andamento.

    /**
     * Dato che e' possibile non leggere tutti gli andamenti del file JSON, il primo andamento letto
     * non avra i dati dell'andamento precedente. Per ovviare a cio' leggero' un andamento in piu' e
     * poi lo rimuovero'. In questo modo il secondo (che diverra' primo) andamento avra' i dati dell'
     * andamento precedente.
     */
    public void removePrimoAndamento(){
        andamentiGiornalieriCovid19.remove(0);
    }

    /**
     * Metodo per ordinare la lista di andamenti secondo cio' che ha scelto l'utente
     * @param ordinamento           un valore della lista ORDINAMENTI della classe OrdinamentiAndamentiCovid19
     * @param tipoOrdinamento       un valore della lista TIPO_ORDINAMENTI della classe OrdinamentiAndamentiCovid19
     */
    public void ordinamentoPer(String ordinamento, String tipoOrdinamento){
        Collections.sort(andamentiGiornalieriCovid19, new OrdinamentiAndamentiCovid19(ordinamento, tipoOrdinamento));
    }
}
