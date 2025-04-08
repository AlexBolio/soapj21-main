package mx.com.edifact.merida.soapj21;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;


/**
 *
 * @author rgarcia
 */
@WebService(serviceName = "ExampleService")
public class ExampleService {

    
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
}
