package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchTransactionsUseCase {
    private final TransactionRepository transactionRepository;

    public SearchTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "search-transactions", description = "Busca transações financeiras pela descrição")
    public List<TransactionOutput> execute(
            @ToolParam(description = "Texto para procurar na descrição da transação") String description) {
        if (description == null || description.isBlank()) {
            return List.of();
        }

        return transactionRepository.findAllByDescriptionContainingIgnoreCase(description.trim()).stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
