package com.joe.consumer.order80.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
