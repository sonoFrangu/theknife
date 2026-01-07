package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import theknife.model.Recensione;
import theknife.model.Ristorante;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ViewReviewsController {

    @FXML private Label etichettaTitolo;
    @FXML private ListView<Recensione> listaRecensioni;

    private Ristorante ristoranteSelezionato;
    private ObservableList<Recensione> recensioniData = FXCollections.observableArrayList();

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_RECENSIONI = "recensioni.csv";

    @FXML
    private void initialize() {
        listaRecensioni.setItems(recensioniData);
    }


}