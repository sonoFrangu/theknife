package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import theknife.model.GestioneFile;
import theknife.model.GestioneRistoranti;
import theknife.model.Ristorante;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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


    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE = "users.csv";

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
        //Prendo id ristorante
        int idRistorante = 0;
        String nomeR = etichettaNome.getText();
        String indirizzoR = etichettaIndirizzo.getText();

        GestioneRistoranti gr = GestioneRistoranti.getInstance();

        Optional<Ristorante> risto = gr.listaRistoranti.stream().filter(x -> x.getNome().equalsIgnoreCase(nomeR) && x.getLuogo().getIndirizzo().equalsIgnoreCase(indirizzoR)).findFirst();
        if (risto.isPresent())
        {
            idRistorante = risto.get().getId();
        } else {
            System.out.println(" === [Ristorante non trovato] ===");
        }

        //Salvo il ristorante nel file user.csv
        aggiungiRistoranteSuCSV(idRistorante);

        //Confermo L'aggiunta del ristorante
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Preferiti");
        a.setHeaderText(null);
        a.setContentText("Ristorante aggiunto ai preferiti.");
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
                      <body>
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

    private void aggiungiRistoranteSuCSV(int idRistorante) {
        String usernameU = Session.getInstance().getUsername();
        File fileUtenti = new File(NOME_CARTELLA, NOME_FILE);
        if (!fileUtenti.exists()) return;
        List<String> righe = new LinkedList<>();
        String primaparte="";
        String idRistorantiPres="";


        try (BufferedReader lettore = new BufferedReader(new FileReader(fileUtenti, StandardCharsets.UTF_8))) {
            String linea;

            while ((linea = lettore.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] parti = linea.split(";");

                // Formato CSV atteso: username;hash;nome;cognome;città;isCliente;isRistoratore
                if (parti.length >= 2) {
                    if (parti[0].equals(usernameU)) {

                        //Salvo la prima parte della riga
                        primaparte = parti[0]+";"+parti[1]+";"+parti[2]+";"+parti[3]+";"+parti[4]+";"+parti[5]+";"+parti[6]+";"+parti[7]+";";

                        // Nella colonna Ristoranti preferiti ci sarà il seguente formato: "1-4-5"
                        if (parti.length > 8) {
                            idRistorantiPres = parti[8].trim();
                            String[] s1 = parti[8].split("-");
                            //Controllo se il ristorante era già presente nei preferiti
                            for(String stringa: s1)
                                if(idRistorante == Integer.valueOf(stringa))
                                {
                                    System.out.println(" === [Ristorante già nei preferiti] ===");
                                    return;
                                }
                            //Salvo il nuovo ristorante
                            idRistorantiPres = idRistorantiPres.trim()+"-"+String.valueOf(idRistorante);
                            continue; // SALTA QUESTA RIGA (è quella vecchia)
                        }
                        else
                        {
                            idRistorantiPres = String.valueOf(idRistorante);
                            continue; // SALTA QUESTA RIGA (è quella vecchia)
                        }
                    }
                }
                righe.add(linea); // Tieni tutte le altre
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Aggiungi la NUOVA versione in fondo alla lista
        String nuovaRiga = primaparte + idRistorantiPres;
        righe.add(nuovaRiga);

        // Riscrivi il file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileUtenti, StandardCharsets.UTF_8))) {
            for (String r : righe) {
                bw.write(r);
                bw.newLine();
            }
        } catch (IOException e) {}
    }
}