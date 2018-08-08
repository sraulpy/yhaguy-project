package com.yhaguy.process.tareasprogramadas;

import com.yhaguy.process.ProcesosArticulos;

public class TareaProcesarHistoricos {
	
	public static void main(String[] args) {
		try {
			ProcesosArticulos.poblarHistoricoStock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
