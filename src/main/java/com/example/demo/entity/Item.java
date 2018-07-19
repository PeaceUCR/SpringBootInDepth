package com.example.demo.entity;

import javax.persistence.Entity;

@Entity
public class Item extends AbstractEntity {

    private String name;
    private String details;

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                '}';
    }

    public Item(){

    }


    public Item(String name, String details){
        this.name = name;
        this.details = details;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
