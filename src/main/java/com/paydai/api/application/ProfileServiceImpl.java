package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.BadCredentialException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.EmailType;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.repository.EmailRepository;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.service.ProfileService;
import com.paydai.api.infrastructure.security.JwtAuthService;
import com.paydai.api.presentation.dto.auth.AuthDtoMapper;
import com.paydai.api.presentation.dto.auth.AuthModelDto;
import com.paydai.api.presentation.dto.auth.AuthRecordDto;
import com.paydai.api.presentation.dto.profile.ProfileDtoMapper;
import com.paydai.api.presentation.dto.profile.ProfileRecord;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
import com.paydai.api.presentation.request.ProfileRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
  private final JwtAuthService jwtService;
  private final RoleDtoMapper roleDtoMapper;
  private final EmailRepository emailRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProfileDtoMapper profileDtoMapper;
  private final AuthDtoMapper authenticationDTOMapper;
  private final WorkspaceDtoMapper workspaceDtoMapper;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  @TryCatchException
  public JapiResponse switchWorkspaceProfile(UUID workspaceId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    String jwt = jwtService.generateToken(userModel);

    UserWorkspaceModel userWorkspaceModel = userWorkspaceRepository.findOneByUserId(userModel.getId(), workspaceId);

    if (userWorkspaceModel == null) throw new NotFoundException("workspace id-" + workspaceId);

    RoleRecord role = roleDtoMapper.apply(userWorkspaceModel.getRole());

    WorkspaceRecord workspace = workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace());

    AuthModelDto buildAuthDto = AuthModelDto.getAuthData(userModel, userWorkspaceModel.getEmail(), jwt, role, workspace);

    AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

    return JapiResponse.success(auth);
  }

  @Override
  @TryCatchException
  public UserModel getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (UserModel) authentication.getPrincipal();
  }

  @Override
  @TryCatchException
  public JapiResponse updateProfile(ProfileRequest profileRequest) {
    // Get logged-in user once
    UserModel userModel = getLoggedInUser();

    // Fetch user's personal email
    EmailModel emailModel = userModel.getEmails().stream()
      .filter(email -> email.getEmailType().equals(EmailType.PERSONAL))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Email not found"));

    // Update email if provided
    if (profileRequest.getEmail() != null && !profileRequest.getEmail().isEmpty()) {
      emailModel.setEmail(profileRequest.getEmail());
      emailRepository.save(emailModel);
    }

    // Update password if old and new passwords are provided
    if (profileRequest.getOldPassword() != null && !profileRequest.getOldPassword().isEmpty()) {
      if (!passwordEncoder.matches(profileRequest.getOldPassword(), emailModel.getPasswordHash())) {
        throw new BadCredentialException("Wrong old password");
      }
      String newPasswordHash = passwordEncoder.encode(profileRequest.getNewPassword());
      emailModel.setPasswordHash(newPasswordHash);
      emailRepository.save(emailModel);
    }

    return JapiResponse.success(null);
  }


  @Override
  @TryCatchException
  public JapiResponse getUserProfile() {
    UserModel userModel = getLoggedInUser();
    ProfileRecord profileRecord = profileDtoMapper.apply(userModel);
    return JapiResponse.success(profileRecord);
  }
}
