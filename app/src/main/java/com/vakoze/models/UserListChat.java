package com.vakoze.models;

public class UserListChat {
    String UId,nom, photo;

    public UserListChat(String UId, String nom, String photo) {
        this.UId = UId;
        this.nom = nom;
        this.photo = photo;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
