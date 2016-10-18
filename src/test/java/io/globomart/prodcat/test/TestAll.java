package io.globomart.prodcat.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import io.globomart.prodcat.App;
import io.globomart.prodcat.entities.ProductEntity;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAll {
	private static Server server;
	private static Client client;

	public TestAll() {
	}

	@BeforeClass
	public static void setUp() throws Exception {
		server = App.createServer(2222);
		server.start();
		client = new Client();

	}

	@AfterClass
	public static void tearDown() throws InterruptedException, Exception {
		server.stop();

	}

	@Test
	public void createProduct1() {
		String body = "{ \"brandName\":\"Samsung\",\"model\":\"galaxy\",\"color\":\"white\"}";
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		System.out.println("Create Status : " + response.getStatus());
		ProductEntity resEntity = response.getEntity(ProductEntity.class);
		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getBrandName(), "Samsung");
		assertEquals(resEntity.getModel(), "galaxy");
		assertEquals(resEntity.getColor(), "white");
	}

	@Test
	public void createProduct2() {
		String body = "{ \"brandName\":\"Apple\",\"model\":\"iPhone7\",\"color\":\"white\"}";
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		System.out.println("Create Status : " + response.getStatus());
		ProductEntity resEntity = response.getEntity(ProductEntity.class);
		assertEquals(resEntity.getProductId().intValue(), 2);
		assertEquals(resEntity.getBrandName(), "Apple");
		assertEquals(resEntity.getModel(), "iPhone7");
		assertEquals(resEntity.getColor(), "white");
	}

	@Test
	public void getProducts() {
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		System.out.println("Get Status : " + response.getStatus());
		List<ProductEntity> productEntityList = response.getEntity(new GenericType<List<ProductEntity>>() {
		});
		ProductEntity resEntity = productEntityList.get(0);
		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getBrandName(), "Samsung");
		assertEquals(resEntity.getModel(), "galaxy");
		assertEquals(resEntity.getColor(), "white");

		ProductEntity resEntity1 = productEntityList.get(1);
		assertEquals(resEntity1.getProductId().intValue(), 2);
		assertEquals(resEntity1.getBrandName(), "Apple");
		assertEquals(resEntity1.getModel(), "iPhone7");
		assertEquals(resEntity1.getColor(), "white");
	}

	@Test
	public void getProductById() {
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products/1");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		System.out.println("GetById Status : " + response.getStatus());
		ProductEntity resEntity = response.getEntity(ProductEntity.class);
		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getBrandName(), "Samsung");
		assertEquals(resEntity.getModel(), "galaxy");
		assertEquals(resEntity.getColor(), "white");
	}

	@Test
	public void getProductByFilter() {
		WebResource webResource = client.resource("http://localhost:2222/prodcat/products");
		ClientResponse response = webResource.queryParam("color", "white").type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		System.out.println("GetById Status : " + response.getStatus());
		List<ProductEntity> productEntityList = response.getEntity(new GenericType<List<ProductEntity>>() {
		});
		System.out.println("getProductByFilter response ProductEntity size :  " + productEntityList.size());
		ProductEntity resEntity = productEntityList.get(0);

		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getBrandName(), "Samsung");
		assertEquals(resEntity.getModel(), "galaxy");
		assertEquals(resEntity.getColor(), "white");
	}
}
