package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import theknife.model.Restaurant;

public class AddReviewController {

    // Etichetta del titolo in alto, dove mostriamo il nome del ristorante
    @FXML private Label etichettaTitolo;

    // Spinner per scegliere il voto (da 1 a 5)
    @FXML private Spinner<Integer> spinnerVoto;

    // Area di testo dove l’utente scrive la recensione
    @FXML private TextArea areaRecensione;

    // Etichetta per mostrare eventuali messaggi di errore
    @FXML private Label etichettaErrore;

    // Ristorante a cui è associata la recensione
    private Restaurant ristoranteDestinazione;

    /**
     * Imposta il ristorante a cui è riferita questa recensione.
     */
    public void setRestaurant(Restaurant restaurant) {
        this.ristoranteDestinazione = restaurant;
    }

    /**
     * Imposta il titolo della finestra con il nome del ristorante.
     * Es: "Aggiungi una recensione - Ristorante X"
     */
    public void setRestaurantName(String nomeRistorante) {
        if (etichettaTitolo != null && nomeRistorante != null && !nomeRistorante.isBlank()) {
            etichettaTitolo.setText("Aggiungi una recensione - " + nomeRistorante);
        }
    }

    /**
     * Inizializzazione automatica chiamata da JavaFX.
     * Qui configuriamo lo spinner del voto (1–5, predefinito 5).
     */
    @FXML
    private void initialize() {
        if (spinnerVoto != null && spinnerVoto.getValueFactory() == null) {
            spinnerVoto.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5)
            );
        }
    }

    /**
     * Chiamato quando l’utente preme il pulsante "Salva".
     * Controlla che ci sia del testo e stampa la recensione in console (per ora solo debug).
     */
    @FXML
    private void onSalva() {
        if (areaRecensione.getText() == null || areaRecensione.getText().isBlank()) {
            etichettaErrore.setText("Scrivi almeno una riga.");
            return;
        }

        int voto = spinnerVoto.getValue();
        String testo = areaRecensione.getText();

        if (ristoranteDestinazione != null) {
            System.out.println("[REVIEW DEBUG] recensione per: " + ristoranteDestinazione.getNome()
                    + " | voto: " + voto
                    + " | testo: " + testo);
        } else {
            System.out.println("[REVIEW DEBUG] nessun ristorante associato (!)");
        }

        chiudiFinestra();
    }

    /**
     * Chiamato quando l’utente preme il pulsante "Annulla".
     * Non salva nulla, chiude solo la finestra.
     */
    @FXML
    private void onAnnulla() {
        chiudiFinestra();
    }

    /**
     * Chiude la finestra corrente di inserimento recensione.
     */
    private void chiudiFinestra() {
        Stage finestra = (Stage) areaRecensione.getScene().getWindow();
        finestra.close();
    }
}