/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import entities.Address;
import entities.Person;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import static io.restassured.mapper.ObjectMapperType.GSON;
import io.restassured.parsing.Parser;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;

/**
 *
 * @author magda
 */
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Person p1 = new Person("Magda", "Wawrzak", "123123", new Date(), new Date());
    private Person p2 = new Person("Hanna", "Zofia", "33", new Date(), new Date());
    

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() throws IOException {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        httpServer.start();
        while (!httpServer.isStarted()) {
        }
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {

        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1.setAddress(new Address("Alle","123","CPH"));
        p2.setAddress(new Address("Street","321","HPC"));

        try {
            em.getTransaction().begin();
            em.createQuery("Delete from Person").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given()
                .when()
                .get("/person")
                .then()
                .statusCode(200);
    }

    
    @Test
    public void testDemo() throws Exception {
        given()
                .contentType("text/html")
                .get("/person/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Person Resources"));
    }
    
    
    @Test
    public void testServerError() throws Exception {
        given()
                .contentType("text/html")
                .get("/person/servererr").then()
                .assertThat()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR_500.getStatusCode())
                .body("message", equalTo("Internal Server Problem. We are sorry for the inconvenience"));
    }

    
    @Test
    public void testGetPersonByID() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/findbyid/" + p2.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", equalTo(p2.getId()));
    }
    
    @Test
    public void NegativTestGetPersonByID() throws Exception {
        int id = 20;
        given()
                .contentType("application/json")
                .get("/person/findbyid/" + id).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("No person with provided id: "+id+" found"))
                .body("code", equalTo(404));
    }

    @Test
    public void testGetAllPerson() {
        given()
                .contentType("application/json")
                .get("/person/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(2))
                .and()
                .body("phone", hasItems("123123", "33"));
    }
    

    @Test
    public void testDeleteById() {
        System.out.println("Rst test delete");
        given()
                .contentType("application/json")
                .when()
                .delete("/person/" + p1.getId())  
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", equalTo(p1.getFirstName()));
    }
    

    @Test
    public void testAddNewPerson() {
        Person person = new Person("Yes","No","Why",new Date(), new Date());
        person.setAddress(new Address ("a","B","c"));
        given()
                .contentType("application/json")
                .body(GSON.toJson(person))
                .when()
                .post("/person")
                .then()
                .body("fName", equalTo("Yes"));

    }
    
    @Test
    public void negativTestAddNewPersonF() {
        given()
                .contentType("application/json")
                .body(GSON.toJson(new PersonDTO(null,"No","Why",new Address ("a","B","c"))))
                .when()
                .post("/person")
                .then()
                .body("message", equalTo("First Name and/or Last Name is missing"))
                .body("code", equalTo(400)
                );

    }
    @Test
    public void negativTestAddNewPersonL() {
        given()
                .contentType("application/json")
                .body(GSON.toJson(new PersonDTO("Yea",null,"Why",new Address ("a","B","c"))))
                .when()
                .post("/person")
                .then()
                .body("message", equalTo("First Name and/or Last Name is missing"))
                .body("code", equalTo(400)
                );

    }

    @Test
    public void testEditPerson() {
        PersonDTO newPerson = new PersonDTO("New", "Person", "Vol.2",new Address ("X","Y","Z"));
        newPerson.setId(p2.getId());
        given()
                .contentType("application/json")
                .body(GSON.toJson(newPerson))//works also for PersonDTO object not only for JSON
                .when()
                .put("/person/{id}", p2.getId())
                .then()
                .body("lName", equalTo(newPerson.getlName()))
                .body("phone", equalTo(newPerson.getPhone()))
                .body("fName", equalTo(newPerson.getfName()));

    }
    
    @Test
    public void negativTestEditPerson() {
        PersonDTO newPerson = new PersonDTO(null, null, "Vol.2",new Address ("X","Y","Z"));
        newPerson.setId(p2.getId());
        given()
                .contentType("application/json")
                .body(GSON.toJson(newPerson))//works also for PersonDTO object not only for JSON
                .when()
                .put("/person/{id}", p2.getId())
                .then()
                .body("message", equalTo("First Name and/or Last Name is missing"))
                .body("code", equalTo(400));

    }

}
