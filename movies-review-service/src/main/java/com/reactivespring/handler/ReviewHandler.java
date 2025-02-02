package com.reactivespring.handler;

import com.mongodb.internal.connection.Server;
import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private final ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    private Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviewsFlux) {
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {

//                .flatMap(review -> {
//                        return reviewReactiveRepository.save(review);
//                })
//                .flatMap(savedReview -> {
//                    return ServerResponse.status(HttpStatus.CREATED)
//                            .bodyValue(savedReview);
//                });


            return request.bodyToMono(Review.class)
                    .flatMap(reviewReactiveRepository::save)
                    .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
     }

    public Mono<ServerResponse> getReviews(ServerRequest request) {

        var movieInfoId = request.queryParam("movieInfoId");

        if(movieInfoId.isPresent()) {
            var reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewsResponse(reviewsFlux);
        }
        var reviewsFlux = reviewReactiveRepository.findAll();
        return ServerResponse.ok().body(reviewsFlux, Review.class);

    }



    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);
        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                    .map(reqReview -> {
                        review.setComment(reqReview.getComment());
                        review.setRating(reqReview.getRating());
                        return review;
                    })
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);
        return existingReview.
                flatMap(review -> reviewReactiveRepository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());
    }




}
