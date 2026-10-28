package fi.haagahelia.personal_finance_tracker.web;

import fi.haagahelia.personal_finance_tracker.domain.Transaction;
import fi.haagahelia.personal_finance_tracker.security.OpaAuthorizationService;
import fi.haagahelia.personal_finance_tracker.service.TransactionService;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final OpaAuthorizationService opaAuthorizationService;

    public TransactionRestController(
            TransactionService transactionService,
            OpaAuthorizationService opaAuthorizationService) {
        this.transactionService = transactionService;
        this.opaAuthorizationService = opaAuthorizationService;
    }

    @GetMapping
    public List<Transaction> getTransactions(
            @AuthenticationPrincipal Jwt jwt) {

        String subjectId = jwt.getSubject();
        String subjectTenantId = jwt.getClaimAsString("tenant_id");

        if (subjectTenantId == null || subjectTenantId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Missing tenant context");
        }

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        @SuppressWarnings("unchecked")
        List<String> roles = realmAccess != null
                ? (List<String>) realmAccess.getOrDefault("roles", List.of())
                : List.of();

        return transactionService.findAllForTenant(subjectTenantId).stream()
                .filter(transaction -> {
                    String resourceTenantId = transaction.getUser().getTenantId();

                    if (resourceTenantId == null || resourceTenantId.isBlank()) {
                        return false;
                    }

                    Map<String, Object> policyInput = Map.of(
                            "subject", Map.of(
                                    "id", subjectId,
                                    "tenant_id", subjectTenantId,
                                    "roles", roles),
                            "action", "READ",
                            "resource", Map.of(
                                    "type", "TRANSACTION",
                                    "id", transaction.getId().toString(),
                                    "tenant_id", resourceTenantId));

                    return opaAuthorizationService.isAllowed(policyInput);
                })
                .toList();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String subjectId = jwt.getSubject();
        String subjectTenantId = jwt.getClaimAsString("tenant_id");

        if (subjectTenantId == null || subjectTenantId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Missing tenant context");
        }

        Transaction transaction = transactionService
                .findByIdForTenant(id, subjectTenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found"));

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        @SuppressWarnings("unchecked")
        List<String> roles = realmAccess != null
                ? (List<String>) realmAccess.getOrDefault("roles", List.of())
                : List.of();

        String resourceTenantId = transaction.getUser().getTenantId();

        if (resourceTenantId == null || resourceTenantId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Resource tenant context unavailable");
        }

        Map<String, Object> policyInput = Map.of(
                "subject", Map.of(
                        "id", subjectId,
                        "tenant_id", subjectTenantId,
                        "roles", roles),
                "action", "READ",
                "resource", Map.of(
                        "type", "TRANSACTION",
                        "id", id.toString(),
                        "tenant_id", resourceTenantId));

        boolean allowed = opaAuthorizationService.isAllowed(policyInput);

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied by authorization policy");
        }

        return transaction;
    }
}
