package fr.univ.hotel.dao.jdbc;

import fr.univ.hotel.dao.ClientDAO;
import fr.univ.hotel.metier.Client;
import fr.univ.hotel.util.ConnexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOJdbc implements ClientDAO {

    private Client map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String telephone = rs.getString("telephone");
        String email = rs.getString("email");
        return new Client(id, nom, prenom, telephone, email);
    }

    @Override
    public Client find(int id) {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Client> findAll() {
        List<Client> liste = new ArrayList<>();
        String sql = "SELECT * FROM client ORDER BY numero";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    @Override
    public boolean insert(Client c) {
        String sql = "INSERT INTO client(nom, prenom, telephone, email) VALUES(?,?,?,?)";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getTelephone());
            ps.setString(4, c.getEmail());

            int nb = ps.executeUpdate();
            if (nb == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Client c) {
        String sql = "UPDATE client SET numero=?, type=?, prix_par_nuit=?, statut=? WHERE id=?";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getTelephone());
            ps.setString(4, c.getEmail());
            ps.setInt(5, c.getId());

            int nb = ps.executeUpdate();
            return nb == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Client c) {
        String sql = "DELETE FROM client WHERE id=?";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, c.getId());
            int nb = ps.executeUpdate();
            return nb == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

   
}
