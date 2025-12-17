package co.com.manager.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("business")
public class BusinessData {

    @Id
    private String nit;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String address;
    private String ownerDocumentId;
}
