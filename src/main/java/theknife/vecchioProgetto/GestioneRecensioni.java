package theknife.vecchioProgetto;

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

    public void rimuoviRecensione(Recensione r)
    {
        recensioni.remove(r);
    }

    public void modificaRecensioni(Recensione r, String text, int numeroStelle)
    {
        if(isPresente(r)) {
            recensioni.remove(r);
            Recensione nuovaRecensione = new Recensione(numeroStelle, text, r.getUtente(), r.getRistorante());
            recensioni.add(nuovaRecensione);
        }
    }
    //verifica se è presente la recensione
    public boolean isPresente(Recensione r)
    {
        return recensioni.contains(r);
    }
    public LinkedList<Recensione> getRecensioni() { return recensioni; }

}
