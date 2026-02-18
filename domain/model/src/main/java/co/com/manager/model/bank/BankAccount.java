package co.com.manager.model.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    private String name;
    private String accountNumber;
    private String accountType;
    private String accountHolder;
}
