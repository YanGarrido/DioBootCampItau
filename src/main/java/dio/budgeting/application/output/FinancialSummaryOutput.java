package dio.budgeting.application.output;

public record FinancialSummaryOutput(
        int transactionCount,
        long totalAmountInCents,
        double totalAmount,
        double averageAmount) {
}
