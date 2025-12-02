package theknife.vecchioProgetto;

import java.util.LinkedList;

public abstract class Utente {


    public abstract LinkedList<Ristorante> cercaRistorante(Luogo luogo, String cucina, double prezzoMinore, double prezzoMaggiore, boolean delivery, boolean prenotazioneOn, double medStelle);

    public abstract LinkedList<Recensione> visualizzaRecensioni(Ristorante ristorante);

    public abstract String visualizzaRistorante(Ristorante ristorante);
}
