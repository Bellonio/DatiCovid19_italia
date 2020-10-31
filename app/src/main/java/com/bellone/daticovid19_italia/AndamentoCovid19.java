package com.bellone.daticovid19_italia;

/**
 * Classe per definire un andamento. Ho commentato diversi attributi che non utilizzo,
 * ma che nel file JSON che leggo ci sono. Non li mostro e neanche utilizzo in quest'app percio' gli
 * ho rimossi.
 */
public class AndamentoCovid19 {

    private final String data;
    private final String nazione_o_regione ;

    //private int ricoverati_ospedale_oggi;         // = totale_ospedalizzati_oggi - terapia_intensiva_oggi

    private final int terapia_intensiva_oggi;
    private final int totale_ospedalizzati_oggi;

    //private final int isolamento_domiciliare_oggi;

    //private final int totale_positivi_oggi;                // = totale_casi - totale_deceduti - totale_guariti

    //private final int effettivi_nuovi_positivi_oggi;
        // = (nuovi_positivi_oggi - (totale_deceduti - totale_decedutiAndamentoPrec) - (totale_guariti - totale_guaritiAndamentoPrec))

    private final int nuovi_positivi_oggi;
    private final int totale_guariti;
    private final int totale_deceduti;

    //private final int totale_casi_da_sospetto_diagnostico ;
    //private final int totale_casi_da_screening;

    private final int totale_casi;
    private final int totale_tamponi;

    //private final int totale_casi_testati;

    //private final String note;

    private int totale_tamponiAndamentoPrec = 0;
    private int totale_guaritiAndamentoPrec = 0;
    private int totale_decedutiAndamentoPrec = 0;


    /* Costruttore con tutti gli attributi. */
    /*public AndamentoCovid19(String data, String nazione_o_regione, int terapia_intensiva_oggi,
                            int totale_ospedalizzati_oggi, int isolamento_domiciliare_oggi,
                            int totale_positivi_oggi, int effettivi_nuovi_positivi_oggi,
                            int nuovi_positivi_oggi, int totale_guariti, int totale_deceduti,
                            int totale_casi_da_sospetto_diagnostico, int totale_casi_da_screening,
                            int totale_casi, int totale_tamponi, int totale_casi_testati, String note) {

        this.data = invertiData(data);
        this.nazione_o_regione = nazione_o_regione;
        this.terapia_intensiva_oggi = terapia_intensiva_oggi;
        this.totale_ospedalizzati_oggi = totale_ospedalizzati_oggi;
        this.isolamento_domiciliare_oggi = isolamento_domiciliare_oggi;
        this.totale_positivi_oggi = totale_positivi_oggi;
        this.effettivi_nuovi_positivi_oggi = effettivi_nuovi_positivi_oggi;
        this.nuovi_positivi_oggi = nuovi_positivi_oggi;
        this.totale_guariti = totale_guariti;
        this.totale_deceduti = totale_deceduti;
        this.totale_casi_da_sospetto_diagnostico = totale_casi_da_sospetto_diagnostico;
        this.totale_casi_da_screening = totale_casi_da_screening;
        this.totale_casi = totale_casi;
        this.totale_tamponi = totale_tamponi;
        this.totale_casi_testati = totale_casi_testati;
        this.note = note;
    }*/


    /**
     * Costruttore con i soli dati utilizzati in quest'app.
     */
    public AndamentoCovid19(String data, String nazione_o_regione, int terapia_intensiva_oggi,
                            int totale_ospedalizzati_oggi,
                            int nuovi_positivi_oggi, int totale_guariti, int totale_deceduti,
                            int totale_casi, int totale_tamponi) {

        this.data = invertiData(data);
        this.nazione_o_regione = nazione_o_regione;
        this.terapia_intensiva_oggi = terapia_intensiva_oggi;
        this.totale_ospedalizzati_oggi = totale_ospedalizzati_oggi;
        this.nuovi_positivi_oggi = nuovi_positivi_oggi;
        this.totale_guariti = totale_guariti;
        this.totale_deceduti = totale_deceduti;
        this.totale_casi = totale_casi;
        this.totale_tamponi = totale_tamponi;
    }

    public void setTotale_tamponiAndamentoPrec(int totale_tamponiAndamentoPrec) {
        this.totale_tamponiAndamentoPrec = totale_tamponiAndamentoPrec;
    }
    public void setTotale_guaritiAndamentoPrec(int totale_guaritiAndamentoPrec) {
        this.totale_guaritiAndamentoPrec = totale_guaritiAndamentoPrec;
    }
    public void setTotale_decedutiAndamentoPrec(int totale_decedutiAndamentoPrec) {
        this.totale_decedutiAndamentoPrec = totale_decedutiAndamentoPrec;
    }

    public int getTotale_tamponiAndamentoPrec() { return totale_tamponiAndamentoPrec; }
    public int getTotale_guaritiAndamentoPrec() { return totale_guaritiAndamentoPrec; }
    public int getTotale_decedutiAndamentoPrec() { return totale_decedutiAndamentoPrec; }

    public String getData() { return data; }
    public String getNazione_o_regione() { return nazione_o_regione; }

    //public int getRicoverati_ospedale_oggi(){ return ricoverati_ospedale_oggi; }

    public int getTerapia_intensiva_oggi() { return terapia_intensiva_oggi; }
    public int getTotale_ospedalizzati_oggi() { return totale_ospedalizzati_oggi; }

    //public int getIsolamento_domiciliare_oggi(){ return isolamento_domiciliare_oggi; }
    //public int getTotale_positivi_oggi() { return totale_positivi_oggi; }

    public int getNuovi_positivi_oggi() { return nuovi_positivi_oggi; }
    public int getTotale_guariti() { return totale_guariti; }
    public int getTotale_deceduti() { return totale_deceduti; }

    //public int getTotale_casi_da_sospetto_diagnostico(){ return totale_casi_da_sospetto_diagnostico; }
    //public int getTotale_casi_da_screening(){ return totale_casi_da_screening; }

    public int getTotale_casi() { return totale_casi; }
    public int getTotale_tamponi() { return totale_tamponi; }

    //public int getTotale_casi_testati(){ return totale_casi_testati; }


    /**
     * Metodo per formattare la data in modo piu' leggibile.
     * @param data     Ad esempio ==> "2020-10-16T17:00:00"
     * @return           esempio ==> "16-10-2020"
     */
    private String invertiData(String data){
            //Un esempio di data letto dal file JSON e' il seguente: "2020-10-16T17:00:00"
        return invertiData( data.split("T") );
    }
    private String invertiData(String[] ora_e_data){
            //ora_e_data[0] = "2020-10-16"  ora_e_data[1]="17:00:00"
        String[] d = ora_e_data[0].split("-");
            //d[0] = "2020"  d[1]="10"  d[2]="16"

        return (d[2]+"-"+d[1]+"-"+d[0]);
            //16-10-2020
    }


        /* Giustamente l'IDE mi ha consigliato di usare il metodo max della classe Math, prima era cosi':
                if(getTotale_tamponi() - getTotale_tamponiAndamentoPrec() > 0){
                    return getTotale_tamponi() - getTotale_tamponiAndamentoPrec();
                }else{ return 0; }
        */

    /**
     * Metodi per avere un determinato dato relativo ad un giorno solo.
     * @return      Semplicemente fa la differenza con il dato del giorno precedente. In caso il dato del giorno precedente
     *                      sia maggiore di quello corrente il metodo ritorna 0.
     */
    public int getGuaritiOggi(){
        return Math.max(getTotale_guariti() - getTotale_guaritiAndamentoPrec(), 0);
    }
    public int getDecedutiOggi(){
        return Math.max(getTotale_deceduti() - getTotale_decedutiAndamentoPrec(), 0);
    }
    public int getTamponiOggi(){
        return Math.max(getTotale_tamponi() - getTotale_tamponiAndamentoPrec(), 0);
    }

}
