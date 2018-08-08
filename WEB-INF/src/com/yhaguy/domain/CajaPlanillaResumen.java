package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class CajaPlanillaResumen extends Domain {

	private Date fecha;
	private String numero;
	private String numeroPlanillas;
	
	private double sobranteFaltante;
	private double efectivoNoDepositado;
	private double chequeNoDepositado;
	
	private String obs_efectivo_no_depositado = "";
	private String obs_cheque_no_depositado = "";
	
	private Set<BancoBoletaDeposito> depositos_valores_bat = new HashSet<BancoBoletaDeposito>(); // reembolso valores baterias..
	private Set<BancoBoletaDeposito> depositos_diferidos = new HashSet<BancoBoletaDeposito>();
	private Set<BancoBoletaDeposito> depositos_generados = new HashSet<BancoBoletaDeposito>();
	private Set<CajaPeriodo> planillas = new HashSet<CajaPeriodo>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return el total en efectivo..
	 */
	public double getResumenEfectivo() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += (planilla.getTotalEfectivoIngreso() - planilla.getTotalEfectivoEgreso());
		}
		return out;
	}
	
	/**
	 * @return el total en efectivo..
	 */
	@DependsOn({ "sobranteFaltante", "efectivoNoDepositado" })
	public double getResumenEfectivo_() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += (planilla.getTotalEfectivoIngreso() - planilla.getTotalEfectivoEgreso());
		}
		return out + this.sobranteFaltante + this.efectivoNoDepositado;
	}
	
	/**
	 * @return el total en cheque al dia..
	 */
	public double getResumenChequeAlDia() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += planilla.getTotalChequeAlDia(this.fecha);
		}
		return out;
	}
	
	/**
	 * @return el total en cheque al dia excluyendo 
	 * los reembolsos de cheques rechazados con cheque al dia y
	 * los cheques al dia que son de prestamo cc..
	 */
	@DependsOn("chequeNoDepositado")
	public double getResumenChequeAlDiaSinReembolsos() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += (planilla.getTotalChequeAlDia(this.fecha) - planilla.getTotalReembolsoChequeRechazadoChequeAldia(this.fecha));
		}
		return out + this.chequeNoDepositado - this.getResumenChequeAlDiaPrestamoCC();
	}
	
	/**
	 * @return el total en cheque diferido..
	 */
	public double getResumenChequeDiferido() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += planilla.getTotalChequeDiferido(this.fecha);
		}
		return out;
	}
	
	/**
	 * @return el total en cheque al dia que son prestamos cc..
	 */
	public double getResumenChequeAlDiaPrestamoCC() {
		double out = 0;
		try {
			for (BancoChequeTercero cheque : this.getChequesAlDia()) {
				if (cheque.isPrestamoCC()) {
					out += cheque.getMonto();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @return el total en cheques diferidos que son prestamos cc..
	 */
	public double getResumenChequeDiferidoPrestamoCC() throws Exception {
		double out = 0;
		for (BancoChequeTercero cheque : this.getChequesDiferidos()) {
			if (cheque.isPrestamoCC()) {
				out += cheque.getMonto();
			}
		}
		return out;
	}
	
	/**
	 * @return el total en transferencias bancarias..
	 */
	public double getResumenTransferenciasBancarias() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += planilla.getTotalTransferenciasBancarias();
			out += planilla.getTotalReembolsoPrestamos();
		}
		return out;
	}
	
	/**
	 * @return el total en tarjeta de credito y debito..
	 */
	public double getResumenTarjeta() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += planilla.getTotalTarjetaCredito();
			out += planilla.getTotalTarjetaDebito();
		}
		return out;
	}
	
	/**
	 * @return el total de resumen de depositos diferidos..
	 */
	public double getResumenChequeDiferidoADepositar() {
		double out = 0;
		for (BancoBoletaDeposito dep : this.depositos_diferidos) {
			out += dep.getTotalImporteGs();
		}
		return out;
	}
	
	/**
	 * @return el total de reembolso de cheques rechazados con cheque al dia..
	 */
	public double getResumenReembolsoChequesRechazados() {
		double out = 0;
		for (CajaPeriodo planilla : this.planillas) {
			out += planilla.getTotalReembolsoChequeRechazadoChequeAldia(this.fecha);
		}
		return out;
	}
	
	/**
	 * @return el total de resumen al dia..
	 */
	@DependsOn({ "sobranteFaltante", "efectivoNoDepositado", "chequeNoDepositado" })
	public double getTotalResumenAlDia() {
		return this.getResumenEfectivo() + this.getResumenChequeAlDia()
				+ this.getResumenTransferenciasBancarias()
				+ this.getResumenTarjeta() + this.sobranteFaltante
				+ this.efectivoNoDepositado + this.chequeNoDepositado;
	}
	
	/**
	 * @return el total de resumen adelantado..
	 */
	public double getTotalResumenAdelantado() {
		return this.getResumenChequeDiferido();
	}
	
	/**
	 * @return el total a depositar..
	 */
	@DependsOn({ "sobranteFaltante", "efectivoNoDepositado", "chequeNoDepositado" })
	public double getTotalADepositar() {
		return this.getResumenEfectivo_() + this.getResumenChequeAlDiaSinReembolsos()
				+ this.getTotalDepositosValoresBat()
				+ this.getResumenTransferenciasBancarias()
				+ this.getResumenChequeDiferidoADepositar()
				+ this.getResumenReembolsoChequesRechazados();
	}
	
	/**
	 * @return los numeros de planillas..
	 */
	public String getNumeroPlanillas_() {
		String out = "";
		for (CajaPeriodo planilla : this.planillas) {
			out += planilla.getNumero() + " - ";
		}
		return out;
	}
	
	/**
	 * @return las planillas de ventas..
	 */
	public List<CajaPeriodo> getPlanillasVentas() {
		List<CajaPeriodo> out = new ArrayList<CajaPeriodo>();
		for (CajaPeriodo planilla : this.planillas) {
			if (planilla.getTipo().equals(CajaPeriodo.TIPO_VENTA)) {
				out.add(planilla);
			}
		}
		return out;
	}
	
	/**
	 * @return las planillas de cobro..
	 */
	public List<CajaPeriodo> getPlanillasCobranzas() {
		List<CajaPeriodo> out = new ArrayList<CajaPeriodo>();
		for (CajaPeriodo planilla : this.planillas) {
			if (planilla.getTipo().equals(CajaPeriodo.TIPO_COBROS) ||
					planilla.getTipo().equals(CajaPeriodo.TIPO_COBROS_MOBILE)) {
				out.add(planilla);
			}
		}
		return out;
	}
	
	/**
	 * @return los cheques al dia..
	 */
	public List<BancoChequeTercero> getChequesAlDia() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		for (CajaPeriodo planilla : this.planillas) {
			out.addAll(planilla.getChequesAlDia());
		}
		return out;
	}
	
	/**
	 * @return los cheques al dia excluyendo cheques que son prestamos cc..
	 */
	public List<BancoChequeTercero> getChequesAlDiaSinPrestamosCC() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		for (CajaPeriodo planilla : this.planillas) {
			for (BancoChequeTercero cheque : planilla.getChequesAlDia()) {
				if (!cheque.isPrestamoCC()) {
					out.add(cheque);
				}
			}
		}
		return out;
	}
	
	/**
	 * @return los cheques diferidos..
	 */
	public List<BancoChequeTercero> getChequesDiferidos() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		for (CajaPeriodo planilla : this.planillas) {
			out.addAll(planilla.getChequesDiferidos());
		}
		return out;
	}
	
	/**
	 * @return los cheques al dia que son prestamo casa central..
	 */
	public List<BancoChequeTercero> getChequesAlDiaPrestamoCC() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		for (BancoChequeTercero cheque : this.getChequesAlDia()) {
			if (cheque.isPrestamoCC()) {
				out.add(cheque);
			}
		}
		return out;
	}
	
	/**
	 * @return los cheques diferidos que son prestamo casa central..
	 */
	public List<BancoChequeTercero> getChequesDiferidosPrestamoCC() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		for (BancoChequeTercero cheque : this.getChequesDiferidos()) {
			if (cheque.isPrestamoCC()) {
				out.add(cheque);
			}
		}
		return out;
	}
	
	@DependsOn("depositos_generados")
	public double getTotalDepositosGenerados() {
		double out = 0;
		for (BancoBoletaDeposito dep : this.depositos_generados) {
			out += dep.getTotalImporteGs();
		}
		return out;
	}
	
	@DependsOn("depositos_diferidos")
	public double getTotalDepositosDiferidos() {
		double out = 0;
		for (BancoBoletaDeposito dep : this.depositos_diferidos) {
			out += dep.getTotalImporteGs();
		}
		return out;
	}
	
	@DependsOn("depositos_valores_bat")
	public double getTotalDepositosValoresBat() {
		double out = 0;
		for (BancoBoletaDeposito dep : this.depositos_valores_bat) {
			out += dep.getTotalImporteGs();
		}
		return out;
	}
	
	/**
	 * @return las transferencias bancarias..
	 */
	public List<ReciboFormaPago> getTransferenciasBancarias() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (CajaPeriodo caja : this.planillas) {
			out.addAll(caja.getDepositosBancarios());
			out.addAll(caja.getDepositosBancariosReembolsoPrestamos());
		}
		return out;
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumeroPlanillas() {
		return numeroPlanillas;
	}

	public void setNumeroPlanillas(String numeroPlanillas) {
		this.numeroPlanillas = numeroPlanillas;
	}

	public Set<CajaPeriodo> getPlanillas() {
		return planillas;
	}

	public void setPlanillas(Set<CajaPeriodo> planillas) {
		this.planillas = planillas;
	}

	public Set<BancoBoletaDeposito> getDepositos_diferidos() {
		return depositos_diferidos;
	}

	public void setDepositos_diferidos(Set<BancoBoletaDeposito> depositos_diferidos) {
		this.depositos_diferidos = depositos_diferidos;
	}

	public Set<BancoBoletaDeposito> getDepositos_generados() {
		return depositos_generados;
	}

	public void setDepositos_generados(Set<BancoBoletaDeposito> depositos_generados) {
		this.depositos_generados = depositos_generados;
	}

	public double getSobranteFaltante() {
		return sobranteFaltante;
	}

	public void setSobranteFaltante(double sobranteFaltante) {
		this.sobranteFaltante = sobranteFaltante;
	}

	public double getEfectivoNoDepositado() {
		return efectivoNoDepositado;
	}

	public void setEfectivoNoDepositado(double efectivoNoDepositado) {
		this.efectivoNoDepositado = efectivoNoDepositado;
	}

	public double getChequeNoDepositado() {
		return chequeNoDepositado;
	}

	public void setChequeNoDepositado(double chequeNoDepositado) {
		this.chequeNoDepositado = chequeNoDepositado;
	}

	public String getObs_efectivo_no_depositado() {
		return obs_efectivo_no_depositado;
	}

	public void setObs_efectivo_no_depositado(String obs_efectivo_no_depositado) {
		this.obs_efectivo_no_depositado = obs_efectivo_no_depositado.toUpperCase();
	}

	public String getObs_cheque_no_depositado() {
		return obs_cheque_no_depositado;
	}

	public void setObs_cheque_no_depositado(String obs_cheque_no_depositado) {
		this.obs_cheque_no_depositado = obs_cheque_no_depositado.toUpperCase();
	}

	public Set<BancoBoletaDeposito> getDepositos_valores_bat() {
		return depositos_valores_bat;
	}

	public void setDepositos_valores_bat(Set<BancoBoletaDeposito> depositos_valores_bat) {
		this.depositos_valores_bat = depositos_valores_bat;
	}
}
