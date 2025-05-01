package com.example.microservices.product;

import io.restassured.RestAssured;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct() {
		String requestBody = """
					{
						"name": "Product 01",
						"description": "In ullamcorper nisl et risus suscipit, vitae semper enim tristique. Etiam dictum, augue eu scelerisque laoreet, massa lorem condimentum metus, id tincidunt leo nulla id est.",
						"price": 100
					}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Product 01"))
				.body("description", Matchers.equalTo(
						"In ullamcorper nisl et risus suscipit, vitae semper enim tristique. Etiam dictum, augue eu scelerisque laoreet, massa lorem condimentum metus, id tincidunt leo nulla id est."))
				.body("price", Matchers.equalTo(100));
	}
}