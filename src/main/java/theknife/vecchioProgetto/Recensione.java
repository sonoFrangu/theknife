package theknife.vecchioProgetto;

import java.util.Date;

public class Recensione {
    private int numeroStelle;
    private String text;
    private Date data;
    private UtenteRegistrato utente;
    private Ristorante ristorante;

    public Recensione(int numeroStelle, String text, UtenteRegistrato utente, Ristorante ristorante)
    {
        if(text.length()>=300)
            text=text.substring(0,300);
        this.numeroStelle = numeroStelle;
        this.text = text;
        this.utente = utente;
        this.ristorante = ristorante;
        this.data = new Date();
    }

    //<editor-fold desc="Getter">
    public int getNumeroStelle() {return numeroStelle;}
    public String getText() {return text;}
    public Date getData() {return data;}
    public UtenteRegistrato getUtente() {return utente;}
    public Ristorante getRistorante() {return ristorante;}
    //</editor-fold>

    @Override
    public String toString() {
        return "Recensione: "+ numeroStelle+" "+text+" "+utente+" "+ristorante+"\n";
    }

    public boolean equals(Recensione r) {
        return this.data.equals(r.data) && this.utente.equals(r.utente) && this.ristorante.equals(r.ristorante);
    }

    @Override
    public boolean equals(Object r) {
        if(r instanceof Recensione)
            return this.equals((Recensione)r);
        return false;
    }
}
