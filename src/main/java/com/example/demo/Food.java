package com.example.demo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="food")
public class Food {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="code")
	private Integer code;
	
	@Column(name="category_code")
	private Integer categoryCode;
	
	@Column(name="name")
	private String name;
	
	//在庫数
	@Column(name="stock_count")
	private Integer stockCount;
	
	//購入数
	@Column(name="buy_count")
	private Integer buyCount;
	
	//購入日
	@Column(name="buy_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date buyDate;
	
	//賞味期限日
	@Column(name="bestbefore")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date bestBefore;

	//賞味期限が入力されているときの評価（賞味期限‐閲覧日の結果)
	@Transient
	private String judgeBestBefore;
	
	//賞味期限が入力されていないときの評価（購入日‐閲覧日の結果)
	@Transient
	private String judgeBuyDate;
	
	//本日(閲覧日)を表示
	@Transient
	private Date today;
	
	public Food() {
		
	}
	
	//＜新規登録・在庫管理・編集・削除＞賞味期限が入力されていない場合
	public Food(Integer code, Integer categoryCode, String name, Integer stockCount, Integer buyCount, Date buyDate) {
		this(categoryCode, name, stockCount, buyCount, buyDate);
		this.code = code;
	}
	public Food(Integer categoryCode, String name, Integer stockCount, Integer buyCount, Date buyDate) {
		this.categoryCode = categoryCode;
		this.name = name;
		this.stockCount = stockCount;
		this.buyCount = buyCount;
		this.buyDate = buyDate;
	}
	
	//＜新規登録・在庫管理・編集・削除＞賞味期限が入力されている場合
	public Food(Integer code, Integer categoryCode, String name, Integer stockCount, Integer buyCount, Date buyDate, Date bestBefore) {
		this(categoryCode, name, stockCount, buyCount, buyDate, bestBefore);
		this.code = code;
	}
	public Food(Integer categoryCode, String name, Integer stockCount, Integer buyCount, Date buyDate, Date bestBefore) {
		this.categoryCode = categoryCode;
		this.name = name;
		this.stockCount = stockCount;
		this.buyCount = buyCount;
		this.buyDate = buyDate;
		this.bestBefore = bestBefore;
	}
	
	//履歴検索
	public Food(String name) {
		this.name = name;
	}
	
	//本日(閲覧日)を表示
	public Date today() {
		Date today = new Date();
		return today;
	}

	//賞味期限が入力されているときの評価（賞味期限‐閲覧日の結果)
	public String judgeBestBefore() {
	    try {
		    // ミリ秒差
		    long diff = bestBefore.getTime() - today().getTime();
		    // 日数に変換
		    TimeUnit time = TimeUnit.DAYS;
		    long diffDate = time.convert(diff, TimeUnit.MILLISECONDS);
		    
		    //賞味期限-閲覧日が3日以上
			if(3 <= diffDate) {
				judgeBestBefore = "💮(賞味期限日まで、あと" + diffDate + "日です）";
			
			//賞味期限-閲覧日が０日以上3日未満
			} else if(0 < diffDate && diffDate < 3) {
				judgeBestBefore = "🟠(賞味期限日まで、あと" + diffDate + "日です）";
				
			//賞味期限当日
			} else if(diffDate == 0){
				judgeBestBefore = "本日です。";
				
			//賞味期限切れ
			} else {
				judgeBestBefore = "❗❗(賞味期限日から" + diffDate*(-1) + "日経過しました）";	
			}
		
		//賞味期限が未入力の場合
	    } catch(Exception e) {
	    	judgeBestBefore = "⚠️";
	    }
		
		return judgeBestBefore;
	}
	
	//賞味期限が入力されていないときの評価（購入日‐閲覧日の結果)
	public String judgeBuyDate() {
	    Date today = new Date();
	    // ミリ秒差
	    long diff = today.getTime() - buyDate.getTime();
	    // 日数に変換
	    TimeUnit time = TimeUnit.DAYS;
	    long diffDate = time.convert(diff, TimeUnit.MILLISECONDS);
		
	    judgeBuyDate = "購入してから" + diffDate + "日経過しています。";
	    if(5 < diffDate && diffDate <= 10) {
	    	judgeBuyDate += "早急に消費してください。";
	    
	    } else if(10 < diffDate) {
	    	judgeBuyDate += "生鮮食品の場合、廃棄も検討してください。";
	    }
	    
		return judgeBuyDate;
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStockCount() {
		return stockCount;
	}
	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}
	public Integer getBuyCount() {
		return buyCount;
	}
	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	public Date getBestBefore() {
		return bestBefore;
	}
	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}
	public String getJudgeBestBefore() {
		return judgeBestBefore;
	}
	public void setJudgeBestBefore(String judgeBestBefore) {
		this.judgeBestBefore = judgeBestBefore;
	}
	public String getJudgeBuyDate() {
		return judgeBuyDate;
	}
	public void setJudgeBuyDate(String judgeBuyDate) {
		this.judgeBuyDate = judgeBuyDate;
	}
	public Date getToday() {
		return today;
	}
	public void setToday(Date today) {
		this.today = today;
	}
	
}
