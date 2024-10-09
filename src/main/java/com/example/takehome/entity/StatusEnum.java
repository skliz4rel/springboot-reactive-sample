package com.example.takehome.entity;

import jdk.jshell.Snippet;
import lombok.Getter;

@Getter
public enum StatusEnum {

    TODO("TODO"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private String status;

    StatusEnum(String status){
        this.status = status;
    }

    /***
     *
     * @param option would be used to recieve the option supplied by the user when creating a new task
     * @return This would return the valid option if it is in the list.
     *
     * If the option is not found. An illegal Argument exception would be returned.
     */
    public static StatusEnum pickOption(String option){

        for(StatusEnum item : values()){
            if(item.getStatus().equalsIgnoreCase(option))
                return item;
        }

        throw new IllegalArgumentException("User supplied an illegal status for a Task. Valid list [Todo, In_progress, done]");
    }

}
