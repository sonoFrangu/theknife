package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import theknife.model.Restaurant;
import theknife.vecchioProgetto.GestioneFile;
import theknife.vecchioProgetto.Luogo;
import theknife.vecchioProgetto.Ristorante;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.stream.Collectors;

// NIENTE import it.unininsubria...Session QUI

public class MainController {

    @FXML private ListView<Restaurant> listaRistoranti;

    // Barra in alto (top bar) con i pulsanti di login/registrazione ecc.
    @FXML private Button bottoneLogin;
    @FXML private Button bottoneRegistrati;
    @FXML private Button bottoneLogout;
    @FXML private Label etichettaRuolo;
    @FXML private Button bottonePreferiti;
    @FXML private Button bottoneMieRecensioni;
    @FXML private Button bottoneMieiRistoranti; // se nel tuo FXML non c’è, puoi toglierlo

    // Pulsanti per le azioni principali
    @FXML private Button bottoneAggiungiRecensione;
    @FXML private Button bottoneAggiungiRistorante;

    // Campi per i filtri di ricerca
    @FXML private TextField campoRicerca;
    @FXML private ComboBox<String> filtroCucina;
    @FXML private CheckBox filtroConsegna;
    @FXML private CheckBox filtroPrenotazione;

    // Lista dei ristoranti usata dal codice (dati) collegata alla ListView
    private final ObservableList<Restaurant> ristoranti = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Carica i ristoranti dal file CSV
        caricaRistorantiDaCsv();

        // Imposta come la lista deve mostrare ogni ristorante (card grafica)
        inizializzaListaRistoranti();

        // Imposta i pulsanti in base al ruolo (parte come "ospite")
        aggiornaInterfaccia();

        // Inizializza il filtro per tipo di cucina
        if (filtroCucina != null) {
            filtroCucina.setItems(FXCollections.observableArrayList(
                    "Tutte", "Italian", "Seafood", "Creative", "Japanese", "Other"
            ));
            filtroCucina.getSelectionModel().selectFirst();
        }
    }

    /* =========================
       CARICAMENTO CSV
       ========================= */

    /**
     * Legge il file CSV dalle risorse del progetto e aggiunge
     * ogni riga come ristorante nella lista.
     */
    private void caricaRistorantiDaCsv() {
        try (InputStream is = getClass().getResourceAsStream("/michelin_my_maps.csv")) {
            if (is == null) {
                System.err.println("/michelin_my_maps.csv non trovato in resources");
                return;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String linea = br.readLine();

                // Salta la riga di intestazione se contiene "name"
                if (linea != null && linea.toLowerCase().contains("name")) {
                    linea = br.readLine();
                }

                // Legge tutte le righe del file
                while (linea != null) {
                    aggiungiDaRigaCsv(linea);
                    linea = br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Converte una singola riga CSV in un oggetto Restaurant
     * e lo aggiunge alla lista dei ristoranti.
     */
    private void aggiungiDaRigaCsv(String linea) {
        if (linea == null || linea.isBlank()) return;

        String[] parti = dividiCsv(linea);

        //todo: SBAGLIATO!!! non bisogna usare i setter ma il costruttore

        String nome = pulisci(parti[0]);

        String[] s = parti[1].split(",");
        String indirizzo = pulisci(s[0]);;

        s = parti[2].split(",");
        String citta = pulisci(s[0]);
        String nazione = pulisci(s[1]);


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

        // Link e info aggiuntive
        String link = pulisci(parti[8]);

        String website = pulisci(parti[9]);

        s = parti[10].split(" ");

        double award;
        String a = s[1].substring(0,4);

        if(parti[10]!=null && a.equals("Star"))
        {
            award = Double.parseDouble(pulisci(s[0]));
        }else{
            award = -1;
        }

        // Se nel CSV non c’è un link, generiamo un link a Google Maps
        if (link == null || link.isBlank()) {
            String maps = "https://www.google.com/maps?q="
                    + inUrl(nome) + "+" + inUrl(indirizzo) + "+" + inUrl(citta);
            link = maps;
        }

        boolean delivery = false;
        boolean booking = false;

        if(parti[14] == "true")
            delivery = true;

        if(parti[15] == "true")
            booking = true;

        Restaurant r = new Restaurant(nome, nazione, citta, indirizzo, latitudine, longitudine, prezzo, delivery, booking, tipoCucina, website, link, award);

        ristoranti.add(r);
    }

    /* =========================
       LIST VIEW / CARD
       ========================= */

    /**
     * Imposta come i ristoranti devono essere mostrati dentro la ListView:
     * ogni riga ha nome, indirizzo, sito e tipo di cucina.
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

            @Override
            protected void updateItem(Restaurant r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setGraphic(null);
                    return;
                }

                // Nome del ristorante
                nomeEtichetta.setText(valoreNonNullo(r.getNome()));

                // Indirizzo completo (indirizzo + città)
                indirizzoEtichetta.setText(
                        (valoreNonNullo(r.getIndirizzo()) + ", " + valoreNonNullo(r.getCitta()))
                                .replaceAll(", $", "")
                );

                // Sito web (se presente lo mostriamo, altrimenti nascondiamo il link)
                String sitoWeb = r.getWebsite();
                if (sitoWeb != null && !sitoWeb.isBlank()) {
                    sitoEtichetta.setText(sitoWeb);
                    sitoEtichetta.setVisible(true);
                    sitoEtichetta.setManaged(true);
                } else {
                    sitoEtichetta.setVisible(false);
                    sitoEtichetta.setManaged(false);
                }

                // Per ora usiamo la label "premi" per mostrare il tipo di cucina
                String cucina = r.getTipoCucina();
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
     */
    private void apriDettagliRistorante(Restaurant rd) {
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
                    rd.getNazione(),
                    rd.getCitta(),
                    rd.getIndirizzo(),
                    rd.getLatitudine(),
                    rd.getLongitudine(),
                    rd.getPrezzo(),           // qui ora passi la stringa tipo €€€
                    rd.isDelivery(),
                    rd.isBooking(),
                    rd.getTipoCucina(),
                    rd.getWebsite(),
                    rd.getLink()
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
     */
    private void aggiornaInterfaccia() {
        Session sessione = Session.getInstance();
        Session.Role ruolo = sessione.getRole();

        boolean isOspite = (ruolo == Session.Role.GUEST);
        boolean isCliente = (ruolo == Session.Role.CLIENTE);
        boolean isRisto   = (ruolo == Session.Role.RISTORATORE);

        // Pulsanti login/registrazione/logout
        if (bottoneLogin != null) {
            bottoneLogin.setVisible(isOspite);
            bottoneLogin.setManaged(isOspite);
        }
        if (bottoneRegistrati != null) {
            bottoneRegistrati.setVisible(isOspite);
            bottoneRegistrati.setManaged(isOspite);
        }
        if (bottoneLogout != null) {
            bottoneLogout.setVisible(!isOspite);
            bottoneLogout.setManaged(!isOspite);
        }

        // Etichetta con ruolo/username
        if (etichettaRuolo != null) {
            if (isOspite) etichettaRuolo.setText("Ospite");
            else if (isCliente) etichettaRuolo.setText("Cliente: " + valoreNonNullo(sessione.getUsername()));
            else if (isRisto) etichettaRuolo.setText("Ristoratore: " + valoreNonNullo(sessione.getUsername()));
        }

        // Pulsanti specifici del cliente
        if (bottonePreferiti != null) {
            bottonePreferiti.setVisible(isCliente);
            bottonePreferiti.setManaged(isCliente);
        }
        if (bottoneMieRecensioni != null) {
            bottoneMieRecensioni.setVisible(isCliente);
            bottoneMieRecensioni.setManaged(isCliente);
        }

        // Pulsante "i miei ristoranti" solo per ristoratori
        if (bottoneMieiRistoranti != null) {
            bottoneMieiRistoranti.setVisible(isRisto);
            bottoneMieiRistoranti.setManaged(isRisto);
        }

        // Azioni abilitate/disabilitate
        if (bottoneAggiungiRecensione != null) {
            bottoneAggiungiRecensione.setDisable(!isCliente);
        }
        if (bottoneAggiungiRistorante != null) {
            bottoneAggiungiRistorante.setDisable(!isRisto);
        }
    }

    // chiamato da login e da register
    public void onLoginSuccess() {
        aggiornaInterfaccia();
    }

    /* =========================
       HANDLER TOP BAR
       ========================= */

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

    @FXML
    private void onLogout() {
        Session.getInstance().logout();
        aggiornaInterfaccia();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Logout effettuato.");
        alert.showAndWait();
    }

    /* =========================
       HANDLER AZIONI
       ========================= */

    @FXML
    private void onAddRestaurant() {
        Session s = Session.getInstance();
        if (s.getRole() != Session.Role.RISTORATORE) {
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

            // se il controller ha setParent, glielo passiamo
            try {
                AddRestaurantController ctrl = loader.getController();
                ctrl.setControllerPrincipale(this);
            } catch (Exception ignored) {}

            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddReview() {
        Session s = Session.getInstance();
        if (s.getRole() != Session.Role.CLIENTE) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Permesso negato");
            a.setHeaderText(null);
            a.setContentText("Solo i clienti possono inserire recensioni.");
            a.showAndWait();
            return;
        }

        if (listaRistoranti == null) {
            System.err.println("listaRistoranti è null (controlla fx:id in main.fxml)");
            return;
        }

        Restaurant selezionato = listaRistoranti.getSelectionModel().getSelectedItem();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       ALTRE VIEW
       ========================= */

    @FXML
    private void onApplyFilters() {
        /*
            todo: DA IMPLEMENTARE IN UNA CLASSE
        *
            LinkedList<Ristorante> r = null;

            if(luogo!=null)
            {
            /*
                //DA CAMBIARE mettendo i ristoranti nelle vicinanze e prendere la lista pubblica
                r = GestioneFile.leggiFile().stream().filter(x -> x.getLuogo().equals(luogo)).collect(Collectors.toCollection(LinkedList::new));
            }
            else
            {
                if (filtroCucina.getValue() != null)//rimozione dei ristoranti con cucine diverse da quella selezionata
                {
                    r.removeIf(x -> !x.getCucina().contains(filtroCucina.getValue()));
                }

                if (prezzoMinore >= 0 && prezzoMaggiore >= 0)//rimozione dei ristoranti con prezzo medio non compreso tra min e max
                {
                    r.removeIf(x -> !(x.prezzo_Medio > prezzoMinore && x.prezzo_Medio < prezzoMaggiore));
                } else if (prezzoMinore >= 0)//rimozione dei ristoranti con prezzo medio minore del min
                {
                    r.removeIf(x -> x.prezzo_Medio < prezzoMinore);
                } else if (prezzoMaggiore >= 0) //rimozione dei ristoranti con prezzo medio maggiore del max
                {
                    r.removeIf(x -> x.prezzo_Medio > prezzoMaggiore);
                }

                if (delivery) //rimozione dei ristoranti che non hanno il servizio di delivery
                {
                    r.removeIf(x -> x.getDomicilio() == false);
                }

                if (prenotazioneOn) //rimozione dei ristoranti che non hanno il servizio di delivery
                {
                    r.removeIf(x -> x.getPrenotazione() == false);
                }

                if (medStelle >= 0) {
                    //r.removeIf(x -> x.getMediaStelle() < medStelle);
                }
            }
            return r;
        }
        */

        System.out.println("[FILTER] testo=" + (campoRicerca != null ? campoRicerca.getText() : "")
                + " cucina=" + (filtroCucina != null ? filtroCucina.getValue() : "")
                + " delivery=" + (filtroConsegna != null && filtroConsegna.isSelected())
                + " booking=" + (filtroPrenotazione != null && filtroPrenotazione.isSelected()));
    }

    @FXML
    private void onResetFilters() {
        if (campoRicerca != null) campoRicerca.clear();
        if (filtroCucina != null) filtroCucina.getSelectionModel().selectFirst();
        if (filtroConsegna != null) filtroConsegna.setSelected(false);
        if (filtroPrenotazione != null) filtroPrenotazione.setSelected(false);
    }

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

    @FXML
    private void onShowFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/favorites.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("I miei preferiti");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    /* =========================
       UTILS
       ========================= */

    /**
     * Rimuove eventuali doppi apici e spazi inutili.
     */
    private String pulisci(String s) {
        if (s == null) return "";
        return s.replace("\"", "").trim();
    }

    /**
     * Restituisce la stringa se non è null, altrimenti stringa vuota.
     * Utile per evitare NullPointerException nelle concatenazioni.
     */
    private String valoreNonNullo(String s) {
        return s == null ? "" : s;
    }

    /**
     * Converte uno spazio in '+' per poter usare la stringa in una URL.
     */
    private String inUrl(String s) {
        return s == null ? "" : s.trim().replace(" ", "+");
    }

    /**
     * Divide una riga CSV in campi, gestendo i campi tra doppi apici.
     */
    private String[] dividiCsv(String line) {
        // split che gestisce anche i campi tra doppi apici
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}