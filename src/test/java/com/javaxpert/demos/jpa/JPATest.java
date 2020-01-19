package com.javaxpert.demos.jpa;

import com.javaxpert.demos.appenders.StaticAppender;
import com.javaxpert.demos.entities.Product;
import org.junit.Before;
import org.junit.BeforeClass;
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
    public void testCachingWorks(){
        logger.trace("starting cache test case");
        em=emf.createEntityManager();
        Product p1 = em.find(Product.class,"001");
        logger.debug("retrieved Product" + p1.getProductName());
        Product p2= em.find(Product.class,"001");
        logger.debug("retrieved Product" + p2.getProductName());
        assertEquals(p1,p2);
        assertTrue(StaticAppender.getEvents().contains(PATTERN_SEARCHED_IN_LOGGER));
        // use the Appender to ensure that only one occurence for the message comes....
        assertEquals(StaticAppender.getEvents().stream().filter(iLoggingEvent -> iLoggingEvent.getMessage().contains(PATTERN_SEARCHED_IN_LOGGER)).count(),OCCURENCES_MAX);
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


        assertTrue(hugeSet.contains(p0));

    }
}
