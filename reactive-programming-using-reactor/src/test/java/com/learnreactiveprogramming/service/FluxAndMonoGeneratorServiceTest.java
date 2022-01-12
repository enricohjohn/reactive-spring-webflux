package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {


    FluxAndMonoGeneratorService fluxAndMonoGeneratorService
                = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben", "chle")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(4);

        StepVerifier.create(namesFlux)
            .expectNext("5-CHLOE")
            .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_async() {

        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        StepVerifier.create(namesFlux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();

    }

    @Test
    void namesFlux_concatmap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
               // .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
    }

    @Test
    void namesMono_flatMap() {

        //given
        int stringLength = 3;

        //when
        var value = fluxAndMonoGeneratorService.namesMono_flatMap(stringLength);

        //then
        StepVerifier.create(value)
                .expectNext(List.of("A","L","E","X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {

        //given
        int stringLength = 3;

        //when
        var value = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        //then
        StepVerifier.create(value)
                .expectNext("A","L","E","X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E","E","N","R","I","C","O","C","A","R","L","O","S")
                //.expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        //given
        //when
            var concatFlux = fluxAndMonoGeneratorService.explore_concat();
        //then
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void explore_merge() {

        //when
            var mergeFlux = fluxAndMonoGeneratorService.explore_merge();

        StepVerifier.create(mergeFlux)
                .expectNext("D","A","E","B","F","C")
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        //when
        var mergeFlux = fluxAndMonoGeneratorService.explore_mergeSequential();

        StepVerifier.create(mergeFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
    //when
    var mergeFlux = fluxAndMonoGeneratorService.exploreZip();
    //then
        StepVerifier.create(mergeFlux)
                .expectNext("AD","BE","CF")
                .verifyComplete();

    }
    @Test
    void exploreZip1() {
        //when
        var mergeFlux = fluxAndMonoGeneratorService.exploreZip1();
        //then
        StepVerifier.create(mergeFlux)
                .expectNext("AD14","BE25","CF36")
                .verifyComplete();
    }

    @Test
    void explorey() {

        //given
        int stringLength = 3;

        //when
        var value = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        //then
        StepVerifier.create(value)
                .expectNext("A","L","E","X")
                .verifyComplete();
    }

}