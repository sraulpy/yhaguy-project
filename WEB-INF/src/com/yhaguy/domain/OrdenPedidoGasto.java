package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class OrdenPedidoGasto extends Domain{  

	private String numero;
	private long idUsuarioCarga;	
	private String nombreUsuarioCarga;
	private Date fechaCarga;
	private String descripcion;
	private String estado; //PEN:pendiente, AUT:autorizado, CAN:cancelado, CER:cerrado 
	private boolean autorizado;
	private boolean confirmado;
	private boolean presupuesto; //Para indicar si la ordenPedidoGasto cuenta con los 3 presupuestos..
	private long idUsuarioAutoriza;	
	private String nombreUsuarioAutoriza;
	private Date fechaAutorizacion;
	private String numeroFactura;
	private String numeroImportacion = "";
	
	private Proveedor proveedor;
	private CondicionPago condicionPago;
	private Set<OrdenPedidoGastoDetalle> ordenPedidoGastoDetalle = new HashSet<OrdenPedidoGastoDetalle>();
	
	private DepartamentoApp departamento;
	private SucursalApp sucursal;
	
	private SubDiario subDiario;
	private Set<Gasto> gastos = new HashSet<Gasto>();	

	public String getNumero() {
		return numero;
	}

	public void setNumero(String i) {
		this.numero = i;
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
		this.descripcion = descripcion;
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

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}	

	public Set<OrdenPedidoGastoDetalle> getOrdenPedidoGastoDetalle() {
		return ordenPedidoGastoDetalle;
	}

	public void setOrdenPedidoGastoDetalle(
			Set<OrdenPedidoGastoDetalle> ordenPedidoGastoDetalle) {
		this.ordenPedidoGastoDetalle = ordenPedidoGastoDetalle;
	}
	
	public DepartamentoApp getDepartamento() {
		return departamento;
	}

	public void setDepartamento(DepartamentoApp departamento) {
		this.departamento = departamento;
	}
	
	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public SubDiario getSubDiario() {
		return subDiario;
	}

	public void setSubDiario(SubDiario subDiario) {
		this.subDiario = subDiario;
	}

	public Set<Gasto> getGastos() {
		return gastos;
	}

	public void setGastos(Set<Gasto> gastos) {
		this.gastos = gastos;
	}

	@Override
	public int compareTo(Object o) {
		OrdenPedidoGasto cmp = (OrdenPedidoGasto) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);
		
		if (isOk) {
			return 0;
		} else {
			return -1;	
		}		
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public String getNumeroImportacion() {
		return numeroImportacion;
	}

	public void setNumeroImportacion(String numeroImportacion) {
		this.numeroImportacion = numeroImportacion;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}
}
