package fr.univ.hotel.dao.jdbc;

import fr.univ.hotel.dao.ClientDAO;
import fr.univ.hotel.metier.Client;
import fr.univ.hotel.util.ConnexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOJdbc implements ClientDAO {

    public ClientDAOJdbc() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        String ddl = "CREATE TABLE IF NOT EXISTS clients (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nom VARCHAR(100) NOT NULL, " +
                "prenom VARCHAR(100) NOT NULL, " +
                "telephone VARCHAR(30) NOT NULL, " +
                "email VARCHAR(255) NOT NULL" +
                ")";

        try (Connection cnx = ConnexionBD.getConnexion();
             Statement st = cnx.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Impossible d'initialiser la table clients", e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT id, nom, prenom, telephone, email FROM clients";
        List<Client> clients = new ArrayList<>();

        try (Connection cnx = ConnexionBD.getConnexion();
             Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setNom(rs.getString("nom"));
                c.setPrenom(rs.getString("prenom"));
                c.setTelephone(rs.getString("telephone"));
                c.setEmail(rs.getString("email"));
                clients.add(c);
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de récupérer les clients", e);
        }
    }

    @Override
    public boolean insert(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, telephone, email) VALUES (?, ?, ?, ?)";

        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getTelephone());
            ps.setString(4, client.getEmail());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        client.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible d'ajouter le client", e);
        }
    }

    @Override
    public boolean update(Client client) {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, email = ? WHERE id = ?";

        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getTelephone());
            ps.setString(4, client.getEmail());
            ps.setInt(5, client.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de mettre à jour le client", e);
        }
    }

    @Override
    public boolean delete(Client client) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, client.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de supprimer le client", e);
        }
    }
}