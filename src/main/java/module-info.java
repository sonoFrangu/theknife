module it.unininsubria.theknifeui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.web;

    exports theknife;
    exports theknife.ui.javafx;
    opens theknife.ui.javafx to javafx.fxml;
    exports theknife.persistence;
    opens theknife.persistence to javafx.fxml;
}