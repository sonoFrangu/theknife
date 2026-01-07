package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import theknife.model.GestioneRistoranti;
import theknife.model.Ristorante;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MyReviewsController {

    @FXML private TableView<ReviewRow> tabellaRecensioni;
    @FXML private TableColumn<ReviewRow, String> colonnaRistorante;
    @FXML private TableColumn<ReviewRow, Integer> colonnaVoto;
    @FXML private TableColumn<ReviewRow, String> colonnaTesto;
    @FXML private Label etichettaVuota;

    private final ObservableList<ReviewRow> dati = FXCollections.observableArrayList();

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_RECENSIONI = "recensioni.csv";
    private static final String NOME_FILE_UTENTI = "users.csv";

    @FXML
    private void initialize() {
        // Configura le colonne
        colonnaRistorante.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        colonnaVoto.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colonnaTesto.setCellValueFactory(new PropertyValueFactory<>("text"));

        tabellaRecensioni.setItems(dati);

        // 1. Avvia la catena: Username -> ID -> Recensioni
        caricaLeMieRecensioni();

        aggiornaMessaggioVuoto();
    }

    private void caricaLeMieRecensioni() {
        dati.clear();

        // Recupera lo username corrente (es. "clt")
        Session session = Session.getInstance();
        if (session.isGuest()) return;

        String mioUsername = session.getUsername();

        // 2. Trova il mio ID numerico leggendo il file users.csv
        int mioId = trovaIlMioIdDaUsername(mioUsername);

        if (mioId == -1) {
            System.err.println("Errore: Impossibile trovare l'ID per l'utente " + mioUsername);
            return;
        }

        // 3. Leggi le recensioni e prendi solo quelle col mio ID
        File file = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta header

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                // Gestione separatore ; o ,
                String[] parti = linea.contains(";") ? linea.split(";") : linea.split(",");

                // CSV Recensioni: N_Stelle;Text;Date;IdUtente;IdRistorante
                if (parti.length >= 5) {
                    try {
                        // ID Utente che ha scritto la recensione (indice 3)
                        int idAutoreRecensione = Integer.parseInt(pulisci(parti[3]));

                        // SE L'AUTORE SONO IO:
                        if (idAutoreRecensione == mioId) {

                            int stelle = Integer.parseInt(pulisci(parti[0]));
                            String testo = pulisci(parti[1]);
                            int idRistorante = Integer.parseInt(pulisci(parti[4]));

                            // Trova il nome del ristorante
                            String nomeRistorante = trovaNomeRistorante(idRistorante);

                            dati.add(new ReviewRow(nomeRistorante, stelle, testo));
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Legge users.csv e cerca l'ID corrispondente allo username dato.
     */
    private int trovaIlMioIdDaUsername(String usernameCercato) {
        File file = new File(NOME_CARTELLA, NOME_FILE_UTENTI);
        if (!file.exists()) return -1;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta header

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                String[] parti = linea.split(";");

                // CSV Utenti: Username(0) ... IdUtente(7)
                if (parti.length > 7) {
                    String userNelFile = pulisci(parti[0]);

                    if (userNelFile.equals(usernameCercato)) {
                        try {
                            return Integer.parseInt(pulisci(parti[7]));
                        } catch (NumberFormatException e) {
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

    private String trovaNomeRistorante(int idRistorante) {
        GestioneRistoranti gr = GestioneRistoranti.getInstance();
        for (Ristorante r : gr.getListaRistoranti()) {
            if (r.getId() == idRistorante) {
                return r.getNome();
            }
        }
        return "Ristorante ID: " + idRistorante;
    }

    private String pulisci(String s) {
        if (s == null) return "";
        return s.trim().replace(";", "").replace("\"", "");
    }

    private void aggiornaMessaggioVuoto() {
        boolean vuota = dati.isEmpty();
        if (etichettaVuota != null) {
            etichettaVuota.setVisible(vuota);
            etichettaVuota.setManaged(vuota);
        }
        if (tabellaRecensioni != null) {
            tabellaRecensioni.setVisible(!vuota);
        }
    }

    // --- Modello Dati ---
    public static class ReviewRow {
        private final String restaurant;
        private final int rating;
        private final String text;

        public ReviewRow(String restaurant, int rating, String text) {
            this.restaurant = restaurant;
            this.rating = rating;
            this.text = text;
        }

        public String getRestaurant() { return restaurant; }
        public int getRating() { return rating; }
        public String getText() { return text; }
    }
}