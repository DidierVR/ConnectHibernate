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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class StoreData {
    
    public static void main(String[] args) {
        // TODO code application logic here
        // create config object (stap 1 bij het opzetten van een hibernate sessie)
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");  //data config bestand inlezen en instellen
		
		// create session factory object (stap 2)
		SessionFactory factory = cfg.buildSessionFactory();
		
		// create session object (eigenlijk start van de hibernate sessie)
		Session session = factory.openSession();
		
		// create transaction object
		Transaction t = session.beginTransaction();
		
		SimpleClass sc1 = new SimpleClass();
		sc1.setId(1);
		sc1.setName("didier");
		sc1.setValue(10.5);
		
		session.persist(sc1); // persisting the object (WAT DAT OOK BETEKENT)
		
		t.commit();  // BELANGRIJK: transacte is pas een feit als ze ge"commit" wordt
		session.close();
		
		System.out.println("save ok");
    }
    
}
