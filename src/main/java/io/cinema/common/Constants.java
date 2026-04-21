package io.cinema.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String PERMISSIONS_POLICY = "geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; fullscreen 'self'; payment 'none'";
    public static final String ROLE_MANAGER = "hasRole('MANAGER')";
    public static final String ROLE_EMPLOYEE = "hasRole('EMPLOYEE')";
    public static final String ROLE_USER = "hasRole('USER')";

}
