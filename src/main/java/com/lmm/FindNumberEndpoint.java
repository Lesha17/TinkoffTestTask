package com.lmm;

import com.lmm.tinkoff.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigInteger;

@Endpoint
public class FindNumberEndpoint {

    private ObjectFactory objectFactory = new ObjectFactory();

    private ResultDAO resultDAO;

    @Autowired
    public FindNumberEndpoint(ResultDAO resultDAO) {
        this.resultDAO = resultDAO;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "FindNumberRequest")
    @ResponsePayload
    public FindNumberResponse findNumber(@RequestPayload FindNumberRequest request) {
        BigInteger number = request.getNumber();

        Result result = objectFactory.createResult();
        result.setCode(Code.NOT_FOUND);

        FindNumberResponse response = objectFactory.createFindNumberResponse();
        response.setResult(result);

        resultDAO.insertResult(number, result);

        return response;
    }
}
