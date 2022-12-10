module com.example.minibattleship {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires org.jsoup;
    requires org.json;

    opens com.example.minibattleship to javafx.fxml;
//    exports com.example.minibattleship;
    exports com.example.minibattleship.Server;
    opens com.example.minibattleship.Server to javafx.fxml;
    exports com.example.minibattleship.Client;
    opens com.example.minibattleship.Client to javafx.fxml;
    opens com.example.minibattleship.Client.Controllers to javafx.fxml;
    exports com.example.minibattleship.Helper;
}
