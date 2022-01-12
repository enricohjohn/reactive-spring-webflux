package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    static String MOVIES_INFO_URL = "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Test
    void getAllMoviesTest(){

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(movieInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getAllMovieInfosByIdTest() {

        //given
        var id = "abc";

        var movieinfo = new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        //when
        when(movieInfoServiceMock.getMovieInfosById(id)).thenReturn(Mono.just(movieinfo));

        //then
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}",id )
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoFound = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoFound  !=null;
                    assert movieInfoFound.getMovieInfoId() !=null;
                });


    }

    @Test
    void addMovieInfo() {
        //given
        var movieInfo = new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        //when
        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        //then
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoFound = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoFound.getName().equals(movieInfo.getName());
                    assert movieInfoFound.getMovieInfoId() !=null;
                   // assertEquals("mockId", movieInfoFound.getMovieInfoId());
                });

    }

    @Test
    void addMovieInfo_validation() {
        //given
        var movieInfo = new MovieInfo(null, "",
                null, List.of(" "), LocalDate.parse("2012-07-20"));

        //then
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                            var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                            System.out.println("Response Body: " + responseBody);
                            var expectedErrorMessage = "movieInfo.cast must be present,movieInfo.name must be present,não deve ser nulo";
                            assert responseBody != null;
                            assertEquals(expectedErrorMessage, responseBody);
                        });
    }

    @Test
    void deleteMovieInfo() {

        var id = "abc";

        //when
        OngoingStubbing<Mono<Mono<Object>>> when = when(movieInfoServiceMock.deleteMovieInfo(isA(String.class))
                .thenReturn(Mono.empty()));
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent()
        ;
    }
}
