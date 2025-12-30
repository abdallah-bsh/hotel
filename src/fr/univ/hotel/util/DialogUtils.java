package fr.univ.hotel.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Utilitaires pour afficher des boîtes de dialogue de confirmation ou d'erreur.
 */
public final class DialogUtils {

    private DialogUtils() {
        // Classe utilitaire
    }

    /**
     * Affiche une boîte de confirmation et retourne vrai si l'utilisateur confirme.
     *
     * @param title   titre de la fenêtre
     * @param message contenu du message
     * @return {@code true} si l'utilisateur a validé la confirmation
     */
    public static boolean showConfirmation(String title, String message) {
        return showConfirmation(title, message, null);
    }

    /**
     * Affiche une boîte de confirmation et retourne vrai si l'utilisateur confirme.
     *
     * @param title   titre de la fenêtre
     * @param message contenu du message
     * @param owner   fenêtre parente (facultative)
     * @return {@code true} si l'utilisateur a validé la confirmation
     */
    public static boolean showConfirmation(String title, String message, Window owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }

        Optional<ButtonType> res = alert.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    /**
     * Affiche une boîte d'erreur avec un titre et un message.
     *
     * @param title   titre de la fenêtre
     * @param message contenu du message
     */
    public static void showError(String title, String message) {
        showError(title, message, null);
    }

    /**
     * Affiche une boîte d'erreur avec un titre et un message, attachée à une fenêtre parente.
     *
     * @param title   titre de la fenêtre
     * @param message contenu du message
     * @param owner   fenêtre parente (facultative)
     */
    public static void showError(String title, String message, Window owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.showAndWait();
    }
}