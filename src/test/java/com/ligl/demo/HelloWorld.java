package com.ligl.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 也可使用REST实例自动流程设置，参照：http://www.mossle.com/docs/activiti/index.html#N15324
 * 导入REST架包即可
 */
public class HelloWorld extends OverFlowTest {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署流程定义
     * SELECT * FROM `ACT_RE_DEPLOYMENT`; #部署对象表
     * SELECT * FROM `ACT_RE_PROCDEF`; #流程定义表
     * SELECT * FROM `ACT_GE_BYTEARRAY`; #资源文件表（一个存储xml，一个存储图片）
     * SELECT * FROM `ACT_GE_PROPERTY`; #逐渐生成策略表（与Id相关）
     */
    @Test
    public void deploymentProcessDefinition() {
        Deployment deployment = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
                .createDeployment() // 创建一个部署对象
                .name("helloworld第一个部署程序") // 设置对应流程的名称
                .addClasspathResource("diagrams/helloworld.bpmn") // 从Classpath的资源中加载，一次只能加载一个文件(windows与linux下面要区分)
                .addClasspathResource("diagrams/helloworld.bpmn") // 从Classpath的资源中加载，图片
                .deploy(); // 完成部署
        System.out.println("部署Id=" + deployment.getId()); // 部署Id
        System.out.println("部署名称=" + deployment.getName()); // 部署名称
    }

    /**
     * 启动流程实例
     * SELECT * FROM `ACT_RU_EXECUTION`; #正在执行的执行对象表（正在执行的流程实例）
     * SELECT * FROM `ACT_HI_PROCINST` WHERE `END_TIME_` IS NULL; #流程实例的历史表（一个流程实例）
     * SELECT * FROM `ACT_RU_TASK`; #正在执行的任务表（只有节点是UserTask的才有数据）
     * SELECT * FROM `ACT_RU_IDENTITYLINK` WHERE PROC_INST_ID_ = '5001';#运行时用户关系信息,流程结束即删除
     * SELECT * FROM `ACT_HI_TASKINST`; #任务历史表（只有节点是UserTask的时候该表存在数据）
     * SELECT * FROM `ACT_HI_ACTINST`; #所有活动节点的历史表（其中包括任务也不包括任务）
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "helloworld"; // 使用Key的启动，默认按照对心版本的流程定义启动
        ProcessInstance pi = processEngine.getRuntimeService() // 与正在执行的流程实例和执行对象相关的Service
                .startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的Key启动流程实例，key对应helloworld.bpmn文件中的流程名称

        System.out.println("流程实例Id" + pi.getId()); // 流程实例Id
        System.out.println("流程定义Id" + pi.getProcessDefinitionId()); // 流程定义Id
    }

    /**
     * 查询当前人的个人任务
     * SELECT * FROM `ACT_RU_IDENTITYLINK` #任务表（个人任务、组任务）运行时用户关系信息,流程结束即删除
     * SELECT * FROM `ACT_HI_IDENTITYLINK` #任务历史表历史的流程运行过程中用户关系,权限
     * 按照张三、李四、王五的顺序
     */
    @Test
    public void findMyPersonalTask() {
        String assignee = "张三";
        List<Task> list = processEngine.getTaskService() // 与正在执行的任务管理相关的Service
                .createTaskQuery() // 创建任务查询对象
                // 查询条件
                .taskAssignee(assignee) //制定个人任务查询，指定办理人
                // .taskCandidateUser(assignee)	//某个人查询
                // .taskCandidateGroup("")		//组任务的办理人查询
                // .processDefinitionId("")		//使用流程定义ID查询
                // .processInstanceId("")		//使用流程实例ID查询
                // .executionId(executionId)	//使用执行对象ID查询
                /** 排序 */
                //.orderByTaskCreateTime().asc()	//使用创建时间的升序排列
                // 返回结果集
                // .singleResult() //返回唯一的结果集
                // .count()	//返回结果集的数量
                // .listPage(firstResult, maxResults)	//分页查询
                .list();

        if (list != null && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务Id：" + task.getId());
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
     * SELECT * FROM `ACT_RU_TASK`; #正在执行的任务表（只有节点是UserTask的才有数据）
     * 按照张三、李四、王五的顺序
     */
    @Test
    public void completeMyPersonalTask() {
//		String taskId = "5004";
//		String taskId = "7502";
        String taskId = "10002";
        processEngine.getTaskService()//与正在执行的任务管理相关的Service
                .complete(taskId);
        System.out.println("完成任务：任务Id" + taskId);
    }

    /**
     * 查询流程状态（判断流程正在执行还是结束）
     */
    @Test
    public void isProcessEnd() {
        String processInstanceId = "5001";
        ProcessInstance pi = processEngine.getRuntimeService()
                .createProcessInstanceQuery()// 创建一个流程实例查询
                .processInstanceId(processInstanceId) // 使用流程实例Id查询
                .singleResult();

        // 获取流程实例查询不到（1）
        // 或者获取流程实例历史，查询结束时间ok（2）
        if (pi == null) {
            System.out.println("流程已经结束");
        } else {
            System.out.println("流程没有结束");
        }
    }

    /**
     * 查询历史任务
     * ACT_HI_TASKINST
     */
    @Test
    public void findHistroyTask() {
//		String assignee = "张三";
        String processInstanceId = "5001";
        List<HistoricTaskInstance> list = processEngine.getHistoryService() // 与历史数据相关的Service
                .createHistoricTaskInstanceQuery()// 历史任务表
                .processInstanceId(processInstanceId)
//						.taskAssignee(assignee) // 设置对应人
                .list();

        if (list != null && list.size() > 0) {
            for (HistoricTaskInstance hti : list) {
                System.out.println(hti.getId() + "" + hti.getName() + "" + hti.getProcessInstanceId() + " " + hti.getStartTime()
                        + "" + hti.getEndTime() + "" + hti.getDurationInMillis());
                System.out.println("#################################################");
            }
        }
    }


    /**
     * 查询历史流程实例
     * ACT_HI_PROCINST
     */
    @Test
    public void finHistroyProcessInstance() {
        String processInstanceId = "5001";
        HistoricProcessInstance hpi = processEngine.getHistoryService() //  与历史数据相关的Service
                .createHistoricProcessInstanceQuery() // 创建流程实例查询表
//						.processDefinitionId("helloworld:1:2504")//流程定义ID
                .processInstanceId(processInstanceId)
                .singleResult();
        System.out.println("====" + hpi.getId() + "" + hpi.getProcessDefinitionId() + "" + hpi.getStartTime() + "" + hpi.getEndTime()
                + "" + hpi.getDurationInMillis());
    }

    /**
     * 查询历史活动
     * 问题：HistoricActivityInstance对应哪个表
     * 问题：HistoricActivityInstance和HistoricTaskInstance有什么区别
     */
    @Test
    public void findHisActivitiList() {
        String processInstanceId = "5001";
        List<HistoricActivityInstance> list = processEngine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricActivityInstance hai : list) {
                System.out.println("===" + hai.getId() + "  " + hai.getActivityName());
            }
        }
    }

    /** 涉及到的表总结  START
     #部署对象和流程定义相关表
     #当key值相同的时候版本升级，id未key+版本+随机生成的值
     SELECT * FROM `ACT_RE_DEPLOYMENT`; #部署对象表
     SELECT * FROM `ACT_RE_PROCDEF`; #流程定义表
     SELECT * FROM `ACT_GE_BYTEARRAY`; #资源文件表（一个存储xml，一个存储图片）
     SELECT * FROM `ACT_GE_PROPERTY`; #逐渐生成策略表（与Id相关）
     #####################################################
     #流程实例，执行对象，任务
     SELECT * FROM `ACT_RU_EXECUTION`; #正在执行的执行对象表（正在执行的流程实例）
     SELECT * FROM `ACT_HI_PROCINST` WHERE `END_TIME_` IS NULL; #流程实例的历史表（一个流程实例）
     SELECT * FROM `ACT_RU_TASK`; #正在执行的任务表（只有节点是UserTask的才有数据）
     SELECT * FROM `ACT_RU_IDENTITYLINK` WHERE PROC_INST_ID_ = '5001';#运行时用户关系信息,流程结束即删除
     SELECT * FROM `ACT_HI_TASKINST`; #任务历史表（只有节点是UserTask的时候该表存在数据）
     SELECT * FROM `ACT_HI_ACTINST`; #所有活动节点的历史表（其中包括任务也不包括任务）
     #流程变量
     SELECT * FROM `ACT_HI_TASKINST`;#正在执行的流程变量表
     SELECT * FROM `ACT_HI_VARINST`; #历史流程变量表
     #流程角色
     SELECT * FROM `ACT_ID_GROUP` #角色表
     SELECT * FROM `ACT_ID_USER` #用户表
     SELECT * FROM `ACT_ID_MEMBERSHIP` #用户角色关联表
     END*
     */


}
