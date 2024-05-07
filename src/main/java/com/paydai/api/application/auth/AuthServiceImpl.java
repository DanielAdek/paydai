package com.paydai.api.application.auth;

import com.paydai.api.domain.exception.InternalServerException;
import com.paydai.api.domain.service.AuthService;
import com.paydai.api.presentation.response.JapiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  public JapiResponse create() {
    try {
      return JapiResponse.builder().status(true).message("Success!").statusCode(HttpStatus.CREATED).data(new Date()).build();
    } catch (Exception ex) {
      logger.info("An error occurred: {} " + ex.getMessage());
      throw new InternalServerException(ex.getMessage(), ex);
    }
  }
}
