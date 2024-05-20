package com.example.redismap.repo;


import org.springframework.data.repository.CrudRepository;

public interface RedisTringIntegerRepository extends CrudRepository<Integer, String> {
}
