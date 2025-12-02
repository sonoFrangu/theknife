package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import theknife.model.Restaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FavoritesController {

    // Lista visibile con i ristoranti preferiti dell'utente
    @FXML private ListView<Restaurant> listaPreferiti;

    // Etichetta mostrata quando non ci sono elementi nella lista
    @FXML private Label etichettaVuota;

    // Lista interna dei preferiti collegata alla ListView
    private final ObservableList<Restaurant> preferiti = FXCollections.observableArrayList();

    /**
     * Inizializzazione automatica chiamata da JavaFX.
     * Collega la lista interna alla ListView e aggiorna la grafica.
     */
    @FXML
    private void initialize() {
        // per ora la lista è vuota → solo grafica
        listaPreferiti.setItems(preferiti);
        aggiornaMessaggioVuoto();
    }

    /**
     * Aggiunge un ristorante ai preferiti.
     * Ignora i duplicati.
     */
    public void addFavorite(Restaurant ristorante) {
        if (ristorante == null) return;
        if (!preferiti.contains(ristorante)) {
            preferiti.add(ristorante);
            aggiornaMessaggioVuoto();
        }
    }

    /**
     * Mostra o nasconde il messaggio “Nessun preferito”
     * in base allo stato della lista.
     */
    private void aggiornaMessaggioVuoto() {
        boolean nessunElemento = preferiti.isEmpty();
        etichettaVuota.setVisible(nessunElemento);
        etichettaVuota.setManaged(nessunElemento);
    }
}