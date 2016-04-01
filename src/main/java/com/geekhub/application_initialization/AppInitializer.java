package com.geekhub.application_initialization;

import com.geekhub.config.HibernateConfig;
import com.geekhub.config.SecurityConfig;
import com.geekhub.config.WebAppConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final String LOCATION = "D:/EFS/";
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 250;
    private static final long MAX_REQUEST_SIZE = 1024 * 1024 * 300;
    private static final int FILE_SIZE_THRESHOLD = 0;

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {
                WebAppConfig.class,
                SecurityConfig.class,
                HibernateConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(getMultipartConfigElement());
    }

    private MultipartConfigElement getMultipartConfigElement(){
        return new MultipartConfigElement(LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
    }

//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
////        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
////        context.register(WebAppConfig.class);
////        servletContext.addListener(new ContextLoaderListener(context));
////
////        context.setServletContext(servletContext);
////
////        ServletRegistration.Dynamic servlet = servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(context));
////        servlet.addMapping("/");
////        servlet.setLoadOnStartup(1);
//
//        AnnotationConfigWebApplicationContext rootContext =
//                new AnnotationConfigWebApplicationContext();
//        rootContext.register(WebAppConfig.class);
//        rootContext.register(HibernateConfig.class);
//        rootContext.register(SecurityConfig.class);
//
//        servletContext.addListener(new ContextLoaderListener(rootContext));
//
//        ServletRegistration.Dynamic dispatcher =
//                servletContext.addServlet("dispatcher", new DispatcherServlet(rootContext));
//        dispatcher.setLoadOnStartup(1);
//        dispatcher.addMapping("/");
//    }
}