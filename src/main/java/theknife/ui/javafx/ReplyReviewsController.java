package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.model.GestioneFile;
import theknife.model.GestioneRistoranti;
import theknife.model.Recensione;
import theknife.model.Ristorante;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la gestione delle risposte alle recensioni da parte del Ristoratore.
 */
public class ReplyReviewsController {

    @FXML private ListView<Recensione> listaRecensioni;

    private List<Integer> mieiRistorantiIds = new ArrayList<>();

    @FXML
    private void initialize() {
        caricaIMieiRistoranti();
        configuraLista();
        caricaRecensioniRicevute();
    }

    /**
     * Recupera gli ID dei ristoranti posseduti dall'utente loggato.
     */
    private void caricaIMieiRistoranti() {
        Session session = Session.getInstance();
        if (!session.isRistoratore()) return;

        // Recuperiamo la lista "MieiRistoranti" dal CSV users (logica simile a AddRestaurant)
        // Nota: Qui simulo il recupero leggendo dal CSV o usando una logica esistente.
        // Se hai già un metodo nel model per "getRistorantiDi(idUtente)" usalo.
        // Qui faccio un parsing rapido basato sulla logica che abbiamo visto prima.

        // Per semplicità, scansioniamo i ristoranti e vediamo se l'utente ne è proprietario
        // SE NON HAI IL CAMPO "OWNER" nel ristorante, dobbiamo usare la stringa nel file users.csv.
        // Esempio rapido di recupero IDs dal file users:

        String username = session.getUsername();
        File fileUsers = new File("doc", "users.csv");
        // ... (Codice di lettura file simile a quello che hai già usato) ...
        // Per brevità, assumiamo che tu abbia popolato la lista mieiRistorantiIds
        // Se non hai un metodo pronto, fammelo sapere e te lo scrivo.

        // ESEMPIO HARDCODED per test (sostituisci con logica reale):
        // mieiRistorantiIds.add(17736);
        // mieiRistorantiIds.add(17737);

        // LOGICA REALE (basata sul tuo AddRestaurantController):
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileUsers))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length > 0 && p[0].equals(username)) {
                    if (p.length > 9 && !p[9].isBlank()) {
                        String[] ids = p[9].split("-"); // colonna "MieiRistoranti"
                        for (String id : ids) {
                            try { mieiRistorantiIds.add(Integer.parseInt(id.trim())); } catch (Exception e){}
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Carica tutte le recensioni e filtra solo quelle dei miei ristoranti.
     */
    private void caricaRecensioniRicevute() {
        List<Recensione> tutte = GestioneFile.leggiRecensioni();
        List<Recensione> filtrate = new ArrayList<>();

        for (Recensione r : tutte) {
            if (mieiRistorantiIds.contains(r.get_id_Ristorante())) {
                filtrate.add(r);
            }
        }
        listaRecensioni.getItems().setAll(filtrate);
    }

    /**
     * Configura la grafica di ogni riga della lista.
     */
    private void configuraLista() {
        listaRecensioni.setCellFactory(lv -> new ListCell<>() {

            // Elementi grafici della cella
            private final Label lblRistorante = new Label();
            private final Label lblStelle = new Label();
            private final Label lblTesto = new Label();
            private final TextArea areaRisposta = new TextArea();
            private final Button btnInvia = new Button("Invia Risposta");
            private final VBox layout = new VBox(8);

            {
                // Stile
                lblRistorante.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                lblStelle.setStyle("-fx-text-fill: gold; -fx-font-size: 14px;");
                lblTesto.setWrapText(true);
                lblTesto.setStyle("-fx-font-style: italic;");

                areaRisposta.setPromptText("Scrivi qui la tua risposta pubblica...");
                areaRisposta.setPrefRowCount(2);
                areaRisposta.setWrapText(true);

                btnInvia.getStyleClass().add("footer-button"); // Usa stile esistente
                btnInvia.setStyle("-fx-font-size: 12px; -fx-padding: 5 15;");

                layout.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
                layout.getChildren().addAll(lblRistorante, lblStelle, lblTesto, new Separator(), areaRisposta, btnInvia);
            }

            @Override
            protected void updateItem(Recensione r, boolean empty) {
                super.updateItem(r, empty);

                if (empty || r == null) {
                    setGraphic(null);
                } else {
                    // Recupera nome ristorante
                    String nomeRist = trovaNomeRistorante(r.get_id_Ristorante());
                    lblRistorante.setText("Ristorante: " + nomeRist);

                    // Stelle
                    lblStelle.setText("Voto: " + "★".repeat(r.getNumeroStelle()));

                    // Testo recensione
                    lblTesto.setText("\"" + r.getText() + "\"");

                    // Azione Bottone
                    btnInvia.setOnAction(e -> {
                        String testoRisposta = areaRisposta.getText();
                        if (testoRisposta.isBlank()) {
                            Alert a = new Alert(Alert.AlertType.WARNING, "Scrivi una risposta prima di inviare.");
                            a.showAndWait();
                            return;
                        }

                        salvaRisposta(r, testoRisposta);

                        areaRisposta.clear();
                        Alert a = new Alert(Alert.AlertType.INFORMATION, "Risposta inviata con successo!");
                        a.showAndWait();
                    });

                    setGraphic(layout);
                }
            }
        });
    }

    /**
     * Salva la risposta su file (o DB).
     * Attualmente appendiamo su un file "risposte.csv".
     */
    private void salvaRisposta(Recensione recensione, String testoRisposta) {
        File fileRisposte = new File("doc", "risposte.csv");
        // Formato: ID_RECENSIONE(se esiste);ID_RISTORANTE;ID_AUTORE_REC;TESTO_RISPOSTA
        // Poiché Recensione non ha ID univoco pubblico facile, usiamo una combinazione

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileRisposte, true))) {
            String riga = recensione.get_id_Ristorante() + ";" +
                    recensione.getIdUtente() + ";" +
                    "RISPOSTA: " + testoRisposta.replace(";", "").replace("\n", " ");

            bw.write(riga);
            bw.newLine();
            System.out.println("Risposta salvata: " + riga);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String trovaNomeRistorante(int id) {
        GestioneRistoranti gr = GestioneRistoranti.getInstance();
        for (Ristorante r : gr.listaRistoranti) {
            if (r.getId() == id) return r.getNome();
        }
        return "Sconosciuto (ID " + id + ")";
    }

    @FXML
    private void onChiudi() {
        Stage stage = (Stage) listaRecensioni.getScene().getWindow();
        stage.close();
    }
}