package com.yhaguy.process.tareasprogramadas;

import java.util.Date;
import java.util.List;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.HistoricoBloqueoClientes;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;

public class TareaBloqueoClientes {

	static final String MOTIVO = "Bloqueo automatico - Mora mayor a plazo vencimiento";
	
	static final String[] SEND_GRP = new String[] { "vanesar@yhaguyrepuestos.com.py", "estelap@yhaguyrepuestos.com.py",
			"laurap@yhaguyrepuestos.com.py" };

	static final String[] SEND_YRSA = new String[] { "vanesar@yhaguyrepuestos.com.py", "estelap@yhaguyrepuestos.com.py",
			"laurap@yhaguyrepuestos.com.py" };
	
	/**
	 * Bloquea clientes con facturas vencidas..
	 */
	
	private static void bloquearClientes() {
		try {
			String texto = "Se bloquearon por mora los siguientes clientes:";
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<CtaCteEmpresaMovimiento> saldos = rr.getCtaCteMovimientosConSaldo();
			boolean bloqueos = false;
			for (CtaCteEmpresaMovimiento movim : saldos) {
				Date emi = movim.getFechaEmision();
				Cliente cl = rr.getClienteByIdEmpresa(movim.getIdEmpresa());
				long dias = Utiles.diasEntreFechas(emi, new Date());
				if (dias >= cl.getPlazoVencimiento()) {
					Empresa emp = rr.getEmpresaById(movim.getIdEmpresa());
					if (emp != null) {
						if (!emp.isCuentaBloqueada()) {
							bloqueos = true;
							ControlCuentaCorriente.bloquearCliente(movim.getIdEmpresa(), MOTIVO, "sys");
							HistoricoBloqueoClientes bloqueo = new HistoricoBloqueoClientes();
							bloqueo.setFecha(new Date());
							bloqueo.setVencimiento(movim.getFechaVencimiento());						
							bloqueo.setCliente(emp.getRazonSocial());
							bloqueo.setNumeroFactura(movim.getNroComprobante_());
							bloqueo.setDiasVencimiento(dias);
							bloqueo.setMotivo(MOTIVO);
							rr.saveObject(bloqueo, "sys");
							System.out.println("BLOQUEADO: " + bloqueo.getCliente() + " - DIAS: " + dias);
							texto += "\n - Días mora: " + dias + " - " + emp.getRazonSocial();
						}						
					}					
				}			
			}
			if (bloqueos) {
				String[] send = SEND_GRP;
				String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
				String asunto = "Bloqueo Automático Clientes - " + Configuracion.empresa;
				EnviarCorreo enviarCorreo = new EnviarCorreo();
				enviarCorreo.sendMessage(send, sendCCO, asunto, texto, "Notificacion.pdf", null);
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TareaBloqueoClientes.bloquearClientes();
	}	
}
