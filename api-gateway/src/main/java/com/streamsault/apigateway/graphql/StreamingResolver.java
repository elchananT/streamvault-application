package com.streamsault.apigateway.graphql;

import com.streamvault.proto.streaming.StreamingGrpcServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StreamingResolver {
    private final StreamingGrpcServiceGrpc.StreamingGrpcServiceBlockingStub streamingStub;


}
