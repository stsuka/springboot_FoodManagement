package com.example.demo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FoodController {

	@Autowired
	HttpSession session;
	
	@Autowired
	FoodRepository foodRepository;
	
	@Autowired
	FoodHistoryRepository foodHistoryRepository;
	
	//初期表示
	@RequestMapping("/")
	public ModelAndView index(ModelAndView mv) {
		
		//日時関連の変数初期化
		LocalDate todayLD = LocalDate.now();
		DateTimeFormatter dtf = 
				DateTimeFormatter.ofPattern("y'年'MM'月'dd'日'");

		//foodテーブル要素全件取得
		List<Food> foodList = foodRepository.findAll();
		
		//判定結果表示
		List<String> judgeList = new ArrayList<>();
		
		//foodテーブルの件数で繰り返し処理
		for(Food extendFoodList : foodList) {
			
			//Optional型(繰り返し処理の回数と同数のコード値があるか判定)
			Optional<Food> bestBeforeOptional = foodRepository.findById(extendFoodList.getCode());
			if(bestBeforeOptional.isPresent()) {
				Food bestBefore2 = bestBeforeOptional.get();
				LocalDate bestBeforeLD =  LocalDate.ofInstant(bestBefore2.getBestbefore().toInstant(), ZoneId.systemDefault());
				long diffDays = todayLD.until(bestBeforeLD,ChronoUnit.DAYS);
				
			    //賞味期限-閲覧日が3日以上
				if(3 <= diffDays) {
					judgeList.add("💮");
				
				//賞味期限-閲覧日が０日以上3日未満
				} else if(0 < diffDays && diffDays < 3) {
					judgeList.add("〇");
					
				//賞味期限当日
				} else if(diffDays == 0){
					judgeList.add("本日です");
					
				//賞味期限切れ
				} else {
					judgeList.add("❗❗");	
				}
			}
		}
		
		mv.addObject("today", todayLD.format(dtf));
		mv.addObject("foodList", foodRepository.findAllByOrderByCodeAsc());
		mv.addObject("judge", judgeList);
		mv.setViewName("home");
		return mv;
		
	}
	
	//履歴表示
	@RequestMapping("history")
	public ModelAndView history(ModelAndView mv) {
		mv.addObject("foodHistoryList", foodHistoryRepository.findAllByOrderByCodeAsc());
		mv.setViewName("history");

		return mv;
	}
	
}
