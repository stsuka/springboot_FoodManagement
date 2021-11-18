package com.example.demo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
	
	//全食材一覧表示ページネーション
	List<Food> findAllByStockCountGreaterThanOrderByBuyDateAscStockCountAsc(Integer i, Pageable limit);
	
	//カテゴリー別食材一覧表示
	List<Food> findByCategoryCodeIsAndStockCountGreaterThanOrderByBuyDateAscStockCountAsc(Integer categoryCode, Integer i);
	
	//カテゴリー別検索機能
	List<Food> findByCategoryCodeIsAndNameContainingAndStockCountGreaterThanOrderByBuyDateAscStockCountAsc(Integer categoryCode, String word, Integer i);

	//検索機能
	List<Food> findByNameContainingAndStockCountGreaterThanOrderByBuyDateAscStockCountAsc(String word, Integer i);

	//履歴検索機能
	List<Food> findByNameContainingOrderByBuyDateAscStockCountAsc(String word);
	
	//履歴表示に使用
	List<Food> findAllByOrderByBuyDateAsc(Pageable limit);
	
	//カテゴリー別食材履歴一覧表示
	List<Food> findByCategoryCodeIsOrderByBuyDateAscStockCountAsc(Integer categoryCode);
	
	//カテゴリー別履歴検索機能
	List<Food> findByCategoryCodeIsAndNameContainingOrderByBuyDateAscStockCountAsc(Integer categoryCode, String word);

}
