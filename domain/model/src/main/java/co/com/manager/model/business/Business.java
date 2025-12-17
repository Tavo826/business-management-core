package co.com.manager.model.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    private String nit;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String address;
    private String ownerDocumentId;
}
