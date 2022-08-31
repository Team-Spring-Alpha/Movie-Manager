package br.com.compass.search.service;

import br.com.compass.search.dto.apiTheMoviedb.movieParams.ParamsSearchByFilters;
import br.com.compass.search.dto.apiTheMoviedb.movieParams.ParamsSearchByName;
import br.com.compass.search.dto.apiTheMoviedb.movieParams.ParamsSearchByRecommendations;
import br.com.compass.search.dto.apiclient.response.ResponseApiClient;
import br.com.compass.search.enums.GenresEnum;
import br.com.compass.search.enums.ProvidersEnum;
import br.com.compass.search.proxy.MovieSearchProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MovieSearchProxy movieSearchProxy;

    @Value("${API_KEY}")
    private String apiKey;


    public HashSet<ResponseApiClient> findMoviesRecommendations(Long movieId) {
        ParamsSearchByRecommendations searchByRecommendations = new ParamsSearchByRecommendations(apiKey);
        return movieSearchProxy.getMovieByRecommendation(searchByRecommendations, movieId);
    }
    public List<ResponseApiClient> findByActor(String movieActor){
        ParamsSearchByName searchByName = new ParamsSearchByName(apiKey, movieActor);
        return movieSearchProxy.getMovieByActorName(searchByName);
    }

    public HashSet<ResponseApiClient> findByFilters(GenresEnum movieGenre, LocalDate dateGte, LocalDate dateLte, ProvidersEnum movieProvider, String movieName) {
        HashSet<ResponseApiClient> movieSearchByName = new HashSet<>();
        HashSet<ResponseApiClient> movieSearchByFilters = new HashSet<>();

        if (movieName != null) {
            ParamsSearchByName searchByName = new ParamsSearchByName(apiKey, movieName);
            movieSearchByName = movieSearchProxy.getMovieSearchByName(searchByName);
        }

        if (movieGenre != null || dateGte != null || dateLte != null || movieProvider != null || movieName == null) {
            ParamsSearchByFilters searchByFilters = new ParamsSearchByFilters(apiKey);
            if (movieGenre != null) {
                searchByFilters.setWith_genres(movieGenre.getIdGenrer());
            }
            if (movieProvider != null) {
                searchByFilters.setWith_watch_providers(movieProvider.getIdProvider());
            }

            String dateAfterString = null;
            String dateBeforeString = null;
            if (dateGte != null) {
                dateAfterString = dateGte.toString();
            }
            if (dateLte != null) {
                dateBeforeString = dateLte.toString();
            }

            movieSearchByFilters = movieSearchProxy.getMovieSearchByFilters(searchByFilters, dateAfterString, dateBeforeString);
        }

        if (!movieSearchByName.isEmpty()) {
            movieSearchByFilters.addAll(movieSearchByName);
        }

        return movieSearchByFilters;
    }
}
