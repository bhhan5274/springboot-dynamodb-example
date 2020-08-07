package com.bhhan.dynamo.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hbh5274@gmail.com on 2020-08-07
 * Github : http://github.com/bhhan5274
 */
public interface CommentRepository extends PagingAndSortingRepository<Comment, String> {
    List<Comment> findAllByMentionIdOrderByCreatedAtAsc(Integer mentionId);
}
