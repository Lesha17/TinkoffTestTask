package com.lmm.tinkoff.task;

import com.lmm.tinkoff.task.index.*;
import com.lmm.tinkoff.task.search.FileDefinedFilesProvider;
import com.lmm.tinkoff.task.search.FilesProvider;
import com.lmm.tinkoff.task.search.IndexedNumberSearcher;
import com.lmm.tinkoff.task.search.NumberSearcher;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
import java.io.File;
import java.io.IOException;

@EnableWs
@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
public class WebServiceConfig extends WsConfigurerAdapter {

    public static final String NAMESPACE_URI = "https://vk.com/madness_mimr/com/lmm/TinkoffTestTask";

    public static final String JDBC_URL_PROPERTY_NAME = "com.lmm.tinkoff.task.jdbc.url";
    public static final String FILES_LIST_PROPERTY_NAME = "com.lmm.tinkoff.task.files_list";
    public static final String INDEX_FILES_PROPERTY_NAME = "com.lmm.tinkoff.task.index_dir";

    @Value("${" + JDBC_URL_PROPERTY_NAME + "}")
    private String jdbcUrl;

    @Value("${" + FILES_LIST_PROPERTY_NAME + "}")
    private String filesListLocation;

    @Value("${" + INDEX_FILES_PROPERTY_NAME + "}")
    private String indexFilesLocation;

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger("application");
    }

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

    @Bean
    public FilesProvider filesProvider() {
        return new FileDefinedFilesProvider(new File(filesListLocation));
    }

    @Bean
    public IndexFilesProvider indexProvider() {
        File indexFilesDir = new File(indexFilesLocation);
        if (!indexFilesDir.exists()) {
            indexFilesDir.mkdirs();
        }

        return new IndexFilesProvider(indexFilesDir);
    }

    @Bean
    public IndexCreator indexCreator() {
        return new IndexCreator();
    }

    @Bean
    public IndexReader indexReader() {
        return new IndexReader();
    }

    @Bean
    public Indexer indexer(FilesProvider filesProvider, IndexFilesProvider indexFilesProvider, IndexCreator indexCreator, IndexReader indexReader) {
        return new BlockingIndexer(filesProvider, indexFilesProvider, indexCreator, indexReader);
    }

    @Bean
    public NumberSearcher numberSearcher(FilesProvider filesProvider, IndexFilesProvider indexFilesProvider, IndexReader indexReader) throws IOException {
        return new IndexedNumberSearcher(filesProvider, indexFilesProvider, indexReader);
    }
}
