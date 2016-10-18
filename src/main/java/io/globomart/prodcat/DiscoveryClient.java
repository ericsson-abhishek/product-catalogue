package io.globomart.prodcat;

import java.util.Scanner;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;


public class DiscoveryClient {
	
	public static void main(String[] args) throws Exception {
		ServiceProvider<Void> provider = getServiceProvider();
		
		Scanner sc =  new Scanner(System.in);
		String input  = sc.nextLine();
		while ( !input.equals("exit"))
		{
		ServiceInstance<Void> instance = provider.getInstance();
		String address = instance.buildUriSpec();
		System.out.println("Address of Service Instance is " +address);
		 input  = sc.nextLine();
		}


	}

	private static ServiceProvider<Void> getServiceProvider() throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
		client.start();

				ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder
				    .builder(Void.class)
				    .basePath("prodcat")
				    .client(client).build();
				serviceDiscovery.start();

				ServiceProvider<Void>serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName("worker").build();
				serviceProvider.start();
				
				return serviceProvider;
	}

}
