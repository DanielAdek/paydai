package com.paydai.api.presentation.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSalesRepDto {
  private UUID srId;
  private double credit;
  private String name;
  private String role;

  public static InvoiceSalesRepDto getInvoiceSalesRepDto(UUID srId, double credit, String name, String role) {
    return new InvoiceSalesRepDto(srId, credit, name, role);
  }
}
