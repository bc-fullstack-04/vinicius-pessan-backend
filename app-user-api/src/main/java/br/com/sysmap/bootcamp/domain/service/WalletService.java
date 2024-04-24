package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final UsersService usersService;
    private final WalletRepository walletRepository;
    private final PointsCalculatorService pointsCalculatorService;

    public void debit(WalletDto walletDto) {
        Users users = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow();

        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

//        wallet.setPoints(); Aqui deve se implementar o desafio de pontos

        int pointsForToday = pointsCalculatorService.calculatePointsForToday();
        wallet.setPoints(wallet.getPoints() + pointsForToday);

        wallet.setLastUpdate(LocalDateTime.now());

        walletRepository.save(wallet);
    }

    private Users getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return usersService.findByEmail(username);
    }


    public Wallet getWalletByUserId(Long id){
        Users users = usersService.findById(id);
        return this.walletRepository.findByUsers(users).orElseThrow(() -> new RuntimeException("Wallet does not exists on this user"));
    }

    public void creditWallet(WalletDto walletDto){
        Users users = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow(() -> new RuntimeException("User with this wallet was not found"));
        wallet.setBalance(wallet.getBalance().add(walletDto.getValue()));
        walletRepository.save(wallet);
    }

}