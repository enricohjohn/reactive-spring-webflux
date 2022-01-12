package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    //FIRST FLUX
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"));
    }

    //FIRST MONO
    public Mono<String> nameMono() {
        return Mono.just("alex");
    }


    //APPLY FILTERS ON THE STREAM ELEMENTS
    public Flux<String> namesFlux_map(int stringLength) {
        //filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(s-> s.length() + "-"+s)
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        //filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                //.map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(String::toUpperCase)
                .flatMap(s-> splitString(s))
                .log();
    }

    //TRANSFORM
    public Flux<String> namesFlux_transform(int stringLength) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s-> s.length() > stringLength);

        //filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(s-> splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    //DEFAULT VALUES
    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .flatMap(s-> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filterMap);

        //filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe", "eNRICO", "CARlos"))
                .transform(filterMap)
                //.defaultIfEmpty("default")
                .switchIfEmpty(defaultFlux)
                .log();
    }

    // ASYNCHRONOUS FLUX
    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        //filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                //.map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(String::toUpperCase)
                .flatMap(s-> splitStringWithDelay(s))
                .log();
    }

    // CONCATMAP
    public Flux<String> namesFlux_concatmap(int stringLength) {
        //filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                //.map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(String::toUpperCase)
                .concatMap(s-> splitStringWithDelay(s))
                .log();
    }

    // RETURNS THE FLUX OF CHARS OF A STRING
    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    // RETURNS THE FLUX OF CHARS OF A SPLIT STRING WITH DELAY
    public Flux<String> splitStringWithDelay(String name) {
        var charArray = name.split("");
        //int delay = new Random().nextInt(1000);
        var delay = 1000;
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    // FLUX ELEMENTS ARE IMMUTABLE AND CAN ONLY BE TRANSFORMED IF DONE DIRECTLY IN THE STREAM
    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex","ben","chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    //MAP A MONO ELEMENT FROM A SPLIT STRING
    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s->s.length() > stringLength);
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s->s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s->s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    public Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    /*
     ----  concat
     1-  Used to combine two reactive streams in one
     2-  Concatenation of Reactive Streams happens in a sequence
       - First one is subscribed first and completes
       - Second one is subscribed after that and then completes
     3- static method in Flux
     ----- concatWith
     1- instance method in Flux and Mono

        Both work similarly
     */

    public Flux<String> explore_concat() {
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        return Flux.concat(abcFlux,defFlux).log();
    }

    public Flux<String> explore_concatWith() {
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concatWith_mono() {
        var aMono = Flux.just("A","B","C");
        var bMono = Flux.just("D","E","F");
        return aMono.concatWith(bMono).log();
    }

    /*
        Both the publishers are subscribed at the same time
            - Publishers are subscribed and the merge happens in an interleaved fashion
            - concat() subscribes to the Publishers in a sequence

     - merge
        static method in Flux
     - mergeWith
        static method in Flux and Mono

     */

    public Flux<String> explore_merge() {
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(90));

        return Flux.merge(abcFlux,defFlux).log();
    }

    public Flux<String> explore_mergeWith() {
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(90));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.mergeWith(bMono).log();
    }


    /*  - mergeSequential
            - Used to combine two Publishers (Flux) in to one
            - Static method in Flux
            - Both the publishers are subscribed at the same time
                - Publishers are subscribed eagerly
                - Even though the publishers are subscribed eagerly the merge happens in a sequence
    */

    public Flux<String> explore_mergeSequential() {
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(90));

        return Flux.mergeSequential(abcFlux,defFlux).log();
    }

        /*  - zip zipWith

            - Used to combine two Publishers (Flux) in to one
            - Static method in Flux
            - Both the publishers are subscribed at the same time
                - Publishers are subscribed eagerly
                - Even though the publishers are subscribed eagerly the merge happens in a sequence
    */

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F")   ;

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second)
                .log();
    }

    public Flux<String> exploreZip1() {
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

        var _123Flux = Flux.just("1","2","3");
        var _456Flux = Flux.just("4","5","6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1()+t4.getT2()+t4.getT3()+t4.getT4())
                //AD14 BE25 CF36
                .log();
    }
    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

        return abcFlux.zipWith(defFlux, (first, second) -> first+second)
                        .log();
    }

    public Mono<String> explore_mergeZipWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

       fluxAndMonoGeneratorService.namesFlux_map(4)
                .subscribe(name -> {
                    System.out.println("Name is: " + (name));
                });

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Mono name is: " + name);
                });
    }
}
