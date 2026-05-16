package theknife.model;

import java.util.LinkedList;

//TODO da rivedere

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
     * Restituisce l'istanza di GestioneRecensioni
     * @author Elia Toschi
     * @author Celestino Resteghini
     * @return instance
     */
    public static synchronized GestioneRecensioni getInstance() {
        if (instance == null) {
            instance = new GestioneRecensioni();
        }
        return instance;
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
