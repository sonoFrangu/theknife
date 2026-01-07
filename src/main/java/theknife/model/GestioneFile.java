package theknife.model;

import java.io.*;
import java.util.LinkedList;

/**
 * @author Elia Toschi
 * @author Celestino Resteghini
 * @author Matteo Franguelli
 * La classe si occupa della gestione dei File
 */
public class GestioneFile {

    private static String nomeCartella = "doc";
    private static String nomeFile = "michelin_my_maps.csv";
    private static final String percorsoFile= System.getProperty("user.dir") + File.separator + nomeCartella + File.separator + nomeFile;

    /**
     * Lettura da file csv
     * @return LinkedList<Ristorante>
     */
    public static LinkedList<Ristorante> leggiFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(percorsoFile))) {
            String riga;
            int numeroRiga = 1;

            while ((riga = br.readLine()) != null) {
                String[] valori = riga.split(",");

                System.out.print(numeroRiga + ": ");
                for (String valore : valori) {
                    System.out.print(valore.trim() + " | ");
                }
                System.out.println();
                numeroRiga++;
            }
            return new LinkedList<Ristorante>();

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            return new LinkedList<Ristorante>();
        }
    }

    /**
     * Scrittura su file csv
     * @param ristorante
     */
    public static void scriviFile(Ristorante ristorante) {
        // Costruzione dei campi da scrivere nel file
        String name = ristorante.getNome();
        String address = ristorante.getLuogo() != null ? "\"" + ristorante.getLuogo()+", " + ristorante.getLuogo().getIndirizzo() + ", " + ristorante.getLuogo().getCitta() + "\"": "null";
        String location = ristorante.getLuogo() != null ? "\"" + ristorante.getLuogo().getCitta() + ", " + ristorante.getLuogo().getNazione() + "\"": "null";
        String price = String.valueOf(ristorante.prezzo);
        String cuisine = ristorante.getCucina() != null ? "\"" + String.join(", ", ristorante.getCucina()) + "\"" : "null";
        String longitude = ristorante.getLuogo() != null ? String.valueOf(ristorante.getLuogo().getLongitudine()) : "null";
        String latitude = ristorante.getLuogo() != null ? String.valueOf(ristorante.getLuogo().getLatitudine()) : "null";
        String phoneNumber = ristorante.getN_tel();
        String domicilio = ristorante.isDelivery() ? "true" : "false";
        String prenotazione = ristorante.isBooking() ? "true" : "false";

        // Non esistente nel costruttore di ristorante
        String url = "null";
        String websiteUrl = "null";
        String award = "null";
        String greenStar = "null";
        String facilitiesAndServices = "null";

        String description = "null"; // non disponibile

        // Costruzione riga CSV
        String[] campi = {
                name, address, location, price, cuisine, longitude, latitude, phoneNumber, url, websiteUrl, award, greenStar, facilitiesAndServices, description, domicilio, prenotazione
        };

        StringBuilder riga = new StringBuilder();
        for (int i = 0; i < campi.length; i++) {
            String valore = (campi[i] == null || campi[i].trim().isEmpty()) ? "null" : campi[i].trim();
            riga.append(valore);
            if (i < campi.length - 1) {
                riga.append(",");
            }
        }

        // Scrittura su file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(percorsoFile, true))) {
            bw.write(riga.toString());
            bw.newLine();
            System.out.println("Ristorante aggiunto correttamente.");
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }
}
