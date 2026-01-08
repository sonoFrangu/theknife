package theknife.model;

import java.util.LinkedList;

/**
 * @author Matteo Franguelli
 * @author Celestino Resteghini
 * @version 1
 */
public abstract class Utente {

    /**
     *
     * @param luogo
     * @param cucina
     * @param prezzoMinore
     * @param prezzoMaggiore
     * @param delivery
     * @param prenotazioneOn
     * @param medStelle
     *
     */
    public abstract LinkedList<Ristorante> cercaRistorante(Luogo luogo, String cucina, double prezzoMinore, double prezzoMaggiore, boolean delivery, boolean prenotazioneOn, double medStelle);

    /**
     *
     * @param ristorante
     */
    public abstract LinkedList<Recensione> visualizzaRecensioni(Ristorante ristorante);

    /**
     *
     * @param ristorante
     */
    public abstract String visualizzaRistorante(Ristorante ristorante);
}
