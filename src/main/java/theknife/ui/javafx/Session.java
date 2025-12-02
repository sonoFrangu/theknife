package theknife.ui.javafx;

/**
 * Gestisce lo stato dell'utente attualmente loggato nell'applicazione.
 * È un singleton: esiste una sola Session per tutta l'app.
 */
public class Session {

    /**
     * Ruoli possibili dell'utente.
     * CLIENTE → può recensire e vedere i preferiti
     * RISTORATORE → può aggiungere ristoranti
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

    // Ruolo dell'utente loggato
    private Role ruolo;

    /**
     * Costruttore privato: la sessione parte come "ospite".
     */
    private Session() {
        this.ruolo = Role.GUEST;
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
     * Effettua il login impostando username e ruolo.
     * Se il ruolo è null → l’utente diventa GUEST.
     */
    public void login(String username, Role ruolo) {
        this.username = username;
        this.ruolo = (ruolo == null ? Role.GUEST : ruolo);
    }

    /**
     * Effettua il logout e torna allo stato di ospite.
     */
    public void logout() {
        this.username = null;
        this.ruolo = Role.GUEST;
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
     * Aggiorna solo il ruolo dell’utente loggato
     * senza cambiare lo username.
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
     * Restituisce il ruolo dell'utente.
     */
    public Role getRole() {
        return ruolo;
    }
}