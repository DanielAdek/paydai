package com.paydai.api.presentation.dto.profile;

import com.paydai.api.domain.model.EmailModel;
import com.paydai.api.domain.model.UserModel;
import com.paydai.api.domain.model.UserWorkspaceModel;
import com.paydai.api.domain.model.WorkspaceModel;
import com.paydai.api.presentation.dto.userWorkspace.UserWorkspaceRecord;
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
  private List<UserWorkspaceRecord> userWorkspaceRecords;

  public static ProfileDto getProfileDto(UserModel userModel, EmailModel emailModel, List<UserWorkspaceRecord> userWorkspaceRecords) {
    return new ProfileDto(
      userModel.getFirstName(),
      userModel.getLastName(),
      emailModel.getEmail(),
      userWorkspaceRecords
    );
  }
}
