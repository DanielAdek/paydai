package com.paydai.api.application;

import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.model.RoleModel;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.domain.repository.RoleRepository;
import com.paydai.api.domain.repository.WorkspaceRepository;
import com.paydai.api.domain.service.RoleService;
import com.paydai.api.presentation.dto.role.RoleDtoMapper;
import com.paydai.api.presentation.dto.role.RoleRecord;
import com.paydai.api.presentation.request.RoleRequest;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
  private final RoleRepository repository;
  private final WorkspaceRepository workspaceRepository;
  private final RoleDtoMapper roleDtoMapper;

  @Override
  public JapiResponse createRole(RoleRequest payload) {
    try {
      RoleModel roleModel = repository.findRole(payload.getRole().trim().toLowerCase());

      if (roleModel != null) throw new ConflictException("Role in use");

      RoleModel buildRole = RoleModel.builder().role(payload.getRole().trim().toLowerCase()).build();

      RoleModel role = repository.save(buildRole);

      RoleRecord roleRecord = roleDtoMapper.apply(role);

      return JapiResponse.success(roleRecord);
    } catch (Exception e) {
      throw e;
    }
  }

  @Override
  public JapiResponse getRoles() {
    try {
      List<RoleModel> roleModels = repository.findRoles();
      return JapiResponse.success(roleModels);
    } catch (Exception e) {
      throw e;
    }
  }
}
