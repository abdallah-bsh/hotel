package fr.univ.hotel.util;

import fr.univ.hotel.util.ConnexionBD;

import java.sql.Connection;
import java.sql.SQLException;

public class testConnectionJava {

    public static void main(String[] args) {
        try (Connection cnx = ConnexionBD.getConnexion()) {
            System.out.println("Connexion OK : " + cnx);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
