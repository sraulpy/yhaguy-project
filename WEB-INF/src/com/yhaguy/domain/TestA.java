package com.yhaguy.domain;

import com.coreweb.domain.Domain;



@SuppressWarnings("serial")
public class TestA extends Domain{
	
	private String name = "";
	
	public TestA(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	


	

}
