package com.yhaguy.util.comision;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class Tabla {
	
	private ArrayList tabla = new ArrayList();
	private Iterator ite;

	private Object[] lineaCC;
	private Hashtable<String,Integer> camp = new Hashtable<String,Integer>();
	
	private int numCampos = 0;
	
	public Tabla(String[] campos){
		for (int i = 0; i < campos.length; i++) {
			this.camp.put(campos[i], i);
		}
		this.numCampos = campos.length;
	}

	public Tabla(String[][] camposCSV){
		for (int i = 0; i < camposCSV.length; i++) {
			this.camp.put(camposCSV[i][0], i);
		}
		this.numCampos = camposCSV.length;
	}
	
	// agregar una fila a la tabla
	public void putRow(Object[] lineaCC){
		this.lineaCC = lineaCC;
		this.tabla.add(this.lineaCC);
	}
	
	public void newRow(){
		this.lineaCC = new Object[this.numCampos];
	}
	
	public void addDataRow(String campo, Object value){
		this.lineaCC[this.camp.get(campo)] = value;
	}

	public void saveRow(){
		this.tabla.add(this.lineaCC);
	}
	
	
	
	
	public int tamannio(){
		return this.tabla.size();
	}
	
	// antes de empezar
	public void inicioRecorrido(){
		this.ite = this.tabla.iterator();
	}
	
	// si quedan elementos en la tabla
	public boolean hayFilas(){
		return this.ite.hasNext();
	}
	
	// leer una linea
	public void leerFila(){
		this.lineaCC = (Object[])this.ite.next();
	}

	// recuperar un valor de la fila
	public Object getRow(String campo){
		return this.lineaCC[this.camp.get(campo)];
	}
	
	// recuperar un valor de la fila
	public String getRowStr(String campo){
		return (String)this.getRow(campo);
	}

	
}
