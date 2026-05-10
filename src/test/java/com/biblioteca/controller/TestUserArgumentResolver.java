package com.biblioteca.controller;

import com.biblioteca.model.User;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Argument resolver de teste que injeta um User fixo quando
 * o controller usa @AuthenticationPrincipal.
 * Substitui a necessidade de SecurityContext nos testes standalone.
 */
class TestUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final User TEST_USER;

    static {
        TEST_USER = new User("Test User", "test@email.com", "hash", null);
        TEST_USER.setId("user-123");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                   ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest,
                                   WebDataBinderFactory binderFactory) {
        return TEST_USER;
    }
}
