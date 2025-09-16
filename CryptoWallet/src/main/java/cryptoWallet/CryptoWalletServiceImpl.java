package cryptoWallet;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoWalletDto;
import api.services.CryptoWalletService;



@RequestMapping("/crypto-wallets")
@RestController
public class CryptoWalletServiceImpl implements CryptoWalletService {

    @Autowired
    private CryptoWalletRepository repo;

    @Override
    public ResponseEntity<?> createCryptoWallet(CryptoWalletDto dto) {
        if (repo.findByEmail(dto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Crypto wallet for this user already exists.");
        }
        
        CryptoWalletModel newWallet = new CryptoWalletModel(dto.getEmail());
        repo.save(newWallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertModelToDto(newWallet));
    }

    @Override
    public ResponseEntity<?> updateCryptoWallet(CryptoWalletDto dto) {
        CryptoWalletModel existingWallet = repo.findByEmail(dto.getEmail());
        if (existingWallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet for this user does not exist.");
        }
        
        existingWallet.setBtc(dto.getBtc());
        existingWallet.setEth(dto.getEth());
        existingWallet.setXrp(dto.getXrp());
        existingWallet.setLtc(dto.getLtc());
        
        repo.save(existingWallet);
        return ResponseEntity.ok(convertModelToDto(existingWallet));
    }

    @Override
    public ResponseEntity<?> deleteCryptoWallet(String email) {
        CryptoWalletModel wallet = repo.findByEmail(email);
        if (wallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet for this user does not exist.");
        }
        
        repo.delete(wallet);
        return ResponseEntity.ok("Crypto wallet for " + email + " successfully deleted.");
    }

    @Override
    public ResponseEntity<List<CryptoWalletDto>> getAllCryptoWallets() {
        List<CryptoWalletModel> wallets = repo.findAll();
        List<CryptoWalletDto> dtos = wallets.stream()
            .map(this::convertModelToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @Override
    public ResponseEntity<?> getCryptoWalletByEmail(String userEmail) {
        CryptoWalletModel wallet = repo.findByEmail(userEmail);
        if (wallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet for this user does not exist.");
        }
        return ResponseEntity.ok(convertModelToDto(wallet));
    }

    private CryptoWalletDto convertModelToDto(CryptoWalletModel model) {
        return new CryptoWalletDto(
            model.getEmail(),
            model.getBtc(),
            model.getEth(),
            model.getXrp(),
            model.getLtc()
        );
    }
}