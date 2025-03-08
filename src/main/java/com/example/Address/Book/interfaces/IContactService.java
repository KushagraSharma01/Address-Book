package com.example.Address.Book.interfaces;

import java.util.*;
import com.example.Address.Book.dto.ContactDTO;
import com.example.Address.Book.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface IContactService {



    public ContactDTO get(Long id) throws Exception;

    public ContactDTO create(ContactDTO user) throws Exception;

    public String clear();

    public List<ContactDTO> getAll();

    public ContactDTO edit(ContactDTO user, Long id) throws Exception;

    public String delete(Long id);

    public ResponseDTO response(String message, String status);
}
