package com.yhaguy.process.tareasprogramadas;

import java.util.Date;
import java.util.List;

import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.HistoricoBloqueoClientes;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.util.Utiles;

public class TareaBloqueoClientes {

	static final String MOTIVO = "Bloqueo automatico - Atraso mayor a 90 días";
	
	/**
	 * Bloquea clientes con facturas vencidas 120 dias..
	 */
	
	private static void bloquearClientes() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<CtaCteEmpresaMovimiento> vencidos = rr.getCtaCteMovimientosVencidos();
			for (CtaCteEmpresaMovimiento movim : vencidos) {
				Date vto = movim.getFechaVencimiento();
				long dias = Utiles.diasEntreFechas(vto, new Date());
				if (dias >= 90) {
					ControlCuentaCorriente.bloquearCliente(movim.getIdEmpresa(), MOTIVO, "sys");
					HistoricoBloqueoClientes bloqueo = new HistoricoBloqueoClientes();
					bloqueo.setFecha(new Date());
					bloqueo.setVencimiento(movim.getFechaVencimiento());
					bloqueo.setCliente(rr.getEmpresaById(movim.getIdEmpresa()).getRazonSocial());
					bloqueo.setNumeroFactura(movim.getNroComprobante_());
					bloqueo.setDiasVencimiento(dias);
					bloqueo.setMotivo(MOTIVO);
					rr.saveObject(bloqueo, "sys");
					System.out.println("BLOQUEADO: " + bloqueo.getCliente() + " - DIAS: " + dias);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TareaBloqueoClientes.bloquearClientes();
	}	
}
