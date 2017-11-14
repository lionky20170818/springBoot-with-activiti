package com.ligl.demo.firstFlow;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Function:
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:38
 * Version: 1.0
 */
@Entity
@Data
//@Table(name="comp")
public class Comp {

    @Id
    @GeneratedValue
    private Long compId;

    private String compName;

    @OneToMany(mappedBy = "comp",targetEntity = Person.class)
    private List<Person> people;

    public Comp(String compName){
        this.compName = compName;
    }

    public Comp(){

    }
}
