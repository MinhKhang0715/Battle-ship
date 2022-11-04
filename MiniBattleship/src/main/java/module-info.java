module com.example.minibattleship {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;

    opens com.example.minibattleship to javafx.fxml;
    exports com.example.minibattleship;
}
