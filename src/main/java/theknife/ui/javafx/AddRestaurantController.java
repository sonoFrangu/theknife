package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AddRestaurantController {

    // Campi di testo per inserire i dati del ristorante
    @FXML private TextField campoNome;
    @FXML private TextField campoNazione;
    @FXML private TextField campoCitta;
    @FXML private TextField campoIndirizzo;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoLatitudine;
    @FXML private TextField campoLongitudine;
    @FXML private TextField campoPrezzo;
    @FXML private TextField campoTipoCucina;
    @FXML private CheckBox checkConsegna;
    @FXML private CheckBox checkPrenotazione;
    @FXML private TextField campoSitoWeb;

    // Etichetta per mostrare i messaggi di errore all’utente
    @FXML private Label etichettaErrore;

    // Riferimento al controller principale della finestra principale
    private MainController controllerPrincipale;

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE = "michelin_my_maps.csv";

    /**
     * Imposta il controller principale (la finestra da cui è stata aperta questa finestra).
     * Questo ti permette, in futuro, di passare il nuovo ristorante alla lista principale.
     */
    public void setControllerPrincipale(MainController controllerPrincipale) {
        this.controllerPrincipale = controllerPrincipale;
    }

    /**
     * Metodo chiamato quando l’utente preme il pulsante "Salva".
     * Qui facciamo un controllo minimo sui dati e poi chiudiamo la finestra.
     */
    @FXML
    private void onSalva() {
        String nome = campoNome.getText();
        String nazione = campoNazione.getText();
        String citta = campoCitta.getText();
        String indirizzo = campoIndirizzo.getText();
        String lat = campoLatitudine.getText();
        String longi = campoLongitudine.getText();
        double prezzo = Double.valueOf(campoPrezzo.getText());
        String tipo = campoTipoCucina.getText();
        boolean delivery = checkConsegna.isSelected();
        boolean booking = checkPrenotazione.isSelected();
        String sito = campoSitoWeb.getText();
        String numTel = campoTelefono.getText();
        String stelle="0";
        //todo aggiungere numero di telefono e stelle

        //Converto il prezzo
        String p = "";
        if(prezzo <= 20)
            p="€";
        else if(prezzo > 20 && prezzo <= 40)
            p="€€";
        else if(prezzo > 40 && prezzo <= 60)
            p="€€€";
        else if(prezzo > 60)
            p="€€€€";

        // Controllo base: il nome del ristorante è obbligatorio
        if (nome == null || nome.isBlank()) {
            etichettaErrore.setText("Il nome è obbligatorio.");
            return;
        }

        //todo: Controllo se il nome del ristorante è gia' presente?
        /*if (usernameEsiste(username)) {
            etichettaErrore.setText("Nome già in uso. Scegline un altro.");
            return;
        }*/

        // Verifica cartella
        File cartellaDoc = new File(NOME_CARTELLA);
        if (!cartellaDoc.exists()) {
            boolean creata = cartellaDoc.mkdirs();
            if (!creata) {
                etichettaErrore.setText("Impossibile creare la cartella " + NOME_CARTELLA);
                return;
            }
        }

        File fileUtenti = new File(cartellaDoc, NOME_FILE);

        // Salva su file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileUtenti, true))) {
            bw.write(nome + "," + "\"" + indirizzo + ", " + citta + "\"" + "," + "\"" +
                    citta + ", " + nazione + "\"" + "," +
                    p + "," + "\"" +
                    tipo + "\"" + "," +
                    longi + "," +
                    lat + "," +
                    numTel + "," +
                    sito + "," +
                    sito + "," +
                    stelle + " Stars" + "," +
                    null + "," + null + "," + null + "," +
                    delivery + "," + booking);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            etichettaErrore.setText("Errore nel salvataggio ristorante su file.");
            return;
        }

        //Avviso l'utente del successo
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Salvataggio effettuato");
        a.setHeaderText(null);
        a.setContentText("Ristorante salvato correttamente.");
        a.showAndWait();

        //todo: aggiornare lista ristoranti

        chiudiFinestra();
    }

    /**
     * Metodo chiamato quando l’utente preme il pulsante "Annulla".
     * Non salva niente, semplicemente chiude la finestra.
     */
    @FXML
    private void onAnnulla() {
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