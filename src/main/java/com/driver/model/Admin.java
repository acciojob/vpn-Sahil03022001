package com.driver.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Admin {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String userName;
    private String password;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    List<ServiceProvider> serviceProviders = new ArrayList<>();

    public Admin(Integer id, String userName, String password, List<ServiceProvider> serviceProviders) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.serviceProviders = serviceProviders;
    }

    public Admin() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ServiceProvider> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<ServiceProvider> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }
}