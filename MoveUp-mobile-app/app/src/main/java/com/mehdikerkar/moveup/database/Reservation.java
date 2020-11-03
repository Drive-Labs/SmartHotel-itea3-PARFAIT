package com.mehdikerkar.moveup.database;

public class Reservation {



    private static int Res_Id = 0;
    private String NumChambre;
    private String NumClient;
    private String Key;
    private String DateD;
    private String DateF;

    @Override
    public String toString() {
        return "Reservation{" +
                "NumChambre='" + NumChambre + '\'' +
                ", NumClient='" + NumClient + '\'' +
                ", Key='" + Key + '\'' +
                ", DateD=" + DateD +
                ", DateF=" + DateF +
                '}';
    }
    public Reservation(){
        this.Res_Id = 0;
        this.NumChambre = null;
        this.NumClient = null;
        this.Key = null;
        this.DateD = null;
        this.DateF = null;
    }
    public Reservation(int id){ this.Res_Id = id + 1;}
    public Reservation(String numChambre, String numClient, String key, String dateD, String dateF) {
        this.Res_Id = Res_Id + 1;
        this.NumChambre = numChambre;
        this.NumClient = numClient;
        this.Key = key;
        this.DateD = dateD;
        this.DateF = dateF;
    }

    public int getRes_id() {
        return Res_Id;
    }

    public void setRes_id(int res_id) {
        this.Res_Id = res_id;
    }

    public String getNumChambre() {
        return NumChambre;
    }

    public void setNumChambre(String numChambre) {
        NumChambre = numChambre;
    }

    public String getNumClient() {
        return NumClient;
    }

    public void setNumClient(String numClient) {
        NumClient = numClient;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDated() {
        return DateD;
    }

    public void setDated(String dated) {
        DateD = dated;
    }

    public String getDatef() {
        return DateF;
    }

    public void setDatef(String datef) {
        DateF = datef;
    }
}


