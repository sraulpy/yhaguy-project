package com.yhaguy.gestion.comun;

import java.util.Date;

import com.coreweb.domain.Tipo;
import com.yhaguy.domain.ControlCombustible;
import com.yhaguy.domain.OrdenCompra;
import com.yhaguy.domain.RegisterDomain;

public class ControlLogistica {

	/**
	 * agrega registro de control de combustible..
	 */
	public static void addControlCombustible(Date fecha, String numeroFactura,
			double kilometraje, double litros, double costoGs, String numeroChapa,
			String chofer, long idCombustible, OrdenCompra orden, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo combustible = rr.getTipoById(idCombustible);
		
		ControlCombustible cc = new ControlCombustible();
		cc.setChofer(chofer);
		cc.setCombustible(combustible);
		cc.setCostoGs(costoGs);
		cc.setFecha(fecha);
		cc.setKilometraje(kilometraje);
		cc.setLitros(litros);
		cc.setNumeroFactura(numeroFactura);
		cc.setNumeroChapa(numeroChapa);
		cc.setOrdenCompra(orden);
		cc.setNumeroOrdenCompra(orden.getNumero());
		rr.saveObject(cc, user);
	}
	
	/**
	 * elimina registro de control combustible..
	 */
	public static void deleteCombustible(long idCombustible) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		ControlCombustible cc = (ControlCombustible) rr.getObject(ControlCombustible.class.getName(), idCombustible);
		rr.deleteObject(cc);
	}
	
}
