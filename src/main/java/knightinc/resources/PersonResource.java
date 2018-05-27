package knightinc.resources;


import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
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
import java.util.Optional;


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
    public Optional<Person> get(@PathParam("id") LongParam id){
        logger.debug("Get individual person:  {}", id);
        return personDAO.findById(id.get());
    }

    @POST
    @UnitOfWork
    public Person add(@Valid Person person) {
        logger.debug("Create person: {}", person.getFullName());
        return personDAO.insert(person);
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Person update(@PathParam("id") LongParam id, @Valid Person person) {
        logger.debug("Update person: {}", id);
        person = person.setId(id.get());
        personDAO.update(person);

        return person;
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void delete(@PathParam("id") LongParam id) throws NotFoundException {
        logger.debug("Delete person: {}", id);
        Person toBeDeletedPerson = findSafely(id.get());
        personDAO.delete(toBeDeletedPerson);
    }

    private Person findSafely(long personId) {
        return personDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
    }
}
