package com.example.demo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@Autowired
	FoodCategoryRepository foodCategoryRepository;
	
	//初期表示
	@RequestMapping("/")
	public ModelAndView index(ModelAndView mv) {
		
		//日時関連の変数初期化　→Food.javaに移転
		LocalDate todayLD = LocalDate.now();
		DateTimeFormatter dtf = 
				DateTimeFormatter.ofPattern("y'年'MM'月'dd'日'");
		
		mv.addObject("today", todayLD.format(dtf));
		mv.addObject("categories", foodCategoryRepository.findAll());
		mv.addObject("foodList", foodRepository.findAllByStockCountGreaterThanOrderByBuyDateAscStockCountAsc(0));
		mv.setViewName("home");
		return mv;
		
	}
	
	//カテゴリーリンク押下
	@RequestMapping("/category/{code}")
	public ModelAndView foodByCode(
			@PathVariable(name="code") int categoryCode,
			ModelAndView mv) {
		
		//日時関連の変数初期化 →Food.javaに移転
		LocalDate todayLD = LocalDate.now();
		DateTimeFormatter dtf = 
				DateTimeFormatter.ofPattern("y'年'MM'月'dd'日'");
		
		mv.addObject("today", todayLD.format(dtf));
		mv.addObject("foodList", foodRepository.findByCategoryCodeOrderByBuyDateAscStockCountAsc(categoryCode));
		mv.addObject("categories", foodCategoryRepository.findAll());
		mv.setViewName("home");
		return mv;
	}
	
	//登録リンク押下
	@RequestMapping("/add")
	public String add() {
		return "add";
	}
	
	//登録ボタン押下
	@RequestMapping("/add/addResult")
	public ModelAndView addResult(
			@RequestParam(name="name", defaultValue="") String name,
			@RequestParam(name="categoryCode", defaultValue="") String categoryCode,
			@RequestParam(name="buyCount", defaultValue="") String buyCount,
			@RequestParam(name="buyDate", defaultValue="") String buyDateString,
			@RequestParam(name="bestBefore", defaultValue="") String bestBeforeString,
			ModelAndView mv) {
		
		Date buyDate = null;
		Date bestBefore = null;
		Food food = null;
		String stockCount = buyCount;
		
		//String→Date型変換
		if(!(buyDateString.equals(""))) {
			buyDate = Date.valueOf(buyDateString);
		} 
		
		if(!(bestBeforeString.equals(""))) {
			bestBefore = Date.valueOf(bestBeforeString);
		} 
		
		//入力チェック(食べ物名・個数・購入日)
		if(name.equals("") || stockCount.equals("") || buyDate == null) {
			mv.addObject("message", "入力されていない項目があります。");
			mv.addObject("name", name);
			mv.addObject("buyCount", buyCount);
			mv.setViewName("add");
		
		} else {
			try {
				//賞味期限が登録された場合
				if(bestBefore == null) {
					food = new Food
							(Integer.parseInt(categoryCode), name, Integer.parseInt(stockCount),
									Integer.parseInt(buyCount), buyDate);
				
				//賞味期限が登録されなかった場合
				} else {
					food = new Food
							(Integer.parseInt(categoryCode), name, Integer.parseInt(stockCount),
									Integer.parseInt(buyCount), buyDate, bestBefore);
				}
				
				foodRepository.saveAndFlush(food);	
				index(mv);
			
			//「個数」欄に数字以外が入力された場合
			} catch(NumberFormatException e) {
				mv.addObject("message", "「個数」には数字を入力してください。");
				mv.setViewName("add");
			}
		}
		
		return mv;
	}
	
	//トップページの食べ物リンクを押下したとき
	@RequestMapping("/update/{code}")
	public ModelAndView update(
		@PathVariable(name="code") String code,
		ModelAndView mv) {
		
		Optional<Food> updateFoodList = foodRepository.findById(Integer.parseInt(code));
	
		mv.addObject("food", updateFoodList.get());
		mv.setViewName("update");
		return mv;
	}
	
	//「在庫管理」の変更ボタンを押下したとき
	@RequestMapping(value="/update/finalUpdate", method=RequestMethod.POST)
	public ModelAndView finalupdate(
			@RequestParam("code") String code,
			@RequestParam("categoryCode") String categoryCode,
			@RequestParam("name") String name,
			@RequestParam("stockCount") String stockCount,
			@RequestParam("buyCount") String buyCount,
			@RequestParam("buyDate") String buyDate,
			@RequestParam(name="bestBefore", defaultValue="") String bestBefore,
			ModelAndView mv) {
		
		Food food = null;
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			if(bestBefore.equals("")) {
				food = new Food
						(Integer.parseInt(code), Integer.parseInt(categoryCode),
							name,Integer.parseInt(stockCount), Integer.parseInt(buyCount), format.parse(buyDate));
			} else {
				food = new Food
						(Integer.parseInt(code), Integer.parseInt(categoryCode), name,
							Integer.parseInt(stockCount), Integer.parseInt(buyCount), format.parse(buyDate), format.parse(bestBefore));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		foodRepository.saveAndFlush(food);
		return index(mv);
	}
	
	//「登録情報の編集」の編集ボタンを押下したとき
	@RequestMapping("/fix")
	public ModelAndView fix(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		Optional<Food> fixFoodList = foodRepository.findById(Integer.parseInt(code));
		
		mv.addObject("food", fixFoodList.get());
		mv.setViewName("fix");
		return mv;
	}
	
	//編集完了ボタン押下
		@RequestMapping("fix/finalFix")
		public ModelAndView fixResult(
				@RequestParam("code") String code,
				@RequestParam(name="fixName", defaultValue="") String name,
				@RequestParam(name="fixCategoryCode", defaultValue="") String categoryCode,
				@RequestParam(name="fixBuyCount", defaultValue="") String buyCount,
				@RequestParam(name="fixBuyDate", defaultValue="") String buyDateString,
				@RequestParam(name="fixBestBefore", defaultValue="") String bestBeforeString,
				ModelAndView mv) {
			
			Date buyDate = null;
			Date bestBefore = null;
			Food food = null;
			String stockCount = buyCount;
			
			//String→Date型変換
			if(!(buyDateString.equals(""))) {
				buyDate = Date.valueOf(buyDateString);
			} 
			
			if(!(bestBeforeString.equals(""))) {
				bestBefore = Date.valueOf(bestBeforeString);
			} 

			try {
				//賞味期限が登録された場合
				if(bestBefore == null) {
					food = new Food
							(Integer.parseInt(code), Integer.parseInt(categoryCode), name, 
									Integer.parseInt(stockCount), Integer.parseInt(buyCount), buyDate);
				
				//賞味期限が登録されなかった場合
				} else {
					food = new Food
							(Integer.parseInt(code), Integer.parseInt(categoryCode), name,
									Integer.parseInt(stockCount), Integer.parseInt(buyCount), buyDate, bestBefore);
				}
				
				foodRepository.saveAndFlush(food);	
				index(mv);
			
			//「個数」欄に数字以外が入力された場合
			} catch(NumberFormatException e) {
				mv.addObject("message", "「個数」には数字を入力してください。");
				mv.setViewName("fix");
			}
	
			return mv;
		}
	
	//削除機能
	@RequestMapping(value="/delete")
	public ModelAndView delete(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		foodRepository.deleteById(Integer.parseInt(code));
		index(mv);
		return mv;
	}
	
	//履歴表示
	@RequestMapping("history")
	public ModelAndView history(ModelAndView mv) {
		mv.addObject("foodList", foodRepository.findAllByOrderByBuyDateAsc());
		mv.setViewName("history");
		return mv;
	}
	
}
