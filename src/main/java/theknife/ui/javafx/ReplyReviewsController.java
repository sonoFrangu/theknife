package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

public class ReplyReviewsController {

    @FXML private ListView<Recensione> listaRecensioni;

    private List<Integer> mieiRistorantiIds = new ArrayList<>();

    @FXML
    private void initialize() {
        caricaIMieiRistoranti();
        configuraLista();
        caricaRecensioniRicevute();
    }

    private void caricaIMieiRistoranti() {
        Session session = Session.getInstance();
        if (!session.isRistoratore()) return;

        String username = session.getUsername();
        File fileUsers = new File("doc", "users.csv");

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileUsers))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length > 0 && p[0].equals(username)) {
                    if (p.length > 9 && !p[9].isBlank()) {
                        String[] ids = p[9].split("-");
                        for (String id : ids) {
                            try { mieiRistorantiIds.add(Integer.parseInt(id.trim())); } catch (Exception e){}
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

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

    private void configuraLista() {
        listaRecensioni.setCellFactory(lv -> new ListCell<>() {

            private final Label lblRistorante = new Label();
            private final Label lblStelle = new Label();
            private final Label lblTesto = new Label();
            private final TextArea areaRisposta = new TextArea();
            private final Button btnInvia = new Button("Invia Risposta");
            private final VBox layout = new VBox(8);

            {
                lblRistorante.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                lblStelle.setStyle("-fx-text-fill: gold; -fx-font-size: 14px;");
                lblTesto.setWrapText(true);
                lblTesto.setStyle("-fx-font-style: italic;");

                areaRisposta.setPromptText("Scrivi qui la tua risposta pubblica...");
                areaRisposta.setPrefRowCount(2);
                areaRisposta.setWrapText(true);

                btnInvia.getStyleClass().add("footer-button");
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
                    String nomeRist = trovaNomeRistorante(r.get_id_Ristorante());
                    lblRistorante.setText("Ristorante: " + nomeRist);
                    lblStelle.setText("Voto: " + "★".repeat(r.getNumeroStelle()));
                    lblTesto.setText("\"" + r.getText() + "\"");

                    btnInvia.setOnAction(e -> {
                        String testoRisposta = areaRisposta.getText();
                        if (testoRisposta.isBlank()) {
                            Alert a = new Alert(Alert.AlertType.WARNING, "Scrivi una risposta prima di inviare.");
                            a.showAndWait();
                            return;
                        }

                        // stampa per toschi
                        System.out.println("--------------------------------------------------");
                        System.out.println("RECENSIONE A CUI SI RISPONDE: " + r.getText());
                        System.out.println("VOTO: " + r.getNumeroStelle());
                        System.out.println("NOME RISTORANTE: " + nomeRist);
                        System.out.println("RISPOSTA INVIATA: " + testoRisposta);
                        System.out.println("--------------------------------------------------");

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

    private void salvaRisposta(Recensione recensione, String testoRisposta) {
        File fileRisposte = new File("doc", "risposte.csv");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileRisposte, true))) {
            String riga = recensione.get_id_Ristorante() + ";" +
                    recensione.getIdUtente() + ";" +
                    "RISPOSTA: " + testoRisposta.replace(";", "").replace("\n", " ");

            bw.write(riga);
            bw.newLine();
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