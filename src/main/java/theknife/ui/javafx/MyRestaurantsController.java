package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import theknife.model.Ristorante;

/**
 * Si occupa della gestione dei ristoranti di cui l'utente è proprietario.
 * @author Matteo Franguelli
 * @author Celestino Resteghini
 * version 2
 */
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

    /**
     * Aggiorna il contenuto della tabella dei ristoranti nella vista.
     * @param lista
     * @author Celestino Resteghini
     * @author Matteo Franguelli
     * @deprecated
     */
    public void setRestaurants(java.util.List<Ristorante> lista) {
        if (lista == null || lista.isEmpty()) {
            tabellaRistoranti.getItems().clear();
        } else {
            tabellaRistoranti.getItems().setAll(lista);
        }
        aggiornaMessaggioVuoto();
    }

    /**
     * Aggiorna la visibilità del campo grafico "etichettaVuota" quando
     * a tabella dei ristoranti è vuota o contiene elementi.
     * <p>
     * Il messaggio viene mostrato solo se la tabella non
     * contiene alcun ristorante.
     * @author Matteo Franguelli
     */
    private void aggiornaMessaggioVuoto() {
        boolean vuota = tabellaRistoranti.getItems().isEmpty();
        etichettaVuota.setVisible(vuota);
        etichettaVuota.setManaged(vuota);
    }
}