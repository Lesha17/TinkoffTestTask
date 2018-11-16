package com.lmm.tinkoff.task.test;


import com.lmm.tinkoff.task.*;
import com.lmm.tinkoff.task.endpoint.FindNumberEndpoint;
import com.lmm.tinkoff.task.endpoint.NumberIsTooLargeException;
import com.lmm.tinkoff.task.search.NumberSearcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.RequestCreators;
import org.springframework.ws.test.server.ResponseMatchers;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import java.math.BigInteger;
import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebServiceConfig.class, FindNumberEndpoint.class, TestConfig.class})
public class EndpointTest {

    @Autowired
    private TestCommons testCommons;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private NumberSearcher searcher;

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
        Source payload = createRequestPayload(BigInteger.valueOf(2));

        client.sendRequest(RequestCreators.withPayload(payload))
                .andExpect(ResponseMatchers.noFault());
    }

    @Test
    public void testTooLargeNumber() throws Exception {
        Source payload = createRequestPayload(BigInteger.valueOf(4000000000L));

        String expectedMessage = new NumberIsTooLargeException().getMessage();

        client.sendRequest(RequestCreators.withPayload(payload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(createResponsePayloadForError(expectedMessage)));
    }

    @Test
    public void testGetSomeExistingNumber() throws Exception {
        int someNumber = testCommons.getExistingNumber(testCommons.getSomeFile());
        Collection<String> expectedFiles = searcher.findNumber(someNumber);

        client.sendRequest(RequestCreators.withPayload(createRequestPayload(BigInteger.valueOf(someNumber))))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.payload(createResponsePayloadForExpectedResult(expectedFiles)));
    }

    private Source createRequestPayload(BigInteger number) throws JAXBException {

        FindNumberRequest findNumberRequest = objectFactory.createFindNumberRequest();
        findNumberRequest.setNumber(number);

        return serialize(findNumberRequest);
    }

    private Source createResponsePayloadForExpectedResult(Collection<String> filenames) throws JAXBException {
        FindNumberResponse response = objectFactory.createFindNumberResponse();
        Result result = objectFactory.createResult();
        response.setResult(result);

        if (!filenames.isEmpty()) {
            result.setCode(Code.OK);
            FileNamesList fileNamesList = objectFactory.createFileNamesList();
            fileNamesList.getFileName().addAll(filenames);
            result.setFileNames(fileNamesList);
        } else {
            result.setCode(Code.NOT_FOUND);
        }

        return serialize(response);
    }

    private Source createResponsePayloadForError(String expectedMessage) throws JAXBException {
        FindNumberResponse response = objectFactory.createFindNumberResponse();
        Result result = objectFactory.createResult();
        response.setResult(result);

        result.setCode(Code.ERROR);
        result.setError(expectedMessage);

        return serialize(response);
    }

    private Source serialize(Object object) throws JAXBException {
        StringResult stringResult = new StringResult();
        marshaller.marshal(object, stringResult);
        return new StringSource(stringResult.toString());
    }
}
