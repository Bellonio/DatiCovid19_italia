package com.bellone.daticovid19_italia;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe per gestire i file. In particolare l'app utilizza due semplici file: uno per salvare le impostazioni
 * in un file JSON per comodita.
 */
public class GestoreFile {

    public static final String NOME_FILE_IMPOSTAZIONI = "impostazioni.json";

    private final String file_path;

    public GestoreFile(String file_path) {
        /*In caso il percorso del file non termini col "/" lo aggiunge. Commentato al
            momento dato che l'app l'ho fatta io, e la creazione del gestoreFile e' fatta una sola
            volta e non sarebbe un problema andare a trovarla in caso ci fossero errori con la denominazion
            del file. */
        //if(file_path.toCharArray()[file_path.length()-1] != '/'){ file_path += "/"; }

        this.file_path = file_path;

        File file;
        file = new File(this.file_path+NOME_FILE_IMPOSTAZIONI);
        if(!file.exists()){
            try {
                file.createNewFile();
                    //Salvo le impostazioni di default
                salvaImpostazioni(new Impostazioni());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Metodo che va a leggere riga per riga il file, tramite il BufferedReader ricevuto come parametro,
     * e ritorna il contenuto del file in stringa.
     */
    private String leggiFile(BufferedReader br){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String riga;
            while((riga=br.readLine()) != null){ stringBuilder.append(riga); }

            return stringBuilder.toString();

        } catch (IOException e) { e.printStackTrace(); return null; }
    }

    /**
     * Metodo che va a leggere il JSON, lo trasforma in oggetto Impostazioni e le ritorna.
     */
    public Impostazioni readImpostazioni(){
        File file = new File(file_path+NOME_FILE_IMPOSTAZIONI);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);


            String str_file = leggiFile(br);

            br.close();

            if(str_file != null){
                JSONObject jsonObject = new JSONObject(str_file);

                return new Impostazioni(
                        jsonObject.getString("andamentoDefault"),
                        jsonObject.getInt("qtaAndamentiNaz"),
                        jsonObject.getInt("qtaAndamentiReg")
                );
            }
        } catch (FileNotFoundException e) { Log.e("mieilog", "salvaImpostazioni: FILE NOT FOUND"); e.printStackTrace();
        } catch (IOException e) { Log.e("mieilog", "salvaImpostazioni: IOException"); e.printStackTrace();
        } catch (JSONException e) { Log.e("mieilog", "salvaImpostazioni: JSONError"); e.printStackTrace(); }

        return null;
    }

    /**
     * Metodo che prende come parametro le impostazioni modificate, le trasforma in stringa,
     * le trasforma in coppie chiave:valore e SOVVRASCRIVE il file JSON precedente.
     */
    public void salvaImpostazioni(Impostazioni impostazioni){
        File file = new File(file_path+NOME_FILE_IMPOSTAZIONI);
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            /* Com'e' costituito il file JSON
                {
                    "andamentoDefault":"andamentoDefault",
                    "qtaAndamentiNaz":qtaAndamentiNaz,
                    "qtaAndamentiReg":+qtaAndamentiReg
                }
            */

            String json =
                "{\"andamentoDefault\":"+impostazioni.getAndamentoDefault()+","
                +"\"qtaAndamentiNaz\":"+impostazioni.getQtaAndamentiNaz()+","
                +"\"qtaAndamentiReg\":"+impostazioni.getQtaAndamentiReg()
                +"}";

            bw.write(json);

            bw.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
