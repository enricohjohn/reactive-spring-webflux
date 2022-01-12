package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setup() {
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfos)
                .blockLast(); //Only allowed in test cases
        // Ensures all data is persisted before invoking the method/
    }

    @AfterEach
    void tearDown(){
        movieInfoRepository.deleteAll();
    }

    @Test
    void findAll() {
        var moviesInfoFlux = movieInfoRepository.findAll();
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        var moviesInfoMono = movieInfoRepository.findById("abc");
        StepVerifier.create(moviesInfoMono)
                //.expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {

        var movieInfo = new MovieInfo(null, "O Psicopata Americano",2000, List.of("Christian Bale", "Jared Leto"), LocalDate.parse("2000-12-22"));
        var moviesInfoMono = movieInfoRepository.save(movieInfo);

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assertEquals("O Psicopata Americano", movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {

        var movieInfo = movieInfoRepository.findById("abc").block();

        assert movieInfo != null;
        movieInfo.setYear(2021);
        var moviesInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo1 -> {
                    assertEquals(2021, movieInfo1.getYear());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieId() {
        //when
        movieInfoRepository.deleteById("abc").block();
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByYear() {
        var moviesInfoFlux = movieInfoRepository.findByYear(2005).log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByName() {
        var moviesInfoMono = movieInfoRepository.findByName("Batman Begins").log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(n -> {
                    assertEquals("Batman Begins", n.getName());
                })
                .verifyComplete();
    }
}