package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import theknife.model.Restaurant;

public class MyRestaurantsController {

    // Tabella che mostrerà la lista dei ristoranti gestiti dall’utente ristoratore
    @FXML private TableView<Restaurant> tabellaRistoranti;

    // Etichetta da mostrare quando la tabella è vuota
    @FXML private Label etichettaVuota;

    /**
     * Metodo chiamato automaticamente da JavaFX dopo il caricamento della view.
     * Al momento la tabella non contiene dati: verranno aggiunti in futuro
     * quando avrai la lista dei ristoranti dell’utente.
     */
    @FXML
    private void initialize() {
        aggiornaMessaggioVuoto();
    }

    /**
     * In futuro potrai usare questo metodo per passare i ristoranti dell’utente
     * e popolare la tabella.
     */
    public void setRestaurants(java.util.List<Restaurant> lista) {
        if (lista == null || lista.isEmpty()) {
            tabellaRistoranti.getItems().clear();
        } else {
            tabellaRistoranti.getItems().setAll(lista);
        }
        aggiornaMessaggioVuoto();
    }

    /**
     * Mostra o nasconde il messaggio "Nessun ristorante"
     * quando la tabella è vuota.
     */
    private void aggiornaMessaggioVuoto() {
        boolean vuota = tabellaRistoranti.getItems().isEmpty();
        etichettaVuota.setVisible(vuota);
        etichettaVuota.setManaged(vuota);
    }
}