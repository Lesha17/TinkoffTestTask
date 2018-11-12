package com.lmm;

import org.postgresql.Driver;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import javax.sql.DataSource;

@EnableWs
@Configuration
@ComponentScan
public class WebServiceConfig extends WsConfigurerAdapter {

    public static final String NAMESPACE_URI = "https://vk.com/madness_mimr/com/lmm/TinkoffTestTask";

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/soap/*");
    }

    @Bean(name = "result")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema resultSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();

        definition.setPortTypeName("FindNumberPort");
        definition.setLocationUri("/soap");
        definition.setTargetNamespace(NAMESPACE_URI);
        definition.setSchema(resultSchema);

        return definition;
    }

    @Bean
    public XsdSchema resultSchema() {
        return new SimpleXsdSchema(new ClassPathResource("result.xsd"));
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tinkoff_task");

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
