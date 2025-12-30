package fr.univ.hotel.metier;

public class Chambre {

    private int id;
    private int numero;
    private String type;
    private double prixParNuit;
    private String statut;

    public Chambre() {
    }

    public Chambre(int id, int numero, String type, double prixParNuit, String statut) {
        this.id = id;
        this.numero = numero;
        this.type = type;
        this.prixParNuit = prixParNuit;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrixParNuit() {
        return prixParNuit;
    }

    public void setPrixParNuit(double prixParNuit) {
        this.prixParNuit = prixParNuit;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Chambre " + numero + " (" + type + ")";
    }
}