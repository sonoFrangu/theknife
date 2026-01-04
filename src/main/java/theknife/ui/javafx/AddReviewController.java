package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import theknife.model.Restaurant;

public class AddReviewController {

    @FXML private Label etichettaTitolo;
    @FXML private TextArea areaRecensione;
    @FXML private Label etichettaErrore;

    // Riferimenti ai 5 bottoni stella
    @FXML private Button star1, star2, star3, star4, star5;

    private Restaurant ristoranteDestinazione;

    // Variabile per tenere traccia del voto (default 5 stelle)
    private int votoSelezionato = 5;

    public void setRestaurant(Restaurant restaurant) {
        this.ristoranteDestinazione = restaurant;
    }

    public void setRestaurantName(String nomeRistorante) {
        if (etichettaTitolo != null && nomeRistorante != null && !nomeRistorante.isBlank()) {
            etichettaTitolo.setText("Recensisci: " + nomeRistorante);
        }
    }

    @FXML
    private void initialize() {
        // Appena si apre la finestra, coloriamo le stelle in base al default (5)
        aggiornaGraficaStelle();
    }

    /* =========================
       GESTIONE STELLE
       ========================= */

    @FXML
    private void onStarClicked(ActionEvent event) {
        Button btn = (Button) event.getSource();
        // Leggiamo "1", "2"... dallo userData definito nell'FXML
        String val = (String) btn.getUserData();
        votoSelezionato = Integer.parseInt(val);

        aggiornaGraficaStelle();
    }

    private void aggiornaGraficaStelle() {
        Button[] stars = {star1, star2, star3, star4, star5};

        for (int i = 0; i < stars.length; i++) {
            // Se l'indice è inferiore al voto selezionato, la stella è Oro
            // Es. voto 3: indici 0, 1, 2 sono Oro.
            if (i < votoSelezionato) {
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 30px; -fx-cursor: hand; -fx-padding: 0; -fx-text-fill: yellow;"); // Oro
            } else {
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 30px; -fx-cursor: hand; -fx-padding: 0; -fx-text-fill: lightgray;"); // Grigio
            }
        }
    }

    /* =========================
       AZIONI SALVA / ANNULLA
       ========================= */

    @FXML
    private void onSalva() {
        if (areaRecensione.getText() == null || areaRecensione.getText().isBlank()) {
            etichettaErrore.setText("Il testo della recensione non può essere vuoto.");
            return;
        }

        String testo = areaRecensione.getText();

        if (ristoranteDestinazione != null) {
            System.out.println("[REVIEW NEW] Ristorante: " + ristoranteDestinazione.getNome()
                    + " | Voto: " + votoSelezionato
                    + " | Testo: " + testo);

            // TODO: Qui dovrai chiamare il metodo per salvare su CSV
            // es: GestioneFile.salvaRecensione(ristoranteDestinazione, votoSelezionato, testo);
        } else {
            System.err.println("Errore: nessun ristorante associato alla recensione.");
        }

        chiudiFinestra();
    }

    @FXML
    private void onAnnulla() {
        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage finestra = (Stage) areaRecensione.getScene().getWindow();
        finestra.close();
    }
}