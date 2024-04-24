package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsValid(){
        Users users = Users.builder().id(1L).name("test").email("test").password("test").build();
        when(usersRepository.save(any(Users.class))).thenReturn(users);

        Users savedUsers = usersService.save(users);

        assertEquals(users, savedUsers);
    }

    @Test
    @DisplayName("Should return all users when users are found")
    public void shouldReturnAllUsersWhenUsersFound() {


        List<Users> usersList = new ArrayList<>();
        usersList.add(Users.builder().id(1L).name("test1").email("test1@example.com").password("pass1").build());
        usersList.add(Users.builder().id(2L).name("test2").email("test2@example.com").password("pass2").build());
        when(usersRepository.findAll()).thenReturn(usersList);


        List<Users> allUsers = usersService.findAllUsers();

        assertEquals(usersList.size(), allUsers.size());
        assertEquals(usersList.get(0), allUsers.get(0));
        assertEquals(usersList.get(1), allUsers.get(1));
    }

    @Test
    @DisplayName("Should return user when user is found by id")
    public void shouldReturnUserWhenUserIsFoundById() {
        Long userId = 1L;
        Users user = Users.builder().id(userId).name("test").email("test@example.com").password("password").build();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        Users foundUser = usersService.findById(userId);

        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("Should update user successfully")
    public void shouldUpdateUserSuccessfully() {

        Long userId = 1L;
        Users existingUser = Users.builder()
                .id(userId)
                .name("existing")
                .email("existing@example.com")
                .password("existingPassword")
                .build();
        Users updatedUser = Users.builder()
                .id(userId)
                .name("updated")
                .email("updated@example.com")
                .password("updatedPassword")
                .build();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usersRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("encodedUpdatedPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(updatedUser);

        Users result = usersService.update(userId, updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals("encodedUpdatedPassword", result.getPassword());
        verify(usersRepository, times(1)).findById(userId);
        verify(usersRepository, times(1)).findByEmail(updatedUser.getEmail());
        verify(usersRepository, times(1)).save(any(Users.class));
    }


    @Test
    @DisplayName("Should load user by username")
    public void shouldLoadUserByUsername() {
        String email = "test@example.com";
        Users user = Users.builder().id(1L).name("test").email(email).password("password").build();
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = usersService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(new ArrayList<>(), new ArrayList<>(userDetails.getAuthorities()));
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    public void shouldAuthenticateUserSuccessfully() {
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Long userId = 1L;

        Users user = Users.builder().id(userId).email(email).password(encodedPassword).build();
        AuthDto authDto = AuthDto.builder().email(email).password(password).build();

        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        AuthDto result = usersService.auth(authDto);

        assertEquals(email, result.getEmail());
        assertEquals(userId, result.getId());

        String expectedToken = Base64.getEncoder().withoutPadding().encodeToString((email + ":" + encodedPassword).getBytes());
        assertEquals(expectedToken, result.getToken());
    }


}
