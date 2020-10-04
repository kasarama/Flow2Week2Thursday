/*,
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flow2.jpa_relations.entities;

import dto.PersonStyleDTO;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author magda
 */
public class Tester {
    
    public static void main(String[] args) {
        System.out.println("Tester");
        
        EntityManagerFactory  emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        
        
        Person p1 = new Person ("Magdalena", 1988 );
        Person p2 = new Person ("Aleksandra", 1993 );
        
        Address add1 = new Address("Gildbrovej",2635,"Ishøj");
        Address add2 = new Address("Langevej",2200,"København");
        
        p1.setAddress(add1);
        p2.setAddress(add2);
        
        
        Fee f1 = new Fee (2500);
        Fee f2 = new Fee (153.50);
        Fee f3 = new Fee(1);
        
        
        p1.addFee(f1);
        p2.addFee(f2);
        p2.addFee(f3);
        
        
        SwimStyle s1= new SwimStyle("Crawl");
        SwimStyle s2= new SwimStyle("Butterfly");
        SwimStyle s3= new SwimStyle("BreastS Stroke");
        SwimStyle s4= new SwimStyle("FreeStyle");
        SwimStyle s5= new SwimStyle("YourStyle");
        
        p1.addSwimStyle(s1);
        p1.addSwimStyle(s3);
        p1.addSwimStyle(s2);
        p2.addSwimStyle(s1);
        p2.addSwimStyle(s2);
        p2.addSwimStyle(s3);
        p2.addSwimStyle(s4);
        
        
        em.getTransaction().begin();
        
        em.persist(p1);
        em.persist(p2);
        em.getTransaction().commit();
        
        
        em.getTransaction().begin();
        
        p1.removeSwimStyle(s1);
        p1.removeSwimStyle(s2);
        em.getTransaction().commit();
       
        System.out.println("p1: "+p1.getPerson_id()+ ", "+p1.getName());
        System.out.println("p2: "+p2.getPerson_id()+ ", "+p2.getName());
        System.out.println("Aleksandras street: "+ p2.getAddress().getStreet());
        System.out.println("does it wor both ways?: " + add1.getPerson().getName());
        System.out.println("who paid f2?: "+f2.getPerson().getName());
        
        
        TypedQuery<Fee> q1 = em.createQuery("SELECT f FROM Fee f", Fee.class);
        List<Fee> fees = q1.getResultList();
        
        for (Fee f : fees ) {
            System.out.println(f.getPerson().getName()+": "+f.getAmount()+", "+f.getPayDate());
        }
        
        
         Query q3 = em.createQuery("SELECT new dto.PersonStyleDTO( p.name, p.year, s.styleName) FROM Person p JOIN p.styles s");
         
         List<PersonStyleDTO> personDetails = q3.getResultList();
         
         for (PersonStyleDTO ps : personDetails) {
             System.out.println("Navn: "+ps.getName()+", "+ps.getYear()+", "+ps.getSwimStyle());
            
        }
    
         
         /*
         
         nope, it won't work 
         
         TypedQuery<PersonStyleDTO> q4 = em.createQuery("SELECT ps FROM Person p Join p.styles  ps", PersonStyleDTO.class);
         List<PersonStyleDTO> personsDTOs = q4.getResultList();
         
         for (PersonStyleDTO ps : personsDTOs) {
             
             System.out.println("Navn: "+ps.getName()+", "+ps.getYear()+", "+ps.getSwimStyle());
            
        }
         */
    }
    
   
    
    ///List<Object>
    
    
    
}
