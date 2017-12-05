package com.ligl.demo.firstFlow;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Function:
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:40
 * Version: 1.0
 * Spring Data介绍：http://blog.csdn.net/u011054333/article/details/61429740
 *                  https://www.cnblogs.com/ityouknow/p/5891443.html
 */

//@NoRepositoryBean//一般用作父类的repository，有这个注解，spring不会去实例化该repository。
public interface PersonRepository extends JpaRepository<Person, Long> {
    public Person findByPersonName(String personName);

//    @Query("from ACT_RU_TASK b where b.NAME_ = :personName")
//    Person withPersonName(@Param("personName") String personName);
}
