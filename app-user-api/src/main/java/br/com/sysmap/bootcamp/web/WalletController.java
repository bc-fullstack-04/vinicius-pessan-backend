package br.com.sysmap.bootcamp.web;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Wallet user")
    @GetMapping
    public ResponseEntity<Wallet> getWalletByUserId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(this.walletService.getWalletByUserId(id));
    }

    @Operation(summary = "Credit value in user wallet")
    @PostMapping("/credit/{value}")
    public ResponseEntity<Void> creditWallet(@PathVariable BigDecimal value, @RequestHeader("email") String email){
        WalletDto walletDto = new WalletDto(email, value);
        walletService.creditWallet(walletDto);
        return ResponseEntity.ok().build();
    }

}



