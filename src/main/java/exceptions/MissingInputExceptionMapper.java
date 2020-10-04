package exceptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author magda
 */
@Provider
public class MissingInputExceptionMapper implements ExceptionMapper<MissingInputException>{

    static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Override
    public Response toResponse(MissingInputException exception) {
       
    Logger.getLogger(MissingInputException.class.getName())
          // .log(Level.INFO, "Input missing", exception)
            ;
    ExceptionDTO ex = new ExceptionDTO(400, exception.getMessage());
    return Response
            .status(400)
            .entity(GSON.toJson(ex))
            .type(MediaType.APPLICATION_JSON)
            .build();
    
    
    }

}
