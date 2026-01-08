package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import theknife.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class AddReviewController {

    private static final String NOME_CARTELLA = "doc";
    private static final String NOME_FILE_RECENSIONI = "recensioni.csv";
    private static final String NOME_FILE_USER = "users.csv";

    private boolean modalitaModifica = false;
    private MyReviewsController.ReviewRow recensioneOriginale;

    @FXML private Label etichettaTitolo;
    @FXML private TextArea areaRecensione;
    @FXML private Label etichettaErrore;

    // Riferimenti ai 5 bottoni stella
    @FXML private Button star1, star2, star3, star4, star5;

    private Ristorante ristoranteDestinazione;

    // Variabile per tenere traccia del voto (default 5 stelle)
    private int votoSelezionato = 5;

    public void setRestaurant(Ristorante restaurant) {
        this.ristoranteDestinazione = restaurant;
    }

    public void setRestaurantName(String nomeRistorante) {
        if (etichettaTitolo != null && nomeRistorante != null && !nomeRistorante.isBlank()) {
            etichettaTitolo.setText("Recensisci: " + nomeRistorante);
        }
    }

    @FXML
    private void initialize() {
        // Appena si apre la finestra, coloriamo le stelle in base al default (5)
        aggiornaGraficaStelle();
    }

    /* =========================
       GESTIONE STELLE
       ========================= */

    @FXML
    private void onStarClicked(ActionEvent event) {
        Button btn = (Button) event.getSource();
        // Leggiamo "1", "2"... dallo userData definito nell'FXML
        String val = (String) btn.getUserData();
        votoSelezionato = Integer.parseInt(val);

        aggiornaGraficaStelle();
    }

    private void aggiornaGraficaStelle() {
        Button[] stars = {star1, star2, star3, star4, star5};

        for (int i = 0; i < stars.length; i++) {
            // Se l'indice è inferiore al voto selezionato, la stella è Oro
            // Es. voto 3: indici 0, 1, 2 sono Oro.
            if (i < votoSelezionato) {
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 30px; -fx-cursor: hand; -fx-padding: 0; -fx-text-fill: gold;"); // Oro
            } else {
                stars[i].setStyle("-fx-background-color: transparent; -fx-font-size: 30px; -fx-cursor: hand; -fx-padding: 0; -fx-text-fill: lightgray;"); // Grigio
            }
        }
    }

    /* =========================
       AZIONI SALVA / ANNULLA
       ========================= */


    @FXML
    private void onAnnulla() {
        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage finestra = (Stage) areaRecensione.getScene().getWindow();
        finestra.close();
    }

    @FXML


    private void onCreate() {
        if (modalitaModifica) {
            rimuoviVecchiaEAgungiNuova();
            return;
        }
        GestioneRecensioni gestRest =  GestioneRecensioni.getInstance();
        GestioneRistoranti gr =  GestioneRistoranti.getInstance();
        String utente     = Session.getInstance().getUsername();
        String indirizzoRistorante = ristoranteDestinazione.getLuogo().getIndirizzo();
        int idUtente =1;
        int  stelle       = votoSelezionato;
        String text      = areaRecensione.getText();

        Ristorante r = gr.getRistoranteDaIndirizzo(indirizzoRistorante);

        int idRistorante= r.getId();

        //Prendere id utente
        File cartellaDoc = new File(NOME_CARTELLA);
        File fileUtenti = new File(cartellaDoc, NOME_FILE_USER);
        try(BufferedReader br = new BufferedReader(new FileReader(fileUtenti, StandardCharsets.UTF_8))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null ) {

                String[] parti = linea.split(";");

                if(parti[0].equals(utente))
                {
                    idUtente= Integer.valueOf(parti[7].trim());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (stelle == 0 || idUtente== 0 || areaRecensione.getText().isEmpty() || idRistorante== 0) {
            etichettaErrore.setText("Inserire tutti i campi");
            return;
        }

        File fileRecensioni = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);

        Recensione recensione= new Recensione(stelle,text,Integer.valueOf(idUtente),idRistorante);
        gestRest.add(recensione);

        // Salva su file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileRecensioni, true))) {
            bw.write(stelle + ";" + text + ";" +
                    LocalDateTime.now() + ";" +
                    idUtente + ";" +
                    idRistorante);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            etichettaErrore.setText("Errore nel salvataggio recensione su file.");
            return;
        }

        chiudiFinestra();
    }

    //METODO USATO PER MODIFICA RECENSIONE
    public void setDatiPerModifica(MyReviewsController.ReviewRow recensioneVecchia) {
        this.modalitaModifica = true;
        this.recensioneOriginale = recensioneVecchia;

        // Riempi i campi con i dati vecchi
        areaRecensione.setText(recensioneVecchia.getText());
        votoSelezionato = recensioneVecchia.getRating();
        aggiornaGraficaStelle();

        // Cambia titolo
        if (etichettaTitolo != null) etichettaTitolo.setText("Modifica recensione");
    }

    private void rimuoviVecchiaEAgungiNuova() {
        File file = new File(NOME_CARTELLA, NOME_FILE_RECENSIONI);
        List<String> righe = new LinkedList<>();

        // Recupera i dati base
        String user = Session.getInstance().getUsername();
        int mioId = GestioneFile.trovaIdUtenteDaUsername(user);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) { righe.add(line); continue; }

                // Se la riga corrisponde a quella vecchia, NON aggiungerla (la stiamo cancellando)
                String[] p = line.split(line.contains(";") ? ";" : ",");
                if (p.length >= 5) {
                    try {
                        int idR = Integer.parseInt(p[4].trim());
                        int idU = Integer.parseInt(p[3].trim());
                        String txt = p[1].trim().replace("\"", "");

                        if (idR == recensioneOriginale.getRawRestaurantId() && idU == mioId && txt.equals(recensioneOriginale.getText())) {
                            continue; // SALTA QUESTA RIGA (è quella vecchia)
                        }
                    } catch(Exception e){}
                }
                righe.add(line); // Tieni tutte le altre
            }
        } catch (IOException e) { e.printStackTrace(); }

        // Aggiungi la NUOVA versione in fondo alla lista
        String nuovaRiga = votoSelezionato + ";" + areaRecensione.getText().replace(";", "").replace("\n", " ") + ";" + LocalDateTime.now() + ";" + mioId + ";" + recensioneOriginale.getRawRestaurantId();
        righe.add(nuovaRiga);

        // Riscrivi il file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            for (String r : righe) {
                bw.write(r);
                bw.newLine();
            }
        } catch (IOException e) {}

        chiudiFinestra();
    }

}