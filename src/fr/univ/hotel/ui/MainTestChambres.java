/*package fr.univ.hotel.ui;

import fr.univ.hotel.dao.ChambreDAO;
import fr.univ.hotel.dao.jdbc.ChambreDAOJdbc;
import fr.univ.hotel.metier.Chambre;

import java.util.List;

public class MainTestChambres {

    public static void main(String[] args) {
        ChambreDAO dao = new ChambreDAOJdbc();

        System.out.println("=== Liste des chambres ===");
        List<Chambre> chambres = dao.findAll();
        for (Chambre c : chambres) {
            System.out.println(c);
        }

        System.out.println("=== Insertion d'une nouvelle chambre ===");
        Chambre nouvelle = new Chambre(0, 303, "DOUBLE", 80.0, "LIBRE");
        boolean ok = dao.insert(nouvelle);
        System.out.println("Insertion OK ? " + ok + ", id = " + nouvelle.getId());
    }
}*/
