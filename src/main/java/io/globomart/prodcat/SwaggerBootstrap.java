package io.globomart.prodcat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerBootstrap extends HttpServlet {
        /**
	 * 
	 */
	private static final long serialVersionUID = 7164828594278082864L;

		@Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setVersion("1.0.2");
            beanConfig.setSchemes(new String[]{"http"});
            beanConfig.setHost("localhost:2222");
            beanConfig.setBasePath("/");
            beanConfig.setResourcePackage("io.globomart.prodcat");
            beanConfig.setScan(true);
        }
    }