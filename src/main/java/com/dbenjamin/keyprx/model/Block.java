package com.dbenjamin.keyprx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Block {
/*
    public Block(String name, Integer capacity, Integer overbookPercent) {
        this.name = name;
        this.capacity = capacity;
        this.overbookPercent = overbookPercent;
    }*/

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private Integer capacity;

    private Integer overbookPercent;

    /**
     *
     * @return
     */
    public Integer computeOverbookedCapacity() {

        /// TODO DPB UNIT TEST THIS *************    // TODO DPB worry about rounding/truncation

        Integer overBookAmount = (int)Math.floor((this.getCapacity() * this.getOverbookPercent()) / 100);
        return this.getCapacity() + overBookAmount;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getOverbookPercent() {
        return overbookPercent;
    }

    public void setOverbookPercent(Integer overbookPercent) {
        this.overbookPercent = overbookPercent;
    }

}
