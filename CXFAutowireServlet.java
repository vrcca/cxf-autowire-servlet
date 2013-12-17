package com.pitang.infra.web.component;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jws.WebService;
import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This class autowires all classes with the annotation {@link Service} and {@link WebService}.
 * 
 * This class uses the {@link WebService} name property for the web service name.
 * 
 * Example:
 * 
 * <pre>
 * &#064;Service
 * &#064;WebService(name="FooTestService") 
 *   public class FooService {
 *      (...) 
 *   }
 * </pre>
 * 
 * <p>
 * The above example would point the web service to /FooTestService
 * </p>
 * 
 * @author vrcca
 * 
 */
public class CXFAutowireServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig servletConfig) {
        super.loadBus(servletConfig);

        // gets spring's application context
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        // searches all spring beans with the annotation @Service
        Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(Service.class);

        Set<Entry<String, Object>> beans = beansWithAnnotation.entrySet();
        for (Entry<String, Object> serviceBean : beans) {
            Object bean = serviceBean.getValue();
            Class<? extends Object> beanClass = bean.getClass();

            // searches the annotation @WebService
            WebService webService = beanClass.getAnnotation(WebService.class);

            // registers the web service
            if (webService != null) {
                String webServiceName = webService.name();
                Endpoint.publish("/" + webServiceName, bean);
            }
        }
    }
}
