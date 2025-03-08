package com.example.Address.Book.services;


import com.example.Address.Book.dto.ContactDTO;
import com.example.Address.Book.dto.ResponseDTO;
import com.example.Address.Book.entities.ContactEntity;
import com.example.Address.Book.interfaces.IContactService;
import com.example.Address.Book.repositories.ContactRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService implements IContactService {

    ObjectMapper obj = new ObjectMapper();

    @Autowired
    ContactRepository contactRepository;

    public ResponseDTO response(String message, String status){
        return new ResponseDTO(message, status);
    }

    public ContactDTO get(Long id) throws Exception{

        ContactEntity foundEmp = contactRepository.findById(id).orElseThrow(()->
        {
            log.error("Cannot find employee with id {}", id);
            return new RuntimeException("Cannot find employee with given id");
        });

        ContactDTO resDto = new ContactDTO(foundEmp.getName(), foundEmp.getEmail(), foundEmp.getId(), foundEmp.getAddress(), foundEmp.getId());

        log.info("Employee DTO send for id: {} is : {}", id, obj.writeValueAsString(resDto));

        return resDto;

    }

    public ContactDTO create(ContactDTO user) throws Exception{
        ContactEntity newUser = new ContactEntity(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress());

        contactRepository.save(newUser);

        log.info("Employee saved in db: {}", obj.writeValueAsString(newUser));

        ContactDTO resDto = new ContactDTO(newUser.getName(), newUser.getEmail(), newUser.getPhoneNumber(), newUser.getAddress(), newUser.getId());

        log.info("Employee DTO sent: {}", obj.writeValueAsString(resDto));

        return resDto;
    }

    public List<ContactDTO> getAll(){

        return contactRepository.findAll().stream().map(entity -> {
                                                                                  ContactDTO newUser = new ContactDTO(entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.getId());
                                                                                  return newUser;
        }).collect(Collectors.toList());

    }

    public ContactDTO edit(ContactDTO user, Long id) throws Exception{
        ContactEntity foundEmp = contactRepository.findById(id).orElseThrow(()->{
            log.error("Cannot find employee with id : {}", id);
            return new RuntimeException("cannot find employee with given id");
        });

        foundEmp.setName(user.getName());
        foundEmp.setEmail(user.getEmail());

        contactRepository.save(foundEmp);

        log.info("Employee saved after editing in db is : {}", obj.writeValueAsString(foundEmp));

        ContactDTO resDto = new ContactDTO(foundEmp.getName(), foundEmp.getEmail(),foundEmp.getPhoneNumber(), foundEmp.getAddress(), foundEmp.getId());

        return resDto;
    }

    public String delete(Long id){
        ContactEntity foundUser = contactRepository.findById(id).orElseThrow(()->{
            log.error("Cannot find user with id : {}", id);
            return new RuntimeException("cannot find user with given id");
        });

        contactRepository.delete(foundUser);

        return "employee deleted";
    }

    public String clear(){

        contactRepository.deleteAll();
        return "db cleared";

    }





}
