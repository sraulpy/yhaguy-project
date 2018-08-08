package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Reserva extends Domain{

	private Date fecha;
	private String descripcion;
	
	private boolean anulado;
	
	private Tipo tipoReserva;	//Reserva por Reparto - por Venta - etc..	
	private Tipo estadoReserva;
	
	private Funcionario funcionarioEmisor;
	private Deposito depositoSalida;
	
	private Set<ReservaDetalle> detalles = new HashSet<ReservaDetalle>();	
	
	public Tipo getTipoReserva() {
		return tipoReserva;
	}

	public void setTipoReserva(Tipo tipoReserva) {
		this.tipoReserva = tipoReserva;
	}	

	public Tipo getEstadoReserva() {
		return estadoReserva;
	}

	public void setEstadoReserva(Tipo estadoReserva) {
		this.estadoReserva = estadoReserva;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Funcionario getFuncionarioEmisor() {
		return funcionarioEmisor;
	}

	public void setFuncionarioEmisor(Funcionario funcionarioEmisor) {
		this.funcionarioEmisor = funcionarioEmisor;
	}

	public Deposito getDepositoSalida() {
		return depositoSalida;
	}

	public void setDepositoSalida(Deposito depositoSalida) {
		this.depositoSalida = depositoSalida;
	}
	
	public Set<ReservaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<ReservaDetalle> detalles) {
		this.detalles = detalles;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}
}
