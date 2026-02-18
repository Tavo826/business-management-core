package co.com.manager.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountData {

    private String name;
    private String accountNumber;
    private String accountType;
    private String accountHolder;
}
