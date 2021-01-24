package com.joe.springcloud.service.impl;

import com.joe.commons.entities.Payment;
import com.joe.springcloud.dao.PaymentDao;
import com.joe.springcloud.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;

    @Override
    public int create(Payment payment) {
       return paymentDao.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.getPaymentById(id);
    }
}
