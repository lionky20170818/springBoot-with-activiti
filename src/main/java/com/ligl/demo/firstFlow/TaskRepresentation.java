package com.ligl.demo.firstFlow;

import lombok.Data;

/**
 * Function:
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:57
 * Version: 1.0
 */
@Data
public class TaskRepresentation {
    private String id;
    private String name;

    public TaskRepresentation(String id, String name){
        this.id = id;
        this.name = name;
    }
}
