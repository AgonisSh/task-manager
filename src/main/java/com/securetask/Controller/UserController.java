package com.securetask.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
