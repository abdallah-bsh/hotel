package fr.univ.hotel.dao.jdbc;

import fr.univ.hotel.dao.ChambreDAO;
import fr.univ.hotel.metier.Chambre;
import fr.univ.hotel.util.ConnexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChambreDAOJdbc implements ChambreDAO {
	
	public ChambreDAOJdbc() {
	        ensureTableExists();
	    }

	    private void ensureTableExists() {
	        String ddl = """
	                CREATE TABLE IF NOT EXISTS chambre (
	                    id INT AUTO_INCREMENT PRIMARY KEY,
	                    numero INT NOT NULL,
	                    type VARCHAR(50) NOT NULL,
	                    prix_par_nuit DOUBLE NOT NULL,
	                    statut VARCHAR(50) NOT NULL
	                )
	                """;
	        try (Connection cnx = ConnexionBD.getConnexion();
	             Statement st = cnx.createStatement()) {
	            st.executeUpdate(ddl);
	        } catch (SQLException e) {
	            throw new RuntimeException("Impossible d'initialiser la table chambre", e);
	        }
	    }

    private Chambre map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int numero = rs.getInt("numero");
        String type = rs.getString("type");
        double prix = rs.getDouble("prix_par_nuit");
        String statut = rs.getString("statut");
        return new Chambre(id, numero, type, prix, statut);
    }

    @Override
    public Chambre find(int id) {
        String sql = "SELECT * FROM chambre WHERE id = ?";
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
    public List<Chambre> findAll() {
        List<Chambre> liste = new ArrayList<>();
        String sql = "SELECT * FROM chambre ORDER BY numero";
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
    public boolean insert(Chambre c) {
        String sql = "INSERT INTO chambre(numero, type, prix_par_nuit, statut) VALUES(?,?,?,?)";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.getNumero());
            ps.setString(2, c.getType());
            ps.setDouble(3, c.getPrixParNuit());
            ps.setString(4, c.getStatut());

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
    public boolean update(Chambre c) {
        String sql = "UPDATE chambre SET numero=?, type=?, prix_par_nuit=?, statut=? WHERE id=?";
        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, c.getNumero());
            ps.setString(2, c.getType());
            ps.setDouble(3, c.getPrixParNuit());
            ps.setString(4, c.getStatut());
            ps.setInt(5, c.getId());

            int nb = ps.executeUpdate();
            return nb == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Chambre c) {
        String sql = "DELETE FROM chambre WHERE id=?";
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

    @Override
    public List<Chambre> findLibres() {
        List<Chambre> liste = new ArrayList<>();
        String sql = "SELECT * FROM chambre WHERE statut = 'LIBRE' ORDER BY numero";
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
}
