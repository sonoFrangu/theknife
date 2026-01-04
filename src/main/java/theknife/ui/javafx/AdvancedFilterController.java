package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
        // Legge il valore "1", "2" ecc dallo userData nell'FXML
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
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: yellow; -fx-cursor: hand; -fx-padding: 0;");
            } else {
                // Stella spenta (Grigio chiaro)
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: lightgray; -fx-cursor: hand; -fx-padding: 0;");
            }
        }
    }

    /* --- AZIONI --- */

    @FXML
    private void onApply() {
        // 1. Raccoglie i dati
        String luogo = campoLuogo.getText();
        String cucina = campoCucina.getText();
        String pMin = campoPrezzoMin.getText();
        String pMax = campoPrezzoMax.getText();
        boolean delivery = checkDelivery.isSelected();
        boolean booking = checkBooking.isSelected();

      // TODO: CELE METTI IL TUO FILTRO

        chiudiFinestra();
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