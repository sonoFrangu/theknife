package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import theknife.model.GestioneRistoranti;
import theknife.model.Ristorante;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * Controller che gestisce la visualizzazione e la gestione
 * delle recensioni scritte dall'utente corrente.
 *
 * @author Matteo Franguelli
 */
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
    /**
     * Inizializza la tabella, il menu contestuale e carica le recensioni.
     *
     * @author Matteo Franguelli
     */
    @FXML
    private void initialize() {
        colonnaRistorante.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        colonnaVoto.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colonnaTesto.setCellValueFactory(new PropertyValueFactory<>("text"));

        tabellaRecensioni.setItems(dati);

        menuTastoDestro();

        caricaLeMieRecensioni();
        aggiornaMessaggioVuoto();
    }

    /**
     * Crea il menu che appare col tasto destro sulla riga
     *
     * @author Matteo Franguelli
     */
    private void menuTastoDestro() {
        tabellaRecensioni.setRowFactory(tv -> {
            TableRow<ReviewRow> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem modifyItem = new MenuItem("Modifica recensione");
            MenuItem deleteItem = new MenuItem("Elimina recensione");

            //Azione quando clicchi "Modifica" ---
            modifyItem.setOnAction(event -> {
                ReviewRow riga = row.getItem();
                if (riga != null) {
                    apriModificaRecensione(riga); // <--- Qui richiami il metodo
                }
            });

            // Azione quando clicchi "Elimina"
            deleteItem.setOnAction(event -> {
                ReviewRow rigaSelezionata = row.getItem();
                if (rigaSelezionata != null) {
                    chiediConfermaEdElimina(rigaSelezionata);
                }
            });
            contextMenu.getItems().addAll(modifyItem, deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }
    /**
     * Apre la finestra di modifica per la recensione selezionata.
     *
     * @author Matteo Franguelli
     */
    private void apriModificaRecensione(ReviewRow riga) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/add_review.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Modifica Recensione");
            stage.initModality(Modality.APPLICATION_MODAL);

            AddReviewController ctrl = loader.getController();
            GestioneRistoranti gr = GestioneRistoranti.getInstance();
            Ristorante r = gr.getRistorante(riga.getRawRestaurantId());

            ctrl.setRestaurant(r);
            if (r != null) ctrl.setRestaurantName(r.getNome());
            ctrl.setDatiPerModifica(riga);
            stage.showAndWait();
            caricaLeMieRecensioni();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Chiede conferma ed elimina la recensione selezionata.
     *
     * @author Matteo Franguelli
     */
    private void chiediConfermaEdElimina(ReviewRow riga) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimina Recensione");
        alert.setHeaderText("Sei sicuro di voler eliminare questa recensione?");
        alert.setContentText("L'operazione e' reversibile.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dati.remove(riga);  //rimozione grafica

            rimuoviRecensioneDalFile(riga); //rimozione da file

            aggiornaMessaggioVuoto();
        }
    }

    /**
     * Riscrive il CSV ignorando la riga che corrisponde alla recensione eliminata.
     *
     * @author Matteo Franguelli
     */
    private void rimuoviRecensioneDalFile(ReviewRow rigaDaEliminare) {
        File file = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);
        List<String> righeDaSalvare = new ArrayList<>();

        Session session = Session.getInstance();
        String mioUsername = session.getUsername();
        int mioId = trovaIlMioIdDaUsername(mioUsername);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    righeDaSalvare.add(linea); // mantieni righe vuote o header
                    continue;
                }

                // Salta l'header se presente "N_Stelle"
                if (linea.toLowerCase().startsWith("n_stelle")) {
                    righeDaSalvare.add(linea);
                    continue;
                }

                String[] parti = linea.contains(";") ? linea.split(";") : linea.split(",");

                if (parti.length >= 5) {
                    try {
                        int stelle = Integer.parseInt(pulisci(parti[0]));
                        String testo = pulisci(parti[1]);
                        int idUtente = Integer.parseInt(pulisci(parti[3]));
                        int idRistorante = Integer.parseInt(pulisci(parti[4]));

                        // (Utente uguale + Ristorante uguale + Testo uguale + Voto uguale)
                        if (idUtente == mioId &&
                                idRistorante == rigaDaEliminare.getRawRestaurantId() &&
                                stelle == rigaDaEliminare.getRating() &&
                                testo.equals(rigaDaEliminare.getText())) {
                        }

                    } catch (Exception e) {
                        // riga illeggibile
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Riscriviamo il file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            for (String riga : righeDaSalvare) {
                bw.write(riga);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Carica dal file tutte le recensioni dell'utente corrente.
     *
     * @author Matteo Franguelli
     */
    private void caricaLeMieRecensioni() {
        dati.clear();
        Session session = Session.getInstance();
        if (session.isGuest()) return;

        String mioUsername = session.getUsername();
        int mioId = trovaIlMioIdDaUsername(mioUsername);

        if (mioId == -1) return;

        File file = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine();

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] parti = linea.contains(";") ? linea.split(";") : linea.split(",");

                if (parti.length >= 5) {
                    try {
                        int idAutoreRecensione = Integer.parseInt(pulisci(parti[3]));

                        if (idAutoreRecensione == mioId) {
                            int stelle = Integer.parseInt(pulisci(parti[0]));
                            String testo = pulisci(parti[1]);
                            int idRistorante = Integer.parseInt(pulisci(parti[4]));

                            String nomeRistorante = trovaNomeRistorante(idRistorante);
                            dati.add(new ReviewRow(nomeRistorante, stelle, testo, idRistorante));
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Restituisce l'id dell'utente a partire dallo username.
     *
     * @author Matteo Franguelli
     */
    private int trovaIlMioIdDaUsername(String usernameCercato) {
        File file = new File(NOME_CARTELLA, NOME_FILE_UTENTI);
        if (!file.exists()) return -1;
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine();
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] parti = linea.split(";");
                if (parti.length > 7) {
                    if (pulisci(parti[0]).equals(usernameCercato)) {
                        try { return Integer.parseInt(pulisci(parti[7])); } catch (Exception e) { return -1; }
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return -1;
    }
    /**
     * Restituisce il nome del ristorante dato il suo id.
     *
     * @author Matteo Franguelli
     */
    private String trovaNomeRistorante(int idRistorante) {
        GestioneRistoranti gr = GestioneRistoranti.getInstance();
        for (Ristorante r : gr.getListaRistoranti()) {
            if (r.getId() == idRistorante) return r.getNome();
        }
        return "Ristorante ID: " + idRistorante;
    }
    /**
     * Pulisce una stringa da caratteri non desiderati.
     *
     * @author Matteo Franguelli
     */
    private String pulisci(String s) {
        if (s == null) return "";
        return s.trim().replace(";", "").replace("\"", "");
    }
    /**
     * Aggiorna la visibilità della tabella e del messaggio di lista vuota.
     *
     * @author Matteo Franguelli
     */
    private void aggiornaMessaggioVuoto() {
        boolean vuota = dati.isEmpty();
        if (etichettaVuota != null) { etichettaVuota.setVisible(vuota); etichettaVuota.setManaged(vuota); }
        if (tabellaRecensioni != null) { tabellaRecensioni.setVisible(!vuota); }
    }



    // --- MODELLO DATI AGGIORNATO ---
    /**
     * Modello dati per la visualizzazione di una recensione nella tabella.
     *
     * @author Matteo Franguelli
     */
    public static class ReviewRow {
        private final String restaurant;
        private final int rating;
        private final String text;
        private final int rawRestaurantId; // Serve per l'eliminazione

        public ReviewRow(String restaurant, int rating, String text, int rawRestaurantId) {
            this.restaurant = restaurant;
            this.rating = rating;
            this.text = text;
            this.rawRestaurantId = rawRestaurantId;
        }

        public String getRestaurant() { return restaurant; }
        public int getRating() { return rating; }
        public String getText() { return text; }
        public int getRawRestaurantId() { return rawRestaurantId; }
    }
}