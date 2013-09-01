package com.jl.crm.web;


import org.h2.server.web.WebServlet;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.*;

/**
 * loads the <A href="http://127.0.0.1:8080/h2/">web-based H2 database console</A>.
 * <p/>
 * To access the database for this application, use the JDBC URI {@code jdbc:h2:mem:crm}.
 *
 * @author Josh Long
 */
public class H2EmbeddedDatbaseConsoleInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebServlet webServlet = new WebServlet();

		ServletRegistration.Dynamic h2Servlet = servletContext.addServlet("H2Console", webServlet);
		h2Servlet.setInitParameter("trace", "true");
		h2Servlet.setAsyncSupported(true);
		h2Servlet.addMapping("/h2/*");
	}

}
