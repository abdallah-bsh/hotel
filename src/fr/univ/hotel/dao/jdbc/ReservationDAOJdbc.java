
package fr.univ.hotel.dao.jdbc;

import fr.univ.hotel.dao.ChambreDAO;
import fr.univ.hotel.dao.ClientDAO;
import fr.univ.hotel.dao.ReservationDAO;
import fr.univ.hotel.metier.Chambre;
import fr.univ.hotel.metier.Client;
import fr.univ.hotel.metier.Reservation;
import fr.univ.hotel.util.ConnexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOJdbc implements ReservationDAO {

    private final ClientDAO clientDAO = new ClientDAOJdbc();
    private final ChambreDAO chambreDAO = new ChambreDAOJdbc();

    public ReservationDAOJdbc() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        String ddl = """
                CREATE TABLE IF NOT EXISTS reservations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    client_id INT NOT NULL,
                    chambre_id INT NOT NULL,
                    date_entree DATE NOT NULL,
                    date_sortie DATE NOT NULL,
                    prix_total DOUBLE NOT NULL,
                    statut VARCHAR(30) NOT NULL,
                    CONSTRAINT fk_reservations_client_id FOREIGN KEY (client_id) REFERENCES clients(id),
                    CONSTRAINT fk_reservations_chambre_id FOREIGN KEY (chambre_id) REFERENCES chambre(id),
                    INDEX idx_reservations_client (client_id),
                    INDEX idx_reservations_chambre (chambre_id)
                )
                """;

        try (Connection cnx = ConnexionBD.getConnexion();
             Statement st = cnx.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Impossible d'initialiser la table reservations", e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
                SELECT id, client_id, chambre_id, date_entree, date_sortie, prix_total, statut
                FROM reservations
                ORDER BY date_entree
                """;

        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reservations.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de récupérer les réservations", e);
        }
        return reservations;
    }

    @Override
    public boolean insert(Reservation reservation) {
        String sql = """
                INSERT INTO reservations
                (client_id, chambre_id, date_entree, date_sortie, prix_total, statut)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reservation.getClient().getId());
            ps.setInt(2, reservation.getChambre().getId());
            ps.setDate(3, Date.valueOf(reservation.getDateEntree()));
            ps.setDate(4, Date.valueOf(reservation.getDateSortie()));
            ps.setDouble(5, reservation.getPrixTotal());
            ps.setString(6, reservation.getStatut());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        reservation.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible d'ajouter la réservation", e);
        }
    }

    @Override
    public boolean delete(Reservation reservation) {
        String sql = "DELETE FROM reservations WHERE id = ?";

        try (Connection cnx = ConnexionBD.getConnexion();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, reservation.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de supprimer la réservation", e);
        }
    }

    private Reservation map(ResultSet rs) throws SQLException {
        int clientId = rs.getInt("client_id");
        int chambreId = rs.getInt("chambre_id");
        LocalDate entree = rs.getDate("date_entree").toLocalDate();
        LocalDate sortie = rs.getDate("date_sortie").toLocalDate();

        Client client = getClientById(clientId);
        Chambre chambre = chambreDAO.find(chambreId);

        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateEntree(entree);
        reservation.setDateSortie(sortie);
        reservation.setPrixTotal(rs.getDouble("prix_total"));
        reservation.setStatut(rs.getString("statut"));
        return reservation;
    }

    private Client getClientById(int id) {
        return clientDAO.findAll().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
}