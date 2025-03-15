package com.example.Address.Book.services;


import com.example.Address.Book.dto.ContactDTO;
import com.example.Address.Book.dto.ResponseDTO;
import com.example.Address.Book.entities.ContactEntity;
import com.example.Address.Book.interfaces.IContactService;
import com.example.Address.Book.repositories.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.info.Contact;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService implements IContactService {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    public ResponseDTO response(String message, String status){
        return new ResponseDTO(message, status);
    }

    public ContactDTO get(Long id, HttpServletRequest request){
        try {

            Long userId = getUserId(request);

            List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getUserId() == id).collect(Collectors.toList());

            if(contacts.size() == 0)
                throw new RuntimeException();

            ContactEntity foundContact = contacts.get(0);

            ContactDTO resDto = new ContactDTO(foundContact.getName(), foundContact.getEmail(), foundContact.getId(), foundContact.getAddress(), foundContact.getId());

            log.info("Contact DTO send for id: {} is : {}", id, getJSON(resDto));

            return resDto;
        }
        catch(RuntimeException e){
            log.error("Cannot find employee with id {}", id);
        }
        return null;
    }

    public ContactDTO create(ContactDTO user, HttpServletRequest request){
        try {

            ContactEntity foundEntity = contactRepository.findByEmail(user.getEmail());

            //fetching userId from token in cookies of user
            Long userId = getUserId(request);

            ContactEntity newUser = new ContactEntity(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress(), userId);

            contactRepository.save(newUser);

            log.info("Contact saved in db: {}", getJSON(newUser));

            ContactDTO resDto = new ContactDTO(newUser.getName(), newUser.getEmail(), newUser.getPhoneNumber(), newUser.getAddress(), newUser.getId());

            log.info("Contact DTO sent: {}", getJSON(resDto));

            return resDto;
        }
        catch(RuntimeException e){
            log.error("Exception : {} Reason : {}", e, "User already created with given email");
        }
        return null;
    }

    public List<ContactDTO> getAll(HttpServletRequest request){

        //fetching userId from token in cookies of user
        Long userId = getUserId(request);

        return contactRepository.findByUserId(userId).stream().map(entity -> {
                                                                                  ContactDTO newUser = new ContactDTO(entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.getId());
                                                                                  return newUser;
        }).collect(Collectors.toList());

    }

    public ContactDTO edit(ContactDTO user, Long id, HttpServletRequest request) {
        
        Long userId = getUserId(request);

        List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getUserId() == id).collect(Collectors.toList());

        if(contacts.size() == 0)
            throw new RuntimeException("No contact with given id found");
        
        ContactEntity foundContact = contacts.get(0);
        
        foundContact.setName(user.getName());
        foundContact.setEmail(user.getEmail());

        contactRepository.save(foundContact);

        log.info("Contact saved after editing in db is : {}", getJSON(foundContact));

        ContactDTO resDto = new ContactDTO(foundContact.getName(), foundContact.getEmail(),foundContact.getPhoneNumber(), foundContact.getAddress(), foundContact.getId());

        return resDto;
    }

    public String delete(Long id, HttpServletRequest request){
        
        Long userId = getUserId(request);

        List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getUserId() == id).collect(Collectors.toList());

        if(contacts.size() == 0)
            throw new RuntimeException("No contact with given id found");

        ContactEntity foundUser = contacts.get(0);

        contactRepository.delete(foundUser);

        return "contact deleted";
    }

    public String clear(){

        contactRepository.deleteAll();
        return "db cleared";

    }

    public String getJSON(Object object){
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(object);
        }
        catch(JsonProcessingException e){
            log.error("Reason : {} Exception : {}", "Conversion error from Java Object to JSON");
        }
        return null;
    }

    public Long getUserId(HttpServletRequest request){

        //fetching token of logged in user
        Cookie foundCookie = null;

        for(Cookie c : request.getCookies()){
            if(c.getName().equals("jwt")){
                foundCookie = c;
                break;
            }
        }
        if(foundCookie == null)
            throw new RuntimeException("Cannot find the login cookie");

        //decode the user id from token in cookie using jwttokenservice
        Long userId = jwtTokenService.decodeToken(foundCookie.getValue());

        return userId;
    }


}
