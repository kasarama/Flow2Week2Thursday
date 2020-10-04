/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;

/**
 * REST Web Service
 *
 * @author magda
 */
@Path("person")
public class PersonResource {

    @Context
    private UriInfo context;

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates a new instance of PersonResource
     */
    public PersonResource() {
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() throws Exception {
      
        
        return "{\"msg\":\"Person Resources\"}";
        
    }

    @Path("servererr")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String serverError() throws Exception {
      
        int[] numb ={1,2,3};
        return "{\"msg\":\"Person Resources "+numb[4]+"bb\"}";
        
    }
    @Path("findbyid/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonByID(@PathParam("id") int id) throws PersonNotFoundException {
        PersonDTO person = FACADE.getPerson(id);
        return new Gson().toJson(person);

    }

    @Path("all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPerson() {
        PersonsDTO all = FACADE.getAllPersons();
        return new Gson().toJson(all.getDtos());
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteById(@PathParam("id") int id) throws PersonNotFoundException {
        PersonDTO deleted = FACADE.deletePerson(id);
        return new Gson().toJson(deleted);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addNewPerson(String PersonJson) throws MissingInputException {
        Person newPerson = GSON.fromJson(PersonJson, Person.class);
        
        PersonDTO addedPerson = FACADE.addPerson(newPerson.getFirstName(), newPerson.getLastName(), newPerson.getPhone(), newPerson.getAddress());
        
        
        return new Gson().toJson(addedPerson);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String editPerson(String personJSON, @PathParam("id") int id) throws PersonNotFoundException, MissingInputException {
        PersonDTO dto = GSON.fromJson(personJSON, PersonDTO.class);
        dto.setId(id);
        PersonDTO edited = FACADE.editPerson(dto);
        //return "{\"msg\":\"Person Edited: \"}"+GSON.toJson(edited);
        return  GSON.toJson(edited);
    }

}

