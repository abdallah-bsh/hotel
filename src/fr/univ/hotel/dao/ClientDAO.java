package fr.univ.hotel.dao;

import fr.univ.hotel.metier.Client;

import java.util.List;

public interface ClientDAO {

    List<Client> findAll();

    boolean insert(Client client);

    boolean update(Client client);

    boolean delete(Client client);
}