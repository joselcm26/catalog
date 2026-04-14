package com.josec.catalog.service;

import com.josec.catalog.dto.BookResponseDTO;
import com.josec.catalog.dto.ReadListResponseDTO;
import com.josec.catalog.dto.mappers.ReadListMapper;
import com.josec.catalog.model.Book;
import com.josec.catalog.model.ReadList;
import com.josec.catalog.repository.BookRepository;
import com.josec.catalog.repository.ReadListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadListService {

    // -- DEPENDENCIAS --
    @Autowired
    private ReadListRepository readListRepository;
    @Autowired
    private ReadListMapper readListMapper;

}
