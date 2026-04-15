package com.josec.catalog.repository;

import com.josec.catalog.model.Book;
import com.josec.catalog.model.ReadList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadListRepository extends JpaRepository<ReadList, Integer> {

    ReadList findByUserId(Integer userId);

    ReadList findByOwnerId(Integer ownerId);
}
