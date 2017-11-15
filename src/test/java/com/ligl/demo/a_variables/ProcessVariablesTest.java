package com.ligl.demo.a_variables;

import com.ligl.demo.OverFlowTest;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;

/**
 * Variables流程变量的设置和获取
 * setVariables 和 setVariablesLocal区别?
 * setVariable：设置流程变量的时候，流程变量名称相同的时候，后一次的值替换前一次的值，而且可以看到TASK_ID的字段不会存放任务ID的值
 * setVariableLocal说明流程变量绑定了当前的任务，当流程继续执行时，下个任务获取不到这个流程变量（因为正在执行的流程变量中没有这个数据），所有查询正在执行的任务时不能查询到我们需要的数据，此时需要查询历史的流程变量。
 */

public class ProcessVariablesTest extends OverFlowTest {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 以流的方式部署流程定义文件
     */
    @Test
    public void deploymentProcessDefinition_inputStream() {
        InputStream inputStreambpmn = this.getClass().getResourceAsStream("/diagrams/processVariables.bpmn");
        InputStream inputStreampng = this.getClass().getResourceAsStream("/diagrams/processVariables.png");

        Deployment deployment = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
                .createDeployment() // 创建一个部署对象
                .name("流程定义输入流测试variables") // 添加部署的名称
                .addInputStream("processVariables.bpmn", inputStreambpmn)// 使用资源文件名称（要求与资源文件的名称要一致），和输入流完成部署
                .addInputStream("processVariables.png", inputStreampng)// 使用资源文件名称（要求与资源文件的名称要一致），和输入流完成部署
                .deploy(); // 完成部署
        System.out.println("部署Id：" + deployment.getId()); // 部署Id:55001
        System.out.println("部署名称：" + deployment.getName()); // 部署名称:流程定义输入流

    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "processVariables"; // 使用Key的启动，默认按照对心版本的流程定义启动
        ProcessInstance pi = processEngine.getRuntimeService() // 与正在执行的流程实例和执行对象相关的Service
                .startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的Key启动流程实例，key对应helloworld.bpmn文件中的流程名称

        System.out.println("流程实例Id" + pi.getId()); // 流程实例Id：17501
        System.out.println("流程定义Id" + pi.getProcessDefinitionId()); // 流程定义Id：psocessVariables:1:15004
    }

    /**
     * 设置流程变量
     */
    @Test
    public void setVeriables() {
        /**与任务（正在执行）*/
        TaskService taskService = processEngine.getTaskService();
        // 任务Id
        String taskId = "17504";
        /**一：设置流程变量 */
        /*taskService.setVariableLocal(taskId, "请假天数", 5); // 表示与任务Id绑定
		taskService.setVariable(taskId, "请假日期", new Date());
		taskService.setVariable(taskId, "请假原因", "回家探亲，一起吃个饭");*/
        /**二：设置流程变量使用Javabean类型*/
        Preson p = new Preson(); // 需要实现序列化接口（使用）
        p.setId(20);
        p.setName("赵四子");
        taskService.setVariable(taskId, "人员信息", p);
        taskService.setVariable(taskId, "人员信息（添加信息）", p);

        System.out.println("设置流程变量成功===");
    }


    /**
     * 获取流程变量
     */
    @Test
    public void getVeriables() {
        /**与任务（正在执行）*/
        TaskService taskService = processEngine.getTaskService();
        // 任务Id
        String taskId = "17504";
        /**一：基本方式获取流程变量*/
		/*Integer days = (Integer)taskService.getVariable(taskId, "请假天数"); // 和任务好绑定，如果是local是按照需求添加的local建议不用
		Date date = (Date)taskService.getVariable(taskId, "请假日期");
		String reason = (String)taskService.getVariable(taskId, "请假原因");
		
		System.out.println("请假天数："+days);
		System.out.println("请假日期："+date);
		System.out.println("请假原因："+reason);*/
        /**二：获取流程变量，使用javaBean类型*/
        /**当一个JavaBean放置到流程变量中要求，javabean的属性，不能再发生变化，如果发生变化在获取的时候，抛出异常*/
        //Preson p = (Preson)taskService.getVariable(taskId, "人员信息");
        Preson p2 = (Preson) taskService.getVariable(taskId, "人员信息（添加信息）");
        //System.out.println(p);
        System.out.println("====" + p2);
    }

    /**
     * 完成我的任务
     */
    @Test
    public void completeMyPersonalTask() {
        String taskId = "22502";
        processEngine.getTaskService()//与正在执行的任务管理相关的Service
                .complete(taskId);
        System.out.println("完成任务：任务Id:" + taskId);
    }

    /**
     * 模拟设置和获取流程变量的场景
     */
    @Test
    public void setAndGetVariables() {
        /**与流程实例，执行对象*/
        RuntimeService runtimeService = processEngine.getRuntimeService();
        /**与任务相关（正在执行）*/
        TaskService taskService = processEngine.getTaskService();

        /** 设置流程变量 */
        //runtimeService.setVariable(executionId, variableName, value);// 表示使用执行Id，流程变量名称，设置流程变量的值（一次只能设置一个值）
        //runtimeService.setVariables(executionId, map);// 表示使用执行id以及流程变量map ，map的key就是流程变量的名称、map集合的value就是流程变量的名称。一次多个

        //taskService.setVariable(raskId, variableName, value);// 表示使用任务Id，流程变量名称，设置流程变量的值（一次只能设置一个值）
        //taskService.setVariables(taskId, map);// 表示使用任务id以及流程变量map ，map的key就是流程变量的名称、map集合的value就是流程变量的名称。一次多个

        //runtimeService.startProcessInstanceById(processInstanceKey, variableMap);// 启动流程实例的时候设置流程变量
        //taskService.complete(taskId, variableMap); // 完成任务的时候进行设置流程变量

        /** 获取流程变量 */
        //runtimeService.getVariable(executionId, variableName);// 使用执行对象Id和流程变量名称获取流程变量的值
        //runtimeService.getVariables(executionId); //使用执行对象Id和流程变量名称获取map集合
        //runtimeService.getVariables(executionId, variableNames); // 使用执行对象id,获取流程变量的值，通过设置流程变量的名称存放到集合里面，然后获取对应的返回值的map

        //taskService.getVariable(taskId, variableName);// 使用任务对象Id和流程变量名称获取流程变量的值
        //taskService.getVariables(taskId); //使用任务对象Id和流程变量名称获取map集合
        //taskService.getVariables(taskId, variableNames); // 使用任务对象id,获取流程变量的值，通过设置流程变量的名称存放到集合里面，然后获取对应的返回值的map
    }

    /**
     * 查询流程变量历史表
     */
    @Test
    public void findHistoryProcessVariables() {
        String processInstanceId = "17501";
        List<HistoricVariableInstance> list = processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
//						.variableName("请假天数")
                .list(); // 针对流程id、任务id等

        if (list != null && list.size() > 0) {
            for (HistoricVariableInstance hvi : list) {
                System.out.println(hvi.getId() + ";" + hvi.getProcessInstanceId() + ";" + hvi.getVariableName() + ";" + hvi.getVariableTypeName() + ";" + hvi.getValue());
                System.out.println("####################################################");

            }
        }
    }
}
