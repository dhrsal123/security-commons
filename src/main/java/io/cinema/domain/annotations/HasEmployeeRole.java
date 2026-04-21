package io.cinema.domain.annotations;


import io.cinema.common.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;


@Inherited
@Documented
@PreAuthorize(Constants.ROLE_EMPLOYEE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface HasEmployeeRole {
}