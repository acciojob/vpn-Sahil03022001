package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        Optional<User> userOptional = userRepository2.findById(userId);
        if(!userOptional.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOptional.get();
        Country country = user.getOriginalCountry();

        if(user.getConnected()) throw new Exception("Already connected");
        if(countryName.equalsIgnoreCase(String.valueOf(user.getOriginalCountry().getCountryName()))) {
            return user;
        }

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        ServiceProvider myServiceProvider = null;
        Country reqCountry = null;

        outer:
        for(ServiceProvider serviceProvider : serviceProviderList) {
            List<Country> countryList = serviceProvider.getCountryList();
            for(Country myCountry : countryList) {
                if(countryName.equalsIgnoreCase(String.valueOf(myCountry.getCountryName()))) {
                    myServiceProvider = serviceProvider;
                    reqCountry = myCountry;
                    break outer;
                }
            }
        }

        if(myServiceProvider == null) {
            throw new Exception("Unable to connect");
        }

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(myServiceProvider);
        myServiceProvider.getConnectionList().add(connection);
        user.getConnectionList().add(connection);
        user.setMaskedIp(reqCountry.getCode() + "." + myServiceProvider.getId() + "." + user.getId());
        user.setConnected(true);
        user.getOriginalCountry().setCountryName(reqCountry.getCountryName());
        user.getOriginalCountry().setCode(reqCountry.getCode());
        return userRepository2.save(user);
    }

    @Override
    public User disconnect(int userId) throws Exception {
        Optional<User> userOptional = userRepository2.findById(userId);
        if(!userOptional.isPresent()) {
            throw new Exception("User not found");
        }

        User user = userOptional.get();
        if(!user.getConnected()) throw new Exception("Already disconnected");

        String maskedIp = user.getMaskedIp();
        String[] arr = user.getMaskedIp().split("[.]");
        user.setConnected(false);
        user.setMaskedIp(null);

        String[] arr2 = user.getOriginalIp().split("[.]");
        String code = arr2[0];
        CountryName countryName = null;

        for (CountryName country : CountryName.values()) {
            if (country.toCode().equals(code)) {
                countryName = CountryName.valueOf(country.name());
                break;
            }
        }

        user.getOriginalCountry().setCode(code);
        user.getOriginalCountry().setCountryName(countryName);

        return userRepository2.save(user);
    }

    //Establish a connection between sender and receiver users
    //To communicate to the receiver, sender should be in the current country of the receiver.
    //If the receiver is connected to a vpn, his current country is the one he is connected to.
    //If the receiver is not connected to vpn, his current country is his original country.
    //The sender is initially not connected to any vpn. If the sender's original country does not match receiver's current country, we need to connect the sender to a suitable vpn. If there are multiple options, connect using the service provider having smallest id
    //If the sender's original country matches receiver's current country, we do not need to do anything as they can communicate. Return the sender as it is.
    //If communication can not be established due to any reason, throw "Cannot establish communication" exception
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        Optional<User> senderOptional = userRepository2.findById(senderId);
        Optional<User> receiverOptional = userRepository2.findById(receiverId);
        if(senderOptional.isPresent() || receiverOptional.isPresent()) {
            throw new Exception("Sender or receiver not present, To connect kaise kroge ???");
        }

        User sender = senderOptional.get();
        User receiver = receiverOptional.get();

        if(sender.getOriginalCountry().getCountryName().equals(receiver.getOriginalCountry().getCountryName())) {
            return sender;
        }

        String receiverCountryCode = receiver.getOriginalCountry().getCode();
        ServiceProvider serviceProvider = null;
        List<ServiceProvider> serviceProviderList = sender.getServiceProviderList();

        outer:
        for(ServiceProvider serviceProvider1 : serviceProviderList) {
            List<Country> countryList = serviceProvider1.getCountryList();
            for(Country country : countryList) {
                if(country.getCode().equals(receiverCountryCode)) {
                    serviceProvider = serviceProvider1;
                    break outer;
                }
            }
        }

        if(serviceProvider == null) {
            throw new Exception("Cannot establish communication");
        }

        return connect(senderId, String.valueOf(receiver.getOriginalCountry().getCountryName()));
    }
}
