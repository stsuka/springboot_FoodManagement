package com.example.demo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FoodController {
	
	@Autowired
	FoodRepository foodRepository;
	
	@Autowired
	FoodCategoryRepository foodCategoryRepository;
	
	//初期表示
	@RequestMapping("/")
	public ModelAndView index(ModelAndView mv) {
		
		java.util.Date today = new java.util.Date();
		
		mv.addObject("today", today);
		mv.addObject("categories", foodCategoryRepository.findAll());
		getItems(mv, 1);
		mv.setViewName("home");
		return mv;
		
	}
	
	//検索機能
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public ModelAndView search(
			@RequestParam(name="word", defaultValue="") String word,
			@RequestParam(name="categoryCode", defaultValue="") String categoryCode,
			ModelAndView mv) {

		java.util.Date today = new java.util.Date();
		
		//検索ボタン押下時、ワード指定がなかった場合
		if(word.equals("")) {
			if(categoryCode.equals("0")) {
				index(mv);
			} else {
				mv.addObject("foodList",
						foodRepository.findByCategoryCodeIsAndStockCountGreaterThanOrderByBuyDateAscStockCountAsc
						(Integer.parseInt(categoryCode), 0));
			}
		
		//検索ボタン押下時、ワード指定があった場合
		} else {
			if(categoryCode.equals("0")) {
				mv.addObject("foodList",
					foodRepository.findByNameContainingAndStockCountGreaterThanOrderByBuyDateAscStockCountAsc(word, 0));
			} else {
				mv.addObject("foodList",
					foodRepository.findByCategoryCodeIsAndNameContainingAndStockCountGreaterThanOrderByBuyDateAscStockCountAsc
					(Integer.parseInt(categoryCode), word, 0));
			}
		}
		
		mv.addObject("today", today);
		mv.addObject("categories", foodCategoryRepository.findAll());
		mv.addObject("word", word);
		mv.addObject("categoryCode", Integer.parseInt(categoryCode));
		mv.setViewName("home");
		return mv;
	}
	
	//新規登録リンク押下
	@RequestMapping("/add")
	public String add() {
		return "add";
	}
	
	//新規登録ページで登録ボタン押下
	@RequestMapping("/add/addResult")
	public ModelAndView addResult(
			@RequestParam(name="name", defaultValue="") String name,
			@RequestParam(name="categoryCode") String categoryCode,
			@RequestParam(name="buyCount", defaultValue="") String buyCount,
			@RequestParam(name="buyDate", defaultValue="") String buyDateString,
			@RequestParam(name="bestBefore", defaultValue="") String bestBeforeString,
			ModelAndView mv) {
		
		String stockCount = buyCount;
		
		//入力チェック(食べ物名・個数・購入日)
		if(name.equals("") || buyCount.equals("") || buyDateString.equals("")) {
			mv.addObject("message", "入力されていない項目があります。");
			mv.addObject("name", name);
			mv.addObject("categoryCode", Integer.parseInt(categoryCode));
			mv.addObject("buyCount", buyCount);
			mv.setViewName("add");
		
		} else {
			try {
				
				Food food = null;
				
				//賞味期限が登録されなかった場合
				if(bestBeforeString.equals("")) {
					food = new Food
							(Integer.parseInt(categoryCode), name, Integer.parseInt(stockCount),
									Integer.parseInt(buyCount), Date.valueOf(buyDateString));
				
				//賞味期限が登録された場合
				} else {
					food = new Food
							(Integer.parseInt(categoryCode), name, Integer.parseInt(stockCount),
									Integer.parseInt(buyCount), Date.valueOf(buyDateString), Date.valueOf(bestBeforeString));
				}
				
				foodRepository.saveAndFlush(food);	
				index(mv);
			
			//「個数」欄に数字以外が入力された場合
			} catch(NumberFormatException e) {
				mv.addObject("message", "「個数」には数字を入力してください。");
				mv.addObject("name", name);
				mv.addObject("categoryCode", Integer.parseInt(categoryCode));
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
		
		Optional<Food> food = foodRepository.findById(Integer.parseInt(code));
		mv.addObject("food", food.get());
		mv.setViewName("update");
		return mv;
	}
	
	//在庫管理ページの変更ボタンを押下したとき
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
			
		try {
			Food food = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			//賞味期限が登録されている場合
			if(bestBefore.equals("")) {
				food = new Food
						(Integer.parseInt(code), Integer.parseInt(categoryCode),
							name,Integer.parseInt(stockCount), Integer.parseInt(buyCount), format.parse(buyDate));
			
			//賞味期限が登録されていない場合
			} else {
				food = new Food
						(Integer.parseInt(code), Integer.parseInt(categoryCode), name,
							Integer.parseInt(stockCount), Integer.parseInt(buyCount),
							format.parse(buyDate), format.parse(bestBefore));
			}
			
			foodRepository.saveAndFlush(food);
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return index(mv);
	}
	
	//在庫管理ページの編集ボタンを押下したとき
	@RequestMapping("/fix")
	public ModelAndView fix(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		Optional<Food> food = foodRepository.findById(Integer.parseInt(code));
		
		mv.addObject("food", food.get());
		mv.setViewName("fix");
		return mv;
	}
	
	//登録情報の編集ページの編集完了ボタン押下
	@RequestMapping("fix/finalFix")
	public ModelAndView fixResult(
			@RequestParam("code") String code,
			@RequestParam(name="fixName") String name,
			@RequestParam(name="fixCategoryCode", defaultValue="") String categoryCode,
			@RequestParam(name="fixBuyCount", defaultValue="") String buyCount,
			@RequestParam(name="buyDate", defaultValue="") String buyDateString,
			@RequestParam(name="bestBefore", defaultValue="") String bestBeforeString,
			ModelAndView mv) {

		//入力チェック(食べ物名・個数・購入日)
		if(name.equals("") || buyCount.equals("") || buyDateString.equals("")) {
			mv.addObject("message", "入力されていない項目があります。");
			mv.addObject("name", name);
			mv.addObject("categoryCode", Integer.parseInt(categoryCode));
			mv.addObject("buyCount", buyCount);
			mv.setViewName("add");
		
		} else {
			try {
				String stockCount = buyCount;
				Food food = null;
				
				//賞味期限が登録されなかった場合
				if(bestBeforeString.equals("")) {
					food = new Food
							(Integer.parseInt(code), Integer.parseInt(categoryCode), name, 
									Integer.parseInt(stockCount), Integer.parseInt(buyCount), Date.valueOf(buyDateString));
				
				//賞味期限が登録されなかった場合
				} else {
					food = new Food
							(Integer.parseInt(code), Integer.parseInt(categoryCode), name,
									Integer.parseInt(stockCount), Integer.parseInt(buyCount), 
									Date.valueOf(buyDateString), Date.valueOf(bestBeforeString));
				}
				
				foodRepository.saveAndFlush(food);
				index(mv);
			
			//「個数」欄に数字以外が入力された場合
			} catch(NumberFormatException e) {
				e.printStackTrace();
				mv.addObject("message", "「個数」には数字を入力してください。");
				fix(code, mv);
			}
		}
		return mv;
	}
	
	//削除機能
	@RequestMapping(value="/delete")
	public ModelAndView delete(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		foodRepository.deleteById(Integer.parseInt(code));
		return index(mv);
	}
	
	//履歴表示
	@RequestMapping("/history/{page}")
	public ModelAndView historyPageNation(
			@PathVariable(name="page") int page,
			ModelAndView mv) {
		
		Pageable limit = PageRequest.of(page - 1, 15);
		long count = foodRepository.count();
		long pages = count / 15;  //答え
		long rem = count % 15;    //あまり
		
		mv.addObject("pages", (rem == 0) ? pages : pages + 1);
		mv.addObject("page", page);
		mv.addObject("foodList", foodRepository.findAllByOrderByBuyDateAsc(limit));
		mv.addObject("pageNation", "pageNation");
		mv.setViewName("history");
		return mv;
	}
	
	//検索機能
	@RequestMapping(value="/history/search", method=RequestMethod.GET)
	public ModelAndView historySearch(
			@RequestParam(name="word", defaultValue="") String word,
			@RequestParam(name="categoryCode", defaultValue="") String categoryCode,
			ModelAndView mv) {
		
		//検索ボタン押下時、ワード指定がなかった場合
		if(word.equals("")) {
			if(categoryCode.equals("0")) {
				historyPageNation(1, mv);
			} else {
				mv.addObject("foodList",
						foodRepository.findByCategoryCodeIsOrderByBuyDateAscStockCountAsc
						(Integer.parseInt(categoryCode)));
			}
		
		//検索ボタン押下時、ワード指定があった場合
		} else {
			if(categoryCode.equals("0")) {
				mv.addObject("foodList",
					foodRepository.findByNameContainingOrderByBuyDateAscStockCountAsc(word));
			} else {
				mv.addObject("foodList",
					foodRepository.findByCategoryCodeIsAndNameContainingOrderByBuyDateAscStockCountAsc
					(Integer.parseInt(categoryCode), word));
			}
		}
		
		mv.addObject("categories", foodCategoryRepository.findAll());
		mv.addObject("word", word);
		mv.addObject("categoryCode", Integer.parseInt(categoryCode));
		mv.setViewName("history");
		return mv;
	}
	
	//ページネーション(全食材一覧表示)
	@RequestMapping("/page/{page}")
	public ModelAndView items(ModelAndView mv,
			@PathVariable(name="page") int page) {
		return getItems(mv, page);
	}
	
	private ModelAndView getItems(ModelAndView mv, int page) {
		java.util.Date today = new java.util.Date();
		Pageable limit = PageRequest.of(page - 1, 10);
		long count = foodRepository.count();
		long pages = count / 10;  //答え
		long rem = count % 10;    //あまり
		
		mv.addObject("today", today);
		mv.addObject("pages", (rem == 0) ? pages : pages + 1);
		mv.addObject("page", page);
		mv.addObject("foodList", foodRepository.findAllByStockCountGreaterThanOrderByBuyDateAscStockCountAsc(0, limit));
		mv.addObject("pageNation", "pageNation");
		mv.setViewName("home");
		return mv;
	}
}
