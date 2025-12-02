package theknife.vecchioProgetto;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class UtenteNonRegistrato extends Utente {
    public UtenteNonRegistrato() {}


    public void registrazione(String username, String nome, String cognome, Date dataNascita, String password, boolean ristoratore, Luogo luogo) {
        //implementazione metodo in corso
        if(!ristoratore)
        {
            UtenteRegistrato u = new UtenteRegistrato(username, nome, cognome, dataNascita, password, luogo);
            //aggiungere l'utente al file (il controllo su tutti i parametri inseriti viene fatto a priopri quando si richiede la registrazione)
        }
        else
        {
            //inserire la parte con il ristoratore
            //aggiungere il ristoratore al file
        }
        System.out.println("Registrazione effettuata con successo");
    }

    @Override
    public LinkedList<Recensione> visualizzaRecensioni(Ristorante ristorante) {
        //implementa metodo
        return new  LinkedList<Recensione>();
    }

    @Override
    public String visualizzaRistorante(Ristorante ristorante ) {
        //implementa metodo
        return  "";
    }

    /*
        Per tipologia di cucina
        Per locazione geografica (DATO OBBLIGATORIO PER OGNI METODO DI RICERCA) !!!
        Per fascia di prezzo (es. “minore di 30€”, “tra 20€ e 50€”)
        In base alla disponibilità del servizio di delivery
        In base alla disponibilità del servizio di prenotazione online
        Per media del numero di stelle
        Una combinazione dei precedenti criteri di ricerca

        possiamo decidere se fare un unico metodo, quindi in base agli elementi inseriti troviamo i ristoranti (gli elementi non inseriti saranno null, i double -1 e i boolean false)
        oppure facciamo più metodi con l'overloading (anche se essendoci la combinazione di più criteri conviene farne uno unico)
    */
    @Override
    public LinkedList<Ristorante> cercaRistorante(Luogo luogo, String cucina, double prezzoMinore, double prezzoMaggiore, boolean delivery, boolean prenotazioneOn, double medStelle)
    {
        //implementazione in corso
        //L'idea è quella di prendere una lista di ristoranti partendo dal primo criterio di ricerca
        // e nel caso di più criteri: eliminare i ristoranti che non li soddisfano contemporaneamente

        LinkedList<Ristorante> r = null;

        if(luogo!=null)
        {
            /*
                for (Ristorante risto : g.leggiFile())
                {
                    if (risto.getLuogo().equals(luogo)) {
                        r.add(risto);
                    }
                }
                Stessa cosa
            */
            //DA CAMBIARE mettendo i ristoranti nelle vicinanze e prendere la lista pubblica
            r = GestioneFile.leggiFile().stream().filter(x -> x.getLuogo().equals(luogo)).collect(Collectors.toCollection(LinkedList::new));
        }
        else
        {
            if (cucina != null)//rimozione dei ristoranti con cucine diverse da quella selezionata
            {
                r.removeIf(x -> !x.getCucina().contains(cucina));
            }

            if (prezzoMinore >= 0 && prezzoMaggiore >= 0)//rimozione dei ristoranti con prezzo medio non compreso tra min e max
            {
                r.removeIf(x -> !(x.prezzo_Medio > prezzoMinore && x.prezzo_Medio < prezzoMaggiore));
            } else if (prezzoMinore >= 0)//rimozione dei ristoranti con prezzo medio minore del min
            {
                r.removeIf(x -> x.prezzo_Medio < prezzoMinore);
            } else if (prezzoMaggiore >= 0) //rimozione dei ristoranti con prezzo medio maggiore del max
            {
                r.removeIf(x -> x.prezzo_Medio > prezzoMaggiore);
            }

            if (delivery) //rimozione dei ristoranti che non hanno il servizio di delivery
            {
                r.removeIf(x -> x.getDomicilio() == false);
            }

            if (prenotazioneOn) //rimozione dei ristoranti che non hanno il servizio di delivery
            {
                r.removeIf(x -> x.getPrenotazione() == false);
            }

            if (medStelle >= 0) {
                //r.removeIf(x -> x.getMediaStelle() < medStelle);
            }
        }
        return r;
    }
}
