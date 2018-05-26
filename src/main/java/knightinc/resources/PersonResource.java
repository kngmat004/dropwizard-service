package knightinc.resources;


import io.dropwizard.hibernate.UnitOfWork;
import knightinc.BookerApplication;
import knightinc.core.Person;
import knightinc.dao.PersonDAO;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/person")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PersonResource {
    private final static Logger logger = LoggerFactory.getLogger(PersonResource.class);

    PersonDAO personDAO;

    public PersonResource(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GET
    @UnitOfWork
    public List<Person> getAll(){
        logger.debug("Get all persons");
        return personDAO.getAll();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Person get(@PathParam("id") Integer id){
        logger.debug("Get individual person:  {}", id);
        return personDAO.findById(id);
    }

    @POST
    @UnitOfWork
    public Person add(@Valid Person person) {
        Person newPerson = personDAO.insert(person);

        return newPerson;
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Person update(@PathParam("id") Integer id, @Valid Person person) {
        person = person.setId(id);
        personDAO.update(person);

        return person;
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void delete(@PathParam("id") Integer id) {
        personDAO.delete(personDAO.findById(id));
    }
}
