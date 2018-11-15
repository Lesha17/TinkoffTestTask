package com.lmm.tinkoff.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Endpoint
public class FindNumberEndpoint {

    private ObjectFactory objectFactory = new ObjectFactory();

    private ResultDAO resultDAO;
    private NumberSearcher numberSearcher;

    @Autowired
    public FindNumberEndpoint(ResultDAO resultDAO, NumberSearcher numberSearcher) {
        this.resultDAO = resultDAO;
        this.numberSearcher = numberSearcher;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "FindNumberRequest")
    @ResponsePayload
    public FindNumberResponse findNumber(@RequestPayload FindNumberRequest request) {
        int number = request.getNumber().intValue();
        if(!BigInteger.valueOf(number).equals(request.getNumber())) {
            throw new IllegalArgumentException("Given number is too large");
        }

        Collection<String> foundIn = numberSearcher.findNumber(number);

        Result result = objectFactory.createResult();
        if(!foundIn.isEmpty()) {
            result.setCode(Code.OK);

            FileNamesList fileNamesList = objectFactory.createFileNamesList();
            fileNamesList.getFileName().addAll(foundIn);
            result.setFileNames(fileNamesList);
        } else {
            result.setCode(Code.NOT_FOUND);
        }

        FindNumberResponse response = objectFactory.createFindNumberResponse();
        response.setResult(result);

        resultDAO.insertResult(BigInteger.valueOf(number), result);

        return response;
    }
}
