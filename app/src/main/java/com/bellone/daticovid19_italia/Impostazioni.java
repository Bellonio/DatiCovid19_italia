package com.bellone.daticovid19_italia;

/**
 * Classe per definite la singola impostazione. Contiene un costruttore con parametri vuoti per
 * creare una impostazione di default
 */
public class Impostazioni {

    private final String andamentoDefault;
    private final int qtaAndamentiNaz;
    private final int qtaAndamentiReg;

    public Impostazioni(String andamentoDefault, int qtaAndamentiNaz, int qtaAndamentiReg) {
        this.andamentoDefault = andamentoDefault;
        this.qtaAndamentiNaz = qtaAndamentiNaz;
        this.qtaAndamentiReg = qtaAndamentiReg;
    }
    public Impostazioni() {
        this.andamentoDefault = "Nazionale";
        this.qtaAndamentiNaz = 60;
        this.qtaAndamentiReg = 60;
    }

    public String getAndamentoDefault() { return andamentoDefault; }
    public int getQtaAndamentiNaz() { return qtaAndamentiNaz; }
    public int getQtaAndamentiReg() { return qtaAndamentiReg; }
}
