package theknife.vecchioProgetto;

import java.util.LinkedList;

public class GestioneRistoranti {
    public LinkedList<Ristorante> listaRistoranti;
    public GestioneRistoranti()
    {
        listaRistoranti = new LinkedList<>();
    }

    /**
     * Aggiunge un ristorante
     * @param ristorante
     */
    public void add(Ristorante ristorante)
    {
        if(ristorante != null && !listaRistoranti.contains(ristorante))
            listaRistoranti.add(ristorante);
    }

    /**
     * Rimuove un ristorante se presente
     * @param ristorante
     */
    public void remove(Ristorante ristorante)
    {
        if(ristorante != null && !listaRistoranti.contains(ristorante))
            listaRistoranti.add(ristorante);
    }

    /**
     * Modifica un ristorante
     * @param new_r
     * @param nome
     * @param luogo
     */
    public void modify(Ristorante new_r, String nome, Luogo luogo)
    {
        listaRistoranti.removeIf(x -> x.getLuogo().equals(luogo)&&x.getNome().equals(nome));
        listaRistoranti.add(new_r);
    }

    /**
     * Restituisce un ristorante avendo l'id
     * @param id
     * @return
     */
    public Ristorante getRistorante(int id)
    {
        for(Ristorante r : listaRistoranti )
            if(r.getId() == id)
                return r;
        return null;
    }

    /**
     * Restituisce una lista di ristoranti ottendento una lista di id
     * @param listaid
     * @return
     */
    public LinkedList<Ristorante> getRistoranti(LinkedList<Integer> listaid)
    {
        LinkedList<Ristorante> list = new LinkedList<>();
        for(Ristorante r : listaRistoranti)
            for(Integer i : listaid )
                if(r.getId()==i)
                    list.add(r);
        return list;
    }

}
