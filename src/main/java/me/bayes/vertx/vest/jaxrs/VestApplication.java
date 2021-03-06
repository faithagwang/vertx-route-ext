/**
 * 
 */
package me.bayes.vertx.vest.jaxrs;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaFileObject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.deploy.impl.java.PackageHelper;

/**
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 * 
 * TODO: GET CLASSES
 * TODO: GET PROPERTIES
 * TODO: GET SINGLETONS
 * TODO: Package scanning of sub packages
 */
public abstract class VestApplication extends Application {
	
	private final static Logger LOG = LoggerFactory.getLogger(VestApplication.class);
	
	/*
	 * Classloader to load scanned classes.
	 */
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	/*
	 * Used if you want to scan packages for all classes annotated with
	 * @Path.
	 */
	private Set<String> packagesToScan = new HashSet<String>(0);
	
	/*
	 * Used to specify the classes annotated with @Path.
	 */
	private final Set<Class<?>> endpointClasses = new HashSet<Class<?>>(0); 
	
	
	public Set<String> getPackagesToScan() {
		return packagesToScan;
	}

	
	/**
	 * @param packagesToScan for classes annotated with {@link Path}. 
	 */
	public void addPackagesToScan(Collection<String> packagesToScan) {
		this.packagesToScan.addAll(packagesToScan);
		addScannedClasses();
	}
	
	public void addPackagesToScan(String... packagesToScan) {
		this.packagesToScan.addAll(Arrays.asList(packagesToScan));
		addScannedClasses();
	}

	public Set<Class<?>> getEndpointClasses() {
		return endpointClasses;
	}

	
	/**
	 * @param endpointClasses to add to the classes annotated with {@link Path}. 
	 * 		  If not annotated with {@link Path} it will be ignored.
	 */
	public void addEndpointClasses(Collection<Class<?>> endpointClasses) {
		for(Class<?> clazz : endpointClasses) {
			if(clazz.getAnnotation(Path.class) != null) {
				this.endpointClasses.add(clazz);
			}
		}
	}
	
	public void addEndpointClasses(Class<?>... endpointClasses) {
		for(Class<?> clazz : endpointClasses) {
			if(clazz.getAnnotation(Path.class) != null) {
				this.endpointClasses.add(clazz);
			}
		}
	}

	@Override
	public Set<Class<?>> getClasses() {
		return endpointClasses;
	}
	
	/*
	 * Use vertx's built in PackageHelper to scan for classes in the classpath. Sub packages are left out for now.
	 * 
	 * TODO: Think about rather using Reflections jar, but this adds a dependency.
	 */
	private void addScannedClasses() {
		
		PackageHelper helper = new PackageHelper(classLoader);

		for(String packageName : packagesToScan) {
			
			final String folderLocationName = packageName.replaceAll("[.]", "/");
			
			try {
				
				for(JavaFileObject javaObject : helper.find(packageName)) {
					
					try {
						
						final String clazzUriString = javaObject.toUri().toString();
						final String clazzName = clazzUriString.substring(
								clazzUriString.lastIndexOf(folderLocationName),
								clazzUriString.lastIndexOf('.'))
									.replaceAll("/", ".");
						final Class<?> clazz = classLoader.loadClass(clazzName);
						
						final Annotation[] annotations = clazz.getAnnotations();
						for(Annotation annotation : annotations) {
							if(annotation.annotationType().equals(Path.class)) {
								this.endpointClasses.add(clazz);
								break;
							}
						}
						
					} catch (ClassNotFoundException e) {
						LOG.error(String.format("Error occurred scanning package %s.", packageName), e);
					}
				}
				
			} catch (IOException e) {
				LOG.error(String.format("Error occurred scanning package %s.", packageName), e);
			}
		}

	}
	

}
