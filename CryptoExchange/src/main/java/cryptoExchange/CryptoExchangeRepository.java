package cryptoExchange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CryptoExchangeRepository extends JpaRepository<CryptoExchangeModel, Integer> {
    
    CryptoExchangeModel findByFromAndTo(String from, String to);
}