module com.cybersek.gestocks {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires transitive org.apache.pdfbox;
    requires javafx.base;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires java.desktop;
    requires transitive org.apache.fontbox;
    requires org.apache.commons.io;
    requires protobuf.java;
    requires info.picocli;
    requires jbcrypt;
    opens com.cybersek.gestocks to javafx.fxml;
    exports com.cybersek.gestocks;
}
