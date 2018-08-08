package com.yhaguy.gestion.comun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class ReservaDTO extends DTO {
	private Date fecha = new Date();
	private String descripcion = "";
	private MyPair tipoReserva = new MyPair();
	private MyPair estadoReserva = new MyPair();
	private MyArray funcionarioEmisor = new MyArray();
	private MyPair depositoSalida = new MyPair();

	private List<ReservaDetalleDTO> detalles = new ArrayList<ReservaDetalleDTO>();

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

	public MyPair getTipoReserva() {
		return tipoReserva;
	}

	public void setTipoReserva(MyPair tipoReserva) {
		this.tipoReserva = tipoReserva;
	}

	public MyPair getEstadoReserva() {
		return estadoReserva;
	}

	public void setEstadoReserva(MyPair estadoReserva) {
		this.estadoReserva = estadoReserva;
	}

	public MyArray getFuncionarioEmisor() {
		return funcionarioEmisor;
	}

	public void setFuncionarioEmisor(MyArray funcionarioEmisor) {
		this.funcionarioEmisor = funcionarioEmisor;
	}

	public MyPair getDepositoSalida() {
		return depositoSalida;
	}

	public void setDepositoSalida(MyPair depositoSalida) {
		this.depositoSalida = depositoSalida;
	}

	public List<ReservaDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<ReservaDetalleDTO> detalles) {
		this.detalles = detalles;
	}

}
