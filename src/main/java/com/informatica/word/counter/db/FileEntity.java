/*
 * Copyright information here.
 */
package com.informatica.word.counter.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "file_status")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    
    private String name;
    
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileEntity { id=[");
        sb.append(id);
        sb.append("] name=[");
        sb.append(name);
        sb.append("] status=[");
        sb.append(status);
        sb.append("] }");

        return sb.toString();
    }
    
}
