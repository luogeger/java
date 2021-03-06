package com.first.json.fastjson;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luoxiaoqing
 * @date 2018-01-29__16:43
 */
@Data
public class School {

    private String id;
    private String name;
    List<User> students = new ArrayList<User>();

}
