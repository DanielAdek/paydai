package com.paydai.api.application;

import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.repository.WorkspaceRepository;
import com.paydai.api.domain.service.WorkspaceService;
import com.paydai.api.presentation.dto.profile.ProfileDtoMapper;
import com.paydai.api.presentation.dto.profile.ProfileRecord;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.userWorkspace.UserWorkspaceRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
  private final WorkspaceRepository repository;
  private final WorkspaceDtoMapper workspaceDtoMapper;
  private final UserWorkspaceRepository userWorkspaceRepository;
  private final ProfileDtoMapper profileDtoMapper;
  private final RoleDtoMapper roleDtoMapper;

  @Override
  public JapiResponse getWorkspace() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel userModel = (UserModel) authentication.getPrincipal();

      WorkspaceModel workspaceModel = repository.findByUserId(userModel.getId());

      if (workspaceModel == null) throw new NotFoundException("Workspace not exiting");

      WorkspaceRecord workspaceRecord = workspaceDtoMapper.apply(workspaceModel);

      return JapiResponse.success(workspaceRecord);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse createWorkspace(WorkspaceRequest payload) {
    try {
      // Get the authenticated user creating workspace;
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel user = (UserModel) authentication.getPrincipal();

      // find the workspace if already existing
      WorkspaceModel workspaceModel = repository.findByName(payload.getName().toLowerCase().trim());

      // check if workspace already created
      if (workspaceModel != null) throw new ConflictException("Workspace in use");

      // Build the workspace object to persist
      WorkspaceModel buildWorkspace = WorkspaceModel.builder().owner(user).name(payload.getName().toLowerCase().trim()).build();

      // Persist the workspace created data
      WorkspaceModel newWorkspace = repository.save(buildWorkspace);

      return JapiResponse.success(newWorkspace);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getSalesRepWorkspaces() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      UserModel user = (UserModel) authentication.getPrincipal();

      List<UserWorkspaceModel> userWorkspaceModels = userWorkspaceRepository.findByUserId(user.getId());

      List<WorkspaceRecord> workspaces = userWorkspaceModels
        .stream()
        .map(userWorkspaceModel -> workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace()))
        .collect(Collectors.toList());

      return JapiResponse.success(workspaces);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse getWorkspaceSalesReps(UUID workspaceId, UUID roleId) {
    try {
      List<UserWorkspaceModel> userWorkspaceModels = userWorkspaceRepository.findUsersByWorkspaceId(workspaceId, roleId);
      List<ProfileRecord> userWorkspaceRecords = userWorkspaceModels
        .stream()
        .map(userWorkspaceModel -> profileDtoMapper.apply(userWorkspaceModel.getUser()))
        .collect(Collectors.toList());
      return JapiResponse.success(userWorkspaceRecords);
    } catch (Exception e) { throw e; }
  }
}
