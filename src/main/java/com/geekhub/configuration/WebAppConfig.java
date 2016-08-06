package com.geekhub.configuration;

import com.geekhub.interceptors.*;
import com.geekhub.loggin.LoggedInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;

@Configuration
@ComponentScan(basePackages = "com.geekhub")
@PropertySource("classpath:database.properties")
@EnableWebMvc
@MultipartConfig
@EnableAspectJAutoProxy
public class WebAppConfig extends WebMvcConfigurerAdapter implements WebApplicationInitializer {

    private static final String[] FILTER_URL_PATTERNS = new String[] {
            "/api/files/*", "/api/links", "/api/links/documents/comments"
    };

    @Inject
    private MainInterceptor mainInterceptor;

    @Inject
    private LoggedInterceptor loggedInterceptor;

    @Inject
    private UserDocumentAccessInterceptor documentAccessInterceptor;

    @Inject
    private UserDirectoryAccessInterceptor directoryAccessInterceptor;

    @Inject
    private UserFilesAccessInterceptor filesAccessInterceptor;

    @Inject
    private FileSharedLinkInterceptor fileSharedLinkInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mainInterceptor);
        registry.addInterceptor(loggedInterceptor);

        registry.addInterceptor(fileSharedLinkInterceptor)
                .addPathPatterns("/api/links/**");

        registry.addInterceptor(documentAccessInterceptor)
                .addPathPatterns("/api/documents/**")
                .excludePathPatterns("/api/documents");

        registry.addInterceptor(directoryAccessInterceptor)
                .addPathPatterns("/api/directories/**");

        registry.addInterceptor(filesAccessInterceptor)
                .addPathPatterns("/api/files/**")
                .excludePathPatterns("/api/files/search", "/api/files/removed", "/api/files/accessible");
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getResolver() throws IOException {
        return new CommonsMultipartResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(true);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addFilter("FilesRequestFilter", RequestsWithJSONFilter.class)
                .addMappingForUrlPatterns(null, false, FILTER_URL_PATTERNS);
    }
}
