package com.jl.crm.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jl.crm.services.ServiceConfiguration;
import com.mangofactory.swagger.configuration.DocumentationConfig;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.*;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * initializes the web application. This is a programmatic equivalent to
 * {@literal web.xml}.
 * {@link AbstractAnnotationConfigDispatcherServletInitializer} sets up the
 * Servlet-3.0 application <EM>and</EM> bootstraps the main
 * {@link org.springframework.context.ApplicationContext application context}
 * instance that powers the Spring MVC application.
 * 
 * @author Josh Long
 */
public class CrmWebApplicationInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	private int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { ServiceConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebMvcConfiguration.class,
				DocumentationConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new HiddenHttpMethodFilter(),
				new MultipartFilter() };
	}

	@Override
	protected void customizeRegistration(
			ServletRegistration.Dynamic registration) {
		File uploadDirectory = ServiceConfiguration.CRM_STORAGE_UPLOADS_DIRECTORY;
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
				uploadDirectory.getAbsolutePath(), maxUploadSizeInMb,
				maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
		registration.setMultipartConfig(multipartConfigElement);
	}
}

//public class CrmWebApplicationInitializer implements WebApplicationInitializer {
//
//	@Override
//	public void onStartup(ServletContext servletContext)
//			throws ServletException {
//		// Create the 'root' Spring application context
//		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
//		ctx.register(ServiceConfiguration.class, WebMvcConfiguration.class,
//				DocumentationConfig.class);
//
//		// Manages the lifecycle
//		servletContext.addListener(new ContextLoaderListener(ctx));
//
//		// Spring WebMVC
//		ServletRegistration.Dynamic springWebMvc = servletContext.addServlet(
//				"dispatcher", new DispatcherServlet(ctx));
//		springWebMvc.setLoadOnStartup(1);
//		springWebMvc.addMapping("/");
//		springWebMvc.setAsyncSupported(true);
//
//	}
//
//}

@Configuration
@ComponentScan
@EnableWebMvc
class WebMvcConfiguration extends WebMvcConfigurerAdapter {
	private List<HttpMessageConverter<?>> messageConverters;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:index.html");
	}

	/**
	 * The message converters for the content types we support.
	 * 
	 * @return the message converters; returns the same list on subsequent calls
	 */
	private List<HttpMessageConverter<?>> getMessageConverters() {
		if (messageConverters == null) {
			messageConverters = new ArrayList<>();

			MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
			final ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
					false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
			mappingJacksonHttpMessageConverter.setObjectMapper(mapper);
			messageConverters.add(mappingJacksonHttpMessageConverter);
		}
		return messageConverters;
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		converters.addAll(getMessageConverters());
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public static PropertyPlaceholderConfigurer swaggerProperties() {
		final PropertyPlaceholderConfigurer swaggerProperties = new PropertyPlaceholderConfigurer();
		swaggerProperties.setLocation(new ClassPathResource("swagger.properties"));
		swaggerProperties.setIgnoreUnresolvablePlaceholders(true);
		return swaggerProperties;
	}

}