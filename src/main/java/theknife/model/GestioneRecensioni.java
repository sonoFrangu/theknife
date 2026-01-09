package theknife.model;

import java.util.LinkedList;

/**
 * @author Elia Toschi
 * @author Matteo Franguelli
 */
public class GestioneRecensioni {
    public LinkedList<Recensione> recensioni;

    private static GestioneRecensioni instance;

    public GestioneRecensioni() {
        recensioni = new LinkedList<>();
    }

    public void add(Recensione r)
    {
        if(!isPresente(r))
            recensioni.add(r);
    }

    /**
     * @author Elia Toschi
     * @author Celestino Resteghini
     * @return null
     */
    public static synchronized GestioneRecensioni getInstance() {
        if (instance == null) {
            instance = new GestioneRecensioni();
        }
        return instance;
    }
    /**
     * rimuove una recensione dalla lista
     * @param r
     */
    public void rimuoviRecensione(Recensione r)
    {
        recensioni.remove(r);
    }

    /**
     * Modifica una recensione
     * @param r
     * @param text
     * @param numeroStelle
     * @author Elia Toschi
     * @author Matteo Franguelli
     */
    public void modificaRecensioni(Recensione r, String text, int numeroStelle)
    {
        if(isPresente(r)) {
            recensioni.remove(r);
            Recensione nuovaRecensione = new Recensione(numeroStelle, text, r.getIdUtente(), r.get_id_Ristorante());
            recensioni.add(nuovaRecensione);
        }
    }

    /**
     * Verifica se la recensione è già presente nella lista
     * @param r
     * @author Celestino Resteghini
     * @return se la recensione è presente
     */
    public boolean isPresente(Recensione r)
    {
        return recensioni.contains(r);
    }

    /**
     * Restituisce tutte le recensioni presenti nella lista
     * @author Matteo Franguelli
     * @return
     */
    public LinkedList<Recensione> getRecensioni() { return recensioni; }

}
