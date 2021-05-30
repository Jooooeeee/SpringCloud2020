# 1 什么是微服务

微服务架构是一种架构模式，它倡导将单一应用程序划分成一组小的服务，服务之间互相协调、互相配合，为用户提供最终价值，每个服务运行在其独立的进程中，服务与服务间采用轻量级的通信机制互相协作。每个服务都围绕着具体业务进行构建，并且能够被独立的部署到生产环境、类生产环境等。另外，应当尽量避免统一的、集中式的服务管理机制，对具体的一个服务而言，应根据业务上下文，选择合适的语言、工具对其进行构建

其中SpringCloud是分布式微服务架构的一站式解决方案，是多种微服务架构落地技术的集合体

# 2 服务注册与发现



## 2.1 什么是服务治理

在传统的RPC远程调用框架[^1]中，管理每个服务与服务之间的依赖关系比较复杂，所以需要使用服务治理，管理服务之间的依赖关系，可以实现服务调用、负载均衡、容错等，实现服务发现与注册

## 2.2 什么是服务注册与发现

在服务注册与发现中，有一个注册中心。当服务启动的时候，会把服务信息比如服务地址等以别名方式注册到注册中心上。另一方（服务消费者 ）以该别名的方式去注册中心上获取到实际的通讯地址，然后在实现RPC调用，RPC远程调用框架核心设计思想在于注册中心。因为使用注册中心管理服务之间的一个依赖关系。在任何RPC远程框架中，都会有一个注册中心（存放服务地址等相关信息）。

## 2.3 Eureka两组件

### 2.3.1 Eureka Server提供服务注册服务

各个微服务节点通过配置启动后，会在EurekaServer中注册，这样EurekaServer中的服务注册表将会存储所有可用服务节点的信息。

### 2.3.2 Eureka Client通过注册中心进行访问

是一个Java客户端，用于简化Eureka Server的交互，客户端同时具备一个内置的、使用轮询负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳（默认周期为30秒）。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中将这个服务节点移除（默认90秒） 

# 3 负载均衡（Load Balancer{LB}）

## 3.1 负载均衡是什么

简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的高可用。常见的负载均衡有软件Nginx，LVS，硬件F5等。

### 3.1.1 负载均衡分为两种

#### 3.1.1.1 集中式LB

即在服务的消费方和提供方之间使用__独立__的LB设备（可以是硬件,如F5，也可以是软件，如nginx）,由该设施负责把访问请求通过某种策略转发至服务的提供方。

#### 3.1.1.2 进程内LB

将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。Ribbon就属于进程内LB，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址

### 3.2 Ribbon负载均衡

Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法和服务调用。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出负载均衡（Load Balancer）后面所有的机器，Ribbon会自动帮助你基于某种规则（如简单轮询，随机连接等）去连接这些机器。我们很容易使用Ribbon是实现自定义的负载均衡算法。

### 3.3 Ribbon 理论总结

Ribbon其实就是一个软负载均衡的客户端组件，他可以和其他所需请求的客户端结合使用，和Eureka结合只是其中一个实例。（__PS__：Eureka内部已经集成Ribbon）。

Ribbon在工作时分成两步：

1. 先选择EurekaServer，它优先选择在同一个区域内负载较少的server。
2. 在根据用户指定的策略，在从server去到服务注册列表中选择一个地址。



#### 3.3.1 Ribbon本地负载均衡客户端VSNginx服务端负载均衡区别

Nginx是服务器负载均衡，客户端所有请求都会交给nginx，然后由nginx实现转发请求，即负载均衡是由服务端实现的。

Ribbon本地负载均衡，在___调用微服务接口___时，从注册中心上获取服务注册信息列表，缓存到本地，然后在本地实现轮询负载均衡策略。

##### 应用场景区别：

__Nginx__适合与服务器端实现负载均衡，比如Tomcat。__Ribbon__适合与在微服务中RPC远程调用实现本地服务负载均衡，比如Dubbo

![本地负载均衡](https://img-blog.csdnimg.cn/20200822143804800.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxOTc3ODM4,size_16,color_FFFFFF,t_70#pic_center)



### 3.4 Ribbon核心组件IRule

#### 3.4.1 IRule：根据特定算法冲服务列表中选取一个要访问的服务

Ribbon已经实现的类有如下：

![ribbon LB算法具体实现类](https://i.loli.net/2021/03/09/DjdWotcUTKryiVq.png)

#### 3.4.2 如何替换

##### 3.4.2.1 注意配置细节

这个自定义配置类不能放在@ComponentScan所扫描的当前包下以及子包下，否则我们自定义的这个配置类就会被所有的Ribbon客户端所共享，达不到特殊化定制的目的了



##### 新建package

![新建package](https://i.loli.net/2021/03/09/tP6Z5qSysaTW7f2.png)

##### 在RibbonRule 包下新建MySelfRule规则类

```java
@Configuration
public class MyselfRule {

    @Bean
    public IRule myRule() {
        return new RandomRule();//随机
    }
}
```

##### 在主启动类中添加@RibbonClient

```java
@RibbonClient(name = "CLOUD-PAYMENT-SERVICE" ,configuration = MyselfRule.class)
```

其中__name__ 为服务名称，__configuration__为规则类

### 3.5 负载均衡算法原理

#### 3.5.1 轮询算法原理

负载均衡算法：rest接口第几次请求 % 服务器集群总数量 = 实际调用服务器位置下标，每次服务重启动后rest接口计数从1 开始

#### 源码

省略部分不重要代码，

```java
public class RoundRobinRule extends AbstractLoadBalancerRule {
    private AtomicInteger nextServerCyclicCounter;
    private static final boolean AVAILABLE_ONLY_SERVERS = true;
    private static final boolean ALL_SERVERS = false;

    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            log.warn("no load balancer");
            return null;
        } else {
            Server server = null;
            int count = 0;

            while(true) {
                if (server == null && count++ < 10) {
                    List<Server> reachableServers = lb.getReachableServers();//获取所有正在运行的服务
                    List<Server> allServers = lb.getAllServers();//获取所有服务
                    int upCount = reachableServers.size();
                    int serverCount = allServers.size();
                    if (upCount != 0 && serverCount != 0) {
                        //计算得到服务下标
                        int nextServerIndex = this.incrementAndGetModulo(serverCount);
                        server = (Server)allServers.get(nextServerIndex);
                        if (server == null) {
                            Thread.yield();
                        } else {
                            if (server.isAlive() && server.isReadyToServe()) {
                                return server;
                            }

                            server = null;
                        }
                        continue;
                    }

                    log.warn("No up servers available from load balancer: " + lb);
                    return null;
                }

                if (count >= 10) {
                    log.warn("No available alive servers after 10 tries from load balancer: " + lb);
                }

                return server;
            }
        }
    }

    private int incrementAndGetModulo(int modulo) {
        int current;
        int next;
        do {
            current = this.nextServerCyclicCounter.get();
            //rest接口第几次请求 % 服务器集群总数量 = 实际调用服务器位置下标，每次服务重启动后rest接口计数从1 开始
            next = (current + 1) % modulo;
        } while(!this.nextServerCyclicCounter.compareAndSet(current, next));

        return next;
    }


}
```

#### 手写轮询算法

控制层代码

```java
    @GetMapping("/payment-lb")
    public String getPaymentLb() {
        List<ServiceInstance> instances = this.discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        ServiceInstance instance = loadBalancer.instance(instances);
        if (instance == null) {
            throw new RuntimeException("没有可用服务");
        }
        return instance.getPort()+"";
    }
```

具体实现逻辑

```java
@Component
public class MyLb implements LoadBalancer{

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private final int getAndIncrement(int modulo) {
        //算法：rest接口第几次请求 % 服务器集群总数量 = 实际调用服务器位置下标，每次服务重启动后rest接口计数从1 开始
       int current;
       int next;
        do {
            current = atomicInteger.get();
            next = (current + 1) % modulo;
        } while (!atomicInteger.compareAndSet(current, next));
        return next;
    }

    @Override
    public ServiceInstance instance(List<ServiceInstance> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        int andIncrement = this.getAndIncrement(list.size());

        return list.get(andIncrement);
    }
}
```



# 4 服务接口调用

## 4.1OpenFeign

### 4.1.1 概述

#### OpenFeign是什么东西

Feign是一个声明式WebService客户端。使用Feign能让编写WebService客户端更加简单。

它的使用方法是<font style="color:red">定义一个服务接口然后在上面添加注解</font>。Feign也支持可拔插式的编码器和解码器。SpringCloud对Feign进行了封装，使其支持了SpringMvc标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

#### 能干嘛

<font style="color:blue">Feign能干嘛</font>

Feign旨在使编写JavaHttp客户端变得更加容易。

前面在使用Ribbon+RestTemplate时，利用RestTemplate对Http请求的封装处理，形成了一套模板化的调用方式。但是在实际开发中，由于对服务依赖的调用可能不止一处，<font style="color:red">往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用</font>，所以，Feign在此基础上做了进一步封装，由它来帮助我们定义和实现依赖接口的定义。在Feign的实现下,<font style="color:red">我们只需创建一个接口并使用注解的方式来配置它</font>>，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。

<font style="color:blue">Feign集成了Ribbon</font>

利用RIbbon维护了Payment的服务列表信息，并且通过轮询实现了客户端的负载均衡。而与Ribbon不同的是，<font style="color:red">通过Feign只需要定义服务绑定接口且声明式的方法</font>，优雅而简单的实现了服务调用

# 5 服务降级

## 5.1概述

### 5.1.1 分布式系统面临的问题

复杂分布式体系结构中的应用程序有数十种依赖关系，每个依赖关系在某些时候将不可避免地失败。

<font style="color:blue">服务雪崩</font>

多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，这就是所谓的“扇出”。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的雪崩效应。

对于高流量的应用来说，单个后端依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能影响整个应用程序或系统。

### 5.1.2 Hystrix

__Hystrix__是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统中，许多依赖不可避免的会调用失败，比如超时，异常等，Hystrix能够保证一个依赖出问题的情况下，不会导致整个服务失败，避免级联故障，以提高分布式系统的弹性。

类似于“断路器”，断路器本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控，向调用方返回一个服务预期的，可处理的备选响应，而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

#### 5.1.3 Hystrix三个重要概念

##### 5.1.3.1 服务降级

服务器忙，请稍后哦再试，不让客户端等待并立刻返回一个友好提示，fallback

哪些情况会触发降级？

1. 程序运行异常
2. 超时
3. 服务熔断触发服务降级
4. 线程池/信号量打满也会导致服务降级

##### 5.1.3.2 服务熔断

类比保险丝达到最大服务访问后，直接拒绝访问，拉闸限电，然后调用服务降级的方法并返回友好提示

服务降级->进而熔断->恢复调用链路

##### 5.1.3.3 服务限流

秒杀高并发等操作，严禁一窝蜂的过来拥挤，大家排队，一秒钟N个，有序进行

# 6 Spring Cloud GateWay 网关

## 6.1 是什么

Spring Cloud Gateway是Spring官方基于Spring 5.0，Spring Boot 2.0和Project Reactor等技术开发的网关，Spring Cloud Gateway旨在为微服务架构提供一种简单而有效的统一的API路由管理方式。Spring Cloud Gateway作为Spring Cloud生态系中的网关，目标是替代ZUUL，其不仅提供统一的路由方式，并且基于Filter链的方式提供了网关基本的功能，例如：安全，监控/埋点，和限流等


## 6.2 功能 

GateWay 网关路由功能不仅仅简单的“转发请求”，在请求到达网关再流转到指定服务之间做什么事情，它不光可以拒绝请求，甚至可以“篡改”请求的参数，

## 6.3 几个重要概念

GateWay中可以定义很多个Route，一个Route就是一套包含完整转发规则的路由，主要由三部分组成

![路由组成](https://img-blog.csdnimg.cn/20210108102507397.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x0MzI2MDMwNDM0)

__· 断言集合__ 断言是路由处理的第一个环节，它是路由的匹配规则，它决定了一个网络请求是否可以匹配给当前路由来处理，之所以它是一个集合的原因是我们可以给一个路由添加多个断言，当每个断言都匹配成功以后才算过了路由的第一关。

__· 过滤器集合__ 如果请求通过了前面的断言匹配，那就表示它被当前路由正式接收了，接下来这个请求就要经过一系列的过滤器集合。过滤器的功能就多种多样了，可以对当前请求做一系列的操作，比如说权限验证，安全，监控/埋点，和限流等，在过滤器这一层依然可以通过Response里的Status Code达到中断效果，比如鉴权失败的访问请求设置状态码为403之后的中断操作。

__· URI__ 如果请求顺利通过过滤器的处理，接下来就是到了最后一步，那就是转发请求。URI是统一资源标识符，它可以是一个具体的网址，也可以是一个IP+端口的组合，或者注册中心的服务名称。

## 6.4 路由的工作流程

让我们从一个请求到达网关开始，看看Gateway内部流程的过程

![路由的工作流程](https://img-blog.csdnimg.cn/20210108102718816.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x0MzI2MDMwNDM0,size_16,color_FFFFFF,t_70)

__· Predicate Handler__ 具体承接类是RoutePredicateHandlerMapping。首先它获取所有的路由（配置的routes），然后依次循环每个Route，把应用请求与Route中的配置的所有断言进行匹配，如果当前Route__所有__断言都验证通过Predicate Handler就选定当前的路由。

__· Filter Handler __ 在前一步选中路由后，由FilteringWebHandler将请求交给过滤器，在具体处理过程中，不仅当前Route中定义的过滤器会生效，我们在项目中添加的全局过滤器（Global Filter）也会一同参与。

__· 寻址__ 这一步将把请求转发到URI指定的地址，在发送请求之前，所有Pre类型过滤器都将被执行，而Post过滤器会在调用请求返回之后起作用。有关过滤器的详细内容将会在稍后的章节里讲到。

## 6.5 GateWay9527 实现

1. 依赖

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
```

2. 配置文件

   ```yaml
   spring:
     application:
       name: cloud-gateway
     cloud:
       gateway:
         routes:
           - id: payment_routh              #路由id，没有固定规则但要求唯一，建议配合服务名
             uri: http://localhost:8001     #匹配后提供服务的路由地址
             predicates:
             - Path=/payment8001/**       #断言，路径相匹配的进行路由
   ```

3. 用编码方式，代替配置文件

   ```java
   @Configuration
   public class GatewayConfig {
   
       @Bean
       public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
           RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
           return builder.route("payment_routh", i -> i.path("/payment8001/**").uri("http://localhost:8001")).build();
       }
   }
   ```

4. 自定义过滤器

   ```java
   @Component
   public class MyFilter3 implements GlobalFilter, Ordered {
   
       /**
        * 具体过滤逻辑
        * @param exchange
        * @param chain
        * @return
        */
       @Override
       public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
           System.out.println(" 第一个过滤器 ");
           String uname =(String) exchange.getRequest().getQueryParams().getFirst("uname");
           if (uname == null) {
               System.out.println(" 没通过过滤 ");
               exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
               return exchange.getResponse().setComplete();
           }
           return chain.filter(exchange);
       }
   
       /**
        * order 越小，越先运行，如果order一样时，根据文件顺序运行。
        * @return
        */
       @Override
       public int getOrder() {
           return 0;
       }
   }
   ```



# 7 Config 分布式配置中心

 ## 概述

​	微服务意味着要将单体应用中的业务分成一个个子服务，每个服务的粒度相对较小，因此系统中会出现大量的服务。由于每个服务都需要必要的配置信息才能运行，所以一套集中式的动态的配置管理设施时必不可少的。

## 7.1 是什么

​	SpringCloud Config 为微服务架中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了一个中心化的外部配置。

## 7.2 怎么玩

​	SpringCloud Config分为服务端和客户端两部分。

​	服务端也称为分布式配置中心，它是一个独立的微服务应用，用来连接配置服务器并为客户端提供获取配置信息，加密/解密信息等访问接口。

​	客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。配置服务器默认采用git来存储配置信息，这样就有助于对环境配置进行版本管理，并且可以通过git客户端工具来方便的管理和访问配置内容。

![配置中心流程](https://i.loli.net/2021/05/26/g9tEONfwBZMJV5b.png)

## 7.3 能干什么

1. 集中管理配置文件
2. 不同环境不同配置，动态化的配置更新，分环境部署比如dev/test/prod等等
3. 运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统拉取自己的信息
4. 当配置发生变动时，服务不再需要重启即可感知到配置的变化并应用新的配置
5. 将配置信息以REST接口形式暴露

[^1]:  在分布式计算，远程过程调用（英语：Remote Procedure Call，缩写为 RPC）是一个计算机通信协议。该协议允许运行于一台计算机的程序调用另一个地址空间（通常为一个开放网络的一台计算机）的子程序，而程序员就像调用本地程序一样，无需额外地为这个交互作用编程（无需关注细节）。RPC是一种服务器-客户端（Client/Server）模式，经典实现是一个通过发送请求-接受回应进行信息交互的系统。 

