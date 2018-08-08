package com.yhaguy.gestion.caja.resumen;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.util.MyArray;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPlanillaResumen;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ResumenDataSource implements JRDataSource {
	
	List<MyArray> dets = new ArrayList<MyArray>();

	public ResumenDataSource(CajaPlanillaResumen resumen) {
		for (CajaPeriodo planilla : resumen.getPlanillasVentas()) {
			this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"EFECTIVO", (planilla.getTotalEfectivoIngreso() - planilla.getTotalEfectivoEgreso()),
					planilla.getTotalResumenEfectivo(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero() 
					+ " - " + planilla.getResponsable().getRazonSocial(),
					"NOTAS DE CREDITO CONTADO", planilla.getTotalNotaCreditoContado(),
					planilla.getTotalResumenEfectivo(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"RETENCIONES", planilla.getTotalRetencionesVentas(),
					planilla.getTotalResumenEfectivo(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"CHEQUES AL DIA", planilla.getTotalChequeAlDia(resumen.getFecha()),
					planilla.getTotalResumenEfectivo(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"TRANSFERENCIAS BANCARIAS", planilla.getTotalTransferenciasBancarias(),
					planilla.getTotalResumenEfectivo(resumen.getFecha())));
		
		}
		for (CajaPeriodo planilla : resumen.getPlanillasCobranzas()) {
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(), 
					"EFECTIVO", (planilla.getTotalEfectivoIngreso() - planilla.getTotalReembolsoChequeRechazadoEfectivo()),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero() 
					+ " - " + planilla.getResponsable().getRazonSocial(),
					"REEMBOLSOS PRÉSTAMOS", planilla.getTotalReembolsoPrestamos(),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"RETENCIONES CONTADO", planilla.getTotalRetencionesAlDia(resumen.getFecha()),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"RETENCIONES CRÉDITO", planilla.getTotalRetencionesDiferidos(resumen.getFecha()),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"RECAUDACIÓN CASA CENTRAL", planilla.getTotalRecaudacionCentral(),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));				
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"REEMBOLSOS CHEQUES RECHAZADOS CON EFECTIVO", planilla.getTotalReembolsoChequeRechazadoEfectivo(),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"REEMBOLSOS CHEQUES RECHAZADOS CON CHEQUE DIFERIDO", planilla.getTotalReembolsoChequeRechazadoChequeDiferido(resumen.getFecha()),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"REEMBOLSOS CHEQUES RECHAZADOS CON CHEQUE AL DIA", planilla.getTotalReembolsoChequeRechazadoChequeAldia(resumen.getFecha()),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"CHEQUES AL DÍA", (planilla.getTotalChequeAlDia(resumen.getFecha()) - planilla.getTotalReembolsoChequeRechazadoChequeAldia(resumen.getFecha())),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"CHEQUES DIFERIDOS", planilla.getTotalChequeDiferido(resumen.getFecha()),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"SALDO CLIENTE GENERADO", planilla.getTotalSaldoClienteGenerado(),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"SALDO CLIENTE COBRADO", planilla.getTotalSaldoClienteCobrado(),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
					 + " - " + planilla.getResponsable().getRazonSocial(),
					"TRANSFERENCIAS BANCARIAS", planilla.getTotalTransferenciasBancarias(),
					planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
		
		}
		for (BancoBoletaDeposito dep : resumen.getDepositos_valores_bat()) {
			for (BancoChequeTercero cheque : dep.getCheques()) {
				this.dets.add(new MyArray("REEMBOLSO VALORES BATERÍAS",
						"CHEQUE NRO. " + cheque.getNumero() + " - " + Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY)
						+ " - " + cheque.getBanco().getDescripcion().toUpperCase(), 
						cheque.getMonto(),
						resumen.getTotalDepositosValoresBat()));
			}
		}			
		for (BancoBoletaDeposito dep : resumen.getDepositos_diferidos()) {
			for (BancoChequeTercero cheque : dep.getCheques()) {
				int length = cheque.getBanco().getDescripcion().length();
				this.dets.add(new MyArray("DEPÓSITOS DIFERIDOS",
						"DEP.NRO." + dep.getNumeroBoleta() + " - CH.NRO." + cheque.getNumero() + " " + Utiles.getDateToString(cheque.getFecha(), "dd/MM/yy")
						+ " " + cheque.getBanco().getDescripcion().toUpperCase().substring(0, length >= 6 ? 6 : length) + ".. "
						+ " REC." + cheque.getNumeroRecibo().replace("001-001-", "")
						+ " CAJ." + cheque.getNumeroPlanilla().substring(0, 9).replace("CJP-", ""), 
						cheque.getMonto(),
						resumen.getTotalDepositosDiferidos()));
			}
		}
		for (BancoBoletaDeposito dep : resumen.getDepositos_generados()) {
			for (BancoChequeTercero cheque : dep.getCheques()) {
				int length = cheque.getBanco().getDescripcion().length();
				this.dets.add(new MyArray("DEPÓSITOS GENERADOS",
						"DEP.NRO." + dep.getNumeroBoleta() + " - CH.NRO." + cheque.getNumero() + " " + Utiles.getDateToString(cheque.getFecha(), "dd/MM/yy")
						+ " " + cheque.getBanco().getDescripcion().toUpperCase().substring(0, length >= 6 ? 6 : length) + ".. "
						+ " REC." + cheque.getNumeroRecibo().replace("001-001-", "")
						+ " CAJ." + cheque.getNumeroPlanilla().substring(0, 9).replace("CJP-", ""), 
						cheque.getMonto(),
						resumen.getTotalDepositosGenerados()));
			}
			if (dep.getTotalEfectivo() > 0) {
				this.dets.add(new MyArray("DEPÓSITOS GENERADOS",
						"DEP.NRO." + dep.getNumeroBoleta() + " - EFECTIVO GS.", 
						dep.getTotalEfectivo(),
						resumen.getTotalDepositosGenerados()));
			}
		}
		for (ReciboFormaPago transf : resumen.getTransferenciasBancarias()) {
			this.dets.add(new MyArray("TRANSFERENCIAS BANCARIAS",
					"DEP.NRO." + transf.getDepositoNroReferencia() 
					+ " - BANCO " + transf.getDepositoBancoCta().getBanco().getDescripcion(), 
					transf.getMontoGs(),
					resumen.getResumenTransferenciasBancarias()));
		}
		// Resumen al dia
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"EFECTIVO", resumen.getResumenEfectivo(), resumen.getTotalResumenAlDia()));
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"CHEQUES", resumen.getResumenChequeAlDia(), resumen.getTotalResumenAlDia()));
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"TRANSFERENCIAS BANCARIAS", resumen.getResumenTransferenciasBancarias(), resumen.getTotalResumenAlDia()));
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"TARJETA", resumen.getResumenTarjeta(), resumen.getTotalResumenAlDia()));
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"SOBRANTE/FALTANTE", resumen.getSobranteFaltante(), resumen.getTotalResumenAlDia()));
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"CAJA NO DEPOSITADA (EFECTIVO)", resumen.getEfectivoNoDepositado(), resumen.getTotalResumenAlDia()));
		this.dets.add(new MyArray("RESUMEN AL DÍA",
				"CAJA NO DEPOSITADA (CHEQUES)", resumen.getChequeNoDepositado(), resumen.getTotalResumenAlDia()));
		
		// Resumen adelantado
		this.dets.add(new MyArray("RESUMEN ADELANTADO",
				"CHEQUES DIFERIDOS", resumen.getResumenChequeDiferido(), resumen.getTotalResumenAdelantado()));
		
		// Resumen a depositar
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"EFECTIVO", resumen.getResumenEfectivo_(), resumen.getTotalADepositar()));
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"CHEQUE AL DIA", resumen.getResumenChequeAlDiaSinReembolsos(), resumen.getTotalADepositar()));
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"REEMBOLSO VALORES BATERIAS", resumen.getTotalDepositosValoresBat(), resumen.getTotalADepositar()));
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"TRANSFERENCIAS BANCARIAS", resumen.getResumenTransferenciasBancarias(), resumen.getTotalADepositar()));
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"DEPÓSITO CHEQUES DIFERIDOS", resumen.getResumenChequeDiferidoADepositar(), resumen.getTotalADepositar()));
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"REEMBOLSO CHEQUES RECHAZADOS", resumen.getResumenReembolsoChequesRechazados(), resumen.getTotalADepositar()));
		this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
				"PRESTAMOS CASA CENTRAL (CHEQUE AL DIA)", resumen.getResumenChequeAlDiaPrestamoCC(), resumen.getTotalADepositar()));
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray item = this.dets.get(index);
		if ("TituloDetalle".equals(fieldName)) {
			value = item.getPos1();
		} else if ("Descripcion".equals(fieldName)) {
			value = item.getPos2();
		} else if ("Importe".equals(fieldName)) {
			double imp = (double) item.getPos3();
			value = Utiles.getNumberFormat(imp);
		} else if ("TotalImporte".equals(fieldName)) {
			double imp = (double) item.getPos4();
			value = Utiles.getNumberFormat(imp);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < dets.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}
