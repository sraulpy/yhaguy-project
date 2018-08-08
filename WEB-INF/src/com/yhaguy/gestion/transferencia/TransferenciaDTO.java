package com.yhaguy.gestion.transferencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.comun.ReservaDTO;

@SuppressWarnings("serial")
public class TransferenciaDTO extends DTO {

	private String numero = "";
	private Date fechaCreacion;
	private Date fechaEnvio;
	private Date fechaRecepcion;
	private MyPair transferenciaEstado = new MyPair();
	private MyPair transferenciaTipo = new MyPair();
	private MyArray funcionarioCreador = new MyArray();
	private MyArray funcionarioEnvio = new MyArray();
	private MyArray funcionarioReceptor = new MyArray();
	private MyPair depositoSalida = new MyPair();
	private MyPair depositoEntrada = new MyPair();
	private MyPair transporte = new MyPair();

	private List<TransferenciaDetalleDTO> detalles = new ArrayList<TransferenciaDetalleDTO>();

	private ReservaDTO reserva;
	private String observacion = "";
	private MyPair sucursal = new MyPair();
	private MyPair sucursalDestino = new MyPair();
	
	public String toString() {
		return "Código transferencia: " + this.numero + " - Depósito Origen: "
				+ this.depositoSalida.getText() + " - Depósito Destino: "
				+ this.depositoEntrada.getText();
	}
	
	@DependsOn("transferenciaEstado")
	public boolean isEnTransito() {
		String sigla = this.transferenciaEstado.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_REP_TRANSITO);
	}
	
	@DependsOn("detalles")
	public double getTotalCosto() {
		double out = 0;
		for (TransferenciaDetalleDTO item : this.detalles) {
			out += item.getCostoTotal();
		}
		return out;
	}
	
	public List<TransferenciaDetalleDTO> getDetalles() {
		Collections.sort(this.detalles,
				new Comparator<TransferenciaDetalleDTO>() {
					@Override
					public int compare(TransferenciaDetalleDTO o1,
							TransferenciaDetalleDTO o2) {
						long id1 = o1.getId().longValue();
						long id2 = o2.getId().longValue();
						if (id1 < 0) {
							return 1;
						}
						return (int) (id1 - id2);
					}
				});
		return detalles;
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

	public MyPair getTransferenciaEstado() {
		return transferenciaEstado;
	}

	public void setTransferenciaEstado(MyPair transferenciaEstado) {
		this.transferenciaEstado = transferenciaEstado;
	}

	public MyPair getTransferenciaTipo() {
		return transferenciaTipo;
	}

	public void setTransferenciaTipo(MyPair transferenciaTipo) {
		this.transferenciaTipo = transferenciaTipo;
	}

	public MyArray getFuncionarioCreador() {
		return funcionarioCreador;
	}

	public void setFuncionarioCreador(MyArray funcionarioCreador) {
		this.funcionarioCreador = funcionarioCreador;
	}

	public MyArray getFuncionarioEnvio() {
		return funcionarioEnvio;
	}

	public void setFuncionarioEnvio(MyArray funcionarioEnvio) {
		this.funcionarioEnvio = funcionarioEnvio;
	}

	public MyArray getFuncionarioReceptor() {
		return funcionarioReceptor;
	}

	public void setFuncionarioReceptor(MyArray funcionarioReceptor) {
		this.funcionarioReceptor = funcionarioReceptor;
	}

	public MyPair getDepositoSalida() {
		return depositoSalida;
	}

	public void setDepositoSalida(MyPair depositoSalida) {
		this.depositoSalida = depositoSalida;
	}

	public MyPair getDepositoEntrada() {
		return depositoEntrada;
	}

	public void setDepositoEntrada(MyPair depositoEntrada) {
		this.depositoEntrada = depositoEntrada;
	}

	public MyPair getTransporte() {
		return transporte;
	}

	public void setTransporte(MyPair transporte) {
		this.transporte = transporte;
	}

	public void setDetalles(List<TransferenciaDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public ReservaDTO getReserva() {
		return reserva;
	}

	public void setReserva(ReservaDTO reserva) {
		this.reserva = reserva;
	}	

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyPair getSucursalDestino() {
		return sucursalDestino;
	}

	public void setSucursalDestino(MyPair sucursalDestino) {
		this.sucursalDestino = sucursalDestino;
	}
}
