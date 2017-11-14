package com.ligl.demo.firstFlow;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Function:
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:40
 * Version: 1.0
 */

//@NoRepositoryBean//一般用作父类的repository，有这个注解，spring不会去实例化该repository。
public interface PersonRepository extends JpaRepository<Person, Long> {
    public Person findByPersonName(String personName);
}
