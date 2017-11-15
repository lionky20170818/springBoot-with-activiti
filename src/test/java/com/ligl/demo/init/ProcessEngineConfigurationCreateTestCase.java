package com.ligl.demo.init;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Assert;
import org.junit.Test;

public class ProcessEngineConfigurationCreateTestCase {

    /**
     * 使用代码创建工作流需要的23张表
     */
    @Test
    public void createTable() {
        // 获取流程引擎配置(创建一个单例子的流程引擎)
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration();
        // 设置数据库信息
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/fsop?useUnicode=true&characterEndocing=utf8");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("root");
        /**
         *  processEngineConfiguration 中的配置
         *  DB_SCHEMA_UPDATE_FALSE = "false"; // 不能自动创建表（能够使用脚本创建）
         *	DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop"; // 先删除再更新
         *  DB_SCHEMA_UPDATE_TRUE = "true"; // 如果表不存在我就自动创建表
         */
        // 设置数据库操作的设置
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取工作流的核心对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        Assert.assertNotNull(processEngine);
    }

    /**
     * 使用配置文件创建流程引擎
     */
    @Test
    public void createByConfig() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml")
                .buildProcessEngine();
        Assert.assertNotNull(processEngine);
    }

    /**
     * 使用默认的配置获取工作流
     */
    @Test
    public void defaultInit() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Assert.assertNotNull(processEngine);
    }
}
