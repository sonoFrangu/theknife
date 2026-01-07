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
    private final ObservableList<Recensione> recensioniData = FXCollections.observableArrayList();

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_RECENSIONI = "recensioni.csv";

    @FXML
    private void initialize() {
        listaRecensioni.setItems(recensioniData);
        impostaGraficaCelle();
    }

    public void setRestaurant(Ristorante r) {
        this.ristoranteSelezionato = r;

        if (this.ristoranteSelezionato != null) {
            if (etichettaTitolo != null) {
                etichettaTitolo.setText("Recensioni: " + r.getNome());
            }
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

    private String pulisci(String s) {
        if (s == null) return "";
        return s.trim().replace(";", "");
    }

    private void impostaGraficaCelle() {
        listaRecensioni.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Recensione item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
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
                    lblTesto.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;");

                    box.getChildren().addAll(lblStelle, lblTesto);
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