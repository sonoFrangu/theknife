package theknife.model;

import java.util.Date;
import java.util.LinkedList;

public class UtenteRegistrato extends Utente{
    String username;
    String nome;
    String cognome;
    Date dataNascita;
    String password;
    boolean ristoratore;
    Luogo luogo;
    private static int id;

    public LinkedList<Integer> ristorantiPreferiti;

    /**
     * Costruttore utente registrato
     * @param username
     * @param nome
     * @param cognome
     * @param dataNascita
     * @param password
     * @param luogo
     */
    public UtenteRegistrato(String username, String nome, String cognome, Date dataNascita, String password, Luogo luogo) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.password = password;
        this.ristoratore = false;
        this.luogo = luogo;
        this.ristorantiPreferiti = new LinkedList<Integer>();
        this.id+=id;

    }

    //<editor-fold desc="Getter">
    public String getUsername() { return username; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public Date getDataNascita() { return dataNascita; }
    public boolean isRistoratore() { return ristoratore; }
    public Luogo getLuogo() { return luogo; }
    public LinkedList<Integer> getRistorantiPreferiti() { return ristorantiPreferiti; }
    public LinkedList<Recensione> getRecensioni() { GestioneRecensioni gr= new GestioneRecensioni();
        return  gr.getRecensioni();
    }
    //</editor-fold>


    /**
     * Aggiunge un ristorante ai preferiti
     * @param ristorante
     * @return true se viene aggiunto
     *      false se è già presente
     */
    public boolean aggiungiPreferito(Ristorante ristorante) {
        if (!ristorantiPreferiti.contains(ristorante.getId())) {
            ristorantiPreferiti.add(ristorante.getId());
            return true;
        }
        return false;
    }

    /**
     * Visualizza i ristoranti preferiti
     * @return lista di ristoranti
     */
    public LinkedList<Ristorante> visualizzaPreferito() {

        LinkedList<Ristorante> list=new LinkedList<>();
        GestioneRistoranti gr = new GestioneRistoranti();

        for(Integer i: ristorantiPreferiti )
            list.add(gr.getRistorante(i));
        return list;

    }

    /**
     * Rimuove un ristorante preferito
     * @param ristorante
     * @return true se viene rimosso, false se non presente
     */
    public boolean rimuoviPreferito(Ristorante ristorante) {
        if (ristorantiPreferiti.remove(ristorante)) {
            return true;
        } else
            return false;
    }

    /**
     * aggiunta di una recensione
     * @param n_stelle
     * @param text
     * @param data
     * @param ristorante
     */
    public void aggiungiRecensione(int n_stelle,String text, Date data, Ristorante ristorante)
    {
        Recensione recensione= new Recensione(n_stelle, text,this.id, ristorante.getId());
        GestioneRecensioni gr= new GestioneRecensioni();

        gr.add(recensione);

    }

    /**
     * Rimuove una recensione se presente
     * @param rec
     * @return true se rimossa
     */
    public boolean rimuoviRecensione(Recensione rec)
    {
        GestioneRecensioni gr= new GestioneRecensioni();
        if(gr.isPresente(rec))
        {
            gr.rimuoviRecensione(rec);
            return true;
        }
        return false;
    }

    /**
     * modifica una recensione se presente
     * @param recensione
     * @param text
     * @param numeroStelle
     * @return true se viene effettuata la modifica
     */
    public boolean modificaRecensione(Recensione recensione, String text, int numeroStelle )
    {
        GestioneRecensioni gr= new GestioneRecensioni();
        if(gr.isPresente(recensione)) {
            numeroStelle = numeroStelle == 0 ? recensione.getNumeroStelle() : numeroStelle;
            text = text.length() == 0 || text == null ? recensione.getText() : text;
            gr.modificaRecensioni(recensione, text, numeroStelle);
            return true;
        }
        return false;
    }

    /**
     * Visualizza le recensioni effettuate da un utente
     * @return LinkedList<Recensione>
     */
    public LinkedList<Recensione> visualizzaRecensioniFatte()
    {
        LinkedList<Recensione> rec = getRecensioni();
        LinkedList<Recensione> recProprie= new LinkedList<>();
        for(Recensione r : rec)
            if( r.getIdUtente()==this.id)
                recProprie.add(r);

        return recProprie;
    }

    /**
     * restituisce tutti i ristoranti recensiti
     * @return LinkedList<Ristorante>
     */
    private LinkedList<Ristorante> visualizzaRistorantiRecensiti()
    {
        LinkedList<Integer> listaid=new LinkedList<>();
        GestioneRecensioni gr = new GestioneRecensioni();
        for(Recensione rec: gr.getRecensioni() )
            if(!listaid.contains(rec.get_id_Ristorante()))
                listaid.add(rec.get_id_Ristorante());
        GestioneRistoranti ges= new GestioneRistoranti();
        return ges.getRistoranti(listaid);

    }

    /**
     * Visualizza le recensioni effettuate ad un ristorante
     * @param ristorante
     * @return
     */
    @Override
    public LinkedList<Recensione> visualizzaRecensioni(Ristorante ristorante)
    {
        GestioneRecensioni gr = new GestioneRecensioni();
        LinkedList<Recensione> lista = new LinkedList<>();

        for ( Recensione r : gr.getRecensioni())
            if(r.get_id_Ristorante()==ristorante.getId())
                lista.add(r);
        return lista;
    }

    /**
     * Visualizza le recensioni effettuate ad un ristorante
     * @param id del ristorante
     * @return
     */
    public LinkedList<Recensione> visualizzaRecensioni(Integer  id)
    {
        GestioneRecensioni gr = new GestioneRecensioni();
        LinkedList<Recensione> lista = new LinkedList<>();

        for ( Recensione r : gr.getRecensioni())
            if(r.get_id_Ristorante()==id)
                lista.add(r);
        return lista;
    }

    /**
     * visualizza il ristorante
     * @param ristorante
     * @return
     */
    @Override
    public String visualizzaRistorante(Ristorante ristorante)
    {
        return ristorante.toString();
    }

    /*
        Per tipologia di cucina
        Per locazione geografica (DATO OBBLIGATORIO PER OGNI METODO DI RICERCA) !!!
        Per fascia di prezzo (es. “minore di 30€”, “tra 20€ e 50€”)
        In base alla disponibilità del servizio di delivery
        In base alla disponibilità del servizio di prenotazione online
        Per media del numero di stelle
        Una combinazione dei precedenti criteri di ricerca

        possiamo decidere se fare un unico metodo, quindi in base agli elementi inseriti troviamo i ristoranti (gli elementi non inseriti saranno null)
        oppure facciamo più metodi con l'overloading (anche se essendoci la combinazione di più criteri conviene farne uno unico)
    */

    /**
     * ricerca di un ristorante
     * @param luogo
     * @param cucina
     * @param prezzoMinore
     * @param prezzoMaggiore
     * @param delivery
     * @param prenotazioneOn
     * @param medStelle
     * @return lista di ristoranti
     */
    @Override
    public LinkedList<Ristorante> cercaRistorante(Luogo luogo, String cucina, double prezzoMinore, double prezzoMaggiore, boolean delivery, boolean prenotazioneOn, double medStelle)
    {
        LinkedList<Ristorante> lista = GestioneFile.leggiFile();


        if(luogo!= null)
        {
            //da gestire opportunamente con il luogo definitivo
        }

        if(cucina != null)
        {
            lista.removeIf((x)-> !(x.getCucina().equals(cucina)));
        }
        //per convenzione se il valore è zero il campo è considerato nullo
        if(prezzoMinore>0)
        {    if(prezzoMaggiore>0)
            {
                //Ricerca completa min>x>max
                lista.removeIf((x)->x.prezzo<prezzoMaggiore);
            }
            //ricerca min>x
            lista.removeIf((x)->x.prezzo<prezzoMinore);
        }
        else
            if(prezzoMaggiore>0)
                //ricerca max>x
                lista.removeIf((x)->x.prezzo<prezzoMaggiore);

        if(delivery)
        {
            lista.removeIf((x)->x.isDelivery()==false);
        }

        if(prenotazioneOn)
        {
            lista.removeIf((x)->x.isBooking()==false);
        }

        //per convenzione lo il num stelle a zero è nullo
        if(medStelle>=0)
        {
            // lista.removeIf((x)->x.getMediaStelle()<medStelle);
        }

        return lista;
        //todo da rivedere se serve
    }

//    public static void main(String args[])
//    {
//        Luogo luogo = new Luogo("rea","fa","d",3,4.5,3.4);
//        UtenteRegistrato ut = new UtenteRegistrato("gig","fa","faf",new Date("23/5/2005"),"12345",luogo);
//        LinkedList<Ristorante> lista = ut.cercaRistorante(luogo,"Italiana",0,35,true,false,0);
//
//        System.out.println(lista.toString());
//
//    }

}
