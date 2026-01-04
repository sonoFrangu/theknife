package theknife.vecchioProgetto;

import java.util.LinkedList;

public class GestioneRistoranti {
    public LinkedList<Ristorante> listaRistoranti;
    public GestioneRistoranti()
    {
        listaRistoranti = new LinkedList<>();
    }
    public void add(Ristorante ristorante)
    {
        if(ristorante != null && !listaRistoranti.contains(ristorante))
            listaRistoranti.add(ristorante);
    }
    public void remove(Ristorante ristorante)
    {
        if(ristorante != null && !listaRistoranti.contains(ristorante))
            listaRistoranti.add(ristorante);
    }
    public void modify(Ristorante new_r, String nome, Luogo luogo)
    {
        listaRistoranti.removeIf(x -> x.getLuogo().equals(luogo)&&x.getNome().equals(nome));
        listaRistoranti.add(new_r);
    }
    public Ristorante getRistorante(int id)
    {
        for(Ristorante r : listaRistoranti )
            if(r.getId() == id)
                return r;
        return null;
    }

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
