package com.paydai.api.application.constant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookConstant {
  // INVOICE WEBHOOK EVENTS
  public final String invoice_created = "invoice.created";
  public final String invoice_finalize_failed = "invoice.finalization_failed";
  public final String invoice_finalize = "invoice.finalized";
  public final String invoice_paid = "invoice.paid";
  public final String invoice_payment_failed = "invoice.payment_failed";
  public final String invoice_payment_succeeded = "invoice.payment_succeeded";
  public final String invoice_sent = "invoice.sent";
  public final String invoice_updated = "invoice.updated";


  // TRANSFER WEBHOOK EVENTS
  public final String transfer_created = "transfer.created";
  public final String transfer_reversed = "transfer.reversed";

  // BALANCE AVAILABLE
  public final String balance = "balance.available";
}
