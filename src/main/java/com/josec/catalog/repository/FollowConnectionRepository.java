package com.josec.catalog.repository;

import com.josec.catalog.model.FollowConnection;
import com.josec.catalog.model.enums.FollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowConnectionRepository extends JpaRepository<FollowConnection, Integer> {

    boolean existByFollowerIdAndFollowedId(Integer followerId, Integer followedId);

    Optional<FollowConnection> findByFollowerAndFollowedId(Integer followerId, Integer followedId);

    //Ver MIS seguidores (seguidores aceptados)
    List<FollowConnection> findByFollowedAndStatus(Integer followedId, FollowStatus status);

    //Ver a quien sigo (aceptados)
    List<FollowConnection> fingByFollowerIdAndStatus(Integer followerId, FollowStatus status);

}
