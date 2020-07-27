package com.example.taskforandroid.Bean;

public class TaskAndUser {

    private Task task;
    private User user;
    private User user1;

    public TaskAndUser(){
        task = new Task();
        user = new User();
        user1 = new User();
    }


    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }
}
