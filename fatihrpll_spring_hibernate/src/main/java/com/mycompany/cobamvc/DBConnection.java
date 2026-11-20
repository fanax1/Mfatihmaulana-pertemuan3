package com.mycompany.cobamvc;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DBConnection {
    private static ApplicationContext context;
    private static SessionFactory sessionFactory;

    static {
        try {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
            sessionFactory = (SessionFactory) context.getBean("sessionFactory");
        } catch (Exception ex) {
            System.err.println("Gagal memuat Spring Context: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
