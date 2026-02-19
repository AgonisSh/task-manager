package com.securetask.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {


    @GetMapping()
    public String getAll() {
        return "coucou v2 ";
    }
    
    
}
