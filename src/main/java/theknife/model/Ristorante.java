package theknife.model;

import java.util.LinkedList;

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
    private static int id;


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
        this.id+=id;

    }
    //temporaneo
    public Ristorante(){}

    //<editor-fold desc="Getter">
    public String getNome(){return nome;}
    public String getN_tel(){return numeroTelefono;}
    public double getPrezzo() { return prezzo; }
    public boolean isDelivery() { return delivery; }
    public boolean isBooking() { return booking; }
    public LinkedList<String> getCucina(){return cucina;}
    public Luogo getLuogo(){return luogo;}
    public String getWebsite() { return website; }
    public String getLink() { return link; }
    public  int getId() { return id;  }

    public double getMediaStelle()
    {
        /*GestioneRecensioni gr= new GestioneRecensioni();
        return gr.mediaStelle(this.id);*/
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

    public boolean equals(Ristorante r) {
        return this.nome.equals(r.nome) && this.luogo.equals(r.luogo);
    }

    @Override
    public boolean equals(Object r) {
        if(r instanceof Ristorante)
            return this.equals((Ristorante) r);
        return false;
    }
}
