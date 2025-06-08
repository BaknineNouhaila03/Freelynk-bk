package org.example.freelynk.service;

import org.example.freelynk.model.Client;
import org.example.freelynk.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    public Client getClientById(UUID clientId) {
        return clientRepository.findById(clientId).orElse(null);
    }
    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email).orElse(null);
    }

}