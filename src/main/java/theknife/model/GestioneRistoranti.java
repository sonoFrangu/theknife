package theknife.model;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Celestino Resteghini
 * @author Matteo Franguelli
 * @author Elia Toschi
 */
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
     * @author Elia Toschi
     */
    public LinkedList<Ristorante> getListaRistoranti() {return listaRistoranti;    }

    /**
     * Aggiunge un ristorante
     * @param ristorante
     */
    public void add(Ristorante ristorante)
    {
        listaRistoranti.add(ristorante);
    }

    /**
     * Restituisce un ristorante a partire dal suo indirizzo
     * @param indirizzo
     * @return r
     * @author Elia Toschi
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

    public Ristorante getRistoranteDaNome(String nome)
    {
        for (Ristorante r : listaRistoranti) {
            if (nome.equalsIgnoreCase(r.getNome())) {
                return r;
            }
        }
        return null;

    }

    /**
     * Restituisce un ristorante avendo l'id
     * @param id
     * @return
     * @author Elia Toschi
     */
    public Ristorante getRistorante(int id)
    {
        for(Ristorante r : listaRistoranti )
            if(r.getId() == id)
                return r;
        return null;
    }

    /**
     * Filtra la lista di tutti i ristoranti con tutti i possibili parametri
     * @param luogo
     * @param cucina
     * @param prezzoMinore
     * @param prezzoMaggiore
     * @param delivery
     * @param booking
     * @param medStelle
     * @return r
     * @author Celestino Resteghini
     */
    public LinkedList<Ristorante> Filtro(String luogo, String cucina, double prezzoMinore, double prezzoMaggiore, boolean delivery, boolean booking, double medStelle)
    {
        LinkedList<Ristorante> r = null;

        if(luogo!=null && luogo.length()>0)
        {
            //Prendo il primo ristorante della città filtrata
            Optional<Ristorante> primoRist = listaRistoranti.stream().filter(x -> x.getLuogo().getCitta().equalsIgnoreCase(luogo)).findFirst();

            if (primoRist.isPresent()) { //Se esiste un ristorante in quella città, prendo lat e long
                double lat1 = primoRist.get().getLuogo().getLatitudine();
                double long1 = primoRist.get().getLuogo().getLongitudine();

                //Filtro tutti i ristoranti (anche di altre città) entro 10 km
                r = listaRistoranti.stream().filter(x -> {
                    Luogo l = x.getLuogo();
                    return l.checkDistance10KM(lat1, long1);
                }).collect(Collectors.toCollection(LinkedList::new));
            }
            else
            {
                System.out.println("=== [MANCANO RISTORANTI IN QUEL LUOGO] ===");
                return r;
            }

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

            if (medStelle > 0) {
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
