package com.ligl.demo.firstFlow;

/**
 * Function:
 * Author: created by liguoliang
 * Date: 2017/11/13 0013 下午 5:43
 * Version: 1.0
 */

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ActivitiService {

    //注入为我们自动配置好的服务
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    //开始流程，传入申请者的id以及公司的id
    public void startProcess(Long personId, Long compId, String processId) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("personId", personId);
        variables.put("compId", compId);
        runtimeService.startProcessInstanceByKey(processId, variables);
    }

    //获得某个人的任务别表
    public List<Task> getTasks(String assignee){
        return taskService.createTaskQuery().taskCandidateUser(assignee).list();
        //指定个人查询，指定办理人act_ru_task
//        String assignee = "张三"; // TODO
//        List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的service
//                .createTaskQuery()// 创建任务查询对象
//                // 查询条件
//                .taskAssignee(assignee)// 指定个人任务查询，指定办理人
//                // .taskCandidateGroup("")//组任务的办理人查询
//                // .processDefinitionId("")//使用流程定义ID查询
//                // .processInstanceId("")//使用流程实例ID查询
//                // .executionId(executionId)//使用执行对象ID查询
//                /** 排序 */
//                .orderByTaskCreateTime().asc()// 使用创建时间的升序排列
//                // 返回结果集
//                // .singleResult() //返回唯一的结果集
//                // .count()//返回结果集的数量
//                // .listPage(firstResult, maxResults)//分页查询
//                .list();// 返回列表
    }

    //完成任务
    public void completeTasks(Boolean joinApproved, String taskId){
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("joinApproved", joinApproved);
        taskService.complete(taskId, taskVariables);
     }

}
