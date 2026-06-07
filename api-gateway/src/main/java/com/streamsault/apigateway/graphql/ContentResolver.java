package com.streamsault.apigateway.graphql;

import com.streamsault.apigateway.dto.ContentGraphqlResponse;
import com.streamvault.proto.content.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContentResolver {

    private final ContentGrpcServiceGrpc.ContentGrpcServiceBlockingStub contentStub;

    @QueryMapping
    public ContentGraphqlResponse content(@Argument String id) {
        ContentResponse response = contentStub.getContentById(
                GetContentByIdRequest.newBuilder().setContentId(id).build()
        );

        return response.getFound() ? toResponse(response) : null;
    }

    @QueryMapping
    public List<ContentGraphqlResponse> listContent(
            @Argument Integer page,
            @Argument Integer size,
            @Argument String genre
    ) {
        var response = contentStub.listContent(
                ListContentRequest.newBuilder()
                        .setPage(page != null ? page : 0)
                        .setSize(size != null ? size : 20)
                        .setGenre(genre != null ? genre : "")
                        .build());

        return response.getContentList().stream().map(this::toResponse).toList();
    }

    @QueryMapping
    public List<ContentGraphqlResponse> searchContent(@Argument String query) {
        var response = contentStub.searchContent(
                SearchContentRequest.newBuilder()
                        .setQuery(query)
                        .setPage(0)
                        .setSize(20)
                        .build());

        return response.getContentList().stream().map(this::toResponse).toList();
    }

    private ContentGraphqlResponse toResponse(ContentResponse response) {
        return new ContentGraphqlResponse(
                response.getContentId(), response.getTitle(),
                response.getDescription(), response.getGenre(),
                response.getType(),  response.getRating(),
                response.getReleaseYear(), response.getThumbnailUrl()
        );
    }
}
