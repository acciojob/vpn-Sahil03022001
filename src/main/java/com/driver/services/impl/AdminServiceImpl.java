package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUserName(username);
        admin.setPassword(password);
        return adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) throws Exception {
        Optional<Admin> adminOptional = adminRepository1.findById(adminId);
        if(!adminOptional.isPresent()) {
            throw new Exception("No Admin Present");
        }

        Admin admin = adminOptional.get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        admin.getServiceProviders().add(serviceProvider);
        serviceProvider.setAdmin(admin);
        return adminRepository1.save(admin);
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
        Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository1.findById(serviceProviderId);
        if(!serviceProviderOptional.isPresent()) {
            throw new Exception("Service provider not found");
        }

        ServiceProvider serviceProvider = serviceProviderOptional.get();
        countryName = countryName.toUpperCase();
        CountryName countryName1;
        switch (countryName) {
            case "IND":
                countryName1 = CountryName.IND;
                break;
            case "AUS":
                countryName1 = CountryName.AUS;
                break;
            case "CHI":
                countryName1 = CountryName.CHI;
                break;
            case "USA":
                countryName1 = CountryName.USA;
                break;
            default:
                countryName1 = CountryName.JPN;
                break;
        }

        String countryCode = countryName1.toCode();

        Country country = new Country();
        country.setCountryName(countryName1);
        country.setCode(countryCode);
        country.setServiceProvider(serviceProvider);
        serviceProvider.getCountryList().add(country);

        return serviceProvider;
    }
}
