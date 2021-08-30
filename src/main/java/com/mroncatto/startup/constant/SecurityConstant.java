package com.mroncatto.startup.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 10; // minutes
    public static final String API_VERSION = "v1/";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String COMPANY_NAME = "COMPANY NAME";
    public static final String COMPANY_DESC = "COMPANY DESCRIPTION";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You must be logged in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You don`t have enough to this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/swagger-ui/**", "/v3/api-docs/**", "/", };
}
