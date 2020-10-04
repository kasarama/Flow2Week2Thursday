Status:

On master :

- To entities with one to one relation. When relatin is bi-directional throuht if statement:      
     
        public void setAddress(Address address) {        
          this.address = address; 
            if (address!=null){           
                address.setPerson(this);
            }
        }
    
 - java.lang.StackOverflowError occures.
  
 - Implementation of PersonFacade and PersonResource with the corresponding tests.
  
 - Implementation of two custom exceptions with the corresponding negativ tests.
    
    

