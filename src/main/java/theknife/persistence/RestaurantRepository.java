package theknife.persistence;

import theknife.model.Restaurant;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe si occupa di leggere i dati dei ristoranti da un file CSV
 * e convertirli in oggetti Ristorante.
 */
public class RestaurantRepository {

    /**
     * Carica i ristoranti da un file CSV.
     * Ogni riga del file rappresenta un ristorante.
     *
     * @param percorso Percorso del file CSV
     * @return Lista di oggetti Ristorante
     */
    public static List<Restaurant> caricaDaCsv(String percorso) {
        List<Restaurant> ristoranti = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(percorso))) {

            String linea;

            br.readLine(); // Salta l'intestazione del CSV (header)

            while ((linea = br.readLine()) != null) {

                // Divide la riga usando sia la virgola che il punto e virgola
                String[] campi = linea.split("[;,]");

                // Controllo minimo per evitare errori
                if (campi.length < 10) continue;

                Restaurant r = new Restaurant();

                // Informazioni principali
                r.setNome(campi[0].trim());
                r.setIndirizzo(campi[1].trim());
                r.setCitta(campi[2].trim());
                r.setPrezzo(campi[3].trim()); // Es. €, €€, €€€

                // Coordinate geografiche
                try {
                    r.setLongitudine(Double.parseDouble(campi[4].trim()));
                    r.setLatitudine(Double.parseDouble(campi[5].trim()));
                } catch (NumberFormatException e) {
                    // In caso di errore, mettiamo coordinate 0,0
                    r.setLongitudine(0);
                    r.setLatitudine(0);
                }

                // Colonne booleane: consegna e prenotazione
                boolean delivery = false;
                boolean booking = false;
                try {
                    delivery = Boolean.parseBoolean(campi[6].trim());
                    booking = Boolean.parseBoolean(campi[7].trim());
                } catch (Exception ignored) {}

                r.setDelivery(delivery);
                r.setBooking(booking);

                // Tipo di cucina e sito web
                if (campi.length > 8) r.setTipoCucina(campi[8].trim());
                if (campi.length > 9) r.setWebsite(campi[9].trim());

                ristoranti.add(r);
            }

        } catch (Exception e) {
            // In caso di errore file non trovato o lettura fallita
            System.err.println("Errore durante la lettura del file CSV:");
            e.printStackTrace();
        }

        return ristoranti;
    }
}