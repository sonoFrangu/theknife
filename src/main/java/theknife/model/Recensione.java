package theknife.model;

import java.util.Date;

public class Recensione {
    private int numeroStelle;
    private String text;
    private Date data;
    private UtenteRegistrato utente;
    private int id_ristorante;
    private static int id;

    public Recensione(int numeroStelle, String text, UtenteRegistrato utente, int id_ristorante)
    {
        if(text.length()>=300)
            text=text.substring(0,300);
        this.numeroStelle = numeroStelle;
        this.text = text;
        this.utente = utente;
        this.id_ristorante = id_ristorante;
        this.data = new Date();
        this.id+=id;

    }

    //<editor-fold desc="Getter">
    public int getNumeroStelle() {return numeroStelle;}
    public String getText() {return text;}
    public Date getData() {return data;}
    public UtenteRegistrato getUtente() {return utente;}
    public int get_id_Ristorante() {return id_ristorante;}
    //</editor-fold>

    @Override
    public String toString() {
        GestioneRistoranti g = new GestioneRistoranti();
        return "Recensione: "+ numeroStelle+" "+text+" "+utente+" "+g.getRistorante(id_ristorante)+"\n";
    }

    public boolean equals(Recensione r) {
        return this.data.equals(r.data) && this.utente.equals(r.utente) && this.id_ristorante==(r.id_ristorante);
    }

    @Override
    public boolean equals(Object r) {
        if(r instanceof Recensione)
            return this.equals((Recensione)r);
        return false;
    }
}
