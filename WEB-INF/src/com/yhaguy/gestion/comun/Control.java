package com.yhaguy.gestion.comun;

import java.util.List;

import com.yhaguy.domain.ControlTalonario;
import com.yhaguy.domain.RegisterDomain;

public class Control {

	/**
	 * actualiza la cantidad facturada..
	 */
	public static void updateControlTalonario(String user, int cantidad) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ControlTalonario> list = rr.getControlTalonarioActivos();
		for (ControlTalonario control : list) {
			int fac = control.getFacturadas();
			control.setFacturadas(fac + cantidad);
			rr.saveObject(control, user);
		}
	}
	
}
