package com.bellone.daticovid19_italia;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe per la gestione della lista di andamenti di un giorno per le varie regioni.
 * Ogni elemento (AndamentoCovid19) della lista e' l'andamento di un giorno di una diversa regione.
 * */
public class AndamentiGiornalieroRegioni_Covid19 {

    private final ArrayList<AndamentoCovid19> andamentiGiornalieroRegioni;

    public AndamentiGiornalieroRegioni_Covid19() {
        this.andamentiGiornalieroRegioni = new ArrayList<>();
    }
    public ArrayList<AndamentoCovid19> getAndamentiGiornalieroRegioni(){ return andamentiGiornalieroRegioni; }
    public AndamentoCovid19 getAndamentoRegione(int pos){ return andamentiGiornalieroRegioni.get(pos); }

    /**
     * Questo metodo verra' richiamato 21 volte, ossia una volta per ogni regione.
     */
    public void addAndGiornalieroRegione(AndamentoCovid19 andGiornalieroRegione){
        andamentiGiornalieroRegioni.add(andGiornalieroRegione);
    }

    /**
     * Metodo per ordinare le regioni (gli andamenti delle regioni) in base a cio' che ha scelto l'utente.
     */
    public void ordinamentoGiornalieroRegioni(String ordinamento, String tipoOrdinamento){
        Collections.sort(andamentiGiornalieroRegioni, new OrdinamentiAndamentiCovid19(ordinamento, tipoOrdinamento));
    }

}
