package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Controller della finestra di login.
 * Gestisce l'accesso dell'utente leggendo le credenziali dal file doc/users.csv.
 */
public class LoginController {

    // Campo di testo per lo username
    @FXML private TextField campoUsername;

    // Campo di testo per la password (nascosta)
    @FXML private PasswordField campoPassword;

    // Etichetta per mostrare eventuali messaggi di errore
    @FXML private Label etichettaErrore;

    // Riferimento al controller principale (finestra principale)
    private MainController controllerPrincipale;

    // Percorso file CSV (corrisponde a quello usato in RegisterController)
    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE = "users.csv";

    /**
     * Imposta il controller principale che ha aperto la finestra di login.
     */
    public void setParentController(MainController parentController) {
        this.controllerPrincipale = parentController;
    }

    /**
     * Handler del pulsante "Login".
     * Controlla che i campi non siano vuoti, verifica le credenziali tramite verificaCredenzialiERuolo
     * e in caso di successo aggiorna la Session.
     */
    @FXML
    private void onLogin(ActionEvent event) {
        String nomeUtente = campoUsername.getText();
        String password   = campoPassword.getText();

        // Controllo base: username e password non possono essere vuoti
        if (nomeUtente == null || nomeUtente.isBlank()
                || password == null || password.isBlank()) {
            etichettaErrore.setText("Compila username e password.");
            return;
        }

        // Verifica nel file utenti e ottiene il ruolo associato
        Session.Role ruolo = verificaCredenzialiERuolo(nomeUtente, password);

        if (ruolo != null) {
            // Login riuscito: aggiorniamo la sessione
            Session.getInstance().login(nomeUtente, ruolo);

            // Avvisiamo la finestra principale che il login è andato a buon fine
            if (controllerPrincipale != null) {
                controllerPrincipale.onLoginSuccess();
            }
            chiudiFinestra();
        } else {
            // Credenziali errate
            etichettaErrore.setText("Credenziali non valide.");
        }
    }

    /**
     * Handler del pulsante "Entra come ospite".
     * Non richiede credenziali e imposta il ruolo GUEST.
     */
    @FXML
    private void onGuest(ActionEvent event) {
        Session.getInstance().login(null, Session.Role.GUEST);
        if (controllerPrincipale != null) {
            controllerPrincipale.onLoginSuccess();
        }
        chiudiFinestra();
    }

    /**
     * Verifica le credenziali nel file doc/users.csv e restituisce il ruolo
     * TODO: Completare commenti per Javadoc
     */
    private Session.Role verificaCredenzialiERuolo(String nomeUtente, String password) {
        File fileUtenti = new File(NOME_CARTELLA, NOME_FILE);

        // Se il file non esiste, nessun utente è registrato
        if (!fileUtenti.exists()) {
            System.err.println("File utenti non trovato in: " + fileUtenti.getAbsolutePath());
            return null;
        }

        try (BufferedReader lettore = new BufferedReader(new FileReader(fileUtenti, StandardCharsets.UTF_8))) {

            // Calcola l'hash della password inserita per il confronto
            String hashPassword = calcolaSha256(password);
            String linea;

            while ((linea = lettore.readLine()) != null) {
                if (linea.isBlank()) continue;

                String[] parti = linea.split(";");

                // Controlla user e password (primi due campi)
                if (parti.length >= 2) {
                    String utenteDaFile = parti[0];
                    String hashDaFile   = parti[1];

                    if (utenteDaFile.equals(nomeUtente) && hashDaFile.equals(hashPassword)) {

                        boolean isRistoratore = false;

                        // Supporta sia il formato vecchio (6 colonne) che quello nuovo (7 colonne)
                        if (parti.length >= 7) {
                            // Formato nuovo: ...;isCliente;isRistoratore
                            isRistoratore = Boolean.parseBoolean(parti[6]);
                        } else if (parti.length >= 6) {
                            // Formato vecchio: ...;isRistoratore
                            isRistoratore = Boolean.parseBoolean(parti[5]);
                        }

                        // Se ha i permessi da ristoratore, restituiamo quel ruolo, altrimenti cliente
                        return isRistoratore ? Session.Role.RISTORATORE : Session.Role.CLIENTE;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calcola l'hash SHA-256 del testo passato e lo restituisce in formato esadecimale.
     * Viene usato per confrontare le password in modo sicuro.
     */
    private String calcolaSha256(String testo) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(testo.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : digest) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 dovrebbe sempre essere disponibile nella JVM
            throw new RuntimeException(e);
        }
    }

    /**
     * Chiude la finestra di login.
     */
    private void chiudiFinestra() {
        Stage stage = (Stage) campoUsername.getScene().getWindow();
        stage.close();
    }
}