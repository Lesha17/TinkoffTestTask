package com.lmm.tinkoff.task.endpoint;

import com.lmm.tinkoff.task.*;
import com.lmm.tinkoff.task.search.FindNumberException;
import com.lmm.tinkoff.task.search.NumberSearcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigInteger;
import java.util.Collection;

@Endpoint
public class FindNumberEndpoint {

    private ObjectFactory objectFactory = new ObjectFactory();

    @Autowired
    private Logger logger;

    @Autowired
    private ResultDAO resultDAO;

    @Autowired
    private NumberSearcher numberSearcher;

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "FindNumberRequest")
    @ResponsePayload
    public FindNumberResponse findNumber(@RequestPayload FindNumberRequest request) {

        try { // Spring soap exceptions handle mechanism is too complex, so why do more work
            FindNumberResponse response;

            int number = request.getNumber().intValue();
            if (!BigInteger.valueOf(number).equals(request.getNumber())) {
                throw new NumberIsTooLargeException();
            }

            Collection<String> foundIn = numberSearcher.findNumber(number);

            response = createResponse();
            if (!foundIn.isEmpty()) {
                response.getResult().setCode(Code.OK);

                FileNamesList fileNamesList = objectFactory.createFileNamesList();
                fileNamesList.getFileName().addAll(foundIn);
                response.getResult().setFileNames(fileNamesList);
            } else {
                response.getResult().setCode(Code.NOT_FOUND);
            }

            resultDAO.insertResult(BigInteger.valueOf(number), response.getResult());

            return response;
        } catch (NumberIsTooLargeException e) {
            return handleIncorrectInputException(e);
        } catch (FindNumberException e) {
            return handleFindNumberException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private FindNumberResponse handleIncorrectInputException(NumberIsTooLargeException e) {
        logger.error(e.getMessage(), e);

        FindNumberResponse response = createErrorResponse();
        response.getResult().setError(e.getMessage());
        return response;
    }

    private FindNumberResponse handleFindNumberException(FindNumberException e) {
        logger.error(e.getMessage(), e);

        FindNumberResponse response = createErrorResponse();
        response.getResult().setError("Some error occurred while searching for a number");
        return response;
    }

    private FindNumberResponse handleException(Exception e) {
        logger.error(e.getMessage(), e);

        FindNumberResponse response = createErrorResponse();
        response.getResult().setError("Internal server error");
        return response;
    }

    private FindNumberResponse createErrorResponse() {
        FindNumberResponse response = createResponse();
        response.getResult().setCode(Code.ERROR);
        return response;
    }

    private FindNumberResponse createResponse() {
        FindNumberResponse response = objectFactory.createFindNumberResponse();
        Result result = objectFactory.createResult();
        response.setResult(result);
        return response;
    }
}
