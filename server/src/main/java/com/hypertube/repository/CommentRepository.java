package com.hypertube.repository;

import com.hypertube.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Comment Database

    ArrayList<Comment> findByMovieId(Long movieId);
    ArrayList<Comment> findByUserId(Long userId);

}
