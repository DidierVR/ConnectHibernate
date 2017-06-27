/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connecthibernate;

/**
 *
 * @author Didier
 */
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class StoreData {

    private static SessionFactory factory;
    private static ServiceRegistry serviceRegistry;

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
        
        factory.close();
        System.out.println("Factory closed");
        
        StandardServiceRegistryBuilder.destroy(serviceRegistry);

    }

    public static void createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        factory = configuration.buildSessionFactory(serviceRegistry);
    }
    
    public static void listEntries( ){
      Session session = factory.openSession();
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
         List entries = session.createQuery("FROM SimpleClass").list(); 
         for (Iterator iterator = 
                           entries.iterator(); iterator.hasNext();){
            SimpleClass sc = (SimpleClass) iterator.next(); 
            System.out.print("Id: " + sc.getId()); 
            System.out.print("  Name: " + sc.getName()); 
            System.out.println("  Value: " + sc.getValue()); 
         }
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
          System.out.println("Close session");
         session.close(); 
      }
   }

}
