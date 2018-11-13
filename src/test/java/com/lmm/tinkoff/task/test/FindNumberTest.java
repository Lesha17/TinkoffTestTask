package com.lmm.tinkoff.task.test;


import com.lmm.tinkoff.task.FindNumberEndpoint;
import com.lmm.tinkoff.task.FindNumberRequest;
import com.lmm.tinkoff.task.ObjectFactory;
import com.lmm.tinkoff.task.WebServiceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.RequestCreators;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import java.math.BigInteger;

import static org.springframework.ws.test.server.ResponseMatchers.noFault;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebServiceConfig.class, FindNumberEndpoint.class})
public class FindNumberTest {

    @Autowired
    private ApplicationContext context;

    private ObjectFactory objectFactory = new ObjectFactory();

    private MockWebServiceClient client;

    private Marshaller marshaller;

    @Before
    public void init() throws Exception {
        client = MockWebServiceClient.createClient(context);
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        marshaller = jaxbContext.createMarshaller();
    }


    @Test
    public void testRequestIsSuccess() throws Exception {
        Source payload = createRequestPayload(2);
        System.out.println(payload);

        client.sendRequest(RequestCreators.withPayload(payload))
                .andExpect(noFault());
    }

    private Source createRequestPayload(int number) throws JAXBException {

        FindNumberRequest findNumberRequest = objectFactory.createFindNumberRequest();
        findNumberRequest.setNumber(BigInteger.valueOf(number));

        StringResult result = new StringResult();
        marshaller.marshal(findNumberRequest, result);
        return new StringSource(result.toString());
    }
}
