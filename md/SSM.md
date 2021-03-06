# Mybatis



- jdbc是获取数据源执行sql语句
- 读取核心配置文件获取sqlSessionFactory, 在获取sqlSession对象，执行mapper.xml的sql语句
    - sqlSession是mybatis通过动态代理产生，过程很麻烦，`sqlSession.getMapper(UserMapper.class)`
    - 映射文件被核心配置文件引入 
    - 1.sqlSessionFactory, sqlSession, mapper接口的初始化，过于复杂，交给spring
        - sqlSessionFactory
        - mapper
    - 2.mybatis核心配置文件的读取，交给spring
    - 3.映射文件的引入，交给spring, 之前有4种方式引入映射文件


### JDBC操作
```bash
    1.  注册数据库驱动
    2.  获取数据库连接
    3.  创建发送SQL对象
    4.  执行SQL语句，获取结果
    5.  遍历结果集
    6.  关闭资源
```

### Maybatis执行sql语句

- `使用步骤`
    - 1.`mybatis-config.xml`
    - 2.`UserMapper.xml`
    - 3.创建SqlSessionFactory
    - 4.获取SqlSession对象   
    - 5.调用方法，执行语句，操作数据库
    - 6.提交事务 `session.commit()`
    - 7.关闭会话 `session.close()`    

- `User.java`

- `UserMapper.xml`

```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!-- 注意：这里是mapper -->
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="UserMapper"><!-- namespace(命名空间)：映射文件的唯一标识 -->
        <!-- 
            1.查询的statement，id：在同一个命名空间下的唯一标识，
            2.resultType：sql语句的结果集封装类型, 需要全路径 -->
        <select id="queryUserById" resultType="cn.item.jdbc.User">
            select * from tb_user where id= #{id}
        </select>
    </mapper>
```

- `mybatis-config.xml`

```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!-- 注意：这里是configuration -->
    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <configuration>
        <!-- 环境：说明可以配置多个，default:指定生效的环境 -->
        <environments default="development">
            <environment id="development"><!-- id:环境的唯一标识 -->
                <transactionManager type="JDBC"/><!-- 事务管理器，type：类型 -->
                <dataSource type="POOLED"><!-- 数据源：type-池类型的数据源 -->
                    <property name="driver" value="com.mysql.jdbc.Driver"/>
                    <property name="url" value="jdbc:mysql://127.0.0.1:3306/fourth"/>
                    <property name="username" value="root"/>
                    <property name="password" value="123456"/>
                </dataSource>
            </environment>
        </environments>

        <mappers><!-- 映射文件, 注意路径 -->
            <mapper resource="UserMapper.xml"/>
        </mappers>
    </configuration>
```

- `test.java`

```java
    public static void main(String[] args) throws IOException {
        SqlSession sqlSession = null;
        try {

            String resource = "mybatis-config.xml";// 指定mybatis的核心配置文件
            // 读取mybatis-config.xml配置文件
            InputStream inputStream = Resources.getResourceAsStream(resource);
            // 构建sqlSessionFactory
            SqlSessionFactory sqlSessionFactory 
                = new SqlSessionFactoryBuilder().build(inputStream);
            sqlSession = sqlSessionFactory.openSession();// 获取sqlSession对象

            User user = sqlSession.selectOne("UserMapper.queryUserById", 5);
            // 执行查询操作，获取结果集。参数：1_命名空间（namespace）+“.”+statementId, 2_sql的占位符参数
            System.out.println(user);
        } finally {
            if (sqlSession != null) {// 关闭连接
                sqlSession.close();
            }
        }
    }
```
> 别名：SELECT *, user_name AS userName FROM tb_user

### CRUD
- 接口 + 实现类 + `Mapper.xml`
    - `UserDao.java`

    ```java
        public interface UserDao {
            User queryUserById(Long id);

            List<User> queryUserAll();

            void insertUser(User user);

            void updateUser(User user);

            void deleteUserById(Long id);

        }

    ```

    - `UserDaoImpl.java`

    ```java
        public class UserDaoImpl implements UserDao {
            private SqlSession sqlSession;

            public UserDao_c(SqlSession sqlSession){
                this.sqlSession = sqlSession;
            }

            @Override
            public User queryUserById(Long id) {
                return this.sqlSession.selectOne("UserDaoMapper.queryUserById", id);
            }
            //@Override ....
        }

    ```

    - `Mapper.xml`

    ```xml
        <mapper namespace="UserDaoMapper">
            <select id="queryUserById"  resultType="cn.item.pojo.User">
                select *, user_name AS userName from tb_user where id = #{id}
            </select>
            
            .....
        </mapper>        
    ```

    - `Test.java`

    ```java
        import org.apache.ibatis.io.Resources;
        import org.apache.ibatis.session.SqlSession;
        import org.apache.ibatis.session.SqlSessionFactory;
        import org.apache.ibatis.session.SqlSessionFactoryBuilder;

        public Class UserDaoTest {
            private UserDao dao;

            @Before
            pubnlic void setUp() throws Exception {
                String resouce = "mybatis-cofnig.xml";
                InputStream is = Resource.getResourceAsStream(resource);

                SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
                SqlSession session = factory.openSession();
                this.dao = new `UserDaoImpl(session);
            }

            @Test
            public void queryUserById (){
                User u = this.dao.queryUserById(3L);
            }
        }
    ```

### Mapper实现类的动态代理
- **命名空间**必须改成 **接口文件**的全路径
  
    - `<mapper namespace="cn.item.Dao.UserDaoMapper">`
- **statement**必须和接口 **方法名**一致, 以及 **结果集的封装类型**和方法的 **返回类型**一致
    - `public User queryUserById(Long id) {`
    - `<select id="queryUserById"  resultType="cn.item.pojo.User">`
- **parameType**
  
    - `select *, user_name AS userName from tb_user where id = #{id}`
- `Test.java`
    - ```java
        private UserMapper userMap;
        @Before
        public void setUp() throws Exception{
            // .....
            SqlSession sqlSession = sqlSessionFactory.openSession(true);// 表示自动提交事务
            this.userMap = sqlSession.getMapper(userMap.class);
        }
        ```

### mybatis-config.xml配置
> mybatis-config.xml讲究严格的顺序

- `configuration`
    - `properties`
    - `settings`
    - `typeAliases`
    - typeHandlers类处理器
    - objectFactory对象工厂
    - plugins插件
    - `environments`
        - `environment`
            - `transactionManager`
            - `dataSource`
    - databaseIdProvider数据库厂商标识
    - `mappers`



​        

# Spring
### overview

- 搭建工程
    - `pom.xml` 依赖管理
    - `applicationContext.xml` 
    - `log4j.properties`

- 控制反转 
> - IoC **Inverse of Control**：把实例化的权利交给第三方工厂
> - 具体实现：Spring工厂 + 反射 + 配置文件

- 代码的演变
    - 传统方式：三层架构，Dao层和Service层高度耦合，
        - `Dao dao = new Dao_c();`
    - 自定义工厂：还是需要创建实体类
        - `return new Dao_c();`
    - 反射：返回Object对象，还是必须要实体类的路径,(硬编码)
        - `Class.forName("cn.item.spring_01.Dao_c").newInstance();` 


- Spring工厂的使用
    - `applicationContext.xml`
    ```xml
        <bean id="Dao_c" class="cn.item.spring01.Dao_c"></bean>
    ```

    - `Service_c.java`
    ```java
        // 1.读取配置文件创建工厂对象(IOC容器)
        // 2.获取Bean对象，得到的是Object，向下转型，参数是Bean的id
        // 3.调用方法
        ApplicationContext ac = 
            new ClassPathXmlApplicationContext("applicationContext.xml");
        Dao dao = (Dao) ac.getBean("Dao_c");// bean的id
        dao.findUser();
    ```
```
    
    - `Test.java`
    ```java
        Service service = new Service_c();
        service.login();
```

### DI: dependency injection

>- 现在Service的功能需要依赖Dao, 对于Service来说要创建工厂获取Dao，是一种主动的行为，有没有一种方法在执行Service方法的时候，自动的解决自身需要的依赖
>- DI的做法是由Spring容器在创建Sercvice、Dao对象，并且在配置中将Dao传入到Service，那么Service对象就包含了Dao对象的引用
>- 在Spring容器创建管理多个对象，通过property标签将对象注入到需要依赖的对象中


- 代码改造
    - `applicationContext.xml`
    ```xml
        <bean id="Dao_c" class="cn.item.spring01.Dao_c"></bean>

        <bean id="service_c" class="cn.item.spring01.Service_c">
            <property name="dao" ref="Dao_c"></property>
            <!-- name: set方法的后缀， ref: 代表复杂数据，是依赖的id -->
        </bean>
    ```

    - `Service_c.java`
    ```java
        // 以下两行代码不需要
        //ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml")
        //Dao dao = (Dao) ac.getBean("Dao_c");

        // 因为Dao注入到Service里，所以内部需要一个私有的成员变量来接收
        private Dao dao;

        public void setDao(Dao dao) {
            this.dao = dao;
        }

        dao.findUser();
    ```

    - `Test.java`
    ```java
        //Service service = new Service_c();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        Service service = (Service) ac.getBean("service_c");// bean的id
        service.login();
    ```

### IoC装配Bean (xml)
- xml装配的4种方式
    - 无参构造方法：默认调用的是无参构造方法
    - 静态工厂方法：在实例化之前可以有一些其他的操作，conn...
    - 实例工厂方法：先有实例才能获取
    - FactoryBean：基于某一种类型创建对象的接口
        - class路径是Bean4Factory，在实例化`Bean4Factory`的时候，会判断时候实现了`FactoryBean`接口，如果实现了就自动调用`getObject`方法，并返回结果


- `Bean.java`
    - `Bean1.java`
    ```java
    public class Bean1 {
        public Bean1 (String str){// 默认调用无参，但是有参就会报错
            System.out.print(str)
        }
    }
    ```
    - `Bean2.java`
    ```java
    public class Bean2 {

        public static Bean2 getBean2 () {
            // connection .,...
            return new Bean2();
        }
    }
    ```
    - `Bean3.java`
    ```java
    public class Bean3 {
    }
    ```
    - `Bean3Factory.java`
    ```java
    public class Bean3Factory {
        public Bean3 getBean3 (){
            return new Bean3();
        }
    }
    ```
    - `Bean4.java`
    ```java
    public class Bean4 {
    }
    ```
    - `Bean4Factory.java`
    ```java
    public class Bean4Factory implements FactoryBean<Bean4> {
        @Override
        public Bean4 getObject() throws Exception {
            return new Bean4();
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }
    ```
    
    

- `applicationContext.xml`
```xml
    <bean class="cn.item.get_bean.Bean1" id="bean1"/>

    <bean class="cn.item.get_bean.Bean2" id="bean2" factory-method="getBean2"/>

    <bean class="cn.item.get_bean.Bean3Factory" id="factory3"/>
    <bean id="bean3" factory-bean="factory3" factory-method="getBean3"/>

    <bean class="cn.item.get_bean.Bean4Factory" id="bean4"/>
```

- `Test.java`
```java
public void getBean () {
    ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");

    // 无参构造方法
    Bean1 bean1 = (Bean1) ac.getBean("bean1");
    System.out.println("bean1 = " + bean1);

    // 静态工厂方法
    Bean2 bean2 = (Bean2) ac.getBean("bean2");
    System.out.println("bean2 = " + bean2);

    // 静态工厂方法
    Bean3 bean3 = (Bean3) ac.getBean("bean3");
    System.out.println("bean3 = " + bean3);

    // 静态工厂方法
    Bean4 bean4 = (Bean4) ac.getBean("bean4");
    System.out.println("bean4 = " + bean4);
}
```

### Bean的作用域

### Bean的生命周期

### Bean属性的依赖注入

> Bean属性 == 类的成员变量

- 构造参数注入
- setter注入
- p名称空间
- spEL表达式


- `Car.java`

```java
    public class Car {
        private Integer id;
        private  String name;
        private Double price;

        public Car(Integer id, String name, Double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public Car( String name, Double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        // @Override toString...
    }

````
- `Person.java`

```java
    public class Person {
        private  Integer id;
        private String name;
        private Car car;
        // getter setter toString
    }
```
- `applicationContext.xml`

```xml
    <!--方式一: 带参构造器注入-->
    <bean class="com.itheima.spring.e_xmlpropertydi.Car" id="car" >
        <!--name:参数名称定位
            index: 参数索引定位
            type: 参数类型
            value: 基本类型赋值
            ref: 对象类型赋值
            -->
        <constructor-arg index="0" value="1246789" />
        <constructor-arg name="name" value="跑跑卡丁车" />
        <!--<constructor-arg type="java.lang.Double" index="2" value="12469d" />-->
        <constructor-arg type="java.lang.Double" index="2">
            <value>234d</value>
        </constructor-arg>
    </bean>

    <!--方式二:setter方法注入-->
    <bean class="com.itheima.spring.e_xmlpropertydi.Person" id="person">
        <property name="id" value="12345678"/>
        <property name="name" value="柳岩"/>
        <!--<property name="car"  ref="car"/>-->
        <property name="car" >
            <ref bean="car" />
        </property>
    </bean>

    <!--p名称空间的使用-->
    <bean class="com.itheima.spring.e_xmlpropertydi.Person" id="person1" p:id="123456" p:name="老王" p:car-ref="car" />

    <!--spEL表达式的使用-->
    <bean class="com.itheima.spring.e_xmlpropertydi.Person" id="person2" p:id="#{person.id}" p:name="#{person.name}" p:car="#{car}" />
```
- `Test.java`

```java
	public void test() {


		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

		Car car = (Car) applicationContext.getBean("car");
		System.out.println("car = " + car);


		Person person = (Person) applicationContext.getBean("person2");
		System.out.println("person = " + person);
	}
```

### IoC装配Bean  (annotation)

> **注解的装配**

- 添加注解
    - @Component
    - @Repository
    - @Service
    - @Controller
- 开启注解
    - `<context:annotation-config />`
    - 混合配置的时候使用
- 开启注解扫描
    - `<context:component-scan base-package="cn.item.spring"/>`
    - `"cn.item.spring"`: 也可以只给`cn`，所有子包都扫描
    - 包含了开启注解，单独使用

```bash
    1. 添加注解
    2. 开启注解
    3. ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
```

> **注解的注入**

```java
    @Value("rose")
    private String name;

    @value("#{c_dao}")// c_dao == id
    private CustomerDao cDao;


    // @Autowired,单独使用时, 默认是根据对象类型装配， xml的调setter方法，一般只有一个类型的对象，因为都是单列，
    // @Autowired + 形参，Spring工厂创建的时候，扫描到注解对应的方法回去执行，再去找对应的形参再赋值，前提是形参对应的对象也属于Spring
    @Autowired     
    @Qualifier(value = "c_dao")// c_dao == id
    private CustomerDao cDao;

    // JSR-250 java自己提供的，单独使用时也是根据类型注入
    @Resource
    @Resource(name="id")


    // JSR-330  需要额外的导包
    @Inject
    @Name("id")

```



### Bean的混合装配

### AOP
```bash
AOP Aspect Oriented Programing 面向切面编程

OCP原则：开闭原则，对拓展开放，对修改关闭

传统的方式：纵向继承体系重复性代码的编写，破坏了代码的封装，具有侵入性
    
AOP：横向抽取机制，在不修改原对象代码的情况下，通过代理对象，调用增强功能代码，从而对原有业务方法的增强

具体实现：动态代理

```

- **应用场景**
    - 记录日志
    - 监控方法运行时间(监控性能)
    - 权限控制
    - 缓存优化
    - 事务管理

- **相关术语**
    - `Aspect`      切面
    - `Joinpoint`   连接点
    - `Pointcut`    切入点
    - `Advice`      通知
    - `Target`      目标对象
    - `Weaving`     织入
    - `Introduction`引介

### JDK底层实现AOP

- `applicationContext.xml`

```xml
    <bean class="cn.item.jdk.Inter_c" id="inter_c_jdk"/>
```

- `Inter.java`

```java
    public interface Inter {
        void save ();
        int find ();
    }

```

- `Inter_c.jav`

```java
    public class Inter_c implements Inter {
        @Override
        public void save() {
            System.out.println("save ...");
        }

        @Override
        public int find() {
            System.out.println("find ...");
            return 0;
        }
    }

```

- `JdkProxy.java`

```java
    public class JdkProxy implements InvocationHandler{
        private Object target;

        public JdkProxy(Object target) {
            this.target = target;
        }

        public Object getProxyObject() {
            return Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),// 类
                    target.getClass().getInterfaces(),// 接口
                    this);// 过程
        }

        @Override
        public Object invoke(Object timeProxy, Method method, Object[] args) throws Throwable {

            if ("save".equals(method.getName()))
                before();
            return method.invoke(target);
        }

        public void before () {
            System.out.println("before ...");
        }
    }
```

- **test.java**

```java
public void jdk () {
        // target       获取目标对象
        // timeProxy        获取代理对象工厂
        // proxyObject  获取代理对象
        // invoke       执行方法

        Inter target = new Inter_c();
        JdkProxy timeProxy = new JdkProxy(target);
        Inter proxyObject = (Inter)timeProxy.getProxyObject();

        proxyObject.save();
        proxyObject.find();

    }
```

### Cglib底层实现AOP

- `applicationContext.xml`

```xml
    <bean class="cn.item.Cglib.Target" id="target_c_cglib"/>
```


- `Targer.java`

```java
    public class Target {
        public void save(){
            System.out.println("ProductService保存了");
        }

        public int find(){
            System.out.println("ProductService查询了");
            return 99;
        }
    }
```

- `CglibProxy.java`

```java
    import org.springframework.cglib.timeProxy.MethodInterceptor;// 注意导包
    public class CglibProxy implements MethodInterceptor {
        private Object target;
        public CglibProxy(Object target) {
            this.target = target;
        }

        public Object getProxyObject() {
            Enhancer e = new Enhancer();// 获取生成器
            e.setSuperclass(target.getClass());// 设置目标对象
            e.setCallback(this);// 设置回调函数
            return e.create();// 返回代理对象
        }

        @Override
        public Object intercept(Object timeProxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            before();
            Object result = method.invoke(target);
            return result;
        }

        public void before () {
            System.out.println("before .....");
        }
    }
```


- `test.java`

```java
    @Test
    public void cglib () {
        CglibService target = new CglibService();
        CglibProxy timeProxy = new CglibProxy(target);
        CglibService proxyObject = (CglibService) timeProxy.getProxyObject();

        proxyObject.save();
        proxyObject.find();
    }
```

>**JDK 和 Cglib的区别**

```bash
    JDK: 基于接口的代理，一定是基于接口，会生成目标对象的接口类型的子对象
    Cglib: 基于类的代理，不需要接口，会生成目标对象类型的子对象

    Spring在运行期间，生成动态代理对象，不需要特殊的编译器

    Spring的两种代理方式：
    1.目标对象实现了若干接口，Spring使用JDK的 java.lang.reflect.Proxy类代理
    2.目标对象没有实现任何接口，Spring使用Cglib库生成目标对象的子类

    注意&提示：
    1.对接口创建代理优于对类创建代理，Spring推荐面向接口编程。
        因为这样会产生更加耦合的系统，所以Spring默认是使用JDK代理
    2.标记为final的方法不能够被通知。
        Spring是为目标类产生子类，任何需要通知的方法都会被重写，通知织入。
        final方法是不允许被重写的。
    3.Spring只支持方法连接点，不提供属性接入点，如果属性被拦截破坏了封装。

```

### Spring AOP编程的两种方式

> **传统动态AOP**

- 使用纯Java实现，不需要专门的编译过程和类加载器，在运行期通过代理方式向目标类植入增强代码，相对复杂

- `applicationContext.xml`

```xml
    <bean class="cn.item.jdk.Inter_c" id="inter_c_jdk"/>

    <bean class="cn.item.Cglib.CglibService" id="c_cglib"/>

    <bean class="cn.item.old_aop.OldAopAdvice" id="old_aop_advice"/>

    <!-- 3.配置切面和切入点 -->
    <aop:config>
        <aop:pointcut id="allBeanAop" expression="execution(* cn.item..*.*(..))"/>

        <aop:advisor advice-ref="old_aop_advice" pointcut-ref="allBeanAop"/>
    </aop:config>
```

- `CglibService.java`

```java
    public class CglibService {
        public void save() {
            System.out.println("类save");
        }

        public int find() {
            System.out.println("类find");
            //int d = 1/0;
            return 99;
        }
    }
```

- `Inter_c.java`

```java
    public class Inter_c implements Inter {
        @Override
        public void save() {
            System.out.println("接口save");
        }

        @Override
        public int find() {
            System.out.println("接口find");
            return 0;
        }
    }
```

- `OldAopAdvice.java`

```java
    public class OldAopAdvice implements MethodInterceptor{
        private static Logger LOGGER = Logger.getLogger(String.valueOf(OldAopAdvice.class));

        @Override
        public Object invoke(MethodInvocation i) throws Throwable {
            long start = System.currentTimeMillis();

            Object proceed = i.proceed();// == method.invoke();

            long end = System.currentTimeMillis();
            long time = end - start;

            System.out.println(i.getThis().getClass().getName() + " >> " + i.getMethod().getName() + " >> " + time);
            LOGGER.info(i.getThis().getClass().getName() + " >> " + i.getMethod().getName() + " >> " + time);

            return proceed;
        }
    }
```

- `test.java`

```java
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = "classpath:applicationContext.xml")
    public class OldAop {

        @Autowired
        private Inter i;

        @Autowired
        private CglibService c;

        @Test
        public void interfaceAndClass () {
            i.find();
            i.save();
            System.out.println("............... ");
            c.find();
            c.save();
        }
    }
```



### .xml配置切面
> AspectJ是一个基于Java语言的AOP框架，Spring2.0开始支持第三方AOP框架(AspectJ),实现另一种AOP编程

- `applicationContext.xml`

```xml
    <bean class="cn.item.jdk.Inter_c" id="inter_c_jdk"/>

    <bean class="cn.item.Cglib.CglibService" id="c_cglib"/>

    <bean class="cn.item.aspect.AspectAdvice" id="aspect_advice"/>


    <aop:config>
        <aop:aspect ref="aspect_advice"><!-- ref="通知的id" -->
            <!-- 切入点 -->
            <aop:pointcut id="allBeanAspect" expression="execution(* cn.item..*.*(..))"/>

            <!-- 前置通知
            <aop:before method="firstBefore" pointcut-ref="allBeanAspect"/>
            <aop:before method="secondBefore" pointcut-ref="allBeanAspect"/>-->

            <!-- 后置通知
            <aop:after method="firstAfter" pointcut-ref="allBeanAspect"/> -->

            <!-- 后置通知且带返回值 returning="val" val和getAfterVal的形参保持一致
            <aop:after-returning method="getAfterVal" returning="val" pointcut-ref="allBeanAspect"/>
            -->

            <!-- 环绕通知-->
            <aop:around method="aroundAdvice" pointcut-ref="allBeanAspect"/>

            <!-- 抛出通知 -->
            <aop:after-throwing method="afterThrowAdvice" throwing="ex" pointcut-ref="allBeanAspect"/>
            
            
            <!-- 最终通知: 就算方法发生异常，最终通知都会执行 -->
            <aop:after method="afterFinally" pointcut-ref="allBeanAspect"/>
            <!--<aop:after method="afterFinally" pointcut="bean(*Service)"/>-->
        </aop:aspect>
    </aop:config>
```

- `AspectAdvice.java`

```java
    public class AspectAdvice {
        public void firstBefore(JoinPoint joinPoint) throws Throwable {
            System.out.println("first before ... ");
        }

        public void secondBefore () {
            System.out.println("second before ... ");
        }

        public void firstAfter() {
            System.out.println("first after... ");
        }


        public void getAfterVal(JoinPoint joinPoint, Object val) throws Throwable {
            System.out.println(" get after value .."+ val);
        }


        public Object aroundAdvice(ProceedingJoinPoint pJP) throws Throwable {
            System.out.println("环绕前...");
            Object result = pJP.proceed();
            System.out.println("环绕后 。。...");// 如果有异常，环绕后不能执行
            return result;
        }


        public void afterThrowAdvice(JoinPoint jP, Throwable ex) throws Throwable {
            System.out.println("::"+ jP.getTarget().getClass().getName());// cn.item.Cglib.CglibService 类的路径
            System.out.println("::"+ jP.getSignature().getName());// find 方法名
            System.out.println("::"+ ex.getMessage());// int i = 1/0; >> / by zero
        }

        public void afterFinally(JoinPoint jP) throws Throwable {
            System.out.println("after finally ...");
        }
    }
```

- `Test.java`

```java
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations="classpath:applicationContext-aspect.xml")
    public class Aspect {
        @Autowired
        private Inter i;

        @Autowired
        private CglibService c;

        @Test
        public void interfaceAndClass () {
            i.find();
            i.save();
            System.out.println(" >>>>>>>>>>> ");
            c.find();
            c.save();
        }

    }

```

> **通知小结**

- 只要掌握around通知类型，就可以实现其他四种效果

```java
    try{
        // 前置通知
        Object result = proceedingJoinPoint.proceed();
        // 后置通知
    } catch(Exception) {
        // 抛出通知
    } finally {
        // 最终通知
    }
```

### @annotation配置切面

- `applicationContext.xml`

```xml
    <!-- 开启注解扫描 -->
    <context:component-scan base-package="cn"/>
    <!-- 配置aop的aspectj的自动代理：
			自动扫描bean组件中，含有@Aspect的bean，将其作为aop管理，开启动态代理-->
    <aop:aspectj-autoproxy timeProxy-target-class="true"/>    
```

- `Advice.java`

```java
    @Component("annoAdvice")// 相当于<bean id="myAspect" class="cn.ttt.spring.a_aspectj.MyAspect"/>
    @Aspect// 相当于<aop:aspect ref="myAspect">
    public class Advice {


    /*    @Before("bean(*service)")
        public void before () {
            System.out.println("before ...");
        }*/


        // 自定义切入点
    /*    @Before("adviceCustomer()")
        public void before () {
            System.out.println("before ...");
        }
        @Pointcut("bean(p_service)")// 这里是自定义切入点
        public void adviceCustomer() throws Throwable{
            System.out.println("advice customer ...");
        }*/


    /*    @AfterReturning(value="cut1() || cut2()", returning = "val")
        public void afterReturn(JoinPoint joinPoint, Object val) {
            System.out.println(val);
            System.out.println("after return ...");
        }

        @Pointcut("bean(c_service)")
        public void cut1() {
        }

        @Pointcut("bean(p_service)")
        public void cut2() {
        }*/


    /*    @Around(value = "pServiceCut()")
        public Object around(ProceedingJoinPoint pJP) throws Throwable {
            System.out.println("环绕前..");
            Object proceed = pJP.proceed();
            System.out.println("环绕后..");
            return proceed;
        }

        @Pointcut("bean(p_service)")
        public void pServiceCut() {

        }*/

        @AfterThrowing(value = "execution(* cn.item.a_aspect_anno.CustomerService_c.*())", throwing = "ex")
        public void afterThrow(JoinPoint jp, Throwable ex) throws Throwable {
            System.out.println("..出异常了");
        }

        @After("bean(*service)")
    //    @After("execution(* cn.item.a_aspect_anno.CustomerService_c.find())")// 具体类的具体方法
        public void after(JoinPoint jp) {
            System.out.println(jp);// execution(void cn.item.a_aspect_anno.ProductService.find())
            System.out.println("最终通知..");
        }
    }
```

- `test.java`

```java
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations="classpath:applicationContext.xml")
    public class ATest {

        @Autowired
        private CustomerService c;

        @Autowired
        private ProductService p;

        @Test
        public void before () {
            c.save();
            c.find();
            ((CustomerService_c) c).update();// 会报错
            // java.lang.ClassCastException:
            // com.sun.timeProxy.$Proxy14 cannot be cast to cn.item.a_aspect_anno.CustomerService_c
            System.out.println(" -------------------");
            p.find();
            p.save();
        }
    }
```


### Spring的web集成
- `缺点`：在创建Spring容器同时，需要对容器中对象初始化。而每次初始化容器的时候，都创建了新的容器对象，消耗了资源，降低了性能。
- `思路`：保证容器对象只有一个。
- `方案`：将Spring容器绑定到Web Servlet容器上，让Web容器来管理Spring容器的创建和销毁。
- `分析`：ServletContext在Web服务运行过程中是唯一的， 其初始化的时候，会自动执行ServletContextListener 监听器 （用来监听上下文的创建和销毁），
- `步骤`：
    - 编写一个`ServletContextListener`监听器，在监听`ServletContext`到创建的时候，创建Spring容器，并将其放到`ServletContext`的属性中保存`setAttribute`(Spring容器名字，Spring容器对象) 。 
    - 无需手动创建该监听器，因为Spring提供了一个叫`ContextLoaderListener`的监听器，它位于`spring-web-4.3.13.RELEASE.jar`中。


### Spring JdbcTemplate

- 手动使用 JdbcTemplate
    - 数据源
    - 配置信息
    - 获取JdbcTemplate
    - 执行sql

- Spring装载JdbcTemplate

- 配置信息使用外部文件

- **每个Dao都需要注入JdbcTemplate**
    - 继承Spring框架封装的JdbcDaoSupport类获得jdbctemplate对象操作数据库
    - 配置spring核心配置文件，注入jdbcTemplate到Dao



### Spring事务管理机制

> Spring事务管理高层抽象主要包括3个`接口`，Spring的事务主要是由他们共同完成的

- `PlatformTransactionManager`：事务管理器
    - `connection.commit()`：提交事务
    - `connection.rollback()`：回滚事务
    - `connection.getTransaction`：获取事务状态

- `TransactionDefinition`：	事务定义信息(隔离、传播、超时、只读) — 通过配置如何进行事务管理。
    - `getIsolationLevel`：隔离级别获取
        - 脏读:一个事务读取了另一个事务改写但还未提交的数据,如果这些数据被回滚，则读到的数据是无效的。
        - 不可重复读：在同一事务中，多次读取同一数据返回的结果有所不同。换句话说就是，后续读取可以读到另一事务已提交的更新数据。
        - 可复读: 在同一事务中多次读取数据时，能够保证所读数据一样，也就是，后续读取不能读到另一事务已提交的更新数据。
        - 幻读：一个事务读取了几行记录后，另一个事务插入一些记录，幻读就发生了。再后来的查询中，第一个事务就会发现有些原来没有的记录。

    - `getPropagationBehavior`：传播行为获取
        - 一个业务调用多个方法，合并成一个事务
        - 多个事务合并成一个事务
        - 嵌套事务，大的事务嵌套小的事务，try...catch
    - `getTimeout`：获取超时时间（事务的有效期）
    - `isReadOnly`: 是否只读(保存、更新、删除—对数据进行操作-变成可读写的，查询-设置这个属性为true，只能读不能写)，事务管理器能够根据这个返回值进行优化。

- `TransactionStatus`：获取事务具体运行状态信息。
    - 只回滚：测试性能，并不存入数据，

- **超级接口和对象之间的关系**
    - 1）首先用户管理事务，需要先配置TransactionManager（事务管理器）进行事务管理
    - 2）然后根据TransactionDefinition(事务定义信息)，通过TransactionManager（事务管理器）进行事务管理；
    - 3）最后事务运行过程中，每个时刻都可以通过获取TransactionStatus（事务状态）来了解事务的运行状态。


### 声明式事务管理

> **一：编程式的事务管理**


> **二：声明式事务管理**


- **xml配置事务**
    - 目标对象：`tranfer`方法需要事务管理，需要增强，
        - 配置`事务管理器`
        - 配置spring提供的`事务通知`
        - 配置`切入点和切面`

    - ```xml
        <tx:method name="transfer" read-only="false" isolation="DEFAULT" propagation="REQUIRED" timeout="-1" no-rollback-for="" rollback-for="">
        ```
```
    
```

- **anno配置事务**
    - 开启注解扫描
      
        - `<context:component-scan base-package="com.ssm.service" />`
    - dataSource 是支持类需要的`JdbcDaoSupport`需要的，并不是`mapper`层需要
        - 调用父类的方法给父类赋值
        ```java
            public void setSuperDataSource(DataSource datasource){
                super.setDataSource(datasource)
            }
        ```
    - 开启事务注解驱动   
      
        - `<tx:annotation-driven transaction-manage="transactionManager" />`



# SpringMVC

- 基于MVC的设计理念，采用松散耦合可插播组件结构，比其他MVC框架更具备扩展性和灵活性
- **Model View Controller** 负责请求的调度和跳转, **DispatcherServlet**是SpringMVC的总导演，总策划，负责截获请求并分配给相应的处理器进行处理
- SpringMVC通过一套MVC注解，让POJO成为处理的请求，无需实现任何接口，同时，SpringMVC还支持REST的URL请求

![](imgs/springmvc.png)

### start

- `<packaging>war</packaging>`
- **前端控制器**`web.xml`
- 配置映射器、适配器、处理器、视图解析器: `springMVC`默认读取`/WEB-INF/{servlet-name}-servlet.xml`这个配置文件
    - 父类中看源码`DispatcherServlet.java`
    - SpringMVC都是面向接口，需要配置对应的实现, 而且实现的方式有很多种
- **映射器**
  
- `HandlerMapping` -- `BeanNameUrlHandlerMapping`将URL映射成对应对象的名称    
  
- **适配器**
    - `HandlerAdapter` -- `SimpleControllerHandlerAdapter`: 处理器必须是Controller接口的实现类
    ```java
        @Override
        public boolean supports(Object handler) {
            return (handler instanceof Controller);
        }

        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {

            return ((Controller) handler).handleRequest(request, response);
        }
    ```

    - `HandlerAdapter` -- `SimpleControllerHandlerAdapter`
    ```java
        @Override
        public boolean supports(Object handler) {
            return (handler instanceof Servlet);
        }

        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            ((Servlet) handler).service(request, response);
            return null;
        }
    ```

- **处理器**
    - `public class HelloController implements Controller`其实是个处理器, 只不过实现了Controller接口, 这时候`实现类`和`springMVC`还没有关系，需要进行配置
        - `<bean name="/first.do" class="com.ssm.controller.HelloController"/>`

- **视图解析器**
    - `UrlBasedViewResolver` -- `InternalResourceViewResolver`
        - `line49: <p>Example: prefix="/WEB-INF/jsp/", suffix=".jsp", viewname="test" ==> "/WEB-INF/jsp/test.jsp"`   
    ```xml
        <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
            <property name="suffix" value=".jsp" />
            <property name="prefix" value="/WEB-INF/pages/" />
        </bean>
    ```

### optimize
- `log4j.properties`日志
- `Servlet`只有在第一次访问的时候才进行初始化，对一个访问很不友好 == `<load-on-startup>1</load-on-startup>`
- **映射器**和 **适配器**不需要配置, => `DispatcherServlet.java -> line265`
    - 配置不过时的映射器`RequestMappingHandlerMapping`和适配器`RequestMappingHandlerAdapters`， 
        - **注解驱动**代替映射器和适配器 == `<mvc:annotation-driven />`
        - 注解驱动的两个功能：1.使用最新的映射器和适配器  2.对json的支持`AnnotationDrivenBeanDefinitionParser.java`
- **处理器**必须是`Controoler`的实现类，而且每个`Controller`又需要配置
    - 注解开发，不用配置`bean`
- **视图解析器**也可以不用配置
    - `mv.setViewName("/WEB-INF/pages/first.jsp")`

### RequestMapping(映射请求)
- 标准URL映射

- Ant风格的映射(通配符)

- restful风格的映射(占位符)

- 限定请求方法
    - `@RequestMapping(value = "cat", method = {RequestMethod.POST, RequestMethod.GET})`

- 限定请求参数
    - `@RequestMapping(value="cat", params="id")`
    - `@RequestMapping(value="cat", params="!id")`
    - `@RequestMapping(value="cat", params="id=1")`
    - `@RequestMapping(value="cat", params="id!=1")`

- 组合注解

### 接收数据和绑定数据
- `Servlet`内置对象：request，response，session
    - ```java
        @RequestMapping("show13")
        public ModelAndView test13(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
            ModelAndView mv = new ModelAndView("hello");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("request:" + request + "<br>");
            stringBuffer.append("response:" + response + "<br>");
            stringBuffer.append("session:" + session + "<br>");
            mv.addObject("msg", "springmvc接受的servlet内置对象:" + stringBuffer);
            return mv;
        }
        ```
```
    
```

- `Model model`
  
- `model.addAttribute("msg", "model")` 会覆盖 `request.setAttribute("msg", "request")`
  
- `cookie`
  
- `public String test(@CookieValue(value = "JSESSIONID") String cok, Model model) {`
  
- `url`中的请求参数
    - `public String test(@RequestParam(value = "username", required = false) String name, Model model) {`
    - `public String test(@RequestParam(value = "username", defaultValue = "goodman") String name, Model model) {`

- 基本数据类型的绑定：表单数据
    - ```java
        @RequestMapping("show20")
        @ResponseStatus(HttpStatus.OK)
        public void test(
                @RequestParam("name") String name,
                @RequestParam("age") Integer age,
                @RequestParam(value = "marry", defaultValue = "false") boolean marry,
                @RequestParam("income") Double income,
                @RequestParam("interests") String[] interests // 字符串数组
        ) {
            StringBuffer sb = new StringBuffer();
            sb.append("name:" + name + "\r\n");
            sb.append("age:" + age + "\r\n");
            sb.append("isMarry:" + marry + "\r\n");
            sb.append("income:" + income + "\r\n");
            sb.append("interests:" + Arrays.toString(interests) + "\r\n");
            System.out.println("sb = " + sb);
        }
        ```



- POJO对象的绑定：获取前端的数组最终也是赋值给实体类
    - 前端传过来的对象的key值和set方法名的后缀要保持一致
    ```java
        @RequestMapping("show21")
        public String test21(Model model, User user) {
            model.addAttribute("msg", user);
            return "hello";
        }
    ```

- 集合的绑定


### file upload 文件上传
- 导包
- 配置`bean`， id名已经约定好了，在`DispatcherServlet.java` line:160

### forward, redirect 转发，重定向
- servlet -> jsp的概念，方法到页面只有转发， 现在是方法到方法的概念: 是`<url>.do`
    - `return redirect:show.do`
    - `return forward:show.do` 可以拿到`request`域的数据

### interceptor 拦截器
- `HandlerInterceptor.java` 
    - `boolean preHandle`：判断是否执行业务方法
    - `void postHandle`: 业务方法完成之后再执行
    - `void afterCompletion`：视图渲染完成之后执行
- 实现`HandlerInterceptor`并重写方法，此时，还需要配置才能够使用
    - ```xml
        <mvc:interceptors>
            <mvc:interceptor>
                <mvc:mapping path="/**"/>
                <bean class="com.ssm.interceptor.MyInterceptor1"/>
            </mvc:interceptor>
            <mvc:interceptor>
                <mvc:mapping path="/**"/>
                <bean class="com.ssm.interceptor.MyInterceptor2"/>
            </mvc:interceptor>
        </mvc:interceptors>
        ```
```    
    
```

- **拦截器和过滤器区别**
    - `Fileter`: 针对`Servlet`, 所有的请求
    - `Interceptor`: 针对处理器方法

| 区别点   | 拦截器   | 过滤器   |
| :--- | :--- | :--- |
| - | 反射机制 | 回调函数 |
| - | 不依赖 | 依赖servlet容器 |
| - | 只能对`action`请求起作用 | 几乎所有请求 |
| 配置文件| `{name}-servlet.xml` | `web.xml` |
| 复用性 | 可以 | - |
| 生命周期 | action周期中可以多次调用 | tomcat启动时创建时调用一次 |
| - | 可以获取IOC容器中的各个bean，可以使用request, response对象 | - |
| 执行顺序 | - | - |

![](imgs/filter-interceptor.jpg)





