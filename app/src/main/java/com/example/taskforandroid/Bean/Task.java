package com.example.taskforandroid.Bean;

public class Task {

    private int id;//事件id
    private String name;//事件名称
    private String content;//事件内容
    private int start_id;//发起人id
    private String start_time;//发起时间
    private String preset_time;//预计时间
    private String execute_people;//执行人
    private String assist_people;//协助人
    private int agree_id;//批准人id
    private String agree_time;//批准时间
    private String finish_time;//完成时间
    private String state;//任务状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStart_id() {
        return start_id;
    }

    public void setStart_id(int start_id) {
        this.start_id = start_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getPreset_time() {
        return preset_time;
    }

    public void setPreset_time(String preset_time) {
        this.preset_time = preset_time;
    }

    public String getExecute_people() {
        return execute_people;
    }

    public void setExecute_people(String execute_people) {
        this.execute_people = execute_people;
    }

    public String getAssist_people() {
        return assist_people;
    }

    public void setAssist_people(String assist_people) {
        this.assist_people = assist_people;
    }

    public int getAgree_id() {
        return agree_id;
    }

    public void setAgree_id(int agree_id) {
        this.agree_id = agree_id;
    }

    public String getAgree_time() {
        return agree_time;
    }

    public void setAgree_time(String agree_time) {
        this.agree_time = agree_time;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getTask_department() {
//        return task_department;
//    }
//
//    public void setTask_department(String task_department) {
//        this.task_department = task_department;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public int getStart_people() {
//        return start_people;
//    }
//
//    public void setStart_people(int start_people) {
//        this.start_people = start_people;
//    }
//
//    public String getStart_people_name() {
//        return start_people_name;
//    }
//
//    public void setStart_people_name(String start_people_name) {
//        this.start_people_name = start_people_name;
//    }
//
//    public String getAssist_people() {
//        return assist_people;
//    }
//
//    public void setAssist_people(String assist_people) {
//        this.assist_people = assist_people;
//    }
//
//    public int getAgree_people() {
//        return agree_people;
//    }
//
//    public void setAgree_people(int agree_people) {
//        this.agree_people = agree_people;
//    }
//
//    public String getAgree_people_name() {
//        return agree_people_name;
//    }
//
//    public void setAgree_people_name(String agree_people_name) {
//
//        if (!"null".equals(agree_people_name)){
//            this.agree_people_name = agree_people_name;
//        }else {
//            this.agree_people_name="无";
//        }
//    }
//
//    public String getTask_start_time() {
//        return task_start_time;
//    }
//
//    public void setTask_start_time(String task_start_time) {
//        this.task_start_time = task_start_time.substring(0,10);
//    }
//
//    public String getTask_execute_time() {
//        return task_execute_time;
//    }
//
//    public void setTask_execute_time(String task_execute_time) {
//        if (!"null".equals(task_execute_time)){
//            this.task_execute_time = task_execute_time.substring(0,10);
//        }else {
//            this.task_execute_time="无";
//        }
//
//    }
//
//    public String getPredict_time() {
//        return predict_time;
//    }
//
//    public void setPredict_time(String predict_time) {
//        this.predict_time = predict_time;
//    }
//
//    public String getTask_state() {
//        return task_state;
//    }
//
//    public void setTask_state(String task_state) {
//        this.task_state = task_state;
//    }


}
