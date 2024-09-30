package com.paydai.api.presentation.dto.profile;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.WorkspaceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
  private String firstName;
  private String lastName;
  private String authEmail;
  private List<WorkspaceModel> workspaces;

  public ProfileDto getProfileDto(UserModel userModel, EmailModel emailModel, List<WorkspaceModel> workspaceModel) {
    return new ProfileDto(
      userModel.getFirstName(),
      userModel.getLastName(),
      emailModel.getEmail(),
      workspaces
    );
  }
}
