/*
 * Copyright information here.
 */
package com.informatica.word.counter.db;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring data repository for file status.
 */
@Repository
public interface FileStatusRepository extends CrudRepository<FileEntity, Long> {
    
    @Override
    @Query("select entity from FileEntity entity order by id desc")
    Iterable<FileEntity> findAll();

}
