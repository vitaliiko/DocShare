//package com.geekhub.config;
//
//import com.geekhub.model.User;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.AnnotationConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.hibernate4.HibernateTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.servlet.ViewResolver;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//
//import java.util.Properties;
//
//@org.springframework.context.annotation.Configuration
//@ComponentScan(basePackages = "com.geekhub")
//@EnableTransactionManagement
//@EnableWebMvc
//public class AppConfig {
//
//    @Bean
//    public SessionFactory getSessionFactory() {
//        org.hibernate.cfg.Configuration config = new AnnotationConfiguration()
//                .addAnnotatedClass(User.class)
//                .addProperties(getProperties());
////        config.configure();
//        return config.buildSessionFactory();
//    }
//
//    @Bean
//    public Properties getProperties() {
//        Properties properties = new Properties();
//        properties.put("connection.url", "jdbc:mysql://localhost:3306/geekdb");
//        properties.put("connection.driver_class", "com.mysql.jdbc.Driver");
//        properties.put("connection.username", "root");
//        properties.put("connection.password", "1111");
//        properties.put("hibernate.show_sql", "true");
//        properties.put("hbm2ddl.auto", "create");
//        properties.put("hibernate.c3p0.min_size", "5");
//        properties.put("hibernate.c3p0.max_size", "20");
//        properties.put("hibernate.c3p0.timeout", "300");
//        properties.put("hibernate.c3p0.max_statements", "3000");
//        properties.put("hibernate.format_sql", "true");
//        properties.put("hibernate.show_sql", "true");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//        return properties;
//    }
//
//    @Bean
//    public HibernateTransactionManager getTransactionManager() {
//        return new HibernateTransactionManager(getSessionFactory());
//    }
//
//    @Bean
//    public ViewResolver getViewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix("/WEB-INF/pages/");
//        resolver.setSuffix(".jsp");
//        return resolver;
//    }
//}
