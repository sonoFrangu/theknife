package theknife.model;

import java.util.LinkedList;

public class GestioneRecensioni {
    public LinkedList<Recensione> recensioni;
    public GestioneRecensioni() {
        recensioni = new LinkedList<>();
    }

    public void add(Recensione r)
    {
        if(!isPresente(r))
            recensioni.add(r);
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
     * @return
     */
    public boolean isPresente(Recensione r)
    {
        return recensioni.contains(r);
    }

    /**
     * Restituisce tutte le recensioni presenti nella lista
     * @return
     */
    public LinkedList<Recensione> getRecensioni() { return recensioni; }

    /**
     * Restituisce la media delle stelle di un ristorante
     * @param idRistorante
     * @return
     */
    public double mediaStelle(int idRistorante)
    {
        int somma=0;
        int count=0;
        for(Recensione r : recensioni)
            if(r.get_id_Ristorante() == idRistorante) {
                count++;
                somma+=r.getNumeroStelle();
            }
        return somma/count;
    }


}
