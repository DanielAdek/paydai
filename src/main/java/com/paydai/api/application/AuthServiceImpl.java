package com.paydai.api.application;

import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.InternalServerException;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
  private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Override
  public JapiResponse create(RegisterRequest payload) {
    try {
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

      if (payload.getUserType().equals(UserType.MERCHANT)) workspaceRepository.save(WorkspaceModel.builder().name(payload.getBusiness().trim().toLowerCase()).owner(userModel).build());

      // Generate token
      String token = jwtService.generateToken(userModel);

      // Build data response to send to client
      AuthModelDto buildAuthDto = AuthModelDto.getAuthData(userModel, emailModel, token, null, null);

      AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

      //Todo: SEND WELCOME NOTIFICATION

      return JapiResponse.builder().status(true).message("Success!").statusCode(HttpStatus.CREATED).data(auth).build();
    } catch (ConflictException e) { throw e; } catch (Exception ex) {
      logger.info("An error occurred: {} ", ex.getMessage());
      throw new InternalServerException(ex.getMessage(), ex);
    }
  }

  @Override
  public JapiResponse authenticate(AuthRequest authCred) {
    try {
      EmailModel emailModel = emailRepository.findEmailQuery(authCred.getEmail());

      if (emailModel == null) throw new NotFoundException("Email not found");

      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailModel.getUser().getId(), authCred.getPassword()));

      String jwt = jwtService.generateToken(emailModel.getUser());

      // add role and permission to user
      UserWorkspaceModel userWorkspaceModel = userWorkspaceRepository.findUserWorkspaceRole(emailModel.getUser().getId());

      RoleRecord role = userWorkspaceModel != null ? roleDtoMapper.apply(userWorkspaceModel.getRole()) : null;

      WorkspaceRecord workspace = userWorkspaceModel != null ? workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace()) : null;

      AuthModelDto buildAuthDto = AuthModelDto.getAuthData(emailModel.getUser(), emailModel, jwt, role, workspace);

      AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

      return JapiResponse.builder().status(true).statusCode(HttpStatus.OK).message("Success!").data(auth).build();
    } catch (BadCredentialsException e) {
      throw new ApiRequestException(e.getMessage(), e);
    } catch (Exception e) {
      throw new InternalServerException(e.getMessage());
    }
  }
}