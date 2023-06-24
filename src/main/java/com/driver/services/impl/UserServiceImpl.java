package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);

        Country country = new Country();
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
        country.setCountryName(countryName1);
        country.setCode(countryCode);

        user.setCountry(country);
        country.setUser(user);

        user.setConnected(false);
        user.setMaskedIp(null);

        user = userRepository3.save(user);
        user.setMaskedIp(countryCode + "." + user.getId());
        return userRepository3.save(user);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        Optional<User> userOptional = userRepository3.findById(userId);
        if(!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository3.findById(serviceProviderId);
        if(!serviceProviderOptional.isPresent()) {
            throw new RuntimeException("Service provider not found");
        }

        User user = userOptional.get();
        ServiceProvider serviceProvider = serviceProviderOptional.get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

       return userRepository3.save(user);
    }
}
