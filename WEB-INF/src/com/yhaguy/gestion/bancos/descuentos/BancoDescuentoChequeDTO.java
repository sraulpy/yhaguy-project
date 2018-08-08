package com.yhaguy.gestion.bancos.descuentos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class BancoDescuentoChequeDTO extends DTO {

	private Date fecha;
	private double totalChequesDescontado = 0;
	private MyPair sucursalApp;
	private MyPair moneda;
	private String observacion = "";
	
	private double liq_impuestos = 0;
	private double liq_gastos_adm = 0;
	private double liq_intereses = 0;
	private double liq_neto_aldia = 0;
	private double liq_neto_diferidos = 0;
	private boolean liq_registrado = false;
	
	private boolean confirmado = false;
	
	private MyArray banco;
	private List<MyArray> cheques = new ArrayList<MyArray>();
	private List<BancoChequeDTO> chequesPropios = new ArrayList<BancoChequeDTO>();
	private List<ReciboFormaPagoDTO> formasPago = new ArrayList<ReciboFormaPagoDTO>();
	
	@DependsOn("formasPago")
	public String getOtrosValores() {
		String out = "";
		for (ReciboFormaPagoDTO fp : this.formasPago) {
			out += fp.getTipo().getText().toUpperCase() + " : " + Utiles.getNumberFormat(fp.getMontoGs()) + " - ";
		}
		return out;
	}
	
	@DependsOn("cheques")
	public double getTotalImporte() {
		double out = 0;
		for (MyArray cheque : this.cheques) {
			out += (double)cheque.getPos5();
		}
		for (BancoChequeDTO cheque : this.chequesPropios) {
			out += cheque.getMonto();
		}
		for (ReciboFormaPagoDTO fp : this.formasPago) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	@DependsOn({ "cheques", "chequesPropios" })
	public List<MyArray> getCheques_() {
		List<MyArray> out = new ArrayList<MyArray>();
		out.addAll(this.cheques);
		out.addAll(this.chequesPropiosToMyArray());
		// ordena la lista segun fecha..
		Collections.sort(out, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				Date fecha1 = (Date) o1.getPos1();
				Date fecha2 = (Date) o2.getPos1();
				return fecha1.compareTo(fecha2);
			}
		});
		return out;
	}
	
	/**
	 * @return cheques propios como myarray..
	 */
	private List<MyArray> chequesPropiosToMyArray() {
		List<MyArray> out = new ArrayList<MyArray>();
		for (BancoChequeDTO cheque : this.chequesPropios) {
			MyArray my = new MyArray();
			my.setId(cheque.getId());
			my.setPos1(cheque.getFechaEmision());
			my.setPos2(cheque.getBanco().getBanco().getPos1());
			my.setPos3(cheque.getNumero() + "");
			my.setPos4(cheque.getBeneficiario().toUpperCase());
			my.setPos5(cheque.getMonto());
			my.setPos6(false);
			my.setPos7(null);
			my.setPos8(this.isReadonly() ? true : false);
			out.add(my);
		}
		return out;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getTotalChequesDescontado() {
		return totalChequesDescontado;
	}

	public void setTotalChequesDescontado(double totalChequesDescontado) {
		this.totalChequesDescontado = totalChequesDescontado;
	}

	public List<MyArray> getCheques() {
		return cheques;
	}

	public void setCheques(List<MyArray> cheques) {
		this.cheques = cheques;
	}

	public MyPair getSucursalApp() {
		return sucursalApp;
	}

	public void setSucursalApp(MyPair sucursalApp) {
		this.sucursalApp = sucursalApp;
	}

	public String getObservacion() {
		return observacion.toUpperCase();
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public MyPair getMoneda() {
		return moneda;
	}

	public void setMoneda(MyPair moneda) {
		this.moneda = moneda;
	}

	public List<BancoChequeDTO> getChequesPropios() {
		return chequesPropios;
	}

	public void setChequesPropios(List<BancoChequeDTO> chequesPropios) {
		this.chequesPropios = chequesPropios;
	}

	public double getLiq_impuestos() {
		return liq_impuestos;
	}

	public void setLiq_impuestos(double liq_impuestos) {
		this.liq_impuestos = liq_impuestos;
	}

	public double getLiq_gastos_adm() {
		return liq_gastos_adm;
	}

	public void setLiq_gastos_adm(double liq_gastos_adm) {
		this.liq_gastos_adm = liq_gastos_adm;
	}

	public double getLiq_intereses() {
		return liq_intereses;
	}

	public void setLiq_intereses(double liq_intereses) {
		this.liq_intereses = liq_intereses;
	}

	public double getLiq_neto_aldia() {
		return liq_neto_aldia;
	}

	public void setLiq_neto_aldia(double liq_neto_aldia) {
		this.liq_neto_aldia = liq_neto_aldia;
	}

	public double getLiq_neto_diferidos() {
		return liq_neto_diferidos;
	}

	public void setLiq_neto_diferidos(double liq_neto_diferidos) {
		this.liq_neto_diferidos = liq_neto_diferidos;
	}

	public boolean isLiq_registrado() {
		return liq_registrado;
	}

	public void setLiq_registrado(boolean liq_registrado) {
		this.liq_registrado = liq_registrado;
	}

	public List<ReciboFormaPagoDTO> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(List<ReciboFormaPagoDTO> formasPago) {
		this.formasPago = formasPago;
	}

	public MyArray getBanco() {
		return banco;
	}

	public void setBanco(MyArray banco) {
		this.banco = banco;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}
}
