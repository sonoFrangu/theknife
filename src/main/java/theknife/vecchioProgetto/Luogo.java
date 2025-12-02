package theknife.vecchioProgetto;

public class Luogo {

    private String nazione;
    private String via;
    private String citta;
    private int n_civico;
    private double latitudine;
    private double longitudine;

    public Luogo(String nazione, String via, String citta, int n_civico,double latitudine, double longitudine)
    {
        this.nazione = nazione;
        this.via = via;
        this.citta = citta;
        this.n_civico = n_civico;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }
    //<editor-fold desc="Get">
    public String getNazione() { return nazione;}
    public String getVia() { return via;}
    public String getCitta() { return citta;}
    public int getN_civico() { return n_civico;}
    public double getLatitudine() { return latitudine;}
    public double getLongitudine() { return longitudine;}
    //</editor-fold>

    public boolean equals(Luogo l)
    {
        return this.nazione.equals(l.nazione) && this.via.equals(l.via) && this.citta.equals(l.citta) &&this.latitudine == l.latitudine && this.longitudine == l.longitudine ;
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof Luogo)
            return this.equals((Luogo)obj);
        return false;
    }
    @Override
    public String toString()
    {
        return "Luogo: "+nazione+" "+via+" "+citta+" "+n_civico+" "+latitudine+" "+longitudine+"\n";
    }


}
