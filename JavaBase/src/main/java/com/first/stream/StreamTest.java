package com.first.stream;

import com.first.entity.User;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTest {

    @Test
    public void jdbc() throws Exception {
        // 1.  注册数据库驱动
        // 2.  获取数据库连接
        // 3.  创建发送SQL对象
        // 4.  执行SQL语句，获取结果
        // 5.  遍历结果集
        // 6.  关闭资源
        String url = "wanzhanqu.mysql.rds.aliyuncs.com/sungoal_public";
        String user = "sungoal_public";
        String pwd = "sun85ydhfAiu1";
        String sql = "select * from brand";

        //DriverManager.registerDriver();
        Connection conn = DriverManager.getConnection(url, user, pwd);
        Statement stt = conn.createStatement();
        ResultSet result = stt.executeQuery(sql);

        while (result.next()) {
            /*int id = result.getInt("id");
            String _user = result.getString("username");
            String _pwd = result.getString("password");
            System.out.println(id + " -> " + _user + " -> " + _pwd);*/
            System.out.println(result);
        }

        result.close();
        stt.close();
        conn.close();

        //return result;
    }

    @Test
    public void test1() {
//        Stream<String> flow = Stream.of("11", "22", "33", "44", "55");
//        flow.stream().map(Integer::parseInt).forEach(System.out::println);

//        String [] list = {"11", "22", "33", "44", "55"};// cannot resolve method 'stream()'
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, "11", "22", "33", "44", "66");
        list.stream().map(Integer::parseInt).forEach(System.out::println);// 把String转换为Integer

        long count = list.stream().map(Integer::parseInt).filter(item -> item > 30).count();
        System.out.println(count);
    }

    @Test
    public void test2() {
        //创建集合对象
        List<String> list = new ArrayList<>();
        //向集合中添加数据
        list.add("张无忌");
        list.add("周芷若");
        list.add("赵敏");
        list.add("张强");
        list.add("张三丰");
        //使用Stream流完成对集合的上述三个操作
        //list.stream();表示获取Stream流的对象
        list.stream()
                .filter(s -> s.startsWith("张"))//首先筛选所有姓张的人
                .filter(s -> s.length() == 3)//然后筛选名字有三个字的人
                .forEach(System.out::println);//最后进行对结果进行打印输出

        List<String> result = list.stream()
                .filter(s -> s.startsWith("张"))
                .filter(s -> s.length() == 3)
                .collect(Collectors.toList());// 转存到新集合
        System.out.println(result.toString());
    }

    @Test
    public void test4() {
        Integer[] nums = {1, 34, 56, 234, 78, 97, 9};// int[] nums = {}; 报错
        Stream<Integer> numStream = Stream.of(nums);
        List<String> list = numStream.map(String::valueOf).collect(Collectors.toList());// Integer转换String
        list.stream().forEach(System.out::println);

    }

    @Test
    public void test3() {
        class User {
            private String userID;
            private boolean isVip;
            private int balance;

            public User(String userID, boolean isVip, int balance) {
                this.userID = userID;
                this.isVip = isVip;
                this.balance = balance;
            }

            public boolean isVip() {
                return this.isVip;
            }

            public String getUserID() {
                return this.userID;
            }

            public int getBalance() {
                return this.balance;
            }
        }

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("2017001", false, 0));
        users.add(new User("2017002", true, 36));
        users.add(new User("2017003", false, 98));
        users.add(new User("2017004", false, 233));
        users.add(new User("2017005", true, 68));
        users.add(new User("2017006", true, 599));
        users.add(new User("2017007", true, 1023));
        users.add(new User("2017008", false, 9));
        users.add(new User("2017009", false, 66));
        users.add(new User("2017010", false, 88));


        //Stream API实现方式
        //也可以使用parallelStream方法获取一个并发的stream，提高计算效率
        Stream<User> stream = users.stream();
        List<String> array =
                stream.filter(User::isVip)
                        .sorted((t1, t2) -> t2.getBalance() - t1.getBalance())// 降序
                        .limit(3)
                        .map(User::getUserID)
                        .collect(Collectors.toList());
        array.forEach(System.out::println);
    }

    @Test
    public void test5() {
        Integer[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        List<Integer> collect = Stream.of(nums)
                .filter((n) -> {
                    return n > 5 && n % 2 != 0;
                })
                .collect(Collectors.toList());
        System.out.println(collect);
        for (Integer i : collect) {
            System.out.println(i);
        }
    }

    @Test
    public void test6() {

        List<Integer> ids = new ArrayList<>();
        Collections.addAll(ids, 51, 52, 59, 60, 62, 70, 71, 75);

        List<Integer> nums = new ArrayList<>();
        Collections.addAll(nums, 0,0,51,51,52,56,57,58);// 所有父节点id

        List<Integer> collect = nums.stream().distinct().filter(item -> item != 0).collect(Collectors.toList());
        //collect.stream().forEach(System.out::println);


        // 去重
        //Collection<Integer> parent = CollectionUtils.removeAll(nums, ids);
        //parent.stream().forEach(System.out::println);

    }

    @Test
    public void test7 () {
        List<String> ss = new ArrayList<>();
        Collections.addAll(ss, "a", "c", "a", "c", "c", "f");

        Map<String, Long> collect = ss.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting()));
        Set<Map.Entry<String, Long>> kvs = collect.entrySet();
        for (Map.Entry<String, Long> kv : kvs) {
            System.out.println(kv.getKey());
            System.out.println(kv.getValue());
            System.out.println(">>");
        }

    }

    @Test
    public void test8 () {
        List<Map<String, Integer>> jobs = new ArrayList();
        HashMap<String, Integer> job1 = new HashMap<>();
        HashMap<String, Integer> job2 = new HashMap<>();
        HashMap<String, Integer> job3 = new HashMap<>();
        HashMap<String, Integer> job4 = new HashMap<>();
        HashMap<String, Integer> job5 = new HashMap<>();
        HashMap<String, Integer> job6 = new HashMap<>();

        job1.put("a", 2);
        job2.put("a", 3);
        job3.put("c", 4);
        job4.put("c", 5);
        job5.put("c", 6);
        job6.put("f", 7);

        Collections.addAll(jobs, job1, job2, job3, job4, job5, job6);

        jobs.forEach(item -> {
//            System.out.println(item.toString());
        });

        //jobs.stream().collect(Collectors.groupingBy(HashMap::))

        List<User> users = new ArrayList<>();
        User u1 = new User().setName("a").setAge(0);
        User u2 = new User().setName("a").setAge(1);
        User u3 = new User().setName("c").setAge(1);
        User u4 = new User().setName("c").setAge(1);
        User u5 = new User().setName("c").setAge(1);
        User u6 = new User().setName("f").setAge(1);
        Collections.addAll(users, u1, u2, u3, u4, u5, u6);

        Map<String, Integer> collect = users.stream().collect(Collectors.groupingBy(User::getName, Collectors.summingInt(User::getAge)));
        collect.entrySet().stream().forEach(item -> {
            System.out.println(item.toString());
        });


        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 2);
        map.put("c", 6);
        map.put("f", 7);

        Set<String> keySet = map.keySet();
        List<String> keys = new ArrayList<>(keySet);
        Collection<Integer> values = map.values();

        keys.forEach(System.out::println);
        values.forEach(System.out::println);

//        System.out.println(users.isEmpty());

    }

    @Test
    public void test9 () {
        List<User> users = new ArrayList<>();
        User u1 = new User().setName("a").setAge(1);
        User u2 = new User().setName("a").setAge(4);
        User u3 = new User().setName("c").setAge(3);
        User u4 = new User().setName("c").setAge(2);
        User u5 = new User().setName("c").setAge(5);
        User u6 = new User().setName("f").setAge(6);
        Collections.addAll(users, u1, u2, u3, u4, u5, u6);

        Map<Integer, List<User>> collect = users.stream().collect(Collectors.groupingBy(User::getAge, Collectors.toList()));
        List<Integer> level = new ArrayList<>(collect.keySet());
        Integer max = Collections.max(level);
        List<User> maxUser = collect.get(max);
        List<String> names = maxUser.stream().map(User::getName).collect(Collectors.toList());
        names.forEach(item -> {
//            System.out.println(item);
        });

        collect.entrySet().forEach(item -> {
            System.out.println(item.getKey());
            System.out.println(item.getValue().toString());
        });

    }

    @Test
    public void test10 () {
        List<Map<String, Integer>> users = new ArrayList<>();
        Map<String, Integer> u1 = new HashMap<>();
        u1.put("a", 2);
        Map<String, Integer> u2 = new HashMap<>();
        u2.put("a", 3);
        Map<String, Integer> u3 = new HashMap<>();
        u3.put("c", 4);
        Map<String, Integer> u4 = new HashMap<>();
        u4.put("c", 5);
        Map<String, Integer> u5 = new HashMap<>();
        u5.put("c", 6);
        Map<String, Integer> u6 = new HashMap<>();
        u6.put("f", 7);
        Collections.addAll(users, u1, u2, u3, u4, u5, u6);


//        Map<String, Integer> collect = users.stream().collect(Collectors.groupingBy(String::toString, Collectors.summingInt()));
//        collect.entrySet().stream().forEach(item -> {
//            System.out.println(item.toString()); //a=5 c=15  f=7
//        });
    }

    /**
     * 操作外部变量
     *
     */
    @Test
    public void test11 () {
        // 操作外部变量
        ArrayList<String> list = new ArrayList<>();

        ArrayList<String> vals = new ArrayList<>();

        vals.add("123");
        vals.add("456");
        vals.add("789");

        vals.forEach(val -> {
            list.add(val);
        });
        System.out.println(list.size());
        System.out.println(list.toString());

//        list.stream().forEach(item -> {
//            if ("123".equals(item)) {
//                list.remove(item);
//            }
//        });
//        java.util.ConcurrentModificationException
//        at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1388)
//        at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:580)
//        at com.first.stream.StreamTest.test11(StreamTest.java:321)

//        System.out.println(list.toString());
    }


    /**
     * 字符串转换：joining, toList, toMap
     */
    @Test
    public void test12() {

        Stream<String> fruit = Stream.of("apple", "banana", "orange");

        //  拼接字符串
//        String collect = fruit.collect(Collectors.joining());
//        System.out.println(collect);


        // List
//        List<String> collect1 = fruit.collect(Collectors.toList());
//        System.out.println(collect1);

        //  Map
        Map<Object, Object> collect2 = fruit.collect(Collectors.toMap(x ->
                        "key-" + x
                , y ->
                        "value-" + y
        ));
        System.out.println(collect2);

    }

    /**
     * peek, map
     */
    @Test
    public void test13 () {
        List<String> ss = new ArrayList();
        ss.add("a");
        ss.add("a");
        ss.add("c");
        ss.add("c");
        ss.add("c");
        ss.add("f");

        List<String> collect = ss.stream().map(x -> "--" + x).collect(Collectors.toList());
        for (String s : collect) {
            System.out.println(s);
        }

//        List<String> collect = ss.stream().peek(x -> "--" + x).collect(Collectors.toList());
        //  Bad return type in lambda expression: String cannot be converted to voidBad return type in lambda expression: String cannot be converted to void
    }


    /**
     * 获取文件流
     */
    @Test
    public void streamFromFile () throws IOException {
        Stream<String> lines = Files.lines(Paths.get("C:\\PC\\workspace\\Java\\JavaBase\\src\\main\\java\\com\\first\\stream\\OptionalTest.java"));

        lines.forEach(System.out::println);
    }


    /**
     * 通过函数生成流
     */
    @Test
    public void streamFromFunction () {
        //  无限流
        Stream<Integer> iterate = Stream.iterate(0, n -> n + 2);
        iterate.limit(10).forEach(System.out::println);


        Stream<Double> generate = Stream.generate(Math::random);
        generate.limit(10).forEach(System.out::println);

    }


    /**
     * lambda 改变外部变量值
     */
    @Test
    public void test14 () {
        boolean flag = false;

        ArrayList<Integer> list = new ArrayList<>();

        ArrayList<Integer> nums = new ArrayList<>();
        Collections.addAll(nums, 1, 2, 3, 4, 5);
        nums.stream().forEach(item -> {
            if (item == 1) {
                System.out.println(item);
                list.add(item);

                // Variable used in lambda expression should be final or effectively final
                // lambda表达式中使用的变量应为final或有效为final
                // flag = true;
            }
        });

        System.out.println(list.toArray());  // [Ljava.lang.Object;@7a7b0070
        System.out.println(list.toString());// [1]

    }



}// end
