package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Controller della finestra di registrazione.
 * Gestisce la creazione di un nuovo utente e il salvataggio in doc/users.csv.
 */
public class RegisterController {

    @FXML private TextField campoNome;
    @FXML private TextField campoCognome;
    @FXML private TextField campoUsername;
    @FXML private PasswordField campoPassword;
    @FXML private TextField campoCitta;

    @FXML private CheckBox checkCliente;
    @FXML private CheckBox checkRistoratore;

    @FXML private Label etichettaErrore;

    private MainController controllerPrincipale;

    // Percorso del file: cartella "doc", file "users.csv"
    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE = "users.csv";

    public void setParentController(MainController parentController) {
        this.controllerPrincipale = parentController;
    }

    @FXML
    private void initialize() {
        // I CheckBox sono indipendenti, non serve ToggleGroup.
        // Di default lasciamo selezionato "Cliente".
        if (checkCliente != null) checkCliente.setSelected(true);
    }

    @FXML
    private void onBack(ActionEvent event) {
        chiudiFinestra();
    }

    @FXML
    private void onCreate(ActionEvent event) {
        String nome         = campoNome.getText();
        String cognome      = campoCognome.getText();
        String username     = campoUsername.getText();
        String password     = campoPassword.getText();
        String citta        = campoCitta.getText();

        boolean isCliente     = checkCliente.isSelected();
        boolean isRistoratore = checkRistoratore.isSelected();

        // Campi obbligatori
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            etichettaErrore.setText("Username e password sono obbligatori.");
            return;
        }

        // Almeno un ruolo selezionato
        if (!isCliente && !isRistoratore) {
            etichettaErrore.setText("Devi selezionare almeno un ruolo.");
            return;
        }

        // Calcolo Hash
        String passwordHashed = calcolaSha256(password);

        File cartellaDoc = new File(NOME_CARTELLA);
        File fileUtenti = new File(cartellaDoc, NOME_FILE);

        // Formato file: username;hash;nome;cognome;città;isCliente;isRistoratore
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileUtenti, true))) {
            bw.write(username + ";" + passwordHashed + ";" +
                    valoreNonNullo(nome) + ";" +
                    valoreNonNullo(cognome) + ";" +
                    valoreNonNullo(citta) + ";" +
                    isCliente + ";" +
                    isRistoratore);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            etichettaErrore.setText("Errore nel salvataggio utente su file.");
            return;
        }

        // AutoLogin
        Session.Role ruoloSessione;
        if (isRistoratore) {
            ruoloSessione = Session.Role.RISTORATORE;
        } else {
            ruoloSessione = Session.Role.CLIENTE;
        }

        Session.getInstance().login(username, ruoloSessione);

        // 7. Aggiornamento UI Principale
        if (controllerPrincipale != null) {
            controllerPrincipale.onLoginSuccess();
        }

        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage stage = (Stage) campoUsername.getScene().getWindow();
        stage.close();
    }

    private String calcolaSha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String valoreNonNullo(String s) {
        return s == null ? "" : s.trim();
    }
}