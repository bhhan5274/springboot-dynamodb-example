package com.bhhan.dynamo.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by hbh5274@gmail.com on 2020-08-07
 * Github : http://github.com/bhhan5274
 */

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.bhhan.dynamo.domain")
public class SpringDataDynamoDbConfig {
    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDbEndpoint;

    @Value("${amazon.dynamodb.region}")
    private String amazonDynamoDbRegion;

    @Value("${amazon.aws.accessKey}")
    private String amazonAwsAccessKey;

    @Value("${amazon.aws.secretKey}")
    private String amazonAwsSecretKey;

    @Primary
    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB){
        return new DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.DEFAULT);
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB(){
        final AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonAwsAccessKey, amazonAwsSecretKey));
        final AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(amazonDynamoDbEndpoint, amazonDynamoDbRegion);

        return AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }
}
