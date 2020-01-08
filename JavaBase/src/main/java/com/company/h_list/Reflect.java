package com.company.h_list;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author luoxiaoqing
 * @date 2020-01-08__10:20
 * @desc
 */
@Data
@Accessors(chain = true)
public class Reflect {
    public static void main(String[] args) {
        ArrayList<Integer> list = new 	ArrayList<>();
        list.add(2);
        list.add(3);
        try {
            //todo ,get hot socore
            list.add(60);
            list.getClass().getMethod("add", Object.class).invoke(list, "活跃度中等【0~100】");
            //todo get fans
            list.add(1500);
            list.getClass().getMethod("add", Object.class).invoke(list, "粉丝数,排名 3689 位");

            //todo get evaluation
            list.add(90);
            list.getClass().getMethod("add", Object.class).invoke(list, "用户评价,超越 92%的用户");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(list.toArray()));
    }
}
