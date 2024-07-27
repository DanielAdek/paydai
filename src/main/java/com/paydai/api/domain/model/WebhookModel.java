package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhook_tbl")
public class WebhookModel {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String url;

  private String event;

  private String reponseJson;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  //  @ElementCollection
  //  @CollectionTable(name = "webhook_events", joinColumns = @JoinColumn(name = "webhook_id"))
  //  @Column(name = "event")
  //  private List<String> events;
}
