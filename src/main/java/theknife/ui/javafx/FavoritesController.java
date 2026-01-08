package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import theknife.model.Ristorante;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Classe che si occupa della gestione dei ristoranti preferiti.
 * @author Celestino Resteghini
 * @author Matteo Franguelli
 * version 2
 *
 */
public class FavoritesController {

    @FXML private ListView<Ristorante> listaPreferiti;

    @FXML private Label etichettaVuota;

    private final ObservableList<Ristorante> preferiti = FXCollections.observableArrayList();

    //TODO: stesso discorso di MyRestaurantController.java, prima devo avere un file da cui prendere i preferiti
    // se volete farlo e' uguale a MyReviewsController.java
    // fatemi sapere se bisogna fare anche quale "Elimina" e "Modifica"

    /**
     * Inizializza la lista vuota.
     * @author Matteo Franguelli
     */
    @FXML
    private void initialize() {
        // per ora la lista è vuota → solo grafica
        listaPreferiti.setItems(preferiti);
        aggiornaMessaggioVuoto();
    }

    /**
     * Si occupa di aggiungere il ristorante passato ai preferiti.
     * @param ristorante
     * @author Celestino Resteghini
     * @author Matteo Franguelli
     */
    public void addFavorite(Ristorante ristorante) {
        if (ristorante == null) return;
        if (!preferiti.contains(ristorante)) {
            preferiti.add(ristorante);
            aggiornaMessaggioVuoto();
        }
    }

    /**
     * Si occupa di mostrare l'etichetta che indica i ristoranti preferiti.
     * @author Matteo Franguelli
     */
    private void aggiornaMessaggioVuoto() {
        boolean nessunElemento = preferiti.isEmpty();
        etichettaVuota.setVisible(nessunElemento);
        etichettaVuota.setManaged(nessunElemento);
    }
}