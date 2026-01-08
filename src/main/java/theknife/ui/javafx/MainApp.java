package theknife.ui.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.awt.Taskbar;
import javax.imageio.ImageIO;

/**
 * Classe principale JavaFX dell'applicazione.
 * Si occupa di:
 * - inizializzare la Session come ospite
 * - caricare la view principale (main.fxml)
 * - impostare titolo e icona della finestra
 * @author Matteo Franguelli
 * @version 2
 */
public class MainApp extends Application {

    /**
     * Avvia l'applicazione JavaFX inizializzando la sessione come ospite
     * e caricando l'interfaccia grafica principale.
     *
     * @param finestra finestra principale dell'applicazione
     * @throws Exception se il caricamento della scena fallisce
     * @author Matteo Franguelli
     */
    @Override
    public void start(Stage finestra) throws Exception {
        // Quando parte l'app: utente impostato come "ospite"
        Session.getInstance().login(null, Session.Role.GUEST);

        // Carichiamo il file FXML principale (interfaccia grafica)
        URL urlFxml = MainApp.class.getResource(
                "/it/unininsubria/theknifeui/ui/javafx/view/main.fxml");
        if (urlFxml == null) {
            throw new IllegalStateException("main.fxml non trovato nel classpath!");
        }

        FXMLLoader caricatore = new FXMLLoader(urlFxml);
        Scene scena = new Scene(caricatore.load(),1024, 768);

        finestra.setTitle("TheKnife");

        // Carichiamo l'icona dell'applicazione
        URL urlIcona = MainApp.class.getResource(
                "/it/unininsubria/theknifeui/ui/javafx/img/logo_theknife.png");
        if (urlIcona != null) {
            // Icona per la finestra JavaFX (funziona ovunque)
            Image iconaFx = new Image(urlIcona.toExternalForm());
            finestra.getIcons().add(iconaFx);

            // Icona per la taskbar/dock (se supportata dal sistema)
            try {
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar barraAttivita = Taskbar.getTaskbar();
                    if (barraAttivita.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        java.awt.Image iconaAwt = ImageIO.read(urlIcona);
                        barraAttivita.setIconImage(iconaAwt);
                    }
                }
            } catch (Exception ignored) {
                // Se qualcosa va storto qui, non blocchiamo l'app
            }
        }

        finestra.setScene(scena);
        finestra.show();
    }

    /**
     * Metodo main: avvia l'applicazione JavaFX usando MainApp come entry point.
     * @author Matteo Franguelli
     */
    public static void main(String[] args) {
        launch(args);
    }
}