package com.yhaguy.util.comision;

public class Rango {
	String desde = "1900.01.01";
	String hasta = "2020.12.31";
	
	public Rango(){
		
	}
	
	public Rango(String desde, String hasta){
		this.desde = desde;
		this.hasta = hasta;;
	}
	
	public boolean pertenece(String fecha){
		boolean out = false;
		if ((this.desde.compareTo(fecha) <= 0)&&(fecha.compareTo(this.hasta)<=0)){
			out = true;
		}
		return out;
	}
	
	public String toString(){
		String out;
		out = "["+this.desde + " - " + this.hasta+"]";
		return out;
	}
	
	public static void main(String[] args) {
		String fecha = "2020.12.31";
		
		Rango r =  new Rango();
		System.out.println(r.pertenece(fecha));
		
	}
	
}