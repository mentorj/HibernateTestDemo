package com.javaxpert.demos.jpa;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
public class JPATest {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    private static Logger logger;
    @BeforeClass
    public static void setUpTest(){
        logger = LoggerFactory.getLogger(JPATest.class);

        emf = Persistence.createEntityManagerFactory("Hello");
        em = emf.createEntityManager();

        logger.debug("test case setup is ok...");

    }
    @Test
    public void testCachingWorks(){
        assertEquals(null,null);
    }
}
