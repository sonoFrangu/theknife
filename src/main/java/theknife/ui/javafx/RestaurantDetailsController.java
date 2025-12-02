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

    // Etichette principali con i dati del ristorante
    @FXML private Label etichettaNome;
    @FXML private Label etichettaIndirizzo;
    @FXML private Label etichettaCitta;

    // Valori di dettaglio (prezzo, cucina, servizi)
    @FXML private Label valorePrezzo;
    @FXML private Label valoreCucina;
    @FXML private Label valoreConsegna;
    @FXML private Label valorePrenotazione;

    // Link al sito
    @FXML private Hyperlink linkSitoWeb;

    // WebView dove visualizzare il sito del ristorante
    @FXML private WebView vistaMappa; // usata come "mini browser" per il sito

    // Pulsanti in alto a destra
    @FXML private Button bottoneApriMaps;
    @FXML private Button bottonePreferiti;

    // Dati interni per mappa e sito
    private double latitudine;
    private double longitudine;
    private String sitoWeb;
    private String urlMaps;        // dal CSV (Michelin)
    private String googleMapsUrl;  // usato dal bottone "Apri in Maps"

    public void setRestaurantData(String name,
                                  String nation,
                                  String city,
                                  String address,
                                  double latitude,
                                  double longitude,
                                  String price,
                                  boolean delivery,
                                  boolean booking,
                                  String cuisine,
                                  String website,
                                  String mapsUrl) {

        this.latitudine = latitude;
        this.longitudine = longitude;
        this.sitoWeb = website;
        this.urlMaps = mapsUrl; // lo teniamo, ma non lo usiamo direttamente per Maps

        etichettaNome.setText(valoreNonNullo(name));
        etichettaIndirizzo.setText(valoreNonNullo(address));

        // città + nazione
        if (city != null && !city.isBlank()) {
            if (nation != null && !nation.isBlank()) {
                etichettaCitta.setText(city + ", " + nation);
            } else {
                etichettaCitta.setText(city);
            }
        } else {
            etichettaCitta.setText(valoreNonNullo(nation));
        }

        // prezzo
        if (price != null && !price.isBlank()) {
            valorePrezzo.setText(price);
        } else {
            valorePrezzo.setText("-");
        }

        // tipo di cucina e servizi
        valoreCucina.setText(valoreNonNullo(cuisine));
        valoreConsegna.setText(delivery ? "Disponibile" : "No");
        valorePrenotazione.setText(booking ? "Disponibile" : "No");

        // sito web → caricato nella WebView
        if (website != null && !website.isBlank()) {
            linkSitoWeb.setText(website);
            linkSitoWeb.setDisable(false);
            linkSitoWeb.setOnAction(e -> apriInWebView(website));
            apriInWebView(website); // carica subito
        } else {
            linkSitoWeb.setText("-");
            linkSitoWeb.setDisable(true);
            mostraMessaggioNessunSito();
        }

        // prepara l'URL da aprire col bottone "Apri in Maps"
        preparaGoogleMapsUrl();

        aggiornaVisibilitaPreferiti();
    }

    /**
     * Prepara la URL per Google Maps (solo Google, niente Michelin).
     */
    private void preparaGoogleMapsUrl() {
        String urlFinale = null;

        if (latitudine != 0 && longitudine != 0) {
            // usa solo le coordinate
            urlFinale = "https://www.google.com/maps?q=" + latitudine + "," + longitudine;
        } else {
            // fallback: usa nome + indirizzo + città
            String query = (valoreNonNullo(etichettaNome.getText()) + " "
                    + valoreNonNullo(etichettaIndirizzo.getText()) + " "
                    + valoreNonNullo(etichettaCitta.getText())).trim();
            if (!query.isBlank()) {
                String encoded = query.replace(" ", "+");
                urlFinale = "https://www.google.com/maps/search/?api=1&query=" + encoded;
            }
        }

        this.googleMapsUrl = urlFinale;

        if (bottoneApriMaps != null) {
            boolean disponibile = (googleMapsUrl != null);
            bottoneApriMaps.setDisable(!disponibile);
            if (!disponibile) {
                bottoneApriMaps.setText("Maps non disponibile");
            } else {
                bottoneApriMaps.setText("Apri in Maps");
            }
        }
    }

    /**
     * Chiamato dal bottone in FXML.
     */
    @FXML
    private void onApriMaps() {
        if (googleMapsUrl != null) {
            apriNelBrowser(googleMapsUrl);
        }
    }

    private void aggiornaVisibilitaPreferiti() {
        Session s = Session.getInstance();
        boolean isCliente = s.getRole() == Session.Role.CLIENTE;
        if (bottonePreferiti != null) {
            bottonePreferiti.setVisible(isCliente);
            bottonePreferiti.setManaged(isCliente);
        }
    }

    @FXML
    private void onAggiungiAiPreferiti() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Preferiti");
        a.setHeaderText(null);
        a.setContentText("Ristorante aggiunto ai preferiti (solo UI).");
        a.showAndWait();
    }

    @FXML
    private void onChiudi() {
        Stage st = (Stage) etichettaNome.getScene().getWindow();
        st.close();
    }

    // --- sito web nella WebView ---

    private void apriInWebView(String url) {
        if (vistaMappa != null && url != null && !url.isBlank()) {
            vistaMappa.getEngine().load(url);
        }
    }

    private void mostraMessaggioNessunSito() {
        if (vistaMappa != null) {
            String html = """
                    <html>
                      <body style="font-family: Arial; color:#444; padding: 12px;">
                        <p>Nessun sito web disponibile per questo ristorante.</p>
                      </body>
                    </html>
                    """;
            vistaMappa.getEngine().loadContent(html);
        }
    }


    private void apriNelBrowser(String url) {
        if (url == null || url.isBlank()) return;

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Apertura link");
                a.setHeaderText(null);
                a.setContentText("Impossibile aprire il browser automaticamente.\nURL: " + url);
                a.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Errore apertura link");
            a.setHeaderText(null);
            a.setContentText("Non sono riuscito ad aprire il link:\n" + url);
            a.showAndWait();
        }
    }

    private String valoreNonNullo(String s) {
        return s == null ? "" : s;
    }
}