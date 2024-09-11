package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.*;
import com.paydai.api.domain.service.AuthService;
import com.paydai.api.infrastructure.security.JwtAuthService;
import com.paydai.api.presentation.dto.auth.AuthDtoMapper;
import com.paydai.api.presentation.dto.auth.AuthModelDto;
import com.paydai.api.presentation.dto.auth.AuthRecordDto;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
import com.paydai.api.presentation.request.AuthRequest;
import com.paydai.api.presentation.request.RegisterRequest;
import com.paydai.api.presentation.response.JapiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository repository;
  private final JwtAuthService jwtService;
  private final RoleDtoMapper roleDtoMapper;
  private final EmailRepository emailRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthDtoMapper authenticationDTOMapper;
  private final WorkspaceDtoMapper workspaceDtoMapper;
  private final WorkspaceRepository workspaceRepository;
  private final AuthenticationManager authenticationManager;
  private final UserWorkspaceRepository userWorkspaceRepository;
  private final AccountLedgerRepository accountLedgerRepository;

  @Override
  @TryCatchException
  @Transactional
  public JapiResponse create(RegisterRequest payload) {
    // Check if email already exist
    EmailModel email = emailRepository.findEmailQuery(payload.getEmail());

    if (email != null) throw new ConflictException("Email in use!");

    // Build user data to save
    UserModel buildUser = UserModel.builder().firstName(payload.getFirstName()).lastName(payload.getLastName()).userType(payload.getUserType()).build();

    // Save user
    UserModel userModel = repository.save(buildUser);

    // Build email
    EmailModel buildEmail = EmailModel.builder()
        .email(payload.getEmail())
        .user(userModel)
        .passwordHash(passwordEncoder.encode(payload.getPassword()))
        .emailType(EmailType.PERSONAL)
        .build();

    // Save email
    EmailModel emailModel = emailRepository.save(buildEmail);

    if (payload.getUserType().equals(UserType.MERCHANT)) {
      workspaceRepository.save(
        WorkspaceModel.builder()
          .name(payload.getBusiness().trim().toLowerCase())
          .owner(userModel).build()
      );
    }

    // Generate token
    String token = jwtService.generateToken(userModel);

    // Build data response to send to client
    AuthModelDto buildAuthDto = AuthModelDto.getAuthData(userModel, emailModel, token, null, null);

    AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

    //Todo: SEND WELCOME NOTIFICATION

    return JapiResponse.success(auth);
  }

  @Override
  @TryCatchException
  public JapiResponse authenticate(AuthRequest authCred) throws StripeException {
    EmailModel emailModel = emailRepository.findEmailQuery(authCred.getEmail());

    if (emailModel == null) throw new NotFoundException("Invalid Email or password");

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailModel.getUser().getId(), authCred.getPassword()));

    String jwt = jwtService.generateToken(emailModel.getUser());

    // add role and permission to user
    UserWorkspaceModel userWorkspaceModel;
    RoleRecord role = null;
    WorkspaceRecord workspace = null;
    if (emailModel.getEmailType().equals(EmailType.COMPANY)) {
      userWorkspaceModel = userWorkspaceRepository.findUserByEmail(emailModel.getId());
      role = userWorkspaceModel != null ? roleDtoMapper.apply(userWorkspaceModel.getRole()) : null;
      workspace = userWorkspaceModel != null ? workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace()) : null;
    }

    if (emailModel.getUser().getUserType().equals(UserType.MERCHANT)) {
      workspace = workspaceDtoMapper.apply(workspaceRepository.findByUserId(emailModel.getUser().getId()));
      role = new RoleRecord(UUID.randomUUID(), "merchant", LocalDateTime.now(), LocalDateTime.now());
    }

    AuthModelDto buildAuthDto = AuthModelDto.getAuthData(emailModel.getUser(), emailModel, jwt, role, workspace);

    AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

    return JapiResponse.success(auth);
  }
}