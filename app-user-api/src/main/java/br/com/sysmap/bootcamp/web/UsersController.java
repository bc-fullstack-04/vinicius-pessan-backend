package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;


    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> allUsers = usersService.findAllUsers();
        return ResponseEntity.ok(allUsers) ;
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        Users user = usersService.findById(id);
        return ResponseEntity.ok(user);
    }


    @Operation(summary = "Create user")
    @PostMapping("/create")
    public ResponseEntity<Users> save(@RequestBody Users user) {
        return ResponseEntity.ok(this.usersService.save(user));
    }

    @Operation(summary = "Update user")
    @PutMapping("/update")
    public ResponseEntity<Users> update(@RequestParam("id") Long id, @RequestBody Users user) {
        return ResponseEntity.ok(this.usersService.update(id, user));
    }

    @Operation(summary = "Authenticate user")
    @PostMapping("/auth")
    public ResponseEntity<AuthDto> auth(@RequestBody AuthDto user) {
        return ResponseEntity.ok(this.usersService.auth(user));
    }


}