package theknife.model;

/**
 * La classe è una risposta ad una recensione
 * @author Elia Toschi
 */
public class Risposta {

    String text;

    /**
     *
     * @param text
     * @author Elia Toschi
     */
    public Risposta( String text)
    {
        this.text = text;
    }

    public String getText()
    {return text;}
    /**
     * stampa
     * @return stringa
     * @author Elia Toschi
     */
    @Override
    public String toString() {
        return "Recensione: "+text+"\n";
    }

    /**
     * @author Elia Toschi
     * @param r
     * @return boolean
     * @author Elia Toschi
     */
    public boolean equals(Risposta r) {
        return  this.text ==(r.text);
    }

    /**
     * equals
     * @param r
     * @return boolean
     * @author Elia Toschi
     */
    @Override
    public boolean equals(Object r) {
        if(r instanceof Risposta)
            return this.equals((Risposta) r);
        return false;
    }



}
