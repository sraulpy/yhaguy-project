package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class ClienteDTO extends DTO {

	private String idPersonaJedi = "";
	private EmpresaDTO empresa = new EmpresaDTO();
	private MyPair estadoCliente;
	private MyPair categoriaCliente;
	private MyPair tipoCliente;
	
	private MyArray cuentaContable;
	private long prioridad = 0;
	private boolean completo = false;
	private String observaciones = "";
	private boolean ventaCredito = false;
	private double limiteCredito = 0;	
	private MyArray cobrador;
	private MyArray listaPrecio;
	
	private List<ContactoInternoDTO> contactosInternos = new ArrayList<ContactoInternoDTO>();
	private List<MyArray> chequesRechazados = new ArrayList<MyArray>();
	
	/**
	 * @return true si la cuenta esta bloqueada..
	 */
	public boolean isCuentaBloqueada() {
		return this.empresa.isCuentaBloqueada();
	}

	public String getIdPersonaJedi() {
		return idPersonaJedi;
	}

	public void setIdPersonaJedi(String idPersonaJedi) {
		this.idPersonaJedi = idPersonaJedi;
	}

	public String getNombre() {
		return this.getEmpresa().getNombre();
	}

	public void setNombre(String nombre) {
		this.getEmpresa().setNombre(nombre);
	}

	public String getCodigoEmpresa() {
		return this.getEmpresa().getCodigoEmpresa();
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.getEmpresa().setCodigoEmpresa(codigoEmpresa);
	}

	public String getRazonSocial() {
		return this.getEmpresa().getRazonSocial();
	}

	public void setRazonSocial(String razonSocial) {
		this.getEmpresa().setRazonSocial(razonSocial);
	}

	public String getRuc() {
		return this.getEmpresa().getRuc();
	}

	public void setRuc(String ruc) {
		this.getEmpresa().setRuc(ruc);
	}

	public long getIdEmpresa() {
		return this.getEmpresa().getId();
	}

	public long getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(long prioridad) {
		this.prioridad = prioridad;
	}

	public boolean isCompleto() {
		return completo;
	}

	public void setCompleto(boolean completo) {
		this.completo = completo;
	}

	public void setEmpresa(EmpresaDTO empresa) {
		this.empresa = empresa;
	}

	public EmpresaDTO getEmpresa() {
		return empresa;
	}

	public void set(EmpresaDTO empresa) {
		this.empresa = empresa;
	}

	public MyPair getEstadoCliente() {
		return estadoCliente;
	}

	public void setEstadoCliente(MyPair estadoCliente) {
		this.estadoCliente = estadoCliente;
	}

	public MyPair getCategoriaCliente() {
		return categoriaCliente;
	}

	public void setCategoriaCliente(MyPair categoriaCliente) {
		this.categoriaCliente = categoriaCliente;
	}

	public MyPair getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(MyPair tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public List<ContactoInternoDTO> getContactosInternos() {
		return contactosInternos;
	}

	public void setContactosInternos(List<ContactoInternoDTO> contactosInternos) {
		this.contactosInternos = contactosInternos;
	}

	public MyArray getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(MyArray cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String toString() {
		String out = "";
		if (this.getEmpresa() != null) {
			out += this.getEmpresa().getNombre() + " ["
					+ this.getEmpresa().getRazonSocial() + "] - ruc:"
					+ this.getEmpresa().getRuc() + "   (" + this.getId() + ")";
		} else {
			out += " sin empresa " + "(" + this.getId() + ")";
		}
		return out;
	}

	public double getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(double limiteCredito) {
		this.limiteCredito = limiteCredito;
	}

	public boolean isVentaCredito() {
		return ventaCredito;
	}

	public void setVentaCredito(boolean ventaCredito) {
		this.ventaCredito = ventaCredito;
	}

	public List<MyArray> getChequesRechazados() {
		return chequesRechazados;
	}

	public void setChequesRechazados(List<MyArray> chequesRechazados) {
		this.chequesRechazados = chequesRechazados;
	}

	public MyArray getCobrador() {
		return cobrador;
	}

	public void setCobrador(MyArray cobrador) {
		this.cobrador = cobrador;
	}

	public MyArray getListaPrecio() {
		return listaPrecio;
	}

	public void setListaPrecio(MyArray listaPrecio) {
		this.listaPrecio = listaPrecio;
	}

}
