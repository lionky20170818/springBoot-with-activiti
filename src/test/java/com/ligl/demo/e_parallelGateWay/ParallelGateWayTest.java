package com.ligl.demo.e_parallelGateWay;

import com.ligl.demo.OverFlowTest;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;

/**
 * 并行网关（ParallelGateWay）
 */
public class ParallelGateWayTest extends OverFlowTest {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署流程定义zip文件
     */
    @Test
    public void deploymentProcessDefinition_inputStream() {
        // 获取同包下面的文件
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("e_parallelGateWay.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("e_parallelGateWay.png");

        Deployment deployment = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
                .createDeployment() // 创建一个部署对象
                .name("并行网关") // 添加部署的名称
                .addInputStream("e_parallelGateWay.bpmn", inputStreamBpmn) //
                .addInputStream("e_parallelGateWay.png", inputStreamPng) //
                .deploy(); // 完成部署
        System.out.println("部署Id：" + deployment.getId()); // 部署Id:82501
        System.out.println("部署名称：" + deployment.getName()); // 部署名称:连线流程定义
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "e_parallelGateWay"; // 使用Key的启动，默认按照对心版本的流程定义启动
        ProcessInstance pi = processEngine.getRuntimeService() // 与正在执行的流程实例和执行对象相关的Service
                .startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的Key启动流程实例，key对应helloworld.bpmn文件中的流程名称

        System.out.println("流程实例Id:" + pi.getId()); // 流程实例Id：102501
        System.out.println("流程定义Id:" + pi.getProcessDefinitionId()); // 流程定义Id：c_exclusiveGateWay:1:100004
    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteProcessDefinition() {
        String deploymentId = "127501";
        /**
         * 不带级联的删除 只能删除没有启动的流程，如果流程启动就会跑出异常
         */
        // processEngine.getRepositoryService()
        // .deleteDeployment(deploymentId);

        /**
         * 级联删除 不管流程是否启动，都能够删除
         */
        processEngine.getRepositoryService().deleteDeployment(deploymentId, Boolean.TRUE);

        System.out.println("删除成功");
    }

    /**
     * 查询当前人的个人任务
     */
    @Test
    public void findMyPersonalTask() {
        String assignee = "买家";
        List<Task> list = processEngine.getTaskService() // 与正在执行的任务管理相关的Service
                .createTaskQuery() // 创建任务查询对象
                /** 查询条件 */
                .taskAssignee(assignee) // 制定个人任务查询，指定办理人
                // .taskCandidateGroup(candidateUser) // 组任务的办理人查询
                // .processInstanceId(processInstanceId)//流程实例Id
                // .processDefinitionId(processDefinitionId)//流程定义id
                // .executionId(executionId)// 使用执行id查询
                /** 排序 */
                .orderByTaskCreateTime().asc()// 使用创建时间排序
                /** 结果集 */
                // .singleResult()//
                // .count()//
                .list();
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务Id：" + task.getId());// 72504
                System.out.println("任务名称：" + task.getName());
                System.out.println("任务的创建时间：" + task.getCreateTime());
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例Id:" + task.getProcessInstanceId());
                System.out.println("执行对象Id:" + task.getExecutionId());
                System.out.println("流程定义Id:" + task.getProcessDefinitionId());
                System.out.println("#####################################################");
            }
        }
    }

    /**
     * 完成我的任务
     */
    @Test
    public void completeMyPersonalTask() {
        String taskId = "142502";

        processEngine.getTaskService()// 与正在执行的任务管理相关的Service
                .complete(taskId);
        System.out.println("完成任务：任务Id" + taskId);
    }
}
