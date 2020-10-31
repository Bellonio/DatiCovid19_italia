package com.bellone.daticovid19_italia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Classe per la gestione della lista di andamenti regionali.
 * Ogni elemento della lista sara un AndamentiGiornalieroRegioni_Covid19, ossia
 * un elemento che conterra' una lista di AndamentoCovid19, 1 andamento per ogni regione.
 */
public class Andamenti_regionali_Covid19 {

    public static final String[] DENOMINAZIONE_REGIONI = new String[]{
        "Abruzzo",
        "Basilicata",
        "Calabria",
        "Campania",
        "Emilia-Romagna",
        "Friuli Venezia Giulia",
        "Lazio",
        "Liguria",
        "Lombardia",
        "Marche",
        "Molise",
        "P.A. Bolzano",
        "P.A. Trento",
        "Piemonte",
        "Puglia",
        "Sardegna",
        "Sicilia",
        "Toscana",
        "Umbria",
        "Valle d'Aosta",
        "Veneto"
    };

    private final ArrayList<AndamentiGiornalieroRegioni_Covid19> andamentiRegioni;
        //La lista di andamentiRegioni ordinata come voluta
    private final ArrayList<AndamentoCovid19> tuttiAndamentiRegionali;

    public Andamenti_regionali_Covid19() {
        this.andamentiRegioni = new ArrayList<>();
        this.tuttiAndamentiRegionali = new ArrayList<>();
    }

    public ArrayList<AndamentiGiornalieroRegioni_Covid19> getAndamentiRegioni() { return andamentiRegioni; }
    public ArrayList<AndamentoCovid19> getTuttiAndamentiRegionali() { return tuttiAndamentiRegionali; }

    /**
     * Metodo per ottenere l'andamento del giorno precedente di una determinata regione
     */
    public AndamentoCovid19 getAndamentoRegionalePrecedente(int posGiorno, int posRegione){
        return andamentiRegioni.get(posGiorno - 1).getAndamentoRegione(posRegione);
    }

    public void addAndamentoGiornalieroRegioni(AndamentiGiornalieroRegioni_Covid19 andamentiGiornalieroRegioni){
        andamentiRegioni.add(andamentiGiornalieroRegioni);
    }

    /**
     * Dato che e' possibile non leggere tutti gli andamenti del file JSON, il primo andamento letto
     * non avra i dati dell'andamento precedente. Per ovviare a cio' leggero' un andamento in piu' e
     * poi lo rimuovero'. In questo modo il secondo (che diverra' primo) andamento avra' i dati dell'
     * andamento precedente
     */
    public void removePrimoAndamentoGiornaliero(){
        andamentiRegioni.remove(0);
    }

    /**
     * Metodo per ordinare la lista di andamentiRegioni a piacimento. Il metodo richiama un metodo
     * statico della classe OrdinamentiAndamentiCovid19 differente in base all'ordinamento scelto.
     * @param ordinamento           un valore della lista ORDINAMENTI della classe OrdinamentiAndamentiCovid19
     * @param tipoOrdinamento       un valore della lista TIPO_ORDINAMENTI della classe OrdinamentiAndamentiCovid19
     * @param perGiornate           true/false, in base a se l'ordinamento dovra' esser effettuato
     *                                  in modo assoluto o fra le giornate
     */
    public void ordinaAndamenti(String ordinamento, String tipoOrdinamento, boolean perGiornate){
        if(perGiornate){
                /*Ordina, all'interno delle giornate, le giornate in base all'ordinamento scelto.
                    Ad esempio: ordinamento = "NUOVI POSITIVI" ==> per ogni giornata ci saranno le regioni
                    ordinate fra loro per "NUOVI POSITIVI"*/
            for (int i = 0; i < andamentiRegioni.size(); i++) {
                andamentiRegioni.get(i).ordinamentoGiornalieroRegioni(ordinamento, tipoOrdinamento);
            }

                /*Ordina le giornate fra loro in base all'ordinamento scelto*/
            Collections.sort(andamentiRegioni, new Comparator<AndamentiGiornalieroRegioni_Covid19>() {
                @Override
                public int compare(AndamentiGiornalieroRegioni_Covid19 and1, AndamentiGiornalieroRegioni_Covid19 and2) {

                    AndamentoCovid19 primoAndamentoAnd1 = and1.getAndamentoRegione(0);
                    AndamentoCovid19 primoAndamentoAnd2 = and2.getAndamentoRegione(0);


                    /*In caso il tipoOrdinamento = "DISCENDENTE" lascia il ritorno normale (cioe' a 1)
                        In caso invece il tipoOrdinamento = "ASCENDENTE" inverte il ritorno, invertendo cosi'
                        l'ordinamento */
                    int ritorno_positivo = 1;
                    if (tipoOrdinamento.equals(OrdinamentiAndamentiCovid19.TIPO_ORDINAMENTI[0])) {
                        ritorno_positivo = -1;
                    }

                    if (ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[0])) {   //DATA

                        return OrdinamentiAndamentiCovid19.ordinaPerData(primoAndamentoAnd1, primoAndamentoAnd2, ritorno_positivo);

                    } else if (ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[2])) {    //NUOVI POSITIVI

                        return OrdinamentiAndamentiCovid19.ordinaPerNuoviPositivi(primoAndamentoAnd1, primoAndamentoAnd2, ritorno_positivo);

                    } else if (ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[3])) {    //DECEDUTI

                        return OrdinamentiAndamentiCovid19.ordinaPerDeceduti(primoAndamentoAnd1, primoAndamentoAnd2, ritorno_positivo);

                    } else if (ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[4])) {    //TERAPIA INTENSIVA

                        return OrdinamentiAndamentiCovid19.ordinaPerTerapIntensiva(primoAndamentoAnd1, primoAndamentoAnd2, ritorno_positivo);

                    } else if (ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[5])) {    //GUARITI

                        return OrdinamentiAndamentiCovid19.ordinaPerGuariti(primoAndamentoAnd1, primoAndamentoAnd2, ritorno_positivo);

                    }else if (ordinamento.equals(OrdinamentiAndamentiCovid19.ORDINAMENTI[6])) {    //TAMPONI FATTI

                        return OrdinamentiAndamentiCovid19.ordinaPerTamponiFatti(primoAndamentoAnd1, primoAndamentoAnd2, ritorno_positivo);

                    }else{
                        /*In verita' qui non ci entrera' mai, l'unico ordinamento che
                            manca e' OrdinamentiAndamentiCovid19.ORDINAMENTI[1], ma quando
                            quell'ordinamento e' richiamato, il valore di "perGiornate" == false  */

                        return 0;
                    }
                }
            });

            /*Ora inserisce tutti i singoli andamenti in una lista piu' grande.
                (Questo perche' il PersonalizedArrayAdapter necessita di una lista di AndamentoCovid19)*/
            riempiTuttiAndamentiRegionali();
        }else{                                                                                      //PER REGIONE
            /*Inserisce tutti i singoli andamenti in una lista piu' grande che verra' poi ordinata.
                Ordinando cosi' non piu' per giornata ma in modo assoluto tra tutti gli andamenti*/
            riempiTuttiAndamentiRegionali();

            Collections.sort(tuttiAndamentiRegionali, new OrdinamentiAndamentiCovid19(ordinamento, tipoOrdinamento));
        }
    }

    /**
     * Metodo che azzera/ripulisce la lista di tutti gli andamenti e poi la riempe con i nuovi andamenti.
     */
    private void riempiTuttiAndamentiRegionali(){
        tuttiAndamentiRegionali.clear();
        for (AndamentiGiornalieroRegioni_Covid19 and : andamentiRegioni) {
            tuttiAndamentiRegionali.addAll(and.getAndamentiGiornalieroRegioni());
        }
    }

    /**
     * Metodo che mi serve per trovare all'interno della lista di andamenti una certa regione,
     * o meglio la sua posizione.
     * @param regione               la denominazione della regione (il nome)
     * @param qtaGiorniTraDate      la quantita' di giornate lette, questo dato mi serve per evitare
     *                                  ciclare per tutta la lista in cerca della prima istanza di quella regione.
     *                                  Ma di ciclare di tot, tot scelto dall'utente nelle impostazioni (1/2/3... mesi)
     * @return                      posizione di dove si trova la regione
     */
    public int trovaPosizioneRegione(String regione, int qtaGiorniTraDate){
        boolean regioneTrovata = false;
        int posRegione = 0;

        while(!regioneTrovata && posRegione < tuttiAndamentiRegionali.size()){
            if(tuttiAndamentiRegionali.get(posRegione) != null &&
                    tuttiAndamentiRegionali.get(posRegione).getNazione_o_regione().equals(regione)){

                regioneTrovata = true;
            }else{ posRegione += qtaGiorniTraDate; }
        }

        return posRegione;
    }
}
