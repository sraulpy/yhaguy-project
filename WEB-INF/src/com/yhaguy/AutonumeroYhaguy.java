package com.yhaguy;

import java.util.Date;

import com.coreweb.domain.IiD;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyPair;

public class AutonumeroYhaguy extends AutoNumeroControl{

	static Misc m = new Misc();
	

	//=============================================================
	// SUBDIARIO
	
	/**
	 * Genera el número de subdiario para el día corriente, ejemplo 2015-03-30.023
	 * @param sucursal
	 * @return
	 * @throws Exception
	 */
	synchronized public static String getNumeroSubDiario(IiD sucursal) throws Exception{
		return getNumeroSubDiario(sucursal, new Date());
	}

	/**
	 * Genera el número de subdiario según la fecha correspondiente. , ejemplo 2015-03-30.023
	 * @param sucursal
	 * @param fecha
	 * @return
	 * @throws Exception
	 */
	synchronized public static String getNumeroSubDiario(IiD sucursal, Date fecha) throws Exception{
		String ff = m.dateToString(fecha, "yyyy"); // es una sóla numeración por año.
		String key = Configuracion.NRO_SUB_DIARIO+"."+sucursal.getId()+"."+ff;
		String out = ff+"."+AutoNumeroControl.getAutoNumero(key, 3);		
		return out;
	}


	/**
	 * Genera el número de subdiario PROVISORIO según la fecha correspondiente. , ejemplo 2015-03-30.023
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	synchronized public static String getNumeroSubDiarioProvisorio(IiD sucursal, Date fecha) throws Exception{
		String ff = m.dateToString(fecha, m.YYYY_MM_DD);
		String key = "SUBDIARIO."+sucursal.getId()+"."+ff;
		String out = ff+"."+AutoNumeroControl.getAutoNumero(key, 3, true);		
		return out;
	}

	
	//===========================================================
	

	
	public static void main(String[] args) {
		try {
			
			MyPair suc = new MyPair();
			suc.setId((long)1);
			String o1 = getNumeroSubDiario(suc, new Date());
			String o2 = getNumeroSubDiarioProvisorio(suc, new Date());
			
			System.out.println(o1+"\n"+o2);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}




