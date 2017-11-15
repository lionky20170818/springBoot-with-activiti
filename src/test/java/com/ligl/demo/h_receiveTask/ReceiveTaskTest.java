package com.ligl.demo.h_receiveTask;

import com.ligl.demo.OverFlowTest;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;

/**
 * 接收任务（ReceiveTask）
 * 在任务创建后，意味着流程会进入等待状态，直到引擎接收了一个特定的消息， 这会触发流程穿过接收任务继续执行。
 */
public class ReceiveTaskTest extends OverFlowTest {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署流程定义zip文件
     */
    @Test
    public void deploymentProcessDefinition_inputStream() {
        // 获取同包下面的文件
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("receiveTask.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("receiveTask.png");

        Deployment deployment = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
                .createDeployment() // 创建一个部署对象
                .name("开始活动") // 添加部署的名称
                .addInputStream("receiveTask.bpmn", inputStreamBpmn) //
                .addInputStream("receiveTask.png", inputStreamPng) //
                .deploy(); // 完成部署
        System.out.println("部署Id：" + deployment.getId()); // 部署Id:82501
        System.out.println("部署名称：" + deployment.getName()); // 部署名称:连线流程定义
    }

    /**
     * 启动流程实例、设置流程变量、获取流程变量、向后执行（当前任务是自动操作的情况）
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "receiveTask"; // 使用Key的启动，默认按照对心版本的流程定义启动
        ProcessInstance pi = processEngine.getRuntimeService() // 与正在执行的流程实例和执行对象相关的Service
                .startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的Key启动流程实例，key对应helloworld.bpmn文件中的流程名称

        System.out.println("流程实例Id:" + pi.getId()); // 流程实例Id：102501
        System.out.println("流程定义Id:" + pi.getProcessDefinitionId()); // 流程定义Id：c_exclusiveGateWay:1:100004

        /** 查询执行对象Id */
        Execution execution1 = processEngine.getRuntimeService()
                .createExecutionQuery()
                .processInstanceId(pi.getId()) // 流程实例id
                .activityId("receivetask1") // 活动节点的id的属性值
                .singleResult();

        /**使用流程变量来设置一个当日销售额用来传递业务参数*/
        processEngine.getRuntimeService()
                .setVariable(execution1.getId(), "汇总当日销售额", 21000);

        /** 向后执行一部，如果流程处于等待状态，使得流程继续等待 */
        processEngine.getRuntimeService()
                .signal(execution1.getId());

        /** 查询执行对象Id */
        Execution execution2 = processEngine.getRuntimeService()
                .createExecutionQuery()
                .processInstanceId(pi.getId()) // 流程实例id
                .activityId("receivetask2") // 活动节点的id的属性值
                .singleResult();

        /**从流程变量中获取汇总当日销售额*/
        Integer value = (Integer) processEngine.getRuntimeService()
                .getVariable(execution2.getId(), "汇总当日销售额");

        System.out.println("给老板发送短信：金额是：" + value);

        /** 向后执行一部，如果流程处于等待状态，使得流程继续等待 */
        processEngine.getRuntimeService()
                .signal(execution2.getId());
    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteProcessDefinition() {
        String deploymentId = "267501";
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
