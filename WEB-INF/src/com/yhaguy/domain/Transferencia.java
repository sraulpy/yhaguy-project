package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Transferencia extends Domain {

	private String numero;
	private Date fechaCreacion;
	private Date fechaEnvio;
	private Date fechaRecepcion;
	private Tipo transferenciaEstado;
	private Tipo transferenciaTipo;
	private Funcionario funcionarioCreador;
	private Funcionario funcionarioEnvio;
	private Funcionario funcionarioReceptor;
	private Deposito depositoSalida;
	private Deposito depositoEntrada;
	private Transporte transporte;
	private String observacion;
	
	private SucursalApp sucursal;
	private SucursalApp sucursalDestino;

	private Set<TransferenciaDetalle> detalles = new HashSet<TransferenciaDetalle>();

	private Reserva reserva = null;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	/**
	 * @return el importe total de la transf..
	 */
	public double getImporteGs() {
		double out = 0;
		for (TransferenciaDetalle item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Tipo getTransferenciaEstado() {
		return transferenciaEstado;
	}

	public void setTransferenciaEstado(Tipo transferenciaEstado) {
		this.transferenciaEstado = transferenciaEstado;
	}

	public Tipo getTransferenciaTipo() {
		return transferenciaTipo;
	}

	public void setTransferenciaTipo(Tipo transferenciaTipo) {
		this.transferenciaTipo = transferenciaTipo;
	}

	public Funcionario getFuncionarioCreador() {
		return funcionarioCreador;
	}

	public void setFuncionarioCreador(Funcionario funcionarioCreador) {
		this.funcionarioCreador = funcionarioCreador;
	}

	public Funcionario getFuncionarioEnvio() {
		return funcionarioEnvio;
	}

	public void setFuncionarioEnvio(Funcionario funcionarioEnvio) {
		this.funcionarioEnvio = funcionarioEnvio;
	}

	public Funcionario getFuncionarioReceptor() {
		return funcionarioReceptor;
	}

	public void setFuncionarioReceptor(Funcionario funcionarioReceptor) {
		this.funcionarioReceptor = funcionarioReceptor;
	}

	public Deposito getDepositoSalida() {
		return depositoSalida;
	}

	public void setDepositoSalida(Deposito depositoSalida) {
		this.depositoSalida = depositoSalida;
	}

	public Deposito getDepositoEntrada() {
		return depositoEntrada;
	}

	public void setDepositoEntrada(Deposito depositoEntrada) {
		this.depositoEntrada = depositoEntrada;
	}

	public Set<TransferenciaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<TransferenciaDetalle> detalles) {
		this.detalles = detalles;
	}

	public Transporte getTransporte() {
		return transporte;
	}

	public void setTransporte(Transporte transporte) {
		this.transporte = transporte;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public SucursalApp getSucursalDestino() {
		return sucursalDestino;
	}

	public void setSucursalDestino(SucursalApp sucursalDestino) {
		this.sucursalDestino = sucursalDestino;
	}

}
