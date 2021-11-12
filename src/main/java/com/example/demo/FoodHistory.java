package com.example.demo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="foodhistory")
public class FoodHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="code")
	private Integer code;
	
	@Column(name="category_code")
	private Integer categoryCode;
	
	@Column(name="name")
	private String name;
	
	@Column(name="count")
	private Integer count;
	
	@Column(name="bestbefore")
	private Date bestbefore;
	
	public FoodHistory() {
		
	}

	public FoodHistory(Integer code, Integer categoryCode, String name, Integer count, Date bestbefore) {
		this(categoryCode, name, count, bestbefore);
		this.code = code;
	}
	
	public FoodHistory(Integer categoryCode, String name, Integer count, Date bestbefore) {
		this.categoryCode = categoryCode;
		this.name = name;
		this.count = count;
		this.bestbefore = bestbefore;
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
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Date getBestbefore() {
		return bestbefore;
	}
	public void setBestbefore(Date bestbefore) {
		this.bestbefore = bestbefore;
	}
	
}
