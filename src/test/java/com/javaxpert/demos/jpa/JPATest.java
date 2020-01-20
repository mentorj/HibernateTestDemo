package com.javaxpert.demos.jpa;

import com.javaxpert.demos.appenders.StaticAppender;
import com.javaxpert.demos.entities.Product;
import org.hibernate.Session;
import org.hibernate.stat.SessionStatistics;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;
public class JPATest {
    private  EntityManagerFactory emf;
    private  EntityManager em;

    private static Logger logger;

    // number of messages to be found if test is ok when grabbing the good pattern
    // in the Hibernate L2 caching test
    // @TODO FIXME please
    private final static int OCCURENCES_MAX = 2;

    // Pattern to be found in  the Hibernate caching test case
    // @TODO FIXME PLEASE
    private final static String PATTERN_SEARCHED_IN_LOGGER="";

    private final static int MAX_ENTITIES_IN_CONTAINERS=500000;

    // @TODO FIXME PLEASE
    private final static int CHECK_HIB_L2_MAX_OCCURENCES=1;

    // @TODO FIXME PLEASE
    private final static String CHECK_HIB_L2_PATTERN="nanoseconds spent performing 0 L2C hits";


    @Before
    // ensure to reset the custom appender before each test
    public void resetLogger(){
        StaticAppender.clearEvents();
    }

    @Before
    public  void setUpTest(){
        logger = LoggerFactory.getLogger(JPATest.class);

        emf = Persistence.createEntityManagerFactory("Hello");
        em = emf.createEntityManager();
        logger.debug("test case setup is ok...");
        Product p1 = new Product();
        p1.setProductId("001");
        p1.setDescription("un produit");
        p1.setProductName("un truc");
        em.getTransaction().begin();
        em.persist(p1);
        em.getTransaction().commit();
        em.close();
        logger.trace("setup finished after insert into db");
    }
    @Test
    /**
     * assert that L1 caching works
     * so one single query sent to dn
     */
    public void testSessionCachingWorks(){
        logger.trace("starting cache test case");
        em=emf.createEntityManager();
        em.getTransaction().begin();
        Product p1 = em.find(Product.class,"001");
        logger.debug("retrieved Product" + p1.getProductName());
        Product p2= em.find(Product.class,"001");
        logger.debug("retrieved Product" + p2.getProductName());
        em.getTransaction().commit();
        assertEquals(p1,p2);
        assertTrue(StaticAppender.getEvents().contains(PATTERN_SEARCHED_IN_LOGGER));
        // use the Appender to ensure that only one occurence for the message comes....
        assertEquals(StaticAppender.getEvents().stream().filter(iLoggingEvent -> iLoggingEvent.getMessage().contains(PATTERN_SEARCHED_IN_LOGGER)).count(),OCCURENCES_MAX);
    }

    /**
     * assertThat L2 caching works
     * so retrieving the same entity from database from2 different sessions
     * induce one single query and one cache hit so checking hit ratio is useful here
     */
    //@TODO FIXME!!!
    @Test
    public void testPojoWithL2Cache(){
        logger.info("starting Hibernate L2 cache test");
        em=emf.createEntityManager();

        // hack in JPA to get access to Hibernate specific Api
        Session session = em.unwrap(Session.class);
        SessionStatistics stats = session.getStatistics();
        em.getTransaction().begin();
        Product p1= em.find(Product.class,"001");
        logger.trace("retrieved Product 001:"+ p1);
        em.getTransaction().commit();

        // now open another session
        // retrieve the same object
        em.getTransaction().begin();
        p1= em.find(Product.class,"001");
        logger.trace("retrieved Product 001:"+ p1);
        em.getTransaction().commit();
        // now check in the Hibernate statistics log if the object has been fetched from cache or not
        // 2 ways : from stats object or from logs through StaticAppender
        //assertTrue(StaticAppender.getEvents().stream().filter(evt -> evt.getMessage().contains(CHECK_HIB_L2_PATTERN)).count()==CHECK_HIB_L2_MAX_OCCURENCES);
        //stats.
        assertTrue(true);
    }


    @Test
    public void testPojoCanBeUsedInsideHashedContainers(){
        HashSet<Product> hugeSet = new HashSet<>(2*MAX_ENTITIES_IN_CONTAINERS);
        Product p1 = new Product();
        p1.setProductId("001");
        p1.setDescription("un produit");
        p1.setProductName("un truc");
        hugeSet.add(p1);
        assertTrue(hugeSet.contains(p1));
        Product p0=null;
        for(int i=0;i < MAX_ENTITIES_IN_CONTAINERS;i++){
            Product p = new Product();
            p.setProductId(""+i);
            p.setProductName("productRef="+i);
            p.setDescription("foo is foobar");
            if(i==0)
                p0=p;
            hugeSet.add(p);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(hugeSet.contains(p0));
        assertTrue(hugeSet.contains(p0));
    }


}
