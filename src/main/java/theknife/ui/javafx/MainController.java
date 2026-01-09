package theknife.ui.javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import theknife.model.GestioneFile;
import theknife.model.GestioneRistoranti;
import theknife.model.Ristorante;
import theknife.model.Luogo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * * Controller principale dell'applicazione.
 *  * <p>
 *  * Coordina l'interazione tra vista e logica applicativa,
 *  * gestendo le azioni dell'utente e l'inizializzazione
 *  * dei componenti principali.
 *
 * @author Celestino Resteghini
 * @author Matteo Franguelli
 * @author Elia Toschi
 * @author Tommaso Ossuzio
 * @version 2
 */
public class MainController {

    @FXML private ListView<Ristorante> listaRistoranti;

    @FXML private Button bottoneLogin;
    @FXML private Button bottoneRegistrati;
    @FXML private Button bottoneLogout;
    @FXML private Label etichettaRuolo;
    @FXML private Button bottonePreferiti;
    @FXML private Button bottoneMieRecensioni;
    @FXML private Button bottoneMieiRistoranti;
    @FXML private Button bottoneRispondiRecensioni;

    @FXML private Button bottoneAggiungiRecensione;
    @FXML private Button bottoneAggiungiRistorante;

    @FXML private TextField campoLuogo;
    @FXML private TextField campoCucina;


    // Lista dei ristoranti usata dal codice (dati) collegata alla ListView
    private final ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList();
    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_DATI = "michelin_my_maps.csv";
    GestioneRistoranti gr = GestioneRistoranti.getInstance();

    /**
     * Esegue compiti di inizializzazione:
     * - Caricare i ristoranti dal file
     * - Imposta come la lista deve mostrare i ristoranti
     * - Imposta i pulsanti in base al ruolo (default: Ospite)
     * @author Matteo Franguelli
     */
    @FXML
    private void initialize() {
        Label placeholder = new Label("Nessun risultato trovato");
        listaRistoranti.setPlaceholder(placeholder);
        // Carica i ristoranti dal file CSV
        caricaRistorantiDaCsv();

        // Imposta come la lista deve mostrare ogni ristorante (card grafica)
        inizializzaListaRistoranti();

        // Imposta i pulsanti in base al ruolo (parte come "ospite")
        aggiornaInterfaccia();

    }

    /* =========================
       CARICAMENTO CSV
       ========================= */

    /**
     * Legge il file CSV dalle risorse del progetto e aggiunge
     * ogni riga come ristorante nella lista.
     * @author Matteo Franguelli
     * @author Celestino Resteghini
     */
    private void caricaRistorantiDaCsv() {
        // Avviamo il thread
        new Thread(() -> {
            // Creiamo una lista temporanea per non bloccare la grafica
            List<Ristorante> bufferTemporaneo = new LinkedList<>();
            InputStream is = null;

            try {
                File fileEsterno = new File(NOME_CARTELLA, NOME_FILE_DATI);

                if (fileEsterno.exists()) {
                    System.out.println("Caricamento dati da: " + fileEsterno.getAbsolutePath());
                    is = new FileInputStream(fileEsterno);
                }

                if (is == null) {
                    System.err.println("ERRORE: " + NOME_FILE_DATI + " non trovato.");
                    return;
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String linea = br.readLine();
                    if (linea != null && linea.toLowerCase().contains("name")) {
                        linea = br.readLine();
                    }

                    while (linea != null) {
                        // Passiamo la lista temporanea al metodo
                        aggiungiDaRigaCsv(linea, bufferTemporaneo);
                        linea = br.readLine();
                    }
                }
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Finito il caricamento, aggiorniamo la lista Observable
            Platform.runLater(() -> {
                ristoranti.addAll(bufferTemporaneo);
                gr.listaRistoranti.addAll(bufferTemporaneo);
            });

        }).start();
    }


    /**
     * Converte una singola riga CSV in un oggetto Restaurant
     * e lo aggiunge alla lista dei ristoranti.
     * @author Celestino Resteghini
     */
    private void aggiungiDaRigaCsv(String linea, List<Ristorante> destinazione) {
        if (linea == null || linea.isBlank()) return;

        String[] parti = dividiCsv(linea);

        String nome = pulisci(parti[0]);

        String[] s = parti[1].split(",");
        String indirizzo = pulisci(s[0]);;

        s = parti[2].split(",");

        String citta   = s.length > 0 ? pulisci(s[0]) : null;
        String nazione = s.length > 1 ? pulisci(s[1]) : null;


        double prezzo = pulisci(parti[3]).length() * 20; //ogni simbolo = 20€

        LinkedList<String> tipoCucina= new LinkedList<>();
        s = parti[4].split(",");

        // Aggiungi ogni elemento alla LinkedList
        for (String e : s) {
            tipoCucina.add(pulisci(e));
        }

        // Coordinate (attenzione agli errori di formato)
        double latitudine=0;
        double longitudine=0;

        try { longitudine = Double.parseDouble(pulisci(parti[5])); } catch (NumberFormatException ignored) {}

        try { latitudine = Double.parseDouble(pulisci(parti[6])); } catch (NumberFormatException ignored) {}

        String num_tel = pulisci(parti[7]);

        // Link e info aggiuntive
        String link = pulisci(parti[8]);

        String website = pulisci(parti[9]);

        s = parti[10].split(" ");

        double award = -1;
        if(s.length > 1) {
            String a = s[1].substring(0, 4);

            if (parti[10] != null && a.equals("Star")) {
                award = Double.parseDouble(pulisci(s[0]));
            } else {
                award = -1;
            }
        }
        // Se nel CSV non c’è un link, generiamo un link a Google Maps
        if (link == null || link.isBlank()) {
            String maps = "https://www.google.com/maps?q="
                    + inUrl(nome) + "+" + inUrl(indirizzo) + "+" + inUrl(citta);
            link = maps;
        }

        boolean delivery = false;
        boolean booking = false;

        if(parti.length > 14 && "true".equalsIgnoreCase(parti[14]))
            delivery = true;

        if(parti.length > 15 && "true".equalsIgnoreCase(parti[15]))
            booking = true;

        Ristorante r = new Ristorante(nome, num_tel, delivery, booking, prezzo, tipoCucina, new Luogo(nazione, indirizzo, citta, latitudine, longitudine), website, link, award);

        //gr.add(r);

        destinazione.add(r);
    }

    /* =========================
       LIST VIEW / CARD
       ========================= */

    /**
     * Imposta come i ristoranti devono essere mostrati dentro la ListView:
     * ogni riga ha nome, indirizzo, sito e tipo di cucina.
     * @author Celestino Resteghini
     */
    private void inizializzaListaRistoranti() {
        listaRistoranti.setItems(ristoranti);
        // Aggiunge una classe CSS per lo stile della lista
        listaRistoranti.getStyleClass().add("restaurant-list");

        listaRistoranti.setCellFactory(lv -> new ListCell<>() {
            private final Label nomeEtichetta = new Label();
            private final Label indirizzoEtichetta = new Label();
            private final Hyperlink sitoEtichetta = new Hyperlink();
            private final Label premiEtichetta = new Label();
            private final VBox box = new VBox(4);

            {
                nomeEtichetta.getStyleClass().add("restaurant-name");
                sitoEtichetta.getStyleClass().add("restaurant-link");
                premiEtichetta.getStyleClass().add("restaurant-awards");
                box.getStyleClass().add("restaurant-card");
                box.getChildren().addAll(nomeEtichetta, indirizzoEtichetta, sitoEtichetta, premiEtichetta);
            }

            /**
             * Aggiorna la visualizzazione dei ristoranti quando aperti in finestra.
             * @param r
             * @param empty
             * @author Matteo Franguelli
             */
            @Override
            protected void updateItem(Ristorante r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setGraphic(null);
                    return;
                }

                // Nome del ristorante
                nomeEtichetta.setText(valoreNonNullo(r.getNome()));

                // Indirizzo completo (indirizzo + città)
                indirizzoEtichetta.setText(
                        (valoreNonNullo(r.getLuogo().getIndirizzo()) + ", " + valoreNonNullo(r.getLuogo().getCitta()))
                                .replaceAll(", $", "")
                );

                // Sito web (se presente lo mostriamo, altrimenti nascondiamo il link)
                String sitoWeb = r.getWebsite();
                if (sitoWeb != null && !sitoWeb.isBlank() && !sitoWeb.equals("null")) {
                    sitoEtichetta.setText(sitoWeb);
                    sitoEtichetta.setVisible(true);
                    sitoEtichetta.setManaged(true);
                } else {
                    sitoEtichetta.setVisible(false);
                    sitoEtichetta.setManaged(false);
                }

                String cucina = String.join(", ", r.getCucina());
                if (cucina != null && !cucina.isBlank()) {
                    premiEtichetta.setText(cucina);
                    premiEtichetta.setVisible(true);
                    premiEtichetta.setManaged(true);
                } else {
                    premiEtichetta.setVisible(false);
                    premiEtichetta.setManaged(false);
                }

                setGraphic(box);

                // Doppio click su un ristorante = apri i dettagli in una nuova finestra
                setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) apriDettagliRistorante(r);
                });
            }
        });
    }

    /**
     * Apre una nuova finestra con i dettagli del ristorante selezionato.
     * @author Matteo Franguelli
     * @author Celestino Resteghini
     */
    private void apriDettagliRistorante(Ristorante rd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unininsubria/theknifeui/ui/javafx/view/restaurant_details.fxml"));
            Scene scene = new Scene(loader.load());

            // Carica lo stylesheet se esiste
            var cssUrl = getClass().getResource("/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(valoreNonNullo(rd.getNome()));

            RestaurantDetailsController ctrl = loader.getController();
            ctrl.setRestaurantData(
                    rd.getNome(),
                    rd.getLuogo().getNazione(),
                    rd.getLuogo().getCitta(),
                    rd.getLuogo().getIndirizzo(),
                    rd.getLuogo().getLatitudine(),
                    rd.getLuogo().getLongitudine(),
                    String.valueOf(rd.getPrezzo()),
                    rd.getN_tel(),
                    rd.isDelivery(),
                    rd.isBooking(),
                    String.join(", ", rd.getCucina()),
                    rd.getWebsite(),
                    rd.getAward()
            );
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       UI / RUOLI
       ========================= */

    /**
     * Aggiorna la visibilità dei pulsanti in base al ruolo dell’utente:
     * - ospite
     * - cliente
     * - ristoratore
     * @author Matteo Franguelli
     */
    private void aggiornaInterfaccia() {
        Session s = Session.getInstance();

        // Recuperiamo i permessi esatti
        boolean isGuest = s.isGuest();
        boolean puoRecensire = s.isCliente();       // Vero se nel CSV è Cliente
        boolean puoAggiungereRisto = s.isRistoratore();  // Vero se nel CSV è ristoratore
        boolean isLogged = !isGuest;

        // Login / Logout / Registrati
        if (bottoneLogin != null) { bottoneLogin.setVisible(isGuest); bottoneLogin.setManaged(isGuest); }
        if (bottoneRegistrati != null) { bottoneRegistrati.setVisible(isGuest); bottoneRegistrati.setManaged(isGuest); }
        if (bottoneLogout != null) { bottoneLogout.setVisible(isLogged); bottoneLogout.setManaged(isLogged); }

        // Etichetta Ruolo in alto
        if (etichettaRuolo != null) {
            if (isGuest) etichettaRuolo.setText("Ospite");
            else if (puoRecensire && puoAggiungereRisto) etichettaRuolo.setText("Cliente e Ristoratore: " + valoreNonNullo(s.getUsername()));
            else if (puoAggiungereRisto) etichettaRuolo.setText("Ristoratore: " + valoreNonNullo(s.getUsername()));
            else etichettaRuolo.setText("Cliente: " + valoreNonNullo(s.getUsername()));
        }

        // Pulsanti Personali (Preferiti / Mie Recensioni): visibili se PUOI recensire
        if (bottonePreferiti != null) {
            bottonePreferiti.setVisible(puoRecensire);
            bottonePreferiti.setManaged(puoRecensire);
        }
        if (bottoneMieRecensioni != null) {
            bottoneMieRecensioni.setVisible(puoRecensire);
            bottoneMieRecensioni.setManaged(puoRecensire);
        }

        // Pulsante "Miei Ristoranti": visibile se PUOI aggiungere ristoranti
        if (bottoneMieiRistoranti != null) {
            bottoneMieiRistoranti.setVisible(puoAggiungereRisto);
            bottoneMieiRistoranti.setManaged(puoAggiungereRisto);
        }

        // Il tasto per rispondere alle recensioni si vede solo se sei ristoratore
        if (bottoneRispondiRecensioni != null) {
            bottoneRispondiRecensioni.setVisible(puoAggiungereRisto);
            bottoneRispondiRecensioni.setManaged(puoAggiungereRisto);
        }

        // Abilitazione Azioni (Footer)
        if (bottoneAggiungiRecensione != null) {
            bottoneAggiungiRecensione.setDisable(!puoRecensire); // Disabilita se non sei cliente
        }
        if (bottoneAggiungiRistorante != null) {
            bottoneAggiungiRistorante.setDisable(!puoAggiungereRisto); // Disabilita se non sei ristoratore
        }
    }

    // chiamato da login e da register
    public void onLoginSuccess() {
        aggiornaInterfaccia();
        Session session = Session.getInstance();
        if (session.isAuthenticated()) {

            String cittaUtente = GestioneFile.recuperaCittaUtente(session.getUsername());
            if (cittaUtente != null && !cittaUtente.isBlank()) {
                session.setCitta(cittaUtente);

                if (campoLuogo != null) {
                    campoLuogo.setText(cittaUtente);
                    onApplyFilters();
                    System.out.println("Filtro applicato automaticamente per città: " + cittaUtente);
                }
            }
        }
    }

    /* =========================
       HANDLER TOP BAR
       ========================= */

    /**
     * Si occupa di mostrare la finestra per effettuare il login.
     */
    @FXML
    private void onShowLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unininsubria/theknifeui/ui/javafx/view/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Login");

            LoginController ctrl = loader.getController();
            ctrl.setParentController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Si occupa di mostrare la finestra di registrazione.
     * @author Matteo Franguelli
     */
    @FXML
    private void onShowRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unininsubria/theknifeui/ui/javafx/view/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Registrati");

            RegisterController ctrl = loader.getController();
            ctrl.setParentController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Si occupa di disconnettere l'utente nel caso di click
     * sul pulsante logout.
     * @author Matteo Franguelli
     */
    @FXML
    private void onLogout() {
        Session.getInstance().logout();
        aggiornaInterfaccia();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Logout effettuato.");
        alert.showAndWait();

        onResetFilters();

    }

    /* =========================
       HANDLER AZIONI
       ========================= */
    /**
     * Si occupa di aprire, se permesso, la finestra per aggiungere una recensione.
     * @author Matteo Franguelli
     */
    @FXML
    private void onAddRestaurant() {
        Session s = Session.getInstance();

        // Verifica se l'utente ha il permesso di aggiungere ristoranti
        if (!s.isRistoratore()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Permesso negato");
            a.setHeaderText(null);
            a.setContentText("Solo i ristoratori possono aggiungere ristoranti.");
            a.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/add_restaurant.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Nuovo ristorante");
            st.initModality(Modality.APPLICATION_MODAL);

            try {
                AddRestaurantController ctrl = loader.getController();
                ctrl.setControllerPrincipale(this);
            } catch (Exception ignored) {}

            st.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }
    /**
     * Si occupa di aprire, se permesso, la finestra per aggiungere una recensione.
     * @author Matteo Franguelli
     * @author Celestino Resteghini
     */
    @FXML
    private void onAddReview() {
        Session s = Session.getInstance();

        // Verifica se l'utente ha il permesso di recensire
        if (!s.isCliente()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Permesso negato");
            a.setHeaderText(null);

            if (s.isGuest())
                a.setContentText("Devi effettuare il login per recensire.");
            else
                a.setContentText("Il tuo account non ha i permessi da Cliente per lasciare recensioni.");

            a.showAndWait();
            return;
        }

        if (listaRistoranti == null) return;
        Ristorante selezionato = listaRistoranti.getSelectionModel().getSelectedItem();

        if (selezionato == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Nessun ristorante");
            a.setHeaderText(null);
            a.setContentText("Seleziona un ristorante prima di aggiungere una recensione.");
            a.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/add_review.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Nuova recensione");
            st.initModality(Modality.APPLICATION_MODAL);

            AddReviewController ctrl = loader.getController();
            ctrl.setRestaurant(selezionato);
            ctrl.setRestaurantName(selezionato.getNome());

            st.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /* =========================
       ALTRE VIEW
       ========================= */

    /**
     * Quando viene premuto il pulsante filtro si occupa dell'applicazione dei parametri
     * di ricerca.
     * @author Celestino Resteghini
     * @author Matteo Franguelli
     */
    @FXML
    protected void onApplyFilters() {
        String luogo = campoLuogo.getText();
        if (luogo == null || luogo.isBlank()) {
            mostraErrore("Campo obbligatorio", "Devi inserire una città per effettuare la ricerca.");
            campoLuogo.requestFocus(); // Rimette il cursore nel campo vuoto
            return;
        }

        LinkedList<Ristorante> rist = gr.Filtro(campoLuogo.getText(), campoCucina.getText(), -1,-1, false, false, -1);

        if(rist!=null)
        {
            ristoranti.clear();       // Svuota lista grafica attuale
            ristoranti.addAll(rist);  // aggiunge risultati filtro
        }
        else { ristoranti.clear(); }
    }

    /**
     * Quando viene premuto il pulsante Reset vengono resettati i filtri di ricerca.
     * @author Matteo Franguelli
     */
    @FXML
    private void onResetFilters() {
        if (campoLuogo != null) campoLuogo.clear();
        if (campoCucina != null) campoCucina.clear();

        mostraRistoranti(gr.listaRistoranti);

        System.out.println("[FILTER] Filtri resettati.");
    }

    /**
     * Quando viene premuto il pulsante "I miei ristoranti" mostra i propri ristoranti.
     * @author Matteo Franguelli
     */
    @FXML
    private void onShowMyRestaurants() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/my_restaurants.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("I miei ristoranti");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quando viene premuto il pulsante Preferiti mostra i ristoranti inseriti nei preferiti.
     * @author Matteo Franguelli
     */
    @FXML
    private void onShowFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/favorites.fxml"));
            Scene scene = new Scene(loader.load());
            Stage st = new Stage();
            st.setScene(scene);
            st.setTitle("I miei preferiti");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quando viene premuto apre la finestra Filtro Avanzato.
     * @author Matteo Franguelli
     */
    @FXML
    private void onShowAdvancedFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/advanced_filter.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Filtro avanzato");
            st.initModality(Modality.APPLICATION_MODAL);

            AdvancedFilterController ctrl = loader.getController();
            ctrl.setParent(this);

            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Quando viene premuto apre la finestra che mostra le proprie recensioni.
     * @author Matteo Franguelli
     */
    @FXML
    private void onShowMyReviews() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/my_reviews.fxml"));
            Scene scene = new Scene(loader.load());
            Stage st = new Stage();
            st.setScene(scene);
            st.setTitle("Le mie recensioni");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onReplyReviews() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/reply_review.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            Stage st = new Stage();
            st.setScene(scene);
            st.setTitle("Rispondi alle recensioni");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Permette la visualizzazione di recensioni dopo aver selezionato un ristorante.
     * @author Matteo Franguelli
     */
    @FXML
    private void onViewReviews() {
        if (listaRistoranti == null) return;
        Ristorante selezionato = listaRistoranti.getSelectionModel().getSelectedItem();

        if (selezionato == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Nessun ristorante");
            a.setHeaderText(null);
            a.setContentText("Seleziona un ristorante prima di vedere le recensioni.");
            a.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/view_reviews.fxml")); // Controlla che il path sia giusto
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Recensioni");
            st.initModality(Modality.APPLICATION_MODAL);

            ViewReviewsController ctrl = loader.getController();
            ctrl.setRestaurant(selezionato);

            st.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }



    /* =========================
       UTILS
       ========================= */

    /**
     * Metodo generico per mostrare errori.
     * @param titolo
     * @param messaggio
     * @author Matteo Franguelli
     */
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attenzione");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    /**
     * Rimuove eventuali doppi apici e spazi inutili.
     * @author Matteo Franguelli
     */
    private String pulisci(String s) {
        if (s == null) return "";
        return s.replace("\"", "").trim();
    }

    /**
     * Restituisce la stringa se non è null, altrimenti stringa vuota.
     * Utile per evitare NullPointerException nelle concatenazioni.
     * @author Matteo Franguelli
     */
    private String valoreNonNullo(String s) {
        return s == null ? "" : s;
    }

    /**
     * Converte uno spazio in '+' per poter usare la stringa in una URL.
     * @author Matteo Franguelli
     */
    private String inUrl(String s) {
        return s == null ? "" : s.trim().replace(" ", "+");
    }

    /**
     * Divide una riga CSV in campi, gestendo i campi tra doppi apici.
     * @author Matteo Franguelli
     */
    private String[] dividiCsv(String line) {
        // split che gestisce anche i campi tra doppi apici
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    /**
     * Aggiunge la lista di ristoranti alla grafica
     * @param nuovaLista
     * @author Matteo Franguelli
     */
    public void mostraRistoranti(List<Ristorante> nuovaLista) {
        ristoranti.clear();
        if (nuovaLista != null && !nuovaLista.isEmpty()) {
            ristoranti.addAll(nuovaLista);
        }
    }
}