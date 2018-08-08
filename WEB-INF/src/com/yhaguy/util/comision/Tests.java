package com.yhaguy.util.comision;

import java.io.RandomAccessFile;
import java.util.*;

import com.coreweb.util.Misc;

public class Tests {
	
	public static void xxprintVendedores(){
		
		System.out.println("--- Vendedores Externos  ----");
		Enumeration<VendedorExterno> eve = Info.vendedoresExternos.elements();
		while(eve.hasMoreElements()){
			System.out.println(eve.nextElement().toString());
		}
	
		System.out.println("--- Vendedores Mostrador ----");
		Enumeration<VendedorMostrador> evm = Info.vendedoresMostrador.elements();
		while(evm.hasMoreElements()){
			System.out.println(evm.nextElement().toString());
		}
		System.out.println("---------------------[fin]");
	}
	
	public static void main(String[] args) {
		
		/*
		Locale.setDefault(new Locale("es", "ES"));
		Locale l = Locale.getDefault();
		System.out.println(l);
		System.out.println(l);
		
		//if (true){ return ; }
		*/
		
		long antes = System.currentTimeMillis();
		try {
			
			Info.comentarios();
			Info.init();
			// printVendedores();
			
			
			
			Misc m = new Misc();
			//String file = "/home/daniel/Dropbox/yhaguy/varios/salida.txt";
			//String fileW = "C:/Sistema_Yhaguy/comisiones/Archivos_de_Calculo/salida.txt";
			//m.grabarStringToArchivo(Cons.direFileError+"-todo", Info.OUT.toString());
			
			m.grabarStringToArchivo(Cons.direFileError, Cons.FECHA_EJECUCION + Info.LOGs.toString());
			


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long despues = System.currentTimeMillis();
		
		System.out.println("Tiempo de ejecucion: " + ((despues - antes)/1000));
	}

}
