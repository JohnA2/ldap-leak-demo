package org.example;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.cfg.Configuration;

@WebListener
public class LDAPLeakDemo implements ServletContextListener {
	public void contextInitialized(ServletContextEvent sce) {
		useLDAP();
		useHibernate(); // It just increases PermGen usage which helps to demonstrate OutOfMemoryError
	}
	
	public void contextDestroyed(ServletContextEvent sce) {}
	
	private void useLDAP() {
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://ldap.forumsys.com:389");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=read-only-admin,dc=example,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "password");
		try {
			DirContext ctx = null;
			try {
				ctx = new InitialDirContext(env);
				System.out.println("Created the initial context");
			} finally {
				if (ctx != null) {
					ctx.close(); 
					System.out.println("Closed the context");
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private void useHibernate() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		new Configuration().setProperties(properties).buildSessionFactory();
	}
}
