package theknife.model;
import static java.lang.Math.*;

public class Luogo {

    private static final double RAGGIOTERRESTRE_KM= 6371;


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


    /**
     * Verifica se un luogo è vicino entro 10 kilometri
     *
     * @param lat2 latitudine da confrontare
     * @param longi2 longitudine da confrontare
     * @return boolean true se < 10 km
     */
    public boolean checkDistance10KM(double lat2, double longi2  )
    {
        double lat1Rad = toRadians(latitudine);
        double long1Rad= toRadians(longitudine);

        double lat2Rad = toRadians(lat2);
        double long2Rad= toRadians(longi2);

        double dLat = lat2 - lat1Rad;
        double dLon = longi2 - long1Rad;

        double a = pow(sin(dLat / 2), 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                        pow(sin(dLon / 2), 2);

        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        double distanzaKm=RAGGIOTERRESTRE_KM*c;
        if(distanzaKm> 10)
            return false;
        else
            return true;
    }

}
