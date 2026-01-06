package theknife.model;

import javafx.collections.FXCollections;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class GestioneRistoranti {
    public LinkedList<Ristorante> listaRistoranti;
    private static GestioneRistoranti instance;
    public GestioneRistoranti()
    {
        listaRistoranti = new LinkedList<>();
    }

    public static synchronized GestioneRistoranti getInstance() {
        if (instance == null) {
            instance = new GestioneRistoranti();
        }
        return instance;
    }

    /**
     * Aggiunge un ristorante
     * @param ristorante
     */
    public void add(Ristorante ristorante)
    {
        //if(ristorante != null && !listaRistoranti.contains(ristorante)) //todo: ci sono alcuni luoghi uguali
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
     *
     * @param indirizzo
     * @return
     */
    public Ristorante getRistoranteDaIndirizzo(String indirizzo)
    {
        for(Ristorante r: listaRistoranti)
        {
            if(r.getLuogo().getIndirizzo().equals(indirizzo))
                return r;
        }
        return null;
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

    public LinkedList<Ristorante> Filtro(String luogo, String cucina, double prezzoMinore, double prezzoMaggiore, boolean delivery, boolean booking, double medStelle)
    {
        LinkedList<Ristorante> r = null;

        if(luogo!=null && luogo.length()>0)
        {
            //todo: DA CAMBIARE mettendo i ristoranti nelle vicinanze e prendere la lista pubblica
            r = listaRistoranti.stream().filter(x -> x.getLuogo().getCitta().equals(luogo)).collect(Collectors.toCollection(LinkedList::new));

            if (cucina != null && cucina.length()>0)//rimozione dei ristoranti con cucine diverse da quella selezionata
            {
                r.removeIf(x -> !x.getCucina().contains(cucina));
            }

            if (prezzoMinore >= 0 && prezzoMaggiore >= 0)//rimozione dei ristoranti con prezzo medio non compreso tra min e max
            {
                r.removeIf(x -> !(x.prezzo > prezzoMinore && x.prezzo < prezzoMaggiore));
            } else if (prezzoMinore >= 0)//rimozione dei ristoranti con prezzo medio minore del min
            {
                r.removeIf(x -> x.prezzo < prezzoMinore);
            } else if (prezzoMaggiore >= 0) //rimozione dei ristoranti con prezzo medio maggiore del max
            {
                r.removeIf(x -> x.prezzo > prezzoMaggiore);
            }

            if (delivery) //rimozione dei ristoranti che non hanno il servizio di delivery
            {
                r.removeIf(x -> x.isDelivery() == false);
            }

            if (booking) //rimozione dei ristoranti che non hanno il servizio di booking
            {
                r.removeIf(x -> x.isBooking() == false);
            }

            if (medStelle >= 0) {
                r.removeIf(x -> x.getMediaStelle() < medStelle); //rimozione dei ristoranti che non hanno medStelle minore
            }
        }
        else
        {
            System.out.println("=== [MANCA IL LUOGO] ===");
        }

        return r;
    }


}
