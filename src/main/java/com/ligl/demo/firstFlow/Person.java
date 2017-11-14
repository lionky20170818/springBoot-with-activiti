package com.ligl.demo.firstFlow;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Function:
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:33
 * Version: 1.0
 */
@Entity
//@Table(name="person")
@Data
public class Person {

    @Id
    @GeneratedValue
    private Long personId;

    private String personName;

    @ManyToOne(targetEntity = Comp.class)
    private Comp comp;

    public Person(){

    }

    public Person(String personName){
        super();
        this.personName = personName;
    }

//    @Id
//    @GeneratedValue(strategy= GenerationType.AUTO)
//    @Column(unique=true, nullable=false)
//    public Long getPersonId() {
//        return personId;
//    }

}
