package com.example.demo.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private MessageSource messageSource;

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver() {

        var templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(true);
        templateResolver.setSuffix(".html");
//        templateResolver.setSuffix(".jspf");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine templateEngine() {

        var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() {

        var viewResolver = new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");

        return viewResolver;
    }

// The web application is based on Spring MVC. Hereâ€™s a configuration class for configuring Spring MVC in the application.

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("forward:/coffeewheel");
        registry.addViewController("/resetPassword").setViewName("resetPassword");
        registry.addViewController("/welcome").setViewName("welcome");
        registry.addViewController("/beerwheel").setViewName("beerwheel");
        registry.addViewController("/teawheel").setViewName("teawheel");
        registry.addViewController("/whiskeywheel").setViewName("whiskeywheel");
        registry.addViewController("/winewheel").setViewName("winewheel");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/login2").setViewName("login2");
        registry.addViewController("/home").setViewName("coffeewheel");
        registry.addViewController("/admin/home").setViewName("adminhome");
        registry.addViewController("/403").setViewName("error");
        registry.addViewController("/upload2").setViewName("upload2");
        registry.addViewController("/upload3").setViewName("upload3");
        registry.addViewController("/upload4").setViewName("upload4");
        registry.addViewController("/registration2").setViewName("registration2");
        registry.addViewController("/success").setViewName("success");
        registry.addViewController("/upload5").setViewName("upload5");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/success").setViewName("register");
        registry.addViewController("/update-user").setViewName("update-user");
        registry.addViewController("/resetPassword").setViewName("resetPassword");
        registry.addViewController("/successForgotPassword").setViewName("successForgotPassword");
        registry.addViewController("/successResetPassword").setViewName("successResetPassword");
        registry.addViewController("/forgotPassword").setViewName("forgotPassword");
        registry.addViewController("/successfulRegistration").setViewName("successfulRegistration");
        registry.addViewController("/accountVerified").setViewName("accountVerified");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/images/**",
                "/css/**",
                "/js/**")
                .addResourceLocations("/", "/resources/",
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/static/images/",
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath*:/templates/");
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }

//    @Override
//    public void addInterceptors(final InterceptorRegistry registry) {
//        registry.addInterceptor(new LoggerInterceptor());
//        registry.addInterceptor(new UserInterceptor());
//        registry.addInterceptor(new SessionTimerInterceptor());
//    }

}