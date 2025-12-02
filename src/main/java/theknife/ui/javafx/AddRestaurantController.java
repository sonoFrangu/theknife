package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddRestaurantController {

    // Campi di testo per inserire i dati del ristorante
    @FXML private TextField campoNome;
    @FXML private TextField campoNazione;
    @FXML private TextField campoCitta;
    @FXML private TextField campoIndirizzo;
    @FXML private TextField campoLatitudine;
    @FXML private TextField campoLongitudine;
    @FXML private TextField campoPrezzo;
    @FXML private TextField campoTipoCucina;
    @FXML private CheckBox checkConsegna;
    @FXML private CheckBox checkPrenotazione;
    @FXML private TextField campoSitoWeb;

    // Etichetta per mostrare i messaggi di errore all’utente
    @FXML private Label etichettaErrore;

    // Riferimento al controller principale della finestra principale
    private MainController controllerPrincipale;

    /**
     * Imposta il controller principale (la finestra da cui è stata aperta questa finestra).
     * Questo ti permette, in futuro, di passare il nuovo ristorante alla lista principale.
     */
    public void setControllerPrincipale(MainController controllerPrincipale) {
        this.controllerPrincipale = controllerPrincipale;
    }

    /**
     * Metodo chiamato quando l’utente preme il pulsante "Salva".
     * Qui facciamo un controllo minimo sui dati e poi chiudiamo la finestra.
     */
    @FXML
    private void onSalva() {
        // Controllo base: il nome del ristorante è obbligatorio
        if (campoNome.getText() == null || campoNome.getText().isBlank()) {
            etichettaErrore.setText("Il nome è obbligatorio.");
            return;
        }

        // Qui in futuro potrai:
        // - leggere tutti i campi (città, indirizzo, ecc.)
        // - creare un oggetto Ristorante
        // - passarlo al controller principale (controllerPrincipale)
        // Per ora la finestra si limita a chiudersi dopo il controllo.

        chiudiFinestra();
    }

    /**
     * Metodo chiamato quando l’utente preme il pulsante "Annulla".
     * Non salva niente, semplicemente chiude la finestra.
     */
    @FXML
    private void onAnnulla() {
        chiudiFinestra();
    }

    /**
     * Chiude la finestra corrente.
     */
    private void chiudiFinestra() {
        Stage finestra = (Stage) campoNome.getScene().getWindow();
        finestra.close();
    }
}