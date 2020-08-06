package com.bhhan.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.bhhan.dynamo.domain.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Created by hbh5274@gmail.com on 2020-08-06
 * Github : http://github.com/bhhan5274
 */
class AwsDynamoDbMapperTestToLearn extends AwsDynamoDbCommonTest{
    private DynamoDBMapper dynamoDBMapper;
    private Comment comment;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.DEFAULT);
        comment = Comment.builder()
                .name("name")
                .mentionId(1)
                .content("content")
                .build();
    }

    @Test
    void createTable_ValidInput_TableHasBeenCreated(){
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(Comment.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes().forEach(idx -> idx.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
            .withProjection(new Projection().withProjectionType("ALL")));

        then(TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest)).isTrue();
    }

    @Test
    void saveItem_ShouldBeCalledAfterTableCreation_IdIsNotNull(){
        then(comment.getId()).isNull();
        dynamoDBMapper.save(comment);
        then(comment.getId()).isNotNull();
    }

    @Test
    void saveAndLoadItem_ShouldBeCalledAfterTableCreation_FoundItem(){
        then(comment.getId()).isNull();
        dynamoDBMapper.save(comment);
        then(comment.getId()).isNotNull();

        final Comment foundComment = dynamoDBMapper.load(Comment.class, comment.getId());
        then(foundComment)
                .hasFieldOrPropertyWithValue("id", comment.getId());
    }

    @Test
    void saveAndUpdateItem_ShouldBeCalledAfterTableCreation_UpdateItem(){
        then(comment.getId()).isNull();
        dynamoDBMapper.save(comment);
        final String commentId = comment.getId();

        then(commentId).isNotNull();
        then(comment).hasFieldOrPropertyWithValue("content", "content");

        final String modifiedContent = "modified content";

        comment.update(modifiedContent);
        dynamoDBMapper.save(comment);

        final Comment foundComment = dynamoDBMapper.load(Comment.class, commentId);
        then(foundComment)
                .hasFieldOrPropertyWithValue("content", modifiedContent);
    }

    @Test
    void saveAndDeleteItem_ShouldBeCalledAfterTableCreation_SameScannedCounts(){
        then(comment.getId()).isNull();
        dynamoDBMapper.save(comment);
        final String commentId = comment.getId();

        then(comment.getId()).isNotNull();

        dynamoDBMapper.delete(comment);

        final Comment comment = dynamoDBMapper.load(Comment.class, commentId);
        then(comment).isNull();
    }

    @Test
    void deleteTable_ShouldBeCalledAfterTableCreation_TableHasBeenCreated(){
        final DeleteTableRequest deleteTableRequest = dynamoDBMapper.generateDeleteTableRequest(Comment.class);
        then(TableUtils.deleteTableIfExists(amazonDynamoDB, deleteTableRequest)).isTrue();
    }
}
