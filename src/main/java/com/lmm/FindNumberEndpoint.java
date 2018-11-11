package com.lmm;

import com.lmm.tinkoff.task.FindNumberRequest;
import com.lmm.tinkoff.task.FindNumberResponse;
import com.lmm.tinkoff.task.ObjectFactory;
import com.lmm.tinkoff.task.Result;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class FindNumberEndpoint {

    private ObjectFactory objectFactory = new ObjectFactory();

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "FindNumberRequest")
    @ResponsePayload
    public FindNumberResponse findNumber(@RequestPayload FindNumberRequest request) {
        Result result = objectFactory.createResult();
        result.setCode("01.Result.NotFound");

        FindNumberResponse response = objectFactory.createFindNumberResponse();
        response.setResult(result);

        return response;
    }
}
