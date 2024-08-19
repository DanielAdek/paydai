package com.paydai.api.domain.exception;

import org.apache.coyote.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TryCatchExceptionAspect {
  @Around(value = "@annotation(com.paydai.api.domain.annotation.TryCatchException)")  // Intercepts methods annotated with @ThrowErrorHandler
  public Object handleMethodErrors(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      return joinPoint.proceed();
    } catch (ApiRequestException e) {
      throw new ApiRequestException(e.getMessage());
    } catch (BadCredentialsException e) {
      throw new BadCredentialException(e.getMessage());
    } catch (BadRequestException e) {
      throw new BadRequestException(e.getMessage());
    } catch (ConflictException e) {
      throw new ConflictException(e.getMessage());
    } catch (ForbiddenException e) {
      throw new ForbiddenException(e.getMessage());
    } catch (NotFoundException e) {
      throw new NotFoundException(e.getMessage());
    } catch (com.stripe.exception.StripeException e) {
      throw new StripeException(e.getMessage());
    } catch (Exception e) {
      throw new InternalServerException(e.getMessage());
    }
  }
}
