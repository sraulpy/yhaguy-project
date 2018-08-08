package com.yhaguy.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Transferencia;

public class ProcesosTransferencias {

	/**
	 * verificar transferencias..
	 */
	public static void verificarTransferencias() throws Exception {
		String desde = "01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Transferencia> transfs = rr.getTransferenciasExternas(desde_, hasta_, 1, 2);
		for (Transferencia trf : transfs) {
			if (trf.getTransferenciaEstado().getSigla().equals(Configuracion.SIGLA_ESTADO_TRANSF_CONFIRMADA)) {
				CtaCteEmpresaMovimiento ct = rr.getCtaCteMovimientoByNumero(trf.getNumero());
				if (ct != null) {
					ct.setFechaEmision(trf.getFechaCreacion());
					ct.setFechaVencimiento(formatter.parse("30-12-2016 00:00:00"));
					rr.saveObject(ct, "sys");
					System.out.println(ct.getNroComprobante() + " actualizado..");
				}				
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			ProcesosTransferencias.verificarTransferencias();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
