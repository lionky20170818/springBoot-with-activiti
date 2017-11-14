package com.ligl.demo.firstFlow;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:流程调用入口
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:53
 * Version: 1.0
 */
@RestController
public class MyRestController {
    @Autowired
    private ActivitiService myService;

    //开启流程实例，入口
    @GetMapping("/process/{personId}/{compId}")
//    @RequestMapping(value="/process/{personId}/{compId}", method= RequestMethod.GET)
    public String startProcessInstance(@PathVariable Long personId, @PathVariable Long compId){
        myService.startProcess(personId,compId);
        return "SUCCESS";
    }

    //获取当前人的任务
    @RequestMapping(value="/tasks", method= RequestMethod.GET)
    public List<TaskRepresentation> getTasks(@RequestParam String assignee){
        List<Task> tasks = myService.getTasks(assignee);
        List<TaskRepresentation> dtos = new ArrayList<TaskRepresentation>();
        for(Task task : tasks){
            dtos.add(new TaskRepresentation(task.getId(), task.getName()));
        }
        return dtos;
    }

    //完成任务
    @RequestMapping(value="/complete/{joinApproved}/{taskId}", method= RequestMethod.GET)
    public String complete(@PathVariable Boolean joinApproved,@PathVariable String taskId){
        myService.completeTasks(joinApproved,taskId);
        return "OK";
    }

}
