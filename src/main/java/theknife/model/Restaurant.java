package theknife.model;

public class Restaurant {
    private String nome;
    private String citta;
    private String indirizzo;
    private String Prezzo;
    private boolean delivery;
    private boolean booking;
    private String tipoCucina;
    private String nazione;
    private double latitudine;
    private double longitudine;
    private String website;
    private String link;
    private String awards;

    public Restaurant() {
    }

    public Restaurant(String nome, String Nazione, String citta, String indirizzo,
                      double Latitudine, double longitudine, String Prezzo,
                      boolean delivery, boolean booking, String tipoCucina) {
        this.nome = nome;
        this.nazione = Nazione;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.latitudine = Latitudine;
        this.longitudine = longitudine;
        this.Prezzo = Prezzo;
        this.delivery = delivery;
        this.booking = booking;
        this.tipoCucina = tipoCucina;
    }

    // getter
    public String getNome() { return nome; }
    public String getCitta() { return citta; }
    public String getIndirizzo() { return indirizzo; }
    public String getPrezzo() { return Prezzo; }
    public boolean isDelivery() { return delivery; }
    public boolean isBooking() { return booking; }
    public String getTipoCucina() { return tipoCucina; }
    public String getNazione() { return nazione; }
    public double getLatitudine() { return latitudine; }
    public double getLongitudine() { return longitudine; }
    public String getWebsite() { return website; }
    public String getLink() { return link; }
    public String getAwards() { return awards; }

    // setter
    public void setNome(String nome) { this.nome = nome; }
    public void setCitta(String citta) { this.citta = citta; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public void setPrezzo(String prezzo) { this.Prezzo = prezzo; }
    public void setDelivery(boolean delivery) { this.delivery = delivery; }
    public void setBooking(boolean booking) { this.booking = booking; }
    public void setTipoCucina(String tipoCucina) { this.tipoCucina = tipoCucina; }
    public void setNazione(String nazione) { this.nazione = nazione; }
    public void setLatitudine(double latitudine) { this.latitudine = latitudine; }
    public void setLongitudine(double longitudine) { this.longitudine = longitudine; }
    public void setWebsite(String website) { this.website = website; }
    public void setLink(String link) { this.link = link; }
    public void setAwards(String awards) { this.awards = awards; }
}