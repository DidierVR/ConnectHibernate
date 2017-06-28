/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connecthibernate;

import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author Didier Deze klasse bevat methodes om de Hibernate factory op te
 * zetten, en om queries uit te voeren.
 *
 * ZWAKKE PUNTEN (20170628): - een deel van de initialisatie van de
 * SessionFactory gebeurt direct in de main method
 * (cfg.configure("hibernate.cfg.xml");), een ander deel in een aparte methode
 * (public static void createSessionFactory()). - er worden 3 verschillende
 * zaken dooreen gehaald: de main method, de initialisatie van de SessionFactory
 * en de methodes om data uit de database te halen en te bewerken zitten
 * allemaal in deze class -> beter is om 3 klassen te creëren
 */
public class StoreData {

    private static SessionFactory factory;
    private static ServiceRegistry serviceRegistry;

    /**
     * Main method van programma ConnectHibernate
     *
     * DVR - 20170628 In een echt programma moet die main method in een
     * zinvollere klasse staan (bijvoorbeeld een klasse met de naam van het
     * project), en moet die klasse enkel het programma opstarten en laten
     * draaien. In deze klasse worden ook de factory ingesteld en de queries
     * gedefinieerd.
     *
     * @param args
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // create config object (stap 1 bij het opzetten van een hibernate sessie)
        System.out.println("Start configuratie");
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");  //data config bestand inlezen en instellen

        System.out.println("Start create factory");
        // create session factory object (stap 2)
        createSessionFactory();

        System.out.println("List entries");
        listEntries();

        System.out.println("Einde list");
        
        System.out.println("Update query");
        updateQuery();
        System.out.println("Nieuwe lijst");
        listEntries();
        
        //System.out.println("Insert query");
        //insertQuery();
        System.out.println("Change object");
        changeObject();
        
        System.out.println("Nieuwe lijst");
        listEntries();
        
        selectQuery();
        
        
        factory.close();
        System.out.println("Factory closed");

        StandardServiceRegistryBuilder.destroy(serviceRegistry);

    }

    /**
     * Initialiseer een Hibernate sessionFactory, op basis van de configuratie
     * die eerder werd aangemaakt.
     *
     * DVR - 20170628 Het factory object is een statisch object van de klasse;
     * dit kan, omdat er normaal slechts 1 factory object gemaakt wordt. In een
     * volgend project zal er een aparte klasse komen voor de initialisatie van
     * de Hibernate factory, en zal er een "gewoon" object worden aangemaakt dat
     * die factory bijhoudt.
     */
    public static void createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        factory = configuration.buildSessionFactory(serviceRegistry);
    }

    /**
     * Eenvoudige "select * from <tabel>" query.
     *
     * DVR - 20170628 Null entries in de tabel geven een foutmelding bij het
     * inlezen van de data. Deze methode is "mijn eerste Hibernate query",
     * vandaar de focus op het verkrijgen van eender welk resultaat ipv een
     * degelijk resultaat, inclusief foutafhandeling en opvangen van null
     * entries in de data.
     */
    public static void listEntries() {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List entries = session.createQuery("FROM SimpleClass").list();
            for (Iterator iterator
                    = entries.iterator(); iterator.hasNext();) {
                SimpleClass sc = (SimpleClass) iterator.next();
                System.out.print("Id: " + sc.getId());
                System.out.print("  Name: " + sc.getName());
                System.out.println("  Value: " + sc.getValue());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            //e.printStackTrace();
        } finally {
            System.out.println("Close session");
            session.close();
        }
    }

    /**
     * Update query uitvoeren
     * 
     * DVR - 20170628
     * Hardcoded query -> aanpasbaar maken
     */
    public static void updateQuery() {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("update SimpleClass set name=:n where id=:i");
            q.setParameter("n", "Xander");
            q.setParameter("i", 1);
            
            int status = q.executeUpdate();
            System.out.println(status);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            //e.printStackTrace();
        } finally {
            System.out.println("Close session");
            session.close();
        }
    }
    
    /**
     * Insert query uitvoeren
     * 
     * DVR - 20170628
     * Hardcoded query -> aanpasbaar maken
     */
    public static void insertQuery() {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SimpleClass sc = new SimpleClass(3,"Maya",15.1);
            session.save(sc);
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            //e.printStackTrace();
        } finally {
            System.out.println("Close session");
            session.close();
        }
    }

    /**
     * Update query uitvoeren door een object te veranderen ipv door een HQL Update query uit te voeren
     * 
     * DVR - 20170628
     * Hardcoded query -> aanpasbaar maken
     */
    public static void changeObject() {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // De volgende lijn leest een object in uit de database: de selectie gebeurt op basis van de Id waarde van
            // object, in dit geval is Id = 3 (2de parameter in load methode)
            SimpleClass sc = (SimpleClass)session.load(SimpleClass.class, 3);
            sc.setName("Ayam");
            // SESSION SAVE IS NIET NODIG, sc is een persistent object en wijzigingen hieraan worden
            // automatisch bewaard wanneer de transactei gecommit wordt
            //session.save(sc);
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            //e.printStackTrace();
        } finally {
            System.out.println("Close session");
            session.close();
        }
    }
    
        /**
     * Select query in HQL
     *
     * DVR - 20170628
     * Bedoeling is om een lijn uit de tabel te kiezen en dan een bepaalde waarde (hier de Id waarde)
     * uit te lezen. De select query genereert een list als resultaat, daaruit wordt dan het eerste object
     * gekozen om een waarde te genereren.
     * -> kan dat efficiënter? 1 enkel object als resultaat, vb. door een soort "TOP 1" selectie?
     * -> blijkbaar kan dat, met de uniqueResult methode
     */
    public static void selectQuery() {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SimpleClass sc = (SimpleClass)session.createQuery("from SimpleClass where id = 1").uniqueResult();
            System.out.println("Unique result is: " + sc.getId() + ", " + sc.getName() + ", " + sc.getValue());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            //e.printStackTrace();
        } finally {
            System.out.println("Close session");
            session.close();
        }
    }
}
