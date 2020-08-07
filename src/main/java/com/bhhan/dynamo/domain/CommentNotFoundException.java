package com.bhhan.dynamo.domain;

/**
 * Created by hbh5274@gmail.com on 2020-08-07
 * Github : http://github.com/bhhan5274
 */
public class CommentNotFoundException extends RuntimeException{
    private String id;

    public CommentNotFoundException(String id) {
        this.id = id;
    }
}
