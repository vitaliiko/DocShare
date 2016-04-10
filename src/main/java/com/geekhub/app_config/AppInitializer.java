package com.geekhub.app_config;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import java.io.File;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private int maxUploadSizeInMb = 500 * 1024 * 1024;

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {
                WebAppConfig.class,
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

//    @Override
//    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
//        registration.setMultipartConfig(getMultipartConfigElement());
//    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new HiddenHttpMethodFilter(), new MultipartFilter(), new OpenEntityManagerInViewFilter()};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {

        File uploadDirectory = new File("C:\\spring_temp");

        MultipartConfigElement multipartConfigElement =
                new MultipartConfigElement(uploadDirectory.getAbsolutePath(),
                        maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);

        registration.setMultipartConfig(multipartConfigElement);

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