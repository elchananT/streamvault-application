package com.streamsault.apigateway;

import com.streamvault.proto.content.ContentGrpcServiceGrpc;
import com.streamvault.proto.content.ContentResponse;
import com.streamvault.proto.content.ListContentResponse;
import com.streamvault.proto.streaming.StreamingGrpcServiceGrpc;
import com.streamvault.proto.user.UserGrpcServiceGrpc;
import com.streamvault.proto.watchlist.WatchlistGrpcServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Content Resolver GraphQL Tests")
public class ContentResolverTest {

    @Autowired WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean ContentGrpcServiceGrpc.ContentGrpcServiceBlockingStub contentStub;
    @MockitoBean UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;
    @MockitoBean StreamingGrpcServiceGrpc.StreamingGrpcServiceBlockingStub streamingStub;
    @MockitoBean WatchlistGrpcServiceGrpc.WatchlistGrpcServiceBlockingStub watchlistStub;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Query content(id) -> returns content fields")
    void getContentById() throws Exception {
        UUID mockContentId = UUID.randomUUID();
        var mockResponse = ContentResponse.newBuilder()
                .setContentId(mockContentId.toString())
                .setTitle("Spiderman Brand New Day")
                .setGenre("Sci-Fi")
                .setType("MOVIE")
                .setRating((float) 9.9)
                .setReleaseYear(2026)
                .setFound(true)
                .build();

        when(contentStub.getContentById(any())).thenReturn(mockResponse);

        String request = """
                query {
                    content(id: %s) {
                        id,
                        title,
                        genre,
                        type,
                        rating
                    }
                }
                """.formatted(mockContentId.toString());

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.title").value("Spiderman Brand New Day"))
                .andExpect(jsonPath("$.data.content.genre").value("Sci-Fi"));
    }

    @Test
    @DisplayName("Query listContent -> returns list")
    void getListContent() throws Exception {
        var mockResponse = ContentResponse.newBuilder()
                .setContentId(UUID.randomUUID().toString())
                .setTitle("Spiderman Brand New Day")
                .setGenre("Sci-Fi")
                .setType("MOVIE")
                .setRating((float) 9.9)
                .setReleaseYear(2026)
                .setFound(true)
                .build();

        var listResponse = ListContentResponse.newBuilder()
                        .addAllContent(List.of(mockResponse))
                                .setTotal(1)
                                .build();

        when(contentStub.listContent(any())).thenReturn(listResponse);

        String request = """
                query {
                    listContent(page: 0, size: 10) {
                        id,
                        title
                    }
                }
        """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.listContent[0].title").value("Spiderman Brand New Day"));
    }
}
