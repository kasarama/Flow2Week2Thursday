/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import entities.Address;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;

/**
 *
 * @author magda
 */
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    Address a1 = new Address("Wallstreet", "12345", "Oslo");
    Address a2 = new Address("Boom", "88", "Taam");
    Address a3 = new Address("Newway", "2200", "There");

    Person p1 = new Person("Magda", "Bobsja", "1236587", new Date(), new Date());
    Person p2 = new Person("Bob", "Sponge", "999999", new Date(), new Date());

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);

    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Person").executeUpdate();
            p1.setAddress(a1);
            p2.setAddress(a2);
            em.persist(p1);
            em.persist(p2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testAddPerson() throws MissingInputException {

        String fName = "Patrick";
        PersonDTO result = facade.addPerson(fName, "last name", "789789", a3);
        assertTrue(result.getfName().equals(fName));
    }

    @Test
    public void negativTestAddPersonF() {
        MissingInputException ex = assertThrows(
                MissingInputException.class,
                () -> {
                    facade.addPerson(null, "lName", "phone", a1);
                });
        assertEquals("First Name and/or Last Name is missing", ex.getMessage());
    }

    @Test
    public void negativTestAddPersonL() {
        MissingInputException ex = assertThrows(
                MissingInputException.class,
                () -> {
                    facade.addPerson("fNAme", null, "phone", a1);
                });
        assertEquals("First Name and/or Last Name is missing", ex.getMessage());

    }

    @Test
    public void testDeletePerson() throws PersonNotFoundException {
        PersonDTO pDTO = facade.deletePerson(p1.getId());
        assertTrue(pDTO.getPhone().equals("1236587"));
        assertTrue(pDTO.getCity() .equals("Oslo"));

    }

    @Test
    public void NegativTestDeletePErson() {
        int id = 20;
        PersonNotFoundException ex = assertThrows(
                PersonNotFoundException.class,
                () -> {
                    PersonDTO pDTO = facade.deletePerson(id);
                });
        assertEquals("Could not delete, provided id: " + id + " does not exist", ex.getMessage());
    }

    @Test
    public void testGetPerson() throws PersonNotFoundException {
        PersonDTO pDTO = facade.getPerson(p2.getId());
        assertTrue(pDTO.getfName().endsWith("Bob"));

    }

    @Test
    public void NegativTestGetPerson() {
        //No person with provided id: "+id+" found
        int id = 20;
        PersonNotFoundException exception = assertThrows(
                PersonNotFoundException.class,
                () -> {
                    PersonDTO pDTO = new PersonDTO("Tom", "Cat", "666666", a3);
                    pDTO.setId(id);
                    facade.getPerson(id);
                });
        assertEquals("No person with provided id: " + id + " found", exception.getMessage());

    }

    @Test
    public void testGetAllPersons() {
        PersonsDTO all = facade.getAllPersons();
        assertEquals(all.getDtos().size(), 2);
    }

    @Test
    public void testEditPerson() throws PersonNotFoundException, MissingInputException {

        PersonDTO pDTO = new PersonDTO("Tom", "Cat", "666666", a3);

        pDTO.setId(p2.getId());
        PersonDTO edited = facade.editPerson(pDTO);

        assertTrue(pDTO.getPhone().equals(edited.getPhone()));

    }

    @Test
    public void negativTestEditPerson() {
        int id = 19;
        PersonNotFoundException exception = assertThrows(
                PersonNotFoundException.class,
                () -> {
                    PersonDTO pDTO = new PersonDTO("Tom", "Cat", "666666", a3);
                    pDTO.setId(id);
                    facade.editPerson(pDTO);
                });
        assertEquals("person with id " + id + " does not exist.", exception.getMessage());

    }

}
