package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionQueryUseCasesTest {
    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository(
            new Transaction("Mercado do bairro", 1000, Category.GROCERIES),
            new Transaction("Farmácia", 2500, Category.PHARMA));

    @Test
    void shouldListAllTransactions() {
        var result = new ListTransactionsUseCase(repository).execute();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("description")
                .containsExactly("Mercado do bairro", "Farmácia");
    }

    @Test
    void shouldSearchTransactionsByDescriptionIgnoringCaseAndWhitespace() {
        var result = new SearchTransactionsUseCase(repository).execute("  MERCADO ");

        assertThat(result).singleElement()
                .extracting("description")
                .isEqualTo("Mercado do bairro");
    }

    @Test
    void shouldCalculateFinancialSummaryInCentsAndReais() {
        var result = new GetFinancialSummaryUseCase(repository).execute();

        assertThat(result.transactionCount()).isEqualTo(2);
        assertThat(result.totalAmountInCents()).isEqualTo(3500);
        assertThat(result.totalAmount()).isEqualTo(35.0);
        assertThat(result.averageAmount()).isEqualTo(17.5);
    }

    private static final class InMemoryTransactionRepository implements TransactionRepository {
        private final List<Transaction> transactions;

        private InMemoryTransactionRepository(Transaction... transactions) {
            this.transactions = new ArrayList<>(List.of(transactions));
        }

        @Override
        public Transaction save(Transaction transaction) {
            transactions.add(transaction);
            return transaction;
        }

        @Override
        public List<Transaction> findAll() {
            return List.copyOf(transactions);
        }

        @Override
        public List<Transaction> findAllByCategory(Category category) {
            return transactions.stream()
                    .filter(transaction -> transaction.getCategory() == category)
                    .toList();
        }

        @Override
        public List<Transaction> findAllByDescriptionContainingIgnoreCase(String description) {
            var normalizedDescription = description.toLowerCase();
            return transactions.stream()
                    .filter(transaction -> transaction.getDescription().toLowerCase().contains(normalizedDescription))
                    .toList();
        }
    }
}
