package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import theknife.model.GestioneRecensioni;
import theknife.model.GestioneRistoranti;
import theknife.model.Ristorante;

import java.util.LinkedList;

public class AdvancedFilterController {

    @FXML private TextField campoLuogo;
    @FXML private TextField campoCucina;
    @FXML private TextField campoPrezzoMin;
    @FXML private TextField campoPrezzoMax;

    @FXML private Button star1, star2, star3, star4, star5;

    @FXML private CheckBox checkDelivery;
    @FXML private CheckBox checkBooking;

    private int stelleSelezionate = 0;
    private MainController controllerPrincipale;

    public void setParent(MainController parent) {
        this.controllerPrincipale = parent;
    }

    /* --- GESTIONE STELLE --- */

    @FXML
    private void onStarClicked(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String val = (String) btn.getUserData();
        stelleSelezionate = Integer.parseInt(val);
        aggiornaGraficaStelle();
    }

    @FXML
    private void onResetStars() {
        stelleSelezionate = 0;
        aggiornaGraficaStelle();
    }

    private void aggiornaGraficaStelle() {
        Button[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < stelleSelezionate) {
                // Stella accesa (Oro)
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: gold; -fx-cursor: hand; -fx-padding: 0;");
            } else {
                // Stella spenta (Grigio chiaro)
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: lightgray; -fx-cursor: hand; -fx-padding: 0;");
            }
        }
    }


    @FXML
    private void onApply() {
        String luogo = campoLuogo.getText();
        String cucina = campoCucina.getText();
        String pMinStr = campoPrezzoMin.getText();
        String pMaxStr = campoPrezzoMax.getText();
        boolean delivery = checkDelivery.isSelected();
        boolean booking = checkBooking.isSelected();

        if (luogo == null || luogo.isBlank()) {
            mostraErrore("Campo obbligatorio", "Devi inserire una città per effettuare la ricerca.");
            campoLuogo.requestFocus(); // Rimette il cursore nel campo vuoto
            return;
        }

        Double prezzoMin = null;
        Double prezzoMax = null;

        try {
            if (pMinStr != null && !pMinStr.isBlank()) {
                // Sostituisce la virgola con il punto per evitare errori e converte
                prezzoMin = Double.parseDouble(pMinStr.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            mostraErrore("Prezzo Minimo non valido", "Inserisci un numero valido");
            return;
        }

        try {
            if (pMaxStr != null && !pMaxStr.isBlank()) {
                prezzoMax = Double.parseDouble(pMaxStr.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            mostraErrore("Prezzo Massimo non valido", "Inserisci un numero valido (es. 50)");
            return;
        }

        // Controllo logico per prezzo
        if (prezzoMin != null && prezzoMax != null && prezzoMin > prezzoMax) {
            mostraErrore("Intervallo non valido", "Il prezzo minimo non può essere maggiore del massimo.");
            return;
        }


        System.out.println("=== [DEBUG ADVANCED FILTER] ===");
        System.out.println("Luogo: " + (luogo != null ? luogo : ""));
        System.out.println("Cucina: " + (cucina != null ? cucina : ""));
        System.out.println("Prezzo Min: " + prezzoMin);
        System.out.println("Prezzo Max: " + prezzoMax);
        System.out.println("Stelle: " + stelleSelezionate);
        System.out.println("Delivery: " + delivery);
        System.out.println("Booking: " + booking);

        if (controllerPrincipale != null) {
            GestioneRistoranti gr = GestioneRistoranti.getInstance();
            LinkedList<Ristorante> rist = gr.Filtro(luogo, cucina, (prezzoMin != null ? prezzoMin : -1), (prezzoMax != null ? prezzoMax : -1), delivery, booking, stelleSelezionate);

           controllerPrincipale.mostraRistoranti(rist);
        }

        chiudiFinestra();
    }

    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore nell'inserimento");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    @FXML
    private void onCancel() {
        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage stage = (Stage) campoLuogo.getScene().getWindow();
        stage.close();
    }
}