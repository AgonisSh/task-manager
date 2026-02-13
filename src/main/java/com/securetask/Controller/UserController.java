package com.securetask.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.UserResponse;
import com.securetask.Service.UserService;


@RestController
@RequestMapping ("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    

    /**
     * @PostMapping
     *              public ResponseEntity<ItemDTO> create(@RequestBody ItemDTO dto)
     *              {
     *              return new ResponseEntity<>(service.create(dto),
     *              HttpStatus.CREATED);
     *              }
     * 
     * @GetMapping
     *             public ResponseEntity<List<ItemDTO>> getAll() {
     *             return ResponseEntity.ok(service.getAll());
     *             }
     * 
     *             @GetMapping("/{id}")
     *             public ResponseEntity<ItemDTO> getById(@PathVariable Long id) {
     *             return ResponseEntity.ok(service.getById(id));
     *             }
     * 
     *             @PutMapping("/{id}")
     *             public ResponseEntity<ItemDTO> update(@PathVariable Long
     *             id,@RequestBody ItemDTO dto) {
     *             return ResponseEntity.ok(service.update(id, dto));
     *             }
     * 
     *             @DeleteMapping("/{id}")
     *             public ResponseEntity<Void> delete(@PathVariable Long id) {
     *             service.delete(id);
     *             return ResponseEntity.noContent().build();
     *             }
     * 
     * 
     */

}
