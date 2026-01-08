package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.model.Recensione;
import theknife.model.Ristorante;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Controller della vista che mostra le recensioni di un ristorante selezionato.
 * @author Matteo Franguelli
 */
public class ViewReviewsController {

    @FXML private Label etichettaTitolo;
    @FXML private ListView<Recensione> listaRecensioni;
    @FXML private Label etichettaMedia;
    @FXML private Label etichettaConteggio;

    private Ristorante ristoranteSelezionato;
    private final ObservableList<Recensione> recensioniData = FXCollections.observableArrayList();

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_RECENSIONI = "recensioni.csv";
    private static final String NOME_FILE_UTENTI = "users.csv";
    private final java.util.Map<Integer, String> utentiAttuali = new java.util.HashMap<>();
    /**
     * Inizializza la lista delle recensioni e la grafica delle celle.
     * @author Matteo Franguelli
     */
    @FXML
    private void initialize() {
        listaRecensioni.setItems(recensioniData);
        impostaGraficaCelle();
    }
    /**
     * Imposta il ristorante corrente e carica le relative recensioni.
     * @author Matteo Franguelli
     */
    public void setRestaurant(Ristorante r) {
        this.ristoranteSelezionato = r;

        if (this.ristoranteSelezionato != null) {
            if (etichettaTitolo != null) {
                etichettaTitolo.setText("Recensioni: " + r.getNome());
            }
            caricaRecensioniSpecifiche();
            calcolaStatistiche();
        }
    }
    /**
     * Carica dal file solo le recensioni associate al ristorante selezionato.
     * @author Matteo Franguelli
     */
    private void caricaRecensioniSpecifiche() {
        recensioniData.clear();

        File file = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta header

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                // Gestisce sia separatore ; che ,
                String[] parti;
                if (linea.contains(";")) parti = linea.split(";");
                else parti = linea.split(",");

                if (parti.length >= 5) {
                    try {
                        String sStelle = pulisci(parti[0]);
                        String testo = pulisci(parti[1]);
                        String sIdUtente = pulisci(parti[3]);
                        String sIdRistorante = pulisci(parti[4]);

                        int idRistCsv = Integer.parseInt(sIdRistorante);
                        int idRistAttuale = ristoranteSelezionato.getId();

                        // Carica solo se gli ID coincidono
                        if (idRistCsv == idRistAttuale) {
                            int stelle = Integer.parseInt(sStelle);
                            int idUtente = Integer.parseInt(sIdUtente);

                            Recensione rec = new Recensione(stelle, testo, idUtente, idRistCsv);
                            recensioniData.add(rec);
                        }

                    } catch (Exception ignored) {
                        // Ignora righe malformate senza intasare la console
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restituisce lo username associato all'id utente.
     * @author Matteo Franguelli
     */
    private String ricavaUsername(int idUtente) {
        if (utentiAttuali.containsKey(idUtente)) return utentiAttuali.get(idUtente);

        File file = new File(NOME_CARTELLA, NOME_FILE_UTENTI);
        if (!file.exists()) return "Utente " + idUtente;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta l'header

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                // Il file usa il punto e virgola
                String[] parti = linea.split(";");

                // IdUtente è all'indice 7, Username all'indice 0
                if (parti.length > 7) {
                    try {
                        int currentId = Integer.parseInt(pulisci(parti[7]));

                        if (currentId == idUtente) {
                            String username = pulisci(parti[0]);
                            utentiAttuali.put(idUtente, username);
                            return username;
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) { e.printStackTrace(); }

        return "Utente ID: " + idUtente;
    }
    /**
     * Pulisce una stringa rimuovendo spazi e separatori non desiderati.
     *
     * @author Matteo Franguelli
     */
    private String pulisci(String s) {
        if (s == null) return "";
        return s.trim().replace(";", "");
    }
    /**
     * Imposta la grafica personalizzata delle celle della lista recensioni.
     *
     * @author Matteo Franguelli
     */
    private void impostaGraficaCelle() {
        listaRecensioni.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Recensione item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    getStyleClass().remove("review-item");
                    setStyle("");
                } else {
                    VBox box = new VBox(5);
                    box.getStyleClass().add("review-item");

                    StringBuilder stelleStr = new StringBuilder();
                    for(int i=0; i<item.getNumeroStelle(); i++) stelleStr.append("★");
                    for(int i=item.getNumeroStelle(); i<5; i++) stelleStr.append("☆");

                    Label lblStelle = new Label(stelleStr.toString());
                    lblStelle.setStyle("-fx-text-fill: gold; -fx-font-size: 16px; -fx-font-weight: bold;");

                    Label lblTesto = new Label(item.getText());
                    lblTesto.setWrapText(true);
                    lblTesto.setMaxWidth(350);
                    lblTesto.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

                    Label lblInfo = new Label("Utente: " + ricavaUsername(item.getIdUtente()));
                    lblInfo.setStyle("-fx-text-fill: gray; -fx-font-size: 12px; -fx-font-style: italic;");

                    box.getChildren().addAll(lblStelle, lblTesto, lblInfo);
                    setGraphic(box);
                }
            }
        });
    }
    /**
     * Chiude la finestra corrente.
     *
     * @author Matteo Franguelli
     */
        @FXML
    private void onChiudi() {
        Stage stage = (Stage) etichettaTitolo.getScene().getWindow();
        stage.close();
    }

    private void calcolaStatistiche() {
        if (recensioniData.isEmpty()) {
            if (etichettaMedia != null) etichettaMedia.setText("Media stelle: N/D");
            if (etichettaConteggio != null) etichettaConteggio.setText("Numero di recensioni: N/D");
            return;
        }

        double sommaStelle = 0;
        for (Recensione rec : recensioniData) {
            sommaStelle += rec.getNumeroStelle();
        }
        double media = sommaStelle / recensioniData.size();


        //todo mettere getMediaStelle
        String mediaFormattata = "0.5";
        if (etichettaMedia != null) {
            etichettaMedia.setText("Media stelle: " + mediaFormattata);
        }
        if (etichettaConteggio != null) {
            etichettaConteggio.setText("Numero di recensioni: " + recensioniData.size());
        }
    }
}