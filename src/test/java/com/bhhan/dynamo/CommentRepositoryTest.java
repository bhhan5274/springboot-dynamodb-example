package com.bhhan.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.bhhan.dynamo.domain.Comment;
import com.bhhan.dynamo.domain.CommentNotFoundException;
import com.bhhan.dynamo.domain.CommentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

/**
 * Created by hbh5274@gmail.com on 2020-08-07
 * Github : http://github.com/bhhan5274
 */

@SpringBootTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @BeforeEach
    void createTable(){
        final CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(Comment.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes()
                .forEach(
                        idx -> idx.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                                    .withProjection(new Projection().withProjectionType("ALL"))
                );
        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
    }

    @AfterEach
    void deleteTable(){
        final DeleteTableRequest deleteTableRequest = dynamoDBMapper.generateDeleteTableRequest(Comment.class);
        TableUtils.deleteTableIfExists(amazonDynamoDB, deleteTableRequest);
    }

    @Test
    void createComment_ValidInput_CreatedComment(){
        final Comment createdComment = commentRepository.save(Comment.builder()
                .mentionId(1)
                .name("name")
                .content("content")
                .build());

        then(createdComment)
                .hasNoNullFieldsOrPropertiesExcept("deletedAt", "deleted")
                .hasFieldOrPropertyWithValue("mentionId", 1)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("content", "content");
    }

    @Test
    void findCreatedComment_ById_FoundComment(){
        final String commentId = commentRepository.save(Comment.builder()
                .mentionId(1)
                .name("name")
                .content("content")
                .build()).getId();

        final Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(IllegalArgumentException::new);

        then(foundComment)
                .hasNoNullFieldsOrPropertiesExcept("deletedAt", "deleted")
                .hasFieldOrPropertyWithValue("mentionId", 1)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("content", "content");
    }

    @Test
    void updateComment_ValidInput_UpdatedComment(){
        final String commentId = commentRepository.save(Comment.builder()
                .mentionId(1)
                .name("name")
                .content("content")
                .build()).getId();

        final Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(IllegalArgumentException::new);

        final String updatedContent = "updated content";
        foundComment.update(updatedContent);
        final Comment modifiedComment = commentRepository.save(foundComment);

        then(modifiedComment)
                .hasNoNullFieldsOrPropertiesExcept("deletedAt", "deleted")
                .hasFieldOrPropertyWithValue("mentionId", 1)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("content", updatedContent);
    }

    @Test
    void deleteCreatedComment_TryToFindDeletedComment_ThrowCommentNotFoundException(){
        final Comment createdComment = commentRepository.save(Comment.builder()
                .mentionId(1)
                .name("name")
                .content("content")
                .build());

        commentRepository.delete(createdComment);

        thenThrownBy(() -> commentRepository.findById(createdComment.getId())
                .orElseThrow(() -> new CommentNotFoundException(createdComment.getId())))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void findComment_ByMentionIdAndOrderByCreatedAtDescDeletedFalse_FoundCommentsInDesignatedOrder(){
        int size = 10;
        IntStream.range(0, size)
                .forEach(i -> commentRepository.save(Comment.builder()
                        .mentionId(1)
                        .name("name" + i)
                        .content("content" + i)
                        .build()));

        List<Comment> foundComment = commentRepository
                .findAllByMentionIdOrderByCreatedAtAsc(1);

        then(foundComment.size())
                .isEqualTo(size);

        IntStream.range(1, size)
                .forEach(i -> {
                    final Comment prev = foundComment.get(i - 1);
                    final Comment next = foundComment.get(i);
                    then(prev.getCreatedAt().isBefore(next.getCreatedAt()))
                            .isTrue();
                });
    }
}
