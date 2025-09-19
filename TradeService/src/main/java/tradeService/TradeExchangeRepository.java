package tradeService;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeExchangeRepository extends JpaRepository<TradeExchangeModel, Integer> {

    TradeExchangeModel findByFromAndTo(String from, String to);
}