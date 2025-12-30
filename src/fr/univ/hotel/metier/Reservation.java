package fr.univ.hotel.metier;

import java.time.LocalDate;

public class Reservation {

    private int id;
    private Client client;
    private Chambre chambre;
    private LocalDate dateEntree;
    private LocalDate dateSortie;
    private double prixTotal;
    private String statut;

    public Reservation() {
    }

    public Reservation(int id,
                       Client client,
                       Chambre chambre,
                       LocalDate dateEntree,
                       LocalDate dateSortie,
                       double prixTotal,
                       String statut) {
        this.id = id;
        this.client = client;
        this.chambre = chambre;
        this.dateEntree = dateEntree;
        this.dateSortie = dateSortie;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Chambre getChambre() {
        return chambre;
    }

    public void setChambre(Chambre chambre) {
        this.chambre = chambre;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}