package com.mroncatto.startup.constant;

public class SecurityConstant {
    public static final long TOKEN_EXPIRES_IN = 10; // minutes
    public static final long REFRESH_TOKEN_EXPIRES_IN = 720; // minutes
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String COMPANY_NAME = "COMPANY NAME";
    public static final String COMPANY_DESC = "COMPANY DESCRIPTION";
    public static final String FORBIDDEN_MESSAGE = "You must be logged in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You don`t have enough to this page";
    public static final String[] PUBLIC_URLS = {"/swagger-ui/**", "/v3/api-docs/**", "/", };
}
