package com.bhhan.dynamo;

import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by hbh5274@gmail.com on 2020-08-06
 * Github : http://github.com/bhhan5274
 */

class AwsDynamoDbSdkTestToLearn extends AwsDynamoDbCommonTest{
    @Test
    void createTable_ValidInput_TableHasBeenCreated(){
        final CreateTableRequest createTableRequest = (new CreateTableRequest())
                .withAttributeDefinitions(
                        new AttributeDefinition("id", ScalarAttributeType.S),
                        new AttributeDefinition("mentionId", ScalarAttributeType.N),
                        new AttributeDefinition("createdAt", ScalarAttributeType.S)
                )
                .withTableName("Comment")
                .withKeySchema(
                        new KeySchemaElement("id", KeyType.HASH)
                )
                .withGlobalSecondaryIndexes(
                        (new GlobalSecondaryIndex())
                                .withIndexName("byMentionId")
                                .withKeySchema(
                                        new KeySchemaElement("mentionId", KeyType.HASH),
                                        new KeySchemaElement("createdAt", KeyType.RANGE)
                                )
                                .withProjection(
                                        (new Projection()).withProjectionType(ProjectionType.ALL)
                                )
                                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                )
                .withProvisionedThroughput(
                        new ProvisionedThroughput(1L, 1L)
                );

        final boolean hasTableBeenCreated = TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
        assertTrue(hasTableBeenCreated);
    }

    @Test
    void putItem_ShouldBeCalledAfterTableCreation_StatusOk(){
        final PutItemRequest putItemRequest = (new PutItemRequest())
                .withTableName("Comment")
                .withItem(item);

        final PutItemResult putItemResult = amazonDynamoDB.putItem(putItemRequest);
        assertEquals(HttpStatus.SC_OK, putItemResult.getSdkHttpMetadata().getHttpStatusCode());
    }

    @Test
    void getItem_ShouldBeCalledAfterPuttingItem_FoundItem(){
        final HashMap<String, AttributeValue> key = new HashMap<>();
        key.put("id", (new AttributeValue()).withS("uuid"));

        final GetItemRequest getItemRequest = (new GetItemRequest())
                .withTableName("Comment")
                .withKey(key);

        final GetItemResult getItemResult = amazonDynamoDB.getItem(getItemRequest);

        then(getItemResult.getItem()).containsAllEntriesOf(item);
    }

    @Test
    void deleteItem_ShouldBeCalledAfterPuttingItem_StatusOk(){
        final HashMap<String, AttributeValue> key = new HashMap<>();
        key.put("id", (new AttributeValue()).withS("uuid"));

        final DeleteItemRequest deleteItemRequest = (new DeleteItemRequest())
                .withTableName("Comment")
                .withKey(key);

        final DeleteItemResult deleteItemResult = amazonDynamoDB.deleteItem(deleteItemRequest);

        then(deleteItemResult.getSdkHttpMetadata().getHttpStatusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    void getItem_ShouldBeCalledAfterDeletingItem_NullItem(){
        final HashMap<String, AttributeValue> key = new HashMap<>();
        key.put("id", (new AttributeValue()).withS("uuid"));

        final GetItemRequest getItemRequest = (new GetItemRequest())
                .withTableName("Comment")
                .withKey(key);

        final GetItemResult getItemResult = amazonDynamoDB.getItem(getItemRequest);
        then(getItemResult.getItem()).isNull();
    }
}
