package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user) {

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if (usersOptional.isPresent()){
            throw new RuntimeException("User already exists");
        }

        user = user.toBuilder().password(this.passwordEncoder.encode(user.getPassword())).build();


        //Criar wallet para usuário

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(10000));
        wallet.setPoints(0L);
        wallet.setLastUpdate(LocalDateTime.now());

        wallet.setUsers(user);

        walletRepository.save(wallet);

        log.info("Saving user: {}", user);
        return this.usersRepository.save(user);
    }



    public List<Users> findAllUsers() {
        List<Users> allUsers = usersRepository.findAll();
        if (allUsers.isEmpty()) {
            throw new RuntimeException("No users found");
        }
        return allUsers;
    }

    public Users findById(Long id) {
        return this.usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }



    public Users update(Long id, Users user){
        Users existingUser = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User does not exist"));

        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if (usersOptional.isPresent() && !usersOptional.get().getId().equals(id)){
            throw new RuntimeException("User with email already exists");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());

        return this.usersRepository.save(existingUser);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public Users findByEmail(String email){
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }



    public AuthDto auth(AuthDto authDto){
        Users users =  this.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());
        AuthDto userDetails = AuthDto.builder().email(users.getEmail()).password(password.toString()).build();

        return AuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(users.getId()).build();
    }





}