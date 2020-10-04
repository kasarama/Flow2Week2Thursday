/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author magda
 */
public class PersonsDTO {
    
    List<PersonDTO> dtos= new ArrayList();

    public PersonsDTO(List<Person> persons) {
        
        persons.stream().map(person -> new PersonDTO(person)).forEachOrdered(dto -> {
            dtos.add(dto);
        });
               
    }

    public List<PersonDTO> getDtos() {
        return dtos;
    }

    
    
    
    
}