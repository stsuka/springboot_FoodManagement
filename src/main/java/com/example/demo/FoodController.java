package com.example.demo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FoodController {

	@Autowired
	HttpSession session;
	
	@Autowired
	FoodRepository foodRepository;
	
	//初期表示
	@RequestMapping("/")
	public ModelAndView index(ModelAndView mv) {
		
		LocalDate todayLD = LocalDate.now();
		DateTimeFormatter dtf = 
				DateTimeFormatter.ofPattern("y'年'MM'月'dd'日'");

		long count = foodRepository.count();
		
		List<String> judgeList = new ArrayList<>();
		
		for(int i = 0; i < count; i++) {
			try {
				Food bestBefore1 = foodRepository.findById(i + 1).get();
				
				//削除機能実装時に再確認
				//しめじを削除した際にbestBefore1が取れなくなる
				//取れない状態でbestBeforeLDの処理が進むため、エラーとなる
				//if文での対応も可、Optional型を用いた条件式作成
				//講師に確認をとってみる
				
				LocalDate bestBeforeLD =  LocalDate.ofInstant(bestBefore1.getBestbefore().toInstant(), ZoneId.systemDefault());
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
			} catch(Exception e) {
				judgeList.add(null);
				continue;
			}
		}
		
		mv.addObject("today", todayLD.format(dtf));
		mv.addObject("foodList", foodRepository.findAllByOrderByCodeAsc());
		mv.addObject("judge", judgeList);
		mv.setViewName("home");
		return mv;
		
	}
	
	//削除機能
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public ModelAndView delete(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		foodRepository.deleteById(Integer.parseInt(code));
		
		index(mv);
		
		return mv;
	}
	

}
