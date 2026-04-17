package com.accenture.franchise.repository;

import com.accenture.franchise.model.Franquicia;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FranquiciaRepository extends ReactiveMongoRepository<Franquicia, String> {
}
