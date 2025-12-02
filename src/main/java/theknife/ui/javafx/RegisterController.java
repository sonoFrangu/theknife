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
 * Gestisce la creazione di un nuovo utente (cliente o ristoratore)
 * e il salvataggio delle credenziali nel file users.csv.
 */
public class RegisterController {

    // Campi di input per i dati dell’utente
    @FXML private TextField campoNome;
    @FXML private TextField campoCognome;
    @FXML private TextField campoUsername;
    @FXML private PasswordField campoPassword;
    @FXML private TextField campoCitta;

    // Scelta del tipo di utente: cliente o ristoratore
    @FXML private RadioButton radioCliente;
    @FXML private RadioButton radioRistoratore;

    // Etichetta per mostrare eventuali errori (campi mancanti, problemi di salvataggio, ecc.)
    @FXML private Label etichettaErrore;

    // Riferimento al controller principale, per aggiornare la UI dopo la registrazione
    private MainController controllerPrincipale;

    // File CSV degli utenti, salvato nella stessa cartella del jar/progetto
    private static final String FILE_UTENTI = "users.csv";

    /**
     * Imposta il controller principale che ha aperto questa finestra.
     */
    public void setParentController(MainController parentController) {
        this.controllerPrincipale = parentController;
    }

    /**
     * Inizializzazione automatica chiamata da JavaFX.
     * Qui colleghiamo le radio allo stesso ToggleGroup
     * e selezioniamo "cliente" come opzione di default.
     */
    @FXML
    private void initialize() {
        ToggleGroup gruppoRuolo = new ToggleGroup();
        radioCliente.setToggleGroup(gruppoRuolo);
        radioRistoratore.setToggleGroup(gruppoRuolo);
        radioCliente.setSelected(true);
    }

    /**
     * Pulsante "Indietro": chiude semplicemente la finestra senza registrare nulla.
     */
    @FXML
    private void onBack(ActionEvent event) {
        chiudiFinestra();
    }

    /**
     * Pulsante "Crea account":
     *  - controlla che username e password siano compilati
     *  - calcola l’hash della password
     *  - aggiunge una riga al file users.csv
     *  - esegue login automatico
     *  - aggiorna la finestra principale
     */
    @FXML
    private void onCreate(ActionEvent event) {
        String nome         = campoNome.getText();
        String cognome      = campoCognome.getText();
        String username     = campoUsername.getText();
        String password     = campoPassword.getText();
        String citta        = campoCitta.getText();
        boolean isRistoratore = radioRistoratore.isSelected();

        // Controllo base: username e password sono obbligatori
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            etichettaErrore.setText("Username e password sono obbligatori.");
            return;
        }

        // Calcola l'hash SHA-256 della password
        String passwordHashed = calcolaSha256(password);

        // Scrive (o aggiunge) la riga nel file CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_UTENTI, true))) {
            // Formato: username;hash;nome;cognome;città;isRistoratore
            bw.write(username + ";" + passwordHashed + ";" +
                    valoreNonNullo(nome) + ";" +
                    valoreNonNullo(cognome) + ";" +
                    valoreNonNullo(citta) + ";" +
                    isRistoratore);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            etichettaErrore.setText("Errore nel salvataggio utente.");
            return;
        }

        // Dopo la registrazione, facciamo login automatico
        Session.Role ruolo = isRistoratore ? Session.Role.RISTORATORE : Session.Role.CLIENTE;
        Session.getInstance().login(username, ruolo);

        // Avvisiamo il controller principale che lo stato di login è cambiato
        if (controllerPrincipale != null) {
            controllerPrincipale.onLoginSuccess();
        }

        // Chiudiamo la finestra di registrazione
        chiudiFinestra();
    }

    /**
     * Chiude la finestra di registrazione.
     */
    private void chiudiFinestra() {
        Stage stage = (Stage) campoUsername.getScene().getWindow();
        stage.close();
    }

    /**
     * Calcola l'hash SHA-256 della stringa in ingresso
     * e lo restituisce in formato esadecimale.
     */
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
            // SHA-256 dovrebbe essere sempre disponibile nella JVM
            throw new RuntimeException(e);
        }
    }

    /**
     * Restituisce una stringa "ripulita" (trim),
     * oppure stringa vuota se il valore è null.
     */
    private String valoreNonNullo(String s) {
        return s == null ? "" : s.trim();
    }
}