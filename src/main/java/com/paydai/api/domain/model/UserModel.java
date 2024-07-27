package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tbl")
public class UserModel implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "user_type")
  @Enumerated(EnumType.STRING)
  private UserType userType;

  @Column(name = "stripe_id")
  private String stripeId;

  @Column(name = "stripe_email")
  private String stripeEmail;

  @Column(name = "extra_field")
  private String extraField;

  @Column(name = "merchant_fee")
  private Float merchantFee; // default debit 1.5

  @Column(name = "sales_rep_fee")
  private Float salesRepFee; // default to 0.5

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<EmailModel> emails;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<UserWorkspaceModel> userWorkspaces;

  @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private WorkspaceModel workspace;

//  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return userWorkspaces.stream()
//      .map(UserWorkspaceModel::getRole)
//      .flatMap(role -> role.getPermissions().stream())
//      .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//      .collect(Collectors.toSet());
//  }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      String authority = (userType != null) ? userType.name() : UserType.SALES_REP.name();
      return List.of(new SimpleGrantedAuthority(authority));
    }

  @Override
  public String getUsername() { return String.valueOf(id); }

  @Override
  public String getPassword() {
    // Return password from one of the associated emails
    return emails.stream()
      .filter(email -> email.getPasswordHash() != null)
      .findFirst()
      .map(email -> email.getPasswordHash())
      .orElse(null);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
