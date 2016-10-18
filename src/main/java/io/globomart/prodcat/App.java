package io.globomart.prodcat;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
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

		int port = 0;
		if (args.length > 0) {
			port = Integer.valueOf(args[0]);
		}

		System.out.println("PORT : " + port);
		Server server = createServer(port);

		try {
			server.start();
			registerService(port);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			server.join();
		}

		// finally {
		// server.destroy();
		// }

	}

	public static Server createServer(int port) throws Exception, InterruptedException {
		String PORT;
		if (port == 0) {
			PORT = System.getenv("PORT");
			if (PORT == null || PORT.isEmpty()) {
				PORT = "2222";
			}
		} else {
			PORT = String.valueOf(port);
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

		// add PersistanceUtil as a servletContexttlistener
		context.addEventListener(new PersistanceUtil());

		// Setup Swagger servlet.
		// Swagger is used for documenting the REST services being exposed by
		// Prodcat Service
		ServletHolder swaggerServ = new ServletHolder(new SwaggerBootstrap());
		swaggerServ.setInitOrder(2);
		context.addServlet(swaggerServ, "/swagger-core");

		// Purposefully allowing CORS to test the services from
		// http://editor.swagger.io/#/
		// MUST be commented for production
		FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		return server;
	}

	public static void registerService(int port) throws Exception {
		// CuratorFramework curatorFramework =
		// CuratorFrameworkFactory.newClient("localhost:2181", new
		// RetryNTimes(5, 1000));
		// curatorFramework.start();

		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
		client.start();

		ServiceInstance<Object> serviceInstance = ServiceInstance.builder()
				.uriSpec(new UriSpec("{scheme}://{address}:{port}")).address("localhost").port(port).name("worker")
				.build();

		ServiceDiscoveryBuilder.builder(Object.class).basePath("prodcat").client(client).thisInstance(serviceInstance)
				.build().start();

	}

}
