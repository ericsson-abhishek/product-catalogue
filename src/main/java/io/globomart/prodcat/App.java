package io.globomart.prodcat;

//
//c:\Work\nginx>..\consul-template\consul-template.exe -consul localhost:8500 -template "conf\prodcat-template.ctmpl:conf\prodcat.conf:nginx.e
//xe -s reload"
import java.util.EnumSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;

import io.globomart.prodcat.dao.PersistanceUtil;

public class App {
	
	static ScheduledExecutorService execService = Executors.newScheduledThreadPool(1);
	
	

	public static void main(String[] args) throws Exception {
		
		

		int port = 0;
		int instanceId=0;
		if (args.length > 1) {
			port = Integer.valueOf(args[0]);
			instanceId = Integer.valueOf(args[1]);
		}

		System.out.println("PORT : " + port);
		Server server = createServer(port);

		try {
			server.start();
			//registerServiceInZK(port);
			//registerServiceInConsul(port,instanceId);
			registerServiceInEureka(port,instanceId);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			server.join();
		}
		
		finally {
		 execService.shutdown();
		 }

	}

	private static void registerServiceInEureka(int port, final int instanceId) throws NotRegisteredException {
		MyDataCenterInstanceConfig myDataCenterInstanceConfig = new MyDataCenterInstanceConfig();
		//ApplicationInfoManager applicationInfoManager 
//		/if (applicationInfoManager == null) {
			//instanceConfig.getMetadataMap().put("eureka.instance.metadataMap.instanceId", "instance1");
			
			InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(myDataCenterInstanceConfig).get();
			ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(myDataCenterInstanceConfig, instanceInfo);
			try{
			EurekaClient eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
			}catch( Throwable e)
			{
				System.out.println("Exception occurred here");
				e.printStackTrace();
			}
			applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
			//eurekaClient.
		//}
		//ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(myDataCenterInstanceConfig);

		
	}
	
	private static void registerServiceInConsul(int port, final int instanceId) throws NotRegisteredException {
		// TODO Auto-generated method stub
		Consul consul = Consul.builder().build(); // connect to Consul on localhost
		final AgentClient agentClient = consul.agentClient();

		String serviceName = "prod";
		final String serviceId = ""+instanceId;

		agentClient.register(port, 5L, serviceName, serviceId); // registers with a TTL of 3 seconds
		execService.scheduleAtFixedRate((new Runnable() {
			
			@Override
			public void run() {
				try {
					//System.out.println("Check in triggerred for intance id "+serviceId);
					agentClient.pass(serviceId);
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}),0L, 3L, TimeUnit.SECONDS);
		
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

	public static void registerServiceInZK(int port) throws Exception {
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
