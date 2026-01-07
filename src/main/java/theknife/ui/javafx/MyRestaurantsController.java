package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import theknife.model.Ristorante;

public class MyRestaurantsController {


    //TODO: Finche' non abbiamo modo di tener traccia dei ristoranti inseriti e da chi non posso farla
    // per ora la tabella la lascio vuota
    @FXML private TableView<Ristorante> tabellaRistoranti;

    // Etichetta da mostrare quando la tabella è vuota
    @FXML private Label etichettaVuota;

    @FXML
    private void initialize() {
        aggiornaMessaggioVuoto();
    }


    public void setRestaurants(java.util.List<Ristorante> lista) {
        if (lista == null || lista.isEmpty()) {
            tabellaRistoranti.getItems().clear();
        } else {
            tabellaRistoranti.getItems().setAll(lista);
        }
        aggiornaMessaggioVuoto();
    }

    private void aggiornaMessaggioVuoto() {
        boolean vuota = tabellaRistoranti.getItems().isEmpty();
        etichettaVuota.setVisible(vuota);
        etichettaVuota.setManaged(vuota);
    }
}