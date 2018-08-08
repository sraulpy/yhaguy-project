package com.yhaguy.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;

public class VerificacionAnulados {

	/**
	 * verificacion de facturas anuladas..
	 */
	public static void verificarVentasAnuladas() throws Exception {
		String desde = "01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> vtasAnuladas = rr.getVentasAnuladas(desde_, hasta_);

		for (Venta venta : vtasAnuladas) {
			CtaCteEmpresaMovimiento ctacte = rr
					.getCtaCteMovimientoByIdMovimiento(venta.getId(), venta
							.getTipoMovimiento().getSigla());
			if (ctacte.isAnulado() == false) {
				System.out.println(venta.getNumero() + " SALDO: " + ctacte.getSaldoFinal());
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			VerificacionAnulados.verificarVentasAnuladas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
