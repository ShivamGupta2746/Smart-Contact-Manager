package com.project.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.MyOrder;

public interface MyOrderRepository extends JpaRepository<MyOrder, Long>{
	public MyOrder findByOrderId(String orderId);
}
