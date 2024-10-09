package com.example.takehome.utils;

import com.example.takehome.entity.StatusEnum;
import com.example.takehome.entity.Task;
import com.example.takehome.entity.User;
import com.example.takehome.models.TaskDTO;
import com.example.takehome.models.UserDTO;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeParseException;

import java.time.LocalDate;

@Component
public class MappingHelper {

    public UserDTO returnUserdto(User user){

        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dob(user.getDob().toString())
                .build();

        return dto;
    }

    public User getUser(UserDTO dto) throws DateTimeParseException {
       User u  = new User();
       u.setDob(LocalDate.parse(dto.getDob()));
       u.setFirstName(dto.getFirstName());
       u.setEmail(dto.getEmail());
       u.setLastName(dto.getLastName());
       return u;
    }

    public TaskDTO returnTaskdto(Task task)
    {
        TaskDTO dto = TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate().toString())
                .status(task.getStatus().name())
                .build();

        return dto;
    }

    public Task getTask(TaskDTO dto) throws IllegalArgumentException, DateTimeParseException{

        Task task = new Task();
        task.setDescription(dto.getDescription());
        task.setTitle(dto.getTitle());
        task.setDueDate(LocalDate.parse(dto.getDueDate()));

       StatusEnum statusEnum =  StatusEnum.pickOption(dto.getStatus());
        task.setStatus(statusEnum);

        return task;
    }

}