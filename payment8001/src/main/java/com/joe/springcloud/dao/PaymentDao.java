package com.joe.springcloud.dao;

import com.joe.commons.entities.Payment;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentDao {
    @Insert("Insert into payment (serial) values (#{serial})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int create(Payment payment);

    @Select("select * from payment where id = #{id}")
    Payment getPaymentById(@Param("id") Long id);
}
