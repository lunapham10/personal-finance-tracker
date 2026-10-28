package fi.haagahelia.personal_finance_tracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.domain.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAllForTenant(String tenantId) {
        setTenantContext(tenantId);
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> findByIdForTenant(Long id, String tenantId) {
        setTenantContext(tenantId);
        return transactionRepository.findById(id);
    }

    private void setTenantContext(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        entityManager.createNativeQuery(
                "SELECT set_config('app.current_tenant', :tenantId, true)")
                .setParameter("tenantId", tenantId)
                .getSingleResult();
    }
}
