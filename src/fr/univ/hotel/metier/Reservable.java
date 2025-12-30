package fr.univ.hotel.metier;

public interface Reservable {
	boolean estDisponible();
    void reserver();
    void liberer();
}
