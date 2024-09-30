package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.NotFoundException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.CommSettingRepository;
import com.paydai.api.domain.repository.TeamRepository;
import com.paydai.api.domain.repository.UserWorkspaceRepository;
import com.paydai.api.domain.repository.WorkspaceRepository;
import com.paydai.api.domain.service.ProfileService;
import com.paydai.api.domain.service.WorkspaceService;
import com.paydai.api.presentation.dto.profile.ProfileDtoMapper;
import com.paydai.api.presentation.dto.profile.ProfileRecord;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.userWorkspace.TeamsDtoMapper;
import com.paydai.api.presentation.dto.userWorkspace.TeamsRecord;
import com.paydai.api.presentation.dto.userWorkspace.UserWorkspaceRecord;
import com.paydai.api.presentation.dto.workspace.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.workspace.WorkspaceRecord;
import com.paydai.api.presentation.request.AssignSalesRepRequest;
import com.paydai.api.presentation.request.WorkspaceRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
  private final TeamsDtoMapper teamsDtoMapper;
  private final TeamRepository teamRepository;
  private final ProfileService profileService;
  private final WorkspaceRepository repository;
  private final ProfileDtoMapper profileDtoMapper;
  private final WorkspaceDtoMapper workspaceDtoMapper;
  private final UserWorkspaceRepository userWorkspaceRepository;

  @Override
  @TryCatchException
  public JapiResponse getWorkspace() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel userModel = (UserModel) authentication.getPrincipal();

    WorkspaceModel workspaceModel = repository.findByUserId(userModel.getId());

    if (workspaceModel == null) throw new NotFoundException("Workspace not exiting");

    WorkspaceRecord workspaceRecord = workspaceDtoMapper.apply(workspaceModel);

    return JapiResponse.success(workspaceRecord);
  }

  @Override
  @TryCatchException
  public JapiResponse createWorkspace(WorkspaceRequest payload) {
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
  }

  @Override
  @TryCatchException
  public JapiResponse getSalesRepWorkspaces() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserModel user = (UserModel) authentication.getPrincipal();

    List<UserWorkspaceModel> userWorkspaceModels = userWorkspaceRepository.findByUserId(user.getId());

    List<WorkspaceRecord> workspaces = userWorkspaceModels
      .stream()
      .map(userWorkspaceModel -> workspaceDtoMapper.apply(userWorkspaceModel.getWorkspace()))
      .collect(Collectors.toList());

    return JapiResponse.success(workspaces);
  }

  @Override
  @TryCatchException
  public JapiResponse getWorkspaceSalesReps(UUID workspaceId, Optional<UUID> roleId) {
    List<UserWorkspaceModel> userWorkspaceModels;
    if (roleId.isPresent()) {
      userWorkspaceModels = userWorkspaceRepository.findUsersByWorkspaceId(workspaceId, roleId);
    } else {
      userWorkspaceModels = userWorkspaceRepository.findUsersByWorkspaceId(workspaceId);
    }

    List<ProfileRecord> userWorkspaceRecords = userWorkspaceModels
      .stream()
      .map(userWorkspaceModel -> profileDtoMapper.apply(userWorkspaceModel.getUser()))
      .collect(Collectors.toList());
    return JapiResponse.success(userWorkspaceRecords);
  }

  @Override
  @TryCatchException
  public JapiResponse getWorkspaceTeams(UUID workspaceId) {
    List<UserWorkspaceModel> userWorkspaceModels = userWorkspaceRepository.findUsersByWorkspaceId(workspaceId);

    List<TeamsRecord> teamsRecords = new ArrayList<>();
    if (!userWorkspaceModels.isEmpty()) {
      teamsRecords = userWorkspaceModels.stream().map(teamsDtoMapper).toList();
    }
    return JapiResponse.success(teamsRecords);
  }

  @Override
  @TryCatchException
  public JapiResponse getManagerTeamMembers(UUID workspaceId) {
    UserModel manager = profileService.getLoggedInUser();

    List<TeamModel> teamModels = teamRepository.findByTeamManager(manager.getId(), workspaceId);

    List<UserWorkspaceModel> members = teamModels.stream()
      .map(teamModel -> userWorkspaceRepository.findOneByUserId(teamModel.getMember().getId(), workspaceId))
      .toList();

    List<TeamsRecord> teamsRecords = members.stream()
      .map(teamsDtoMapper)
      .toList();

    return JapiResponse.success(teamsRecords.isEmpty() ? Collections.emptyList() : teamsRecords);
  }

  @Override
  @TryCatchException
  public JapiResponse assignTeamMembers(AssignSalesRepRequest assignSalesRepRequest) {
    return null;
  }

  @Override
  @TryCatchException
  public JapiResponse removeWorkspaceMember(UUID userId, UUID workspaceId) {
    UserWorkspaceModel userWorkspaceModel = userWorkspaceRepository.findOneByUserId(userId, workspaceId);
    if (userWorkspaceModel == null) throw new NotFoundException("user");
    userWorkspaceModel.setRemoved(true);
    userWorkspaceRepository.save(userWorkspaceModel);
    return JapiResponse.success(null);
  }
}
