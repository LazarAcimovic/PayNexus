package cryptoWallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Integer> {
    
    CryptoWalletModel findByEmail(String email);
    
    
    
    void deleteByEmail(String email);
}