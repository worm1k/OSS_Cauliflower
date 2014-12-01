package com.naukma.cauliflower.entities;

import com.naukma.cauliflower.dao.TaskName;

import java.io.Serializable;

/**
 * Created by Max on 27.11.2014.
 */

public class Task implements Serializable {
    private int taskId;
    private int userRoleId;
    private int serviceOrderId;
    private int taskStatusId;
    private String taskStatus;
    private TaskName taskName;

    public Task(int taskId, int userRoleId, int serviceOrderId, int taskStatusId, String taskStatus, TaskName taskName) {
        this.taskId = taskId;
        this.userRoleId = userRoleId;
        this.serviceOrderId = serviceOrderId;
        this.taskStatusId = taskStatusId;
        this.taskStatus = taskStatus;
        this.taskName = taskName;
    }


    public int getTaskId() {
        return taskId;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public int getServiceOrderId() {
        return serviceOrderId;
    }

    public int getTaskStatusId() {
        return taskStatusId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public TaskName getTaskName() {
        return taskName;
    }
}
