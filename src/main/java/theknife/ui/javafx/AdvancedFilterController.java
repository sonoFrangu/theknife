package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdvancedFilterController {

    // Campi testuali per inserire i dettagli del filtro avanzato
    @FXML private TextField campoNome;
    @FXML private TextField campoIndirizzo;
    @FXML private TextField campoCitta;
    @FXML private TextField campoNazione;
    @FXML private TextField campoPrezzoMax;
    @FXML private TextField campoTipoCucina;
    @FXML private TextField campoServizi;

    // Filtri booleani
    @FXML private CheckBox checkConsegna;
    @FXML private CheckBox checkPrenotazione;
    @FXML private CheckBox checkStellaVerde; // Michelin Green Star

    // Etichetta per mostrare errori o avvisi all’utente
    @FXML private Label etichettaErrore;

    // Riferimento al MainController per applicare i filtri alla lista principale
    private MainController controllerPrincipale;

    /**
     * Imposta il controller principale della finestra principale.
     * Permetterà di passare i parametri del filtro.
     */
    public void setParent(MainController parent) {
        this.controllerPrincipale = parent;
    }

    /**
     * Chiamato quando l’utente clicca su “Applica”.
     * Per ora stampa solo i valori inseriti per verifica grafica.
     */
    @FXML
    private void onApply() {
        System.out.println("[ADV-FILTER] nome=" + campoNome.getText()
                + " | città=" + campoCitta.getText()
                + " | nazione=" + campoNazione.getText()
                + " | prezzoMax=" + campoPrezzoMax.getText()
                + " | cucina=" + campoTipoCucina.getText()
                + " | servizi=" + campoServizi.getText()
                + " | consegna=" + checkConsegna.isSelected()
                + " | prenotazione=" + checkPrenotazione.isSelected()
                + " | stellaVerde=" + checkStellaVerde.isSelected());

        // TODO: in futuro qui potrai passare una struttura dati al MainController

        chiudiFinestra();
    }

    /**
     * Chiamato quando l’utente clicca su “Annulla”.
     * Non applica nulla, chiude solo la finestra.
     */
    @FXML
    private void onCancel() {
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