package com.ligl.demo.a0_definition;

import com.ligl.demo.OverFlowTest;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Function:部署流程资源的三种方式
 * Author: created by liguoliang
 * Date: 2017/11/15 0015 下午 3:09
 * Version: 1.0
 */
public class ProcessDefinitionAddHello extends OverFlowTest {
    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署流程定义zip文件
     */
    @Test
    public void deploymentProcessDefinition_zip() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/zip/helloworld.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        Deployment deployment = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
                .createDeployment() // 创建一个部署对象
                .name("zip流程定义") // 添加部署的名称
                .addZipInputStream(zipInputStream) // 制定zip格式文件完成部署
                .deploy(); // 完成部署
        System.out.println("部署Id：" + deployment.getId()); // 部署Id:22501
        System.out.println("部署名称：" + deployment.getName()); // 部署名称:zip流程定义
    }

    /**
     * 查询流程定义 ACT_RE_PROCDEF
     */
    @Test
    public void findProcessDefinition() {
        List<ProcessDefinition> list = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery() // 创建不同的查询不同的表、创建一个查询就能够操作这张表
                /** 制定查询条件，where条件*/
                //.deploymentId(deploymentId)// 使用部署对象Id查询
                //.processDefinitionKey(processDefinitionKey)//使用流程定义的Key进行查询
                //.processDefinitionId(processDefinitionId)// 使用流程定义Id查询
                //.processDefinitionNameLike(processDefinitionNameLike)//路程名称模糊查询
                /** 排序 */
                //.orderByProcessDefinitionVersion().asc()//按照版本升序排列
                //.orderByProcessDefinitionName().desc()//按照流程定义的名称将序排列
                /** 返回的结果集 */
                //.list();// 返回一个集合列表，封装流程定义
                //.singleResult();//返回唯一的结果集
                //.count();// 返回结果集数量
                //.listPage(paramInt1, paramInt2);// 分页查询
                .orderByProcessDefinitionVersion().asc()
                .list();

        if (list != null && list.size() > 0) {
            for (ProcessDefinition pd : list) {
                System.out.println("流程定义Id:" + pd.getId()); // 流程定义的key+版本+随机生成树
                System.out.println("流程定义名称：" + pd.getName()); // 对应helloworld.bpmn文件中的name属性值
                System.out.println("流程定义的Key：" + pd.getKey()); // 对应helloworld.bpmn文件中的id属性值
                System.out.println("流程定义的版本：" + pd.getVersion()); // 对应流程定义的key值相同下，版本升级，默认1
                System.out.println("资源名称bpmn文件：" + pd.getResourceName()); //
                System.out.println("资源名称png文件：" + pd.getDiagramResourceName());
                System.out.println("部署对象Id" + pd.getDeploymentId());
                System.out.println("####################################################");
            }
        }
    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteProcessDefinition() {
        String deploymentId = "70001";
        /**
         * 不带级联的删除
         * 	只能删除没有启动的流程，如果流程启动就会抛出异常
         */
//		processEngine.getRepositoryService()
//						.deleteDeployment(deploymentId);

        /**
         * 级联删除
         * 	不管流程是否启动，都能够删除
         */
        processEngine.getRepositoryService()
                .deleteDeployment(deploymentId, Boolean.TRUE);

        System.out.println("删除成功");
    }

    /**
     * 查看流程图
     */
    @Test
    public void viewPic() {
        String deploymentId = "2501";

        // 获取图片资源
        List<String> list = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);

        // 定义图片资源名称
        String resourceName = "";
        if (list != null && list.size() > 0) {
            for (String name : list) {
                if (name.indexOf(".png") >= 0) {
                    resourceName = name;
                }
            }
        }

        // 获取图片的输入流
        InputStream in = processEngine.getRepositoryService()
                .getResourceAsStream(deploymentId, resourceName);

        // 将图片生成到D盘的目录下
        File file = new File("D:/" + resourceName);
        System.out.println("in:" + in + "to:" + file);
        // 将输入流的图片写到D盘上：工具类
        //FileCopyUtils.copy(in, outFile)
        // TODO 前端获取路径，进行展示
    }


    /**
     * 附加功能，查询最新版本流程定义
     */
    @Test
    public void findLastVersionProcessDefinition() {
        List<ProcessDefinition> list = processEngine.getRepositoryService() //
                .createProcessDefinitionQuery() //
                .orderByProcessDefinitionVersion().asc()// 使用流程定义的版本号做升序排列
                .list();

        /**使用map定义的东西进行转换，获得最后一个版本的值*/

        Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
        if (list != null && list.size() > 0) {
            for (ProcessDefinition pd : list) {
                map.put(pd.getKey(), pd);
            }
        }

        List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());

        if (pdList != null && pdList.size() > 0) {
            for (ProcessDefinition pd : pdList) {
                System.out.println("流程定义Id:" + pd.getId()); // 流程定义的key+版本+随机生成树
                System.out.println("流程定义名称：" + pd.getName()); // 对应helloworld.bpmn文件中的name属性值
                System.out.println("流程定义的Key：" + pd.getKey()); // 对应helloworld.bpmn文件中的id属性值
                System.out.println("流程定义的版本：" + pd.getVersion()); // 对应流程定义的key值相同下，版本升级，默认1
                System.out.println("资源名称bpmn文件：" + pd.getResourceName()); //
                System.out.println("资源名称png文件：" + pd.getDiagramResourceName());
                System.out.println("部署对象Id" + pd.getDeploymentId());
                System.out.println("####################################################");
            }
        }
    }


    /**
     * 附加功能：删除流程定义（删除key相同的所有不同版本的流程定义）
     */
    @Test
    public void deleteProcessDefinitionByKey() {
        String processDefinitionKey = "";
        // 先试用流程定义的Key查询定义，查询出所有版本
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey) // 流程定义id
                .list();

        if (list != null && list.size() > 0) {
            for (ProcessDefinition pd : list) {
                String deploymentId = pd.getDeploymentId();
                processEngine.getRepositoryService().deleteDeployment(deploymentId, Boolean.TRUE);
            }
        }

        System.out.println("通过key进行删除");
    }
}
