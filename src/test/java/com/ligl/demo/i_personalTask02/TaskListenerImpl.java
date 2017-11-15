package com.ligl.demo.i_personalTask02;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskListenerImpl implements TaskListener {

    private static final long serialVersionUID = -7596650939441567109L;

    /**
     * 用来指定任务的办理人
     */
    public void notify(DelegateTask delegateTask) {
        // 指定个人任务的办理人、也可以指定组任务的班里人
        delegateTask.setAssignee("灭绝师太");

        // 可以在用户表里面具有用户关系，然后查询后进行设置
    }

}
