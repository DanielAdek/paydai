package com.paydai.api.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TryCatchExceptionAspect {
  @Around(value = "@annotation(com.paydai.api.domain.annotation.TryCatchException)")  // Intercepts methods annotated with @ThrowErrorHandler
  public Object handleMethodErrors(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      return joinPoint.proceed();
    } catch (ApiRequestException e) {
      log.info(e.getMessage() + e);
      throw new ApiRequestException(e.getMessage());
    } catch (BadCredentialsException e) {
      log.info(e.getMessage() + e);
      throw new BadCredentialException(e.getMessage());
    } catch (BadRequestException e) {
      log.info(e.getMessage() + e);
      throw new BadRequestException(e.getMessage());
    } catch (ConflictException e) {
      log.info(e.getMessage() + e);
      throw new ConflictException(e.getMessage());
    } catch (ForbiddenException e) {
      log.info(e.getMessage() + e);
      throw new ForbiddenException(e.getMessage());
    } catch (NotFoundException e) {
      log.info(e.getMessage() + e);
      throw new NotFoundException(e.getMessage());
    } catch (com.stripe.exception.StripeException e) {
      log.info(e.getMessage() + e);
      throw new StripeException(e.getMessage());
    } catch (IncorrectResultSizeDataAccessException e) {
      log.info(e.getMessage());
      throw new ApiRequestException("Duplicate Record: Query result");
    } catch (Exception e) {
      log.info(e.getMessage() + e);
      throw new InternalServerException(e.getMessage());
    }
  }
}
