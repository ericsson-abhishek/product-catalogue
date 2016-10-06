package io.globomart.prodcat;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import io.globomart.prodcat.dao.PersistanceUtil;

public class App {
	
	public static void main(String[] args) throws Exception {

		Server server = createServer();
		
		try {
			server.start();
			server.join();
		} finally {
			server.destroy();
		}
		
	}

	public static Server createServer() throws Exception, InterruptedException {
		String PORT = System.getenv("PORT");
		if(PORT == null || PORT.isEmpty())
		{
			PORT="2222";
		}
		
		
		Server server = new Server(Integer.valueOf(PORT));
		ServletContextHandler context = new ServletContextHandler(server, "/*");

		// Configure Jersey resources
		// including the base package of JAX-RS resources
		// and some of the swagger packages
		ResourceConfig config = new ResourceConfig();
		config.packages("io.globomart.prodcat", "io.swagger.jaxrs.json", "io.swagger.jaxrs.listing");
		
		// setup the jersey servlet
		ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
		context.addServlet(jerseyServlet, "/*");

		// Setup Swagger servlet
		ServletHolder swaggerServ = new ServletHolder(new SwaggerBootstrap());
		swaggerServ.setInitOrder(2);
		context.addServlet(swaggerServ, "/swagger-core");
		
		// add PersistanceUtil as a servletContexttlistener
		context.addEventListener(new PersistanceUtil());
		
//		FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
//		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
//		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
//		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
//		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		return server;
	}
}
