
package fr.univ.hotel.dao;

import fr.univ.hotel.metier.Reservation;

import java.util.List;

public interface ReservationDAO {

    List<Reservation> findAll();

    boolean insert(Reservation reservation);

    boolean delete(Reservation reservation);
}