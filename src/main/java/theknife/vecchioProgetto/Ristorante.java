package theknife.vecchioProgetto;

import java.util.LinkedList;

public class Ristorante {
    private String nome;
    private String numeroTelefono;
    private boolean delivery;  //Disponibilità del servizio di delivery (true/false)
    private boolean prenotazioneOn;    //Disponibilità del servizio di prenotazione online (true/false)
    public double prezzo_Medio;
    public LinkedList<String> cucina;
    private Luogo luogo;


    public Ristorante(String nome, String numeroTelefono, boolean delivery, boolean prenotazioneOn, double prezzo_Medio, LinkedList<String> cucina, Luogo luogo)
    {
        this.nome = nome;
        this.numeroTelefono = numeroTelefono;
        this.delivery = delivery;
        this.prenotazioneOn = prenotazioneOn;
        this.prezzo_Medio = prezzo_Medio;
        this.cucina = cucina;
        this.luogo = luogo;

    }
    //temporaneo
    public Ristorante(){}

    //<editor-fold desc="Getter">
    public String getNome(){return nome;}
    public String getN_tel(){return numeroTelefono;}
    public boolean getDomicilio(){return delivery;}
    public boolean getPrenotazione(){return prenotazioneOn;}
    public LinkedList<String> getCucina(){return cucina;}
    public Luogo getLuogo(){return luogo;}


//    public double getMediaStelle()
//    {
//        if(!recensioni.isEmpty())
//        {
//            int somma = 0;
//            for (Recensione r : recensioni) {
//                somma += r.getNumeroStelle();
//            }
//            return (double) somma / recensioni.size();
//        }
//        else
//            return 0;
//    }
    //</editor-fold>

    public String toString() {
        String cucine="";
        for(String c : cucina)
        {
            cucine+=c;
        }
        return "Ristorante: "+nome+" "+numeroTelefono+" "+(delivery ? "servizio di delivery disponibile" : "servizio di delivery non disponibile")+" "
                +(prenotazioneOn ? "servizio di prenotazione online disponibile" : "servizio di prenotazione online non disponibile")+" "+cucine+" "+luogo.toString()+"\n";
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
