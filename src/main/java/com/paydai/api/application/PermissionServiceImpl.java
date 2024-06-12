package com.paydai.api.application;

import com.paydai.api.domain.model.PermissionModel;
import com.paydai.api.domain.repository.PermissionRepository;
import com.paydai.api.domain.service.PermissionService;
import com.paydai.api.presentation.dto.permission.WorkspaceDtoMapper;
import com.paydai.api.presentation.dto.permission.WorkspaceRecord;
import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
  private final PermissionRepository repository;

  private final WorkspaceDtoMapper permissionDtoMapper;

  @Override
  public JapiResponse getPermissions() {
    try {
      List<PermissionModel> permissionModels = repository.findAllPermissions();
      return JapiResponse.success(permissionModels);
    } catch (Exception e) { throw e; }
  }

  @Override
  public JapiResponse create(PermissionRequest payload) {
    try {
      PermissionModel buildPermission = PermissionModel.builder().permission(payload.getPermission()).build();

      PermissionModel permissionModel = repository.save(buildPermission);

      WorkspaceRecord permissionDto = permissionDtoMapper.apply(permissionModel);

      return JapiResponse.success(permissionDto);
    } catch (Exception e) { throw e; }
  }
}
