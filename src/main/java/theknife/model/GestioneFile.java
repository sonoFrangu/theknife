package theknife.model;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Elia Toschi
 * @author Celestino Resteghini
 * @author Matteo Franguelli
 * La classe si occupa della gestione dei File
 */
public class GestioneFile {

    private static String nomeCartella = "doc";
    private static String nomeFileRistoranti = "michelin_my_maps.csv";
    private static String nomeFileUtenti = "users.csv";
    private static String nomeFileRecensioni = "recensioni.csv";

    private static final String NOME_CARTELLA_DOC = "doc";


    // Percorso base per la cartella doc
    private static final String percorsoBase = System.getProperty("user.dir") + File.separator + nomeCartella + File.separator;

    private static final String percorsoFileRistoranti = percorsoBase + nomeFileRistoranti;
    private static final String percorsoFileUtenti = percorsoBase + nomeFileUtenti;
    private static final String percorsoFileRecensioni = percorsoBase + nomeFileRecensioni;

    /**
     * Lettura da file csv dei ristoranti
     * @return LinkedList<Ristorante>
     */
    public static LinkedList<Ristorante> leggiFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(percorsoFileRistoranti))) {
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


    public static void scriviFile(Ristorante ristorante) {
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

        String url = "null";
        String websiteUrl = "null";
        String award = "null";
        String greenStar = "null";
        String facilitiesAndServices = "null";
        String description = "null";

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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(percorsoFileRistoranti, true))) {
            bw.write(riga.toString());
            bw.newLine();
            System.out.println("Ristorante aggiunto correttamente.");
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }

    // =============================================================
    // NUOVI METODI AGGIUNTI
    // =============================================================

    /**
     * Cerca nel file users.csv l'ID corrispondente a un dato username.
     * @param usernameCercato Lo username da cercare (es. "clt")
     * @return int L'ID dell'utente, oppure -1 se non trovato o errore.
     */
    public static int trovaIdUtenteDaUsername(String usernameCercato) {
        File file = new File(percorsoFileUtenti);
        if (!file.exists()) {
            System.err.println("File utenti non trovato in: " + percorsoFileUtenti);
            return -1;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta header (se presente) o leggilo

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                // Nota: users.csv usa il punto e virgola come separatore
                String[] parti = linea.split(";");

                // Controlliamo che ci siano abbastanza colonne (ID è indice 7, Username indice 0)
                if (parti.length > 7) {
                    String userNelFile = pulisci(parti[0]);

                    if (userNelFile.equals(usernameCercato)) {
                        try {
                            return Integer.parseInt(pulisci(parti[7]));
                        } catch (NumberFormatException e) {
                            System.err.println("Errore formato ID per utente " + usernameCercato);
                            return -1;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // Utente non trovato
    }

    /**
     * Cerca nel file users.csv la città associata allo username.
     * @author Matteo Franguelli
     */
    public static String recuperaCittaUtente(String usernameTarget) {
        File file = new File("doc", "users.csv");
        if (!file.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta header
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                String[] parti = linea.split(";");
                if (parti.length > 0 && pulisci(parti[0]).equalsIgnoreCase(usernameTarget)) {
                    if (parti.length > 4) {
                        return pulisci(parti[4]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metodo di utilità per pulire le stringhe lette da CSV.
     * Rimuove spazi iniziali/finali, doppi apici e punto e virgola.
     */
    private static String pulisci(String s) {
        if (s == null) return "";
        return s.trim().replace("\"", "").replace(";", "");
    }

    public static int recuperaId(String username)
    {
        File cartellaDoc = new File(NOME_CARTELLA_DOC);
        File fileUtenti = new File(cartellaDoc, nomeFileUtenti);
        int id=0;
        try(BufferedReader br = new BufferedReader(new FileReader(fileUtenti, StandardCharsets.UTF_8))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null ) {

                String[] parti = linea.split(";");

                if(parti[0].equals(username))
                {
                    id= Integer.valueOf(parti[7].trim());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Legge le recensioni dal file.
     * NOTA: Poiché la classe Recensione imposta la data a "adesso" nel costruttore,
     * la data storica presente nel file CSV viene ignorata in fase di caricamento.
     */
    public static LinkedList<Recensione> leggiRecensioni() {
        LinkedList<Recensione> lista = new LinkedList<>();
        File file = new File(percorsoFileRecensioni);

        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.toLowerCase().startsWith("n_stelle")) continue;

                String[] parti = linea.contains(";") ? linea.split(";") : linea.split(",");

                if (parti.length >= 5) {
                    try {
                        int stelle = Integer.parseInt(pulisci(parti[0]));
                        String testo = pulisci(parti[1]);
                        // parti[2] era data
                        int idUtente = Integer.parseInt(pulisci(parti[3]));
                        int idRistorante = Integer.parseInt(pulisci(parti[4]));

                        Recensione r = new Recensione(stelle, testo, idUtente, idRistorante);
                        lista.add(r);

                    } catch (Exception e) {
                        System.err.println("Errore lettura riga: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Rimuove la recensione dal file CSV confrontando Utente, Ristorante, Voto e Testo.
     * La data viene ignorata nel confronto.
     */
    public static void rimuoviRecensione(int idUtente, int idRistorante, int voto, String testo) {
        File file = new File(percorsoFileRecensioni);
        List<String> righeDaSalvare = new LinkedList<>();
        boolean trovata = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.toLowerCase().startsWith("n_stelle")) {
                    righeDaSalvare.add(linea);
                    continue;
                }
                String[] parti = linea.contains(";") ? linea.split(";") : linea.split(",");
                boolean daEliminare = false;

                if (parti.length >= 5) {
                    try {
                        int rVoto = Integer.parseInt(pulisci(parti[0]));
                        String rTesto = pulisci(parti[1]);
                        int rIdUtente = Integer.parseInt(pulisci(parti[3]));
                        int rIdRistorante = Integer.parseInt(pulisci(parti[4]));

                        if (!trovata &&
                                rIdUtente == idUtente &&
                                rIdRistorante == idRistorante &&
                                rVoto == voto &&
                                rTesto.equals(testo)) {

                            daEliminare = true;
                            trovata = true;
                        }
                    } catch (Exception e) {
                    }
                }

                if (!daEliminare) {
                    righeDaSalvare.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (trovata) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                for (String riga : righeDaSalvare) {
                    bw.write(riga);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}