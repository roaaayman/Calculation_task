package service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Calculation;

import java.util.List;

@Stateless
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CalculationService {

    @PersistenceContext(unitName = "calculation")
    private EntityManager entityManager;

    @POST
    @Path("calc")
    public Response createCalculation(Calculation calculationData) {
        int number1 = calculationData.getNumber1();
        int number2 = calculationData.getNumber2();
        String operation = calculationData.getOperation();

        try {
            int result = performCalculation(number1, number2, operation);

            Calculation calculation = new Calculation(number1, number2, operation, result);
            entityManager.persist(calculation);

            return Response.status(Response.Status.OK).entity(calculation).build();
        } catch (IllegalArgumentException e) {
             return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("calculations")
    public Response getAllCalculations() {
        try {
            List<Calculation> calculations = entityManager.createQuery("SELECT c FROM Calculation c", Calculation.class).getResultList();
            return Response.status(Response.Status.OK).entity(calculations).build();
        } catch (Exception e) {
            // Return error response with status 500
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    private int performCalculation(int number1, int number2, String operation) {
        switch (operation) {
            case "+":
                return number1 + number2;
            case "-":
                return number1 - number2;
            case "*":
                return number1 * number2;
            case "/":
                if (number2 != 0) {
                    return number1 / number2;
                } else {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
