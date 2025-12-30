package fr.univ.hotel.dao;

import fr.univ.hotel.metier.Chambre;
import java.util.List;

public interface ChambreDAO extends DAO<Chambre> {
    List<Chambre> findLibres();
}
