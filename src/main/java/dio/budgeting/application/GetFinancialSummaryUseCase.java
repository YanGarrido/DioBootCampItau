package dio.budgeting.application;

import dio.budgeting.application.output.FinancialSummaryOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GetFinancialSummaryUseCase {
    private final TransactionRepository transactionRepository;

    public GetFinancialSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-financial-summary", description = "Calcula um resumo das transações financeiras, com quantidade, total e média")
    public FinancialSummaryOutput execute() {
        var transactions = transactionRepository.findAll();
        var totalAmountInCents = transactions.stream()
                .mapToLong(transaction -> transaction.getAmount())
                .sum();
        var transactionCount = transactions.size();
        var averageAmount = transactionCount == 0
                ? 0.0
                : BigDecimal.valueOf(totalAmountInCents)
                        .divide(BigDecimal.valueOf(transactionCount), 2, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        .doubleValue();

        return new FinancialSummaryOutput(
                transactionCount,
                totalAmountInCents,
                centsToAmount(totalAmountInCents),
                averageAmount);
    }

    private double centsToAmount(long cents) {
        return BigDecimal.valueOf(cents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
