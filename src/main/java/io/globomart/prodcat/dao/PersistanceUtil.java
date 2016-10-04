package io.globomart.prodcat.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PersistanceUtil implements ServletContextListener {

	private static EntityManagerFactory emf;

	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// initialize emf
		emf = Persistence.createEntityManagerFactory("product");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// destroy emf
		emf.close();
	}

	public static EntityManager createEntityManager() {
		
		//TODO this call can be handled in a graceful way by using a latch
		if (emf == null) {
			throw new IllegalStateException("Context is not initialized yet.");
		}

		return emf.createEntityManager();
	}

}
