package com.paydai.api.application;

import com.paydai.api.domain.annotation.TryCatchException;
import com.paydai.api.domain.model.PermissionModel;
import com.paydai.api.domain.repository.PermissionRepository;
import com.paydai.api.domain.service.PermissionService;
import com.paydai.api.presentation.dto.permission.PermissionDtoMapper;
import com.paydai.api.presentation.dto.permission.PermissionRecord;
import com.paydai.api.presentation.request.PermissionRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
  private final PermissionRepository repository;

  private final PermissionDtoMapper permissionDtoMapper;

  @Override
  @TryCatchException
  public JapiResponse getPermissions() {
    List<PermissionModel> permissionModels = repository.findAllPermissions();
    return JapiResponse.success(permissionModels);
  }

  @Override
  @TryCatchException
  public JapiResponse create(PermissionRequest payload) {
    PermissionModel buildPermission = PermissionModel.builder().permission(payload.getPermission()).build();

    PermissionModel permissionModel = repository.save(buildPermission);

    PermissionRecord permissionDto = permissionDtoMapper.apply(permissionModel);

    return JapiResponse.success(permissionDto);
  }
}
