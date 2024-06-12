package com.paydai.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tbl")
public class UserModel implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "user_type")
  private String userType;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user")
  private List<EmailModel> emails;

  @OneToMany(mappedBy = "user")
  private List<PasswordModel> passwords;

  @OneToMany(mappedBy = "user")
  private List<UserWorkspaceModel> userWorkspaces;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userWorkspaces.stream()
      .map(UserWorkspaceModel::getRole)
      .flatMap(role -> role.getPermissions().stream())
      .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
      .collect(Collectors.toSet());
  }

  @Override
  public String getUsername() { return String.valueOf(userId); }

  @Override
  public String getPassword() {
    return passwords.isEmpty() ? null : passwords.get(0).getPasswordHash();
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
