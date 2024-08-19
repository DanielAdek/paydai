package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.service.ProfileService;
import com.paydai.api.infrastructure.security.JwtAuthService;
import com.paydai.api.presentation.dto.auth.AuthDtoMapper;
import com.paydai.api.presentation.dto.auth.AuthModelDto;
import com.paydai.api.presentation.dto.auth.AuthRecordDto;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
  private final JwtAuthService jwtService;
  private final RoleDtoMapper roleDtoMapper;
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

    RoleRecord role = userWorkspaceModel != null ? roleDtoMapper.apply(userWorkspaceModel.getRole()) : null;

    WorkspaceRecord workspace = userWorkspaceModel != null ? workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace()) : null;

    AuthModelDto buildAuthDto = AuthModelDto.getAuthData(userModel, Objects.requireNonNull(userWorkspaceModel).getEmail(), jwt, role, workspace);

    AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

    return JapiResponse.success(auth);
  }
}
