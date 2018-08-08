package com.yhaguy.util.population;

import com.coreweb.domain.Domain;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;


public class DBPopulationArticulo {
	
	private static RegisterDomain rr = RegisterDomain.getInstance();
	
	
	private static void grabarDB(Domain d) throws Exception {
		rr.saveObject(d, Configuracion.USER_SYSTEMA);
	}
	
	
	public static void CargaArticuloMarcaModelo(){
		
		
		
		
	}

}
