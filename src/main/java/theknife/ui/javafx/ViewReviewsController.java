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

public class ViewReviewsController {

    @FXML private Label etichettaTitolo;
    @FXML private ListView<Recensione> listaRecensioni;

    private Ristorante ristoranteSelezionato;
    private ObservableList<Recensione> recensioniData = FXCollections.observableArrayList();

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_RECENSIONI = "recensioni.csv";

    @FXML
    private void initialize() {
        // Collega i dati alla ListView
        listaRecensioni.setItems(recensioniData);

        // Imposta la grafica usando le classi CSS del tuo file
        impostaGraficaCelle();
    }

    public void setRestaurant(Ristorante r) {
        this.ristoranteSelezionato = r;

        if (ristoranteSelezionato != null) {
            etichettaTitolo.setText("Recensioni: " + r.getNome());
            caricaRecensioniSpecifiche();
        }
    }

    private void caricaRecensioniSpecifiche() {
        recensioniData.clear();
        File file = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);

        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // Salta header

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                String[] parti = linea.split(";");
                if (parti.length >= 5) {
                    try {
                        int idRistCsv = Integer.parseInt(parti[4].trim());

                        // Carica solo se l'ID corrisponde al ristorante selezionato
                        if (idRistCsv == ristoranteSelezionato.getId()) {
                            int stelle = Integer.parseInt(parti[0].trim());
                            String testo = parti[1].trim();
                            int idUtente = Integer.parseInt(parti[3].trim());

                            Recensione rec = new Recensione(stelle, testo, idUtente, idRistCsv);
                            recensioniData.add(rec);
                        }
                    } catch (NumberFormatException e) {
                        // Ignora righe errate
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applica lo stile definito nel CSS (.review-item, .review-stars, ecc.)
     */
    private void impostaGraficaCelle() {
        listaRecensioni.setCellFactory(param -> new ListCell<Recensione>() {
            @Override
            protected void updateItem(Recensione item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    // Importante: rimuove lo stile se la cella diventa vuota
                    getStyleClass().remove("review-item");
                } else {
                    // Contenitore principale della recensione
                    VBox box = new VBox(5);
                    // Applica la classe .review-item (sfondo bianco, bordo, ombra)
                    box.getStyleClass().add("review-item");

                    // 1. STELLE
                    StringBuilder stelleStr = new StringBuilder();
                    for(int i=0; i<item.getNumeroStelle(); i++) stelleStr.append("★");
                    for(int i=item.getNumeroStelle(); i<5; i++) stelleStr.append("☆");

                    Label lblStelle = new Label(stelleStr.toString());
                    // Applica la classe .review-stars (colore oro, grassetto)
                    lblStelle.getStyleClass().add("review-stars");

                    // 2. TESTO RECENSIONE
                    Label lblTesto = new Label(item.getText());
                    lblTesto.setWrapText(true);
                    lblTesto.setMaxWidth(420); // Limita la larghezza per evitare scroll orizzontale
                    // Applica la classe .review-text
                    lblTesto.getStyleClass().add("review-text");

                    // 3. INFO UTENTE
                    Label lblInfo = new Label("Utente ID: " + item.getIdUtente());
                    // Applica la classe .review-user-info (grigio, font piccolo)
                    lblInfo.getStyleClass().add("review-user-info");

                    box.getChildren().addAll(lblStelle, lblTesto, lblInfo);
                    setGraphic(box);
                }
            }
        });
    }

    @FXML
    private void onChiudi() {
        Stage stage = (Stage) etichettaTitolo.getScene().getWindow();
        stage.close();
    }
}