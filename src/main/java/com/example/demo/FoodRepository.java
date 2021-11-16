package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

	//履歴表示に使用
	List<Food> findAllByOrderByBuyDateAsc();
	
	//カテゴリー別食材一覧表示
	List<Food> findByCategoryCodeOrderByBuyDateAscStockCountAsc(Integer categoryCode);
	
	//全食材一覧表示
	List<Food> findAllByStockCountGreaterThanOrderByBuyDateAscStockCountAsc(Integer i);
	
}
