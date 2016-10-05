package io.globomart.prodcat.test;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import io.globomart.prodcat.App;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAll {
	private static Server server;

	public TestAll() {
	}

	@BeforeClass
	public static void setUp() throws Exception {
		server = App.createServer();
		server.start();

	}

	@AfterClass
	public static void tearDown() throws InterruptedException, Exception {
		server.stop();

	}

	@Test
	public void createProduct() {
		String body = "{ \"brandName\":\"Galaxy\",\"model\":\"x2\",\"color\":\"white\"}";
		Client client = Client.create();
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, body);
		System.out.println("Create Status : " + response.getStatus());
	}

	@Test
	public void getProducts() {
		Client client = Client.create();
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		System.out.println("Get Status : " + response.getStatus());
	}
}
