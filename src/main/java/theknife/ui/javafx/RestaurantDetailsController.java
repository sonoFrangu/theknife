package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
/**
 * Controller che gestisce la visualizzazione dei dettagli di un ristorante.
 *
 * @author Matteo Franguelli
 */
public class RestaurantDetailsController {


    @FXML private Label etichettaNome;
    @FXML private Label etichettaIndirizzo;
    @FXML private Label etichettaCitta;
    @FXML private Label valorePrezzo;
    @FXML private Label valoreCucina;
    @FXML private Label valoreConsegna;
    @FXML private Label valorePrenotazione;
    @FXML private Label valoreTelefono;
    @FXML private Label valoreStelle;

    @FXML private Hyperlink linkSitoWeb;

    @FXML private WebView vistaSito;

    @FXML private Button bottoneApriMaps;
    @FXML private Button bottonePreferiti;

    private double latitudine;
    private double longitudine;
    private String googleMapsUrl;

    /**
     * Imposta e visualizza tutte le informazioni del ristorante selezionato.
     * @param name
     * @param nation
     * @param city
     * @param address
     * @param latitude
     * @param longitude
     * @param price
     * @param phoneNumber
     * @param delivery
     * @param booking
     * @param cuisine
     * @param website
     * @param mediaStelle
     * @author Matteo Franguelli
     *
     */
    public void setRestaurantData(String name,
                                  String nation,
                                  String city,
                                  String address,
                                  double latitude,
                                  double longitude,
                                  String price,
                                  String phoneNumber,
                                  boolean delivery,
                                  boolean booking,
                                  String cuisine,
                                  String website,
                                  double mediaStelle) {

        this.latitudine = latitude;
        this.longitudine = longitude;

        etichettaNome.setText(valoreNonNullo(name));
        etichettaIndirizzo.setText(valoreNonNullo(address));

        // Format città
        if (city != null && !city.isBlank()) {
            if (nation != null && !nation.isBlank()) etichettaCitta.setText(city + ", " + nation);
            else etichettaCitta.setText(city);
        } else {
            etichettaCitta.setText(valoreNonNullo(nation));
        }

        valorePrezzo.setText((price != null && !price.isBlank()) ? price : "-");
        valoreTelefono.setText((phoneNumber != null && !phoneNumber.isBlank()) ? phoneNumber : "-");
        valoreCucina.setText(valoreNonNullo(cuisine));
        valoreConsegna.setText(delivery ? "Disponibile" : "No");
        valorePrenotazione.setText(booking ? "Disponibile" : "No");

        mostraMediaStelle(mediaStelle);

        // GESTIONE SITO WEB (WebView)
        if (website != null && !website.isBlank() && !website.equalsIgnoreCase("null")){
            linkSitoWeb.setText(website);
            linkSitoWeb.setDisable(false);
            linkSitoWeb.setOnAction(e -> apriInWebView(website));
            apriInWebView(website);
        } else {
            linkSitoWeb.setText("-");
            linkSitoWeb.setDisable(true);
            mostraMessaggioNessunSito();
        }

        // Bottono "Apri in Maps"
        preparaGoogleMapsUrl();

        // Gestione bottone Preferiti in base al ruolo
        aggiornaVisibilitaPreferiti();
    }
    /**
     * Prepara l'URL per l'apertura del ristorante su Google Maps.
     *
     * @author Matteo Franguelli
     */
    private void preparaGoogleMapsUrl() {
        String urlFinale = " ";

        if (latitudine != 0 && longitudine != 0) {
            // Coordinate precise
            urlFinale = "https://www.google.com/maps?q=" + latitudine + "," + longitudine;
        }

        this.googleMapsUrl = urlFinale;

        if (bottoneApriMaps != null) {
            boolean disponibile = (googleMapsUrl != null);
            bottoneApriMaps.setDisable(!disponibile);
            if (!disponibile) bottoneApriMaps.setText("Maps non disponibile");
            else bottoneApriMaps.setText("Apri in Maps");
        }
    }
    /**
     * Apre la posizione del ristorante in Google Maps.
     *
     * @author Matteo Franguelli
     */
    @FXML
    private void onApriMaps() {
        if (googleMapsUrl != null) apriNelBrowser(googleMapsUrl);
    }
    /**
     * Aggiorna la visibilità del bottone Preferiti in base al ruolo utente.
     *
     * @author Matteo Franguelli
     */
    private void aggiornaVisibilitaPreferiti() {
        Session s = Session.getInstance();
        // Visibile se ha permessi da CLIENTE (quindi anche se è Ristoratore+Cliente)
        boolean visibile = s.isCliente();
        if (bottonePreferiti != null) {
            bottonePreferiti.setVisible(visibile);
            bottonePreferiti.setManaged(visibile);
        }
    }
    /**
     * Gestisce l'aggiunta del ristorante ai preferiti.
     *
     * @author Matteo Franguelli
     */
    @FXML
    private void onAggiungiAiPreferiti() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Preferiti");
        a.setHeaderText(null);
        a.setContentText("Ristorante aggiunto ai preferiti (UI).");
        a.showAndWait();

        //TODO: Salvare nei preferiti, gia' creato preferiti.csv
    }
    /**
     * Chiude la finestra dei dettagli del ristorante.
     *
     * @author Matteo Franguelli
     */
    @FXML
    private void onChiudi() {
        Stage st = (Stage) etichettaNome.getScene().getWindow();
        st.close();
    }

    // --- LOGICA WEBVIEW ---
    /**
     * Carica il sito web del ristorante nella WebView.
     *
     * @author Matteo Franguelli
     */
    private void apriInWebView(String url) {
        if (vistaSito != null && url != null && !url.isBlank()) {
            // Aggiungi http:// se manca, altrimenti la WebView potrebbe non caricare
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            vistaSito.getEngine().load(url);
        }
    }
    /**
     * Mostra un messaggio informativo se il sito web non è disponibile.
     *
     * @author Matteo Franguelli
     */
    private void mostraMessaggioNessunSito() {
        if (vistaSito != null) {
            String html = """
                    <html>
                      <body>
                        <h2>Nessun sito web disponibile</h2>
                        <p>Questo ristorante non ha un sito web specificato.</p>
                      </body>
                    </html>
                    """;
            vistaSito.getEngine().loadContent(html);
        }
    }
    /**
     * Apre un URL nel browser di sistema.
     *
     * @author Matteo Franguelli
     */
    private void apriNelBrowser(String url) {
        if (url == null || url.isBlank()) return;
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Restituisce una stringa non nulla per l'output grafico.
     *
     * @author Matteo Franguelli
     */
    private String valoreNonNullo(String s) {
        return s == null ? "" : s;
    }
    /**
     * Mostra la media delle stelle Michelin in formato grafico.
     *
     * @author Matteo Franguelli
     */
    private void mostraMediaStelle(double media) {
        if (media <= 0) {
            valoreStelle.setText("Nessuna stella Michelin");
            valoreStelle.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;"); // Grigio
            return;
        }
        long stellePiene = Math.round(media);

        StringBuilder sb = new StringBuilder();

        // Costruisce la stringa di stelle
        for (int i = 0; i < 3; i++) {
            if (i < stellePiene) {
                sb.append("★");
            } else {
                sb.append("☆");
            }
        }

        valoreStelle.setText(sb.toString());

        // Imposta colore ORO e font più grande
        valoreStelle.setStyle("-fx-text-fill: gold; -fx-font-size: 18px; -fx-font-weight: bold;");
    }
}