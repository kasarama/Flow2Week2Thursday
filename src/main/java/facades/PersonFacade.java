package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author magda
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    private PersonFacade() {
    }

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, Address address) throws MissingInputException {
        if (fName == null || lName == null) {
            throw new MissingInputException("First Name and/or Last Name is missing");
        } else
        if (address ==  null) {
            throw new MissingInputException("Address is missing");
        } else {
        EntityManager em = emf.createEntityManager();
        Person p = new Person(fName, lName, phone, new Date(), new Date());
        p.setAddress(address);
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return new PersonDTO(p);
        }
    }

    @Override
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {

        EntityManager em = emf.createEntityManager();
        Person p = em.find(Person.class, id);
        if (p == null) {
            throw new PersonNotFoundException("Could not delete, provided id: " + id + " does not exist");
        }
        PersonDTO dto = new PersonDTO(p);

        try {
            em.getTransaction().begin();
            em.remove(p.getAddress());
            em.remove(p);
            em.getTransaction().commit();
          //  System.out.println(dto.toString());
            return dto;

        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO getPerson(int id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        PersonDTO pDTO;
        try {
            em.getTransaction().begin();

            Person p = em.find(Person.class, id);

            if (p == null) {
                throw new PersonNotFoundException("No person with provided id: " + id + " found");
            }

            pDTO = new PersonDTO(p);
            em.getTransaction().commit();
            return pDTO;

        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
            return new PersonsDTO(query.getResultList());
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException {
        if (p.getfName() == null || p.getlName() == null) {
            throw new MissingInputException("First Name and/or Last Name is missing");
        }
        EntityManager em = emf.createEntityManager();
        PersonDTO pDTO;
        try {
            em.getTransaction().begin();
            Person person = em.find(Person.class, p.getId());
            if (person == null) {
                throw new PersonNotFoundException("person with id " + p.getId() + " does not exist.");
            }

            person.setFirstName(p.getfName());
            person.setLastName(p.getlName());
            person.setPhone(p.getPhone());
            person.setLastEdited(new Date());
            pDTO = new PersonDTO(person);
            em.getTransaction().commit();
            return pDTO;

        } finally {
            em.close();
        }
    }

}
