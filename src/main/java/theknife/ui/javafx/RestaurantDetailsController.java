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

    @FXML
    private void onApriMaps() {
        if (googleMapsUrl != null) apriNelBrowser(googleMapsUrl);
    }

    private void aggiornaVisibilitaPreferiti() {
        Session s = Session.getInstance();
        // Visibile se ha permessi da CLIENTE (quindi anche se è Ristoratore+Cliente)
        boolean visibile = s.isCliente();
        if (bottonePreferiti != null) {
            bottonePreferiti.setVisible(visibile);
            bottonePreferiti.setManaged(visibile);
        }
    }

    @FXML
    private void onAggiungiAiPreferiti() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Preferiti");
        a.setHeaderText(null);
        a.setContentText("Ristorante aggiunto ai preferiti (Mock).");
        a.showAndWait();
    }

    @FXML
    private void onChiudi() {
        Stage st = (Stage) etichettaNome.getScene().getWindow();
        st.close();
    }

    // --- LOGICA WEBVIEW ---

    private void apriInWebView(String url) {
        if (vistaSito != null && url != null && !url.isBlank()) {
            // Aggiungi http:// se manca, altrimenti la WebView potrebbe non caricare
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            vistaSito.getEngine().load(url);
        }
    }

    private void mostraMessaggioNessunSito() {
        if (vistaSito != null) {
            String html = """
                    <html>
                      <body style="font-family: Arial; color:#666; padding: 20px; text-align:center;">
                        <h2>Nessun sito web disponibile</h2>
                        <p>Questo ristorante non ha un sito web specificato.</p>
                      </body>
                    </html>
                    """;
            vistaSito.getEngine().loadContent(html);
        }
    }

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

    private String valoreNonNullo(String s) {
        return s == null ? "" : s;
    }

    /**
     * Arrotonda la media all'intero più vicino e imposta le stelle grafiche.
     * Esempio: 3.7 diventa "★★★★☆ (3.7/5)"
     */
    private void mostraMediaStelle(double media) {
        if (media <= 0) {
            valoreStelle.setText("☆ Nessuna recensione");
            valoreStelle.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;"); // Grigio
            return;
        }
        long stellePiene = Math.round(media);

        StringBuilder sb = new StringBuilder();

        // Costruisce la stringa di stelle
        for (int i = 0; i < 5; i++) {
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