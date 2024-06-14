package com.paydai.api.application;

import com.paydai.api.domain.exception.ApiRequestException;
import com.paydai.api.domain.exception.ConflictException;
import com.paydai.api.domain.exception.InternalServerException;
import com.paydai.api.domain.model.*;
import com.paydai.api.domain.repository.EmailRepository;
import com.paydai.api.domain.repository.PasswordRepository;
import com.paydai.api.domain.repository.StripeAccountRepository;
import com.paydai.api.domain.repository.UserRepository;
import com.paydai.api.domain.service.AuthService;
import com.paydai.api.infrastructure.security.JwtAuthService;
import com.paydai.api.presentation.dto.auth.AuthDtoMapper;
import com.paydai.api.presentation.dto.auth.AuthModelDto;
import com.paydai.api.presentation.dto.auth.AuthRecordDto;
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
  private final UserRepository userRepository;

  private final PasswordRepository passwordRepository;

  private final StripeAccountRepository stripeAccountRepository;

  private final EmailRepository emailRepository;

  private final JwtAuthService jwtService;

  private final PasswordEncoder passwordEncoder;

  private final AuthDtoMapper authenticationDTOMapper;

  private final AuthenticationManager authenticationManager;
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
      UserModel userModel = userRepository.save(buildUser);

      // Build email
      EmailModel buildEmail = EmailModel.builder().email(payload.getEmail()).user(userModel).emailType(EmailType.PERSONAL).build();

      // Save email
      EmailModel emailModel = emailRepository.save(buildEmail);

      // Build password
      PasswordModel buildPass = PasswordModel.builder().email(emailModel).user(userModel).passwordHash(passwordEncoder.encode(payload.getPassword())).build();

      // Save Password
      passwordRepository.save(buildPass);

      // Generate token
      String token = jwtService.generateToken(userModel);

      // Build data response to send to client
      AuthModelDto buildAuthDto = AuthModelDto.getAuthData(userModel, emailModel, token, null);

      AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

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

      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailModel.getUser().getUserId(), authCred.getPassword()));

      String jwt = jwtService.generateToken(emailModel.getUser());

      // Find stripe account;
      StripeAccountModel stripeAccountModel = stripeAccountRepository.findByUser(emailModel.getUser().getUserId());

      String stripeId = stripeAccountModel != null ? stripeAccountModel.getStripeId() : null;

      AuthModelDto buildAuthDto = AuthModelDto.getAuthData(emailModel.getUser(), emailModel, jwt, stripeId);

      AuthRecordDto auth = authenticationDTOMapper.apply(buildAuthDto);

      return JapiResponse.builder().status(true).statusCode(HttpStatus.OK).message("Success!").data(auth).build();
    } catch (BadCredentialsException e) {
      throw new ApiRequestException(e.getMessage(), e);
    } catch (Exception e) {
      throw new InternalServerException(e.getMessage());
    }
  }
}