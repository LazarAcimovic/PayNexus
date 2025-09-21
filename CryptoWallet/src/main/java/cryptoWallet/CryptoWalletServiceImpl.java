package cryptoWallet;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import api.dtos.CryptoWalletDto;
import api.dtos.OperationResponseDto;
import api.dtos.UserDto;
import api.proxies.UserProxy;
import api.services.CryptoWalletService;
import util.exceptions.ConflictException;
import util.exceptions.NoDataFoundException;



@RequestMapping("/crypto-wallets")
@RestController
public class CryptoWalletServiceImpl implements CryptoWalletService {

    @Autowired
    private CryptoWalletRepository repo;
    
    @Autowired
    private UserProxy usersProxy;

    @Override
    public ResponseEntity<?> createCryptoWallet(CryptoWalletDto dto) {
        ResponseEntity<UserDto> userResponse = usersProxy.getUserByEmail(dto.getEmail());
        
        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new NoDataFoundException("User with email " + dto.getEmail() + " not found.");
        }
        
        if (repo.findByEmail(dto.getEmail()) != null) {
            throw new ConflictException("Crypto wallet for this user already exists.");
        }
        
        CryptoWalletModel newWallet = new CryptoWalletModel(dto.getEmail());
        
        repo.save(newWallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertModelToDto(newWallet));
    }

    @Override
    public ResponseEntity<?> updateCryptoWallet(CryptoWalletDto dto) {
        CryptoWalletModel existingWallet = repo.findByEmail(dto.getEmail());
        if (existingWallet == null) {
        	throw new NoDataFoundException("Crypto wallet for this user does not exist.", null);
        }
        
        existingWallet.setBTC(dto.getBTC());
        existingWallet.setETH(dto.getETH());
        existingWallet.setXRP(dto.getXRP());
        existingWallet.setLTC(dto.getLTC());
        
        repo.save(existingWallet);
        return ResponseEntity.ok(convertModelToDto(existingWallet));
    }

    @Override
    public ResponseEntity<?> deleteCryptoWallet(String email) {
        CryptoWalletModel wallet = repo.findByEmail(email);
        if (wallet == null) {
        	throw new NoDataFoundException("Crypto wallet for this user does not exist.", null);
        }
        
        repo.delete(wallet);
        return ResponseEntity.ok(new OperationResponseDto("Crypto wallet for " + email + " successfully deleted."));
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
            throw new NoDataFoundException("Crypto wallet for this user does not exist.", null);
        }
        return ResponseEntity.ok(convertModelToDto(wallet));
    }

    private CryptoWalletDto convertModelToDto(CryptoWalletModel model) {
        return new CryptoWalletDto(
            model.getEmail(),
            model.getBTC(),
            model.getETH(),
            model.getXRP(),
            model.getLTC()
        );
    }
}