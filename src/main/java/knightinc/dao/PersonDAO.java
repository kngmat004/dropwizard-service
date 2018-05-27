package knightinc.dao;

import io.dropwizard.hibernate.AbstractDAO;
import knightinc.core.Person;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public class PersonDAO extends AbstractDAO<Person> {

    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public List<Person> getAll() {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<Person> criteriaQuery = builder.createQuery(Person.class);
        criteriaQuery.from(Person.class);
        return currentSession().createQuery(criteriaQuery).getResultList();
    }

    public List<Person> findAll() {
        return list(namedQuery("knightinc.core.core.Person.findAll"));
    }

    public void delete(Person person) {
        currentSession().delete(person);
    }

    public void update(Person person) {
        currentSession().saveOrUpdate(person);
    }

    public Person insert(Person person) {
        return persist(person);
    }

    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

}
