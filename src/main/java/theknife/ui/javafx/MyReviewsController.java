package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

// modello fittizio solo per la view
public class MyReviewsController {

    // Tabella che mostra le recensioni dell’utente
    @FXML private TableView<ReviewRow> tabellaRecensioni;

    // Colonna con il nome del ristorante
    @FXML private TableColumn<ReviewRow, String> colonnaRistorante;

    // Colonna con il voto
    @FXML private TableColumn<ReviewRow, Integer> colonnaVoto;

    // Colonna con il testo della recensione
    @FXML private TableColumn<ReviewRow, String> colonnaTesto;

    // Etichetta mostrata quando la tabella è vuota
    @FXML private Label etichettaVuota;

    // Dati fittizi (in futuro verranno riempiti dal MainController)
    private final ObservableList<ReviewRow> dati = FXCollections.observableArrayList();

    /**
     * Inizializzazione chiamata automaticamente da JavaFX.
     * Configura le colonne e collega la lista dei dati alla tabella.
     */
    @FXML
    private void initialize() {
        // Associa i getter alle colonne
        colonnaRistorante.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        colonnaVoto.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colonnaTesto.setCellValueFactory(new PropertyValueFactory<>("text"));

        // Collega la tabella alla lista dati
        tabellaRecensioni.setItems(dati);

        // Per ora non ci sono recensioni → solo grafica
        aggiornaMessaggioVuoto();
    }

    /**
     * Chiamato quando l’utente preme il pulsante "Modifica".
     * Per ora è solo una funzione di debug grafico.
     */
    @FXML
    private void onEdit() {
        System.out.println("[MY-REVIEWS] modifica recensione...");
    }

    /**
     * Chiamato quando l’utente preme il pulsante "Elimina".
     * Per ora è solo grafica.
     */
    @FXML
    private void onDelete() {
        System.out.println("[MY-REVIEWS] elimina recensione...");
    }

    /**
     * Mostra o nasconde il messaggio "Nessuna recensione"
     * in base al contenuto della tabella.
     */
    private void aggiornaMessaggioVuoto() {
        boolean vuota = dati.isEmpty();
        etichettaVuota.setVisible(vuota);
        etichettaVuota.setManaged(vuota);
    }

    /**
     * Classe interna usata come modello per la tabella.
     * Un oggetto rappresenta una riga della tabella.
     */
    public static class ReviewRow {
        private final String restaurant;
        private final int rating;
        private final String text;

        public ReviewRow(String restaurant, int rating, String text) {
            this.restaurant = restaurant;
            this.rating = rating;
            this.text = text;
        }

        public String getRestaurant() {
            return restaurant;
        }

        public int getRating() {
            return rating;
        }

        public String getText() {
            return text;
        }
    }
}