package bankAccount;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;


public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer> {

    BankAccountModel findByEmail(String email);

    @Modifying
    @Transactional
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccountModel b SET b.usd = ?2, b.eur = ?3, b.gbp = ?4, b.chf = ?5, b.rsd = ?6 WHERE b.email = ?1")
    void updateAllBalances(String email, BigDecimal usd, BigDecimal eur, BigDecimal gbp, BigDecimal chf, BigDecimal rsd);
}