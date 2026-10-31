package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private String nom;
    private String login;
    private List<String> roles = new ArrayList<>();

    public User() {
    }

    public User(String nom, String login, String... roles) {
        this.nom = nom;
        this.login = login;
        if (roles != null) {
            this.roles.addAll(Arrays.asList(roles));
        }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String role) {
        if (role == null || roles == null || roles.isEmpty()) {
            return false;
        }
        String roleToCheck = role.trim().toUpperCase();
        for (String r : roles) {
            if (r != null && r.trim().toUpperCase().equals(roleToCheck)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{nom='" + nom + "', login='" + login + "', roles=" + roles + '}';
    }
}
