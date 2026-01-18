package com.unnivy.unnivy_app.repository;

import com.unnivy.unnivy_app.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentsRepository extends JpaRepository<Comments,Long> {

    @Query("SELECT COUNT(c) > 0 FROM Comments c WHERE c.from.user_id = :fromId AND c.to.user_id = :toId")
    boolean existsByFromIdAndToId(@Param("fromId") Long fromId, @Param("toId") Long toId);

}
