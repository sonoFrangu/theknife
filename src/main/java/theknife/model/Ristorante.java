package theknife.model;

import theknife.ui.javafx.Session;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Celestino Resteghini
 * @author Elia Toschi
 * @author Matteo Franguelli
 */
public class Ristorante {
    private String nome;
    private String numeroTelefono;
    private boolean delivery;  //Disponibilità del servizio di delivery (true/false)
    private boolean booking;    //Disponibilità del servizio di prenotazione online (true/false)
    public double prezzo;
    public LinkedList<String> cucina;
    private Luogo luogo;
    private String website;
    private String link;
    private double awards;
    private  int id;
    private static int contatore=0;

    /**
     *
     * @param nome
     * @param numeroTelefono
     * @param delivery
     * @param booking
     * @param prezzo
     * @param cucina
     * @param luogo
     * @param website
     * @param link
     * @param awards
     *
     *
     * @author Celestino Resteghini
     * @author Elia Toschi
     * @author Matteo Franguelli
     */
    public Ristorante(String nome, String numeroTelefono, boolean delivery, boolean booking, double prezzo, LinkedList<String> cucina, Luogo luogo, String website, String link, double awards)
    {
        this.nome = nome;
        this.numeroTelefono = numeroTelefono;
        this.delivery = delivery;
        this.booking = booking;
        this.prezzo = prezzo;
        this.cucina = cucina;
        this.luogo = luogo;
        this.website = website;
        this.link = link;
        this.awards = awards;
        this.id =++contatore;

    }

    //<editor-fold desc="Getter">
    public String getNome(){return nome;}
    public String getN_tel(){return numeroTelefono;}
    public double getPrezzo() { return prezzo; }
    public boolean isDelivery() { return delivery; }
    public boolean isBooking() { return booking; }
    public LinkedList<String> getCucina(){return cucina;}
    public Luogo getLuogo(){return luogo;}
    public String getWebsite() { return website; }
    public  int getId() { return id;  }

    /**
     * @author Celestino Resteghini
     * @return Restituisce la media delle stelle Michelin.
     * versione 1
     */
    public double getMediaStelle()
    {
        int cont=0;
        double sommaVoti=0;

        File file = new File("doc", "recensioni.csv");

        // Recupera voti delle recensioni
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) { continue; }

                String[] p = line.split(line.contains(";") ? ";" : ",");
                if (p.length >= 5) {
                    try {
                        if (id == Integer.valueOf(p[4]))
                        {
                            sommaVoti += Double.valueOf(p[0]);
                            cont++;
                        }
                    } catch(Exception e){}
                }
            }
        } catch (IOException e) { e.printStackTrace(); }

        if(sommaVoti>0)
            return sommaVoti/cont;
        else
            return -1;
    }

    public double getAward()
    {
        return awards;
    }

    public String toString() {
        String cucine="";
        for(String c : cucina)
        {
            cucine+=c;
        }
        return "Ristorante: "+nome+" "+numeroTelefono+" "+(delivery ? "servizio di delivery disponibile" : "servizio di delivery non disponibile")+" "
                +(booking ? "servizio di prenotazione online disponibile" : "servizio di prenotazione online non disponibile")+" "+cucine+" "+luogo.toString()+"\n";
    }

    /**
     * @author Celestino Resteghini
     * @param r
     *
     */
    public boolean equals(Ristorante r) {
        return this.nome.equals(r.nome) && this.luogo.equals(r.luogo);
    }

    /**
     * @author Celestino Resteghini
     * @param r   the reference object with which to compare.
     * @return
     */
    @Override
    public boolean equals(Object r) {
        if(r instanceof Ristorante)
            return this.equals((Ristorante) r);
        return false;
    }
}
