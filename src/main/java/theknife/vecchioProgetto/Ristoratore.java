package theknife.vecchioProgetto;

import java.util.Date;
import java.util.LinkedList;

public class Ristoratore extends UtenteRegistrato{

    private LinkedList<Ristorante> ristoranti;

    public Ristoratore(String username, String nome, String cognome, Date dataNascita, String psw, Luogo luogo, LinkedList<Recensione> recensioni)
    {
        super(username, nome,cognome,dataNascita,psw, luogo);
        ristoratore=true;
        ristoranti = new LinkedList<>();

    }

    public LinkedList<Ristorante> getRistoranti() { return ristoranti;}

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public String toString()
    {
        String s="";
        for(Ristorante r : ristoranti)
            s+= r.toString();
        return  super.toString()+ s;
    }


    public void aggiungiRistorante(Ristorante ristorante)
    {
        ristoranti.add(ristorante);
    }

    public LinkedList<Recensione> visualizzaRecensioni()
    {
        LinkedList<Recensione> recensioni =new LinkedList<>();
        GestioneRecensioni gr = new GestioneRecensioni();
        for(Ristorante r :ristoranti)
            for (Recensione rec: gr.getRecensioni())
                if(rec.getRistorante().equals(r))
                    recensioni.add(rec);
        return recensioni;
    }

    public void rispostaRecensione()
    {

    }

    public int numeroRecensioni()
    {
        GestioneRecensioni gr = new GestioneRecensioni();
        int cont=0;
        for(Ristorante r : ristoranti)
            for(Recensione rec : gr.getRecensioni())
                if(rec.getRistorante().equals(r))
                    cont++;
        return cont;
    }

    public double calcolaMedia()
    {
        GestioneRecensioni gr = new GestioneRecensioni();
        int contStelle=0;
        for(Ristorante rist: ristoranti)
            for(Recensione rec : gr.getRecensioni())
                if(rec.getRistorante().equals(rist))
                    contStelle+=rec.getNumeroStelle();


        return contStelle/numeroRecensioni();
    }

    public String visualizzaRiepilogo(Ristorante ristorante)
    {
        String s = "";
        GestioneRecensioni gr = new GestioneRecensioni();
        s+="Le stelle medie sono "+calcolaMedia()+" ("+numeroRecensioni()+")\n";
        for(Ristorante r: ristoranti)
        {
            s+=r.toString();
            for (Recensione rec: gr.getRecensioni())
                if(rec.getRistorante().equals(ristorante))
                    s+="\t"+rec.toString();
        }

        return s;
    }

    public LinkedList<Ristorante> visualizzaPropriRistoranti()
    {
       return getRistoranti();
    }





}
