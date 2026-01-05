package theknife.ui.javafx;

/**
 * Gestisce lo stato dell'utente attualmente loggato nell'applicazione.
 * È un singleton: esiste una sola Session per tutta l'app.
 * Mantiene sia il ruolo principale (per visualizzazione) che i permessi specifici.
 */
public class Session {

    /**
     * Ruoli possibili dell'utente (usati principalmente per etichette UI).
     * CLIENTE → utente standard
     * RISTORATORE → gestore di ristoranti
     * GUEST → utente non autenticato
     */
    public enum Role {
        CLIENTE,
        RISTORATORE,
        GUEST
    }

    // Istanza singleton della sessione
    private static Session instance;

    // Nome utente attualmente loggato (null se ospite)
    private String username;

    // Ruolo principale dell'utente loggato (per visualizzazione)
    private Role ruolo;

    // Permessi specifici (per gestire utenti che sono SIA clienti CHE ristoratori)
    private boolean permessiCliente;
    private boolean permessiRistoratore;

    /**
     * Costruttore privato: la sessione parte come "ospite" senza permessi.
     */
    private Session() {
        this.ruolo = Role.GUEST;
        this.permessiCliente = false;
        this.permessiRistoratore = false;
    }

    /**
     * Restituisce l'unica istanza della Session,
     * creandola se non esiste ancora.
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * Effettua il login impostando username e ruolo principale.
     * I permessi specifici vengono resettati in base al ruolo,
     * ma è consigliabile usare setPermessi() subito dopo per precisione.
     */
    public void login(String username, Role ruolo) {
        this.username = username;
        this.ruolo = (ruolo == null ? Role.GUEST : ruolo);

        // Imposta permessi di default in base al ruolo semplice
        // (Verranno sovrascritti se si usa setPermessi)
        if (this.ruolo == Role.GUEST) {
            this.permessiCliente = false;
            this.permessiRistoratore = false;
        } else if (this.ruolo == Role.CLIENTE) {
            this.permessiCliente = true;
            this.permessiRistoratore = false;
        } else if (this.ruolo == Role.RISTORATORE) {
            // Di base un ristoratore ha i suoi permessi,
            // ma se è anche cliente lo si deve impostare con setPermessi
            this.permessiRistoratore = true;
        }
    }

    /**
     * Imposta esplicitamente i permessi dell'utente.
     * Utile quando un utente ha il doppio ruolo (es. Ristoratore che vuole recensire).
     *
     * @param isCliente true se l'utente può lasciare recensioni e avere preferiti
     * @param isRistoratore true se l'utente può aggiungere ristoranti
     */
    public void setPermessi(boolean isCliente, boolean isRistoratore) {
        this.permessiCliente = isCliente;
        this.permessiRistoratore = isRistoratore;
    }

    /**
     * Restituisce true se l'utente ha i permessi da Cliente (es. recensioni).
     */
    public boolean isCliente() {
        return permessiCliente;
    }

    /**
     * Restituisce true se l'utente ha i permessi da Ristoratore (es. aggiunta ristoranti).
     */
    public boolean isRistoratore() {
        return permessiRistoratore;
    }

    /**
     * Effettua il logout e torna allo stato di ospite.
     */
    public void logout() {
        this.username = null;
        this.ruolo = Role.GUEST;
        this.permessiCliente = false;
        this.permessiRistoratore = false;
    }

    /**
     * Ritorna true se l’utente è un ospite (non loggato).
     */
    public boolean isGuest() {
        return ruolo == Role.GUEST;
    }

    /**
     * Ritorna true se l’utente NON è ospite.
     */
    public boolean isAuthenticated() {
        return ruolo != Role.GUEST;
    }

    /**
     * Imposta lo stato "autenticato / non autenticato".
     * Se impostato a false → logout.
     * Se impostato a true e il ruolo attuale è GUEST → diventa CLIENTE.
     */
    public void setAuthenticated(boolean autenticato) {
        if (!autenticato) {
            logout();
        } else {
            // se non aveva un ruolo definito, diventa un cliente normale
            if (this.ruolo == Role.GUEST) {
                this.ruolo = Role.CLIENTE;
                this.permessiCliente = true;
            }
        }
    }

    /**
     * Aggiorna lo username dell’utente loggato.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Aggiorna solo il ruolo principale dell’utente loggato.
     */
    public void setRole(Role ruolo) {
        this.ruolo = (ruolo == null ? Role.GUEST : ruolo);
    }

    /**
     * Restituisce lo username dell'utente attualmente loggato.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Restituisce il ruolo principale dell'utente.
     */
    public Role getRole() {
        return ruolo;
    }
}