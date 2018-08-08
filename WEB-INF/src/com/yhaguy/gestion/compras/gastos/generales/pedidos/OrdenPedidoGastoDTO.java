package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDTO;
import com.yhaguy.gestion.contabilidad.subdiario.SubDiarioDTO;

@SuppressWarnings("serial")
public class OrdenPedidoGastoDTO extends DTO{
	
	private String numero = "...";
	private long idUsuarioCarga = 0;	
	private String nombreUsuarioCarga = "";
	private Date fechaCarga = new Date();
	private String descripcion = "Sin desc..";
	private String estado = "PEN"; //PEN:pendiente, AUT:autorizado, CAN:cancelado, CER:cerrado 
	private boolean autorizado = false;
	private boolean confirmado = false;
	private boolean presupuesto = false; //Para indicar si la ordenPedidoGasto cuenta con los 3 presupuestos..
	private long idUsuarioAutoriza = 0;	
	private String nombreUsuarioAutoriza = "";
	private Date fechaAutorizacion;

	private MyArray proveedor = new MyArray(); //pos1: codigoEmpresa - pos2: razonSocial - pos3: ruc - pos6: idEmpresa
	private MyArray condicionPago = new MyArray();
	private List<OrdenPedidoGastoDetalleDTO> ordenPedidoGastoDetalle = new ArrayList<OrdenPedidoGastoDetalleDTO>();
	
	private MyArray departamento = new MyArray();
	private MyPair sucursal = new MyPair();
	
	private SubDiarioDTO subDiario = new SubDiarioDTO();
	private List<GastoDTO> gastos = new ArrayList<GastoDTO>();
	
	@DependsOn("ordenPedidoGastoDetalle")
	public double getTotalIva10() {
		double out = 0;
		for (OrdenPedidoGastoDetalleDTO item : this.ordenPedidoGastoDetalle) {
			if (item.isIva10())
				out += item.getImpuesto();
		}
		return out;
	}
	
	@DependsOn("ordenPedidoGastoDetalle")
	public double getTotalIva5() {
		double out = 0;
		for (OrdenPedidoGastoDetalleDTO item : this.ordenPedidoGastoDetalle) {
			if (item.isIva10() == false)
				out += item.getImpuesto();
		}
		return out;
	}
		
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public long getIdUsuarioCarga() {
		return idUsuarioCarga;
	}
	
	public void setIdUsuarioCarga(long idUsuarioCarga) {
		this.idUsuarioCarga = idUsuarioCarga;
	}
	
	public String getNombreUsuarioCarga() {
		return nombreUsuarioCarga;
	}
	
	public void setNombreUsuarioCarga(String nombreUsuarioCarga) {
		this.nombreUsuarioCarga = nombreUsuarioCarga;
	}
	
	public Date getFechaCarga() {
		return fechaCarga;
	}
	
	public void setFechaCarga(Date fechaCarga) {
		this.fechaCarga = fechaCarga;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public boolean isAutorizado() {
		return autorizado;
	}
	
	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}
	
	public boolean isPresupuesto() {
		return presupuesto;
	}
	
	public void setPresupuesto(boolean presupuesto) {
		this.presupuesto = presupuesto;
	}
	
	public long getIdUsuarioAutoriza() {
		return idUsuarioAutoriza;
	}
	
	public void setIdUsuarioAutoriza(long idUsuarioAutoriza) {
		this.idUsuarioAutoriza = idUsuarioAutoriza;
	}
	
	public String getNombreUsuarioAutoriza() {
		return nombreUsuarioAutoriza;
	}
	
	public void setNombreUsuarioAutoriza(String nombreUsuarioAutoriza) {
		this.nombreUsuarioAutoriza = nombreUsuarioAutoriza;
	}
	
	public Date getFechaAutorizacion() {
		return fechaAutorizacion;
	}
	
	public void setFechaAutorizacion(Date fechaAutorizacion) {
		this.fechaAutorizacion = fechaAutorizacion;
	}
	
	public MyArray getProveedor() {
		return proveedor;
	}
	
	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
	}
	
	public MyArray getCondicionPago() {
		return condicionPago;
	}
	
	public void setCondicionPago(MyArray condicionPago) {
		this.condicionPago = condicionPago;
	}	
	
	public List<OrdenPedidoGastoDetalleDTO> getOrdenPedidoGastoDetalle() {
		return ordenPedidoGastoDetalle;
	}
	
	public void setOrdenPedidoGastoDetalle(
			List<OrdenPedidoGastoDetalleDTO> ordenPedidoGastoDetalle) {
		this.ordenPedidoGastoDetalle = ordenPedidoGastoDetalle;
	}
	
	public MyArray getDepartamento() {
		return departamento;
	}
	
	public void setDepartamento(MyArray departamento) {
		this.departamento = departamento;
	}
	
	public MyPair getSucursal() {
		return sucursal;
	}
	
	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}
	
	public SubDiarioDTO getSubDiario() {
		return subDiario;
	}
	
	public void setSubDiario(SubDiarioDTO subDiario) {
		this.subDiario = subDiario;
	}
	
	public List<GastoDTO> getGastos() {
		return gastos;
	}
	
	public void setGastos(List<GastoDTO> gastos) {
		this.gastos = gastos;
	}
	
	public String toString(){
		return this.numero + " - " + this.descripcion + " - " + this.fechaCarga;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}
}
