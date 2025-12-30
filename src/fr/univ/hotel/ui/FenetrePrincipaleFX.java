package fr.univ.hotel.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class FenetrePrincipaleFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestion de l'hôtel");

        // FENETRE PRINCIPALE
        TabPane tabPane = new TabPane();
        
          FenetreChambresFX fenetreChambresFX = new FenetreChambresFX();
          BorderPane chambresContent = fenetreChambresFX.creerContenu();
        
          FenetreClientsFX clientsView = new FenetreClientsFX();
          FenetreReservationsFX reservationsView = new FenetreReservationsFX();

        
         // ONGLET CHAMBRES␊
         Tab tabChambres = new Tab("Chambres", chambresContent);
         tabChambres.setClosable(false);

        // ONGLET CLIENTS
        Tab tabClients = new Tab("Clients", clientsView);
        tabClients.setClosable(false);

     // ONGLET RESERVATIONS
        Tab tabReservations = new Tab("Réservations", reservationsView);
        tabReservations.setClosable(false);

        tabPane.getTabs().addAll(tabChambres, tabClients, tabReservations);

        Scene scene = new Scene(tabPane, 900, 600);
        Styles.appliquerCssGlobal(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // POINT D'ENTREE
    public static void main(String[] args) {
        launch(args);
    }
}