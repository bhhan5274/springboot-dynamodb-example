package com.bhhan.dynamo;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;

/**
 * Created by hbh5274@gmail.com on 2020-08-06
 * Github : http://github.com/bhhan5274
 */
abstract class AwsDynamoDbCommonTest {
    protected AmazonDynamoDB amazonDynamoDB;
    protected HashMap<String, AttributeValue> item;

    @BeforeEach
    void setUp(){
        final AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAIDOBHZALS3D2N3YA", "lXslmRzOI62PUxDjlu4vdhd3v7y9mYrlfFHTVmHK");
        final AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        final AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "ap-northeast-2");

        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(endpointConfiguration)
                .build();

        item = new HashMap<>();
        item.put("id", (new AttributeValue()).withS("uuid"));
        item.put("mentionId", (new AttributeValue()).withN("1"));
        item.put("content", (new AttributeValue()).withS("comment content"));
        item.put("deleted", (new AttributeValue()).withBOOL(false));
        item.put("createdAt", (new AttributeValue()).withS("1836-03-07T02:21:30.536Z"));
    }
}
