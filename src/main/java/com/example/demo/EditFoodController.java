package com.example.demo;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EditFoodController {

	@Autowired
	HttpSession session;
	
	@Autowired
	FoodController foodController;
	
	@Autowired
	FoodRepository foodRepository;
	
	@Autowired
	EditFoodRepository editFoodRepository;
	
	//登録リンク押下
	@RequestMapping("/add")
	public String add() {
		return "addFood";
	}
	
	//登録ボタン押下
	@RequestMapping("/add/addResult")
	public ModelAndView addResult(
			@RequestParam(name="name", defaultValue="") String name,
			@RequestParam(name="categoryCode", defaultValue="") String categoryCode,
			@RequestParam(name="count", defaultValue="") String count,
			@RequestParam(name="dateBuy", defaultValue="") String dateBuyString,
			@RequestParam(name="dateBefore", defaultValue="") String dateBeforeString,
//			@RequestParam(name="dateBuy") Date dateBuy,
//			@RequestParam(name="dateBefore") Date dateBefore,
			ModelAndView mv) {
		
		Date dateBuy = null;
		Date dateBefore = null;
		
		//String→Date型変換
		if(!(dateBuyString.equals(""))) {
			dateBuy = Date.valueOf(dateBuyString);
		} 
		
		if(!(dateBeforeString.equals(""))) {
			dateBefore = Date.valueOf(dateBeforeString);
		} 
		
		//入力チェック(食べ物名・個数)
		if(name.equals("") || categoryCode.equals("") || count.equals("")) {
			mv.addObject("message", "入力されていない項目があります。");
			mv.setViewName("addFood");
		
		} else {
			Food food = new Food
				(Integer.parseInt(categoryCode), name, Integer.parseInt(count), dateBefore);
			foodRepository.saveAndFlush(food);	
			foodController.index(mv);
		}	
		
		return mv;
	}
	
	//「在庫管理」の更新ボタンを押下したとき
	@RequestMapping("/update")
	public ModelAndView update(
		@RequestParam("code") String code,
		ModelAndView mv) {
		
		Optional<Food> editFoodList = editFoodRepository.findById(Integer.parseInt(code));
		
		mv.addObject("count", editFoodList.get().getCount());
		mv.setViewName("update");
		return mv;
	}
	
	
	//「登録情報の編集」の編集ボタンを押下したとき
	@RequestMapping("/fix")
	public ModelAndView fix(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		Optional<Food> fixFoodList = editFoodRepository.findById(Integer.parseInt(code));
		
		mv.addObject("fixFoodList", fixFoodList.get());
		mv.setViewName("fix");
		return mv;
	}
	
	
	//削除機能
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public ModelAndView delete(
			@RequestParam("code") String code,
			ModelAndView mv) {
		
		foodRepository.deleteById(Integer.parseInt(code));
		foodController.index(mv);
		return mv;
	}
}
