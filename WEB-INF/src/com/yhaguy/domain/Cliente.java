package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class Cliente extends Domain {

	private String idPersonaJedi = "";	
	private long prioridad = 0;
	private boolean completo = false;
	private String observaciones = "";
	
	private boolean ventaCredito;
	private double limiteCredito;
	
	private Empresa empresa;
	private Tipo estadoCliente;
	private Tipo categoriaCliente;
	private Tipo tipoCliente;
	private ArticuloListaPrecio listaPrecio;
	private CuentaContable cuentaContable;
	private Set<ContactoInterno> contactosInternos = new HashSet<ContactoInterno>();
	private Funcionario cobrador;
	
	/**
	 * @return true si el cliente coincide con el ruc..
	 */
	public boolean isCliente(String ruc) {
		return this.empresa.getRuc().equals(ruc);
	}
	
	/**
	 * @return true si el cliente es py autopartes..
	 */
	public boolean isPyAutopartes() {
		return this.empresa.getRuc().equals(Configuracion.RUC_PY_AUTOPARTES);
	}
	
	/**
	 * @return true si el cliente es acceso sur..
	 */
	public boolean isAccesoSur() {
		return this.id == Configuracion.ID_SUC_ACCESO_SUR;
	}
	
	/**
	 * @return los cheques rechazados del cliente..
	 */
	public List<BancoChequeTercero> getChequesRechazados() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getChequesRechazados(this.id);
	}
	
	/**
	 * @return la descripcion del rubro..
	 */
	public String getRubro() {
		String out = "NO DEFINIDO";
		if (this.empresa.getRubroEmpresas().size() > 0) {
			for (Tipo rubro : this.empresa.getRubroEmpresas()) {
				out = rubro.getDescripcion();
			}
		}
		return out;
	}
	
	public String getDescripcion() {
		return this.empresa.getRazonSocial();
	}
	
	public void setDescripcion(String descripcion) {
	}

	public String getNombre() {
		return this.getEmpresa().getNombre();
	}

	public void setNombre(String nombre) {
	}
	
	public String getDireccion() {
		return this.empresa.getDireccion_();
	}
	
	public void setDireccion(String direccion) {
	}
	
	public String getTelefono() {
		for (Sucursal sucursal : this.empresa.getSucursales()) {
			return sucursal.getTelefono();
		}
		return "...";
	}
	
	public void setTelefono(String direccion) {
	}

	public String getCodigoEmpresa() {
		return this.empresa.getCodigoEmpresa();
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
	}
	
	public boolean isCuentaBloqueada() {
		return this.empresa.isCuentaBloqueada();
	}
	
	public void setCuentaBloqueada(boolean cuentabloqueada) {
	}

	public String getIdPersonaJedi() {
		return idPersonaJedi;
	}

	public void setIdPersonaJedi(String idPersonaJedi) {
		this.idPersonaJedi = idPersonaJedi;
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

	public Tipo getEstadoCliente() {
		return estadoCliente;
	}

	public void setEstadoCliente(Tipo estadoCliente) {
		this.estadoCliente = estadoCliente;
	}

	public Tipo getCategoriaCliente() {
		return categoriaCliente;
	}

	public void setCategoriaCliente(Tipo categoriaCliente) {
		this.categoriaCliente = categoriaCliente;
	}

	public Tipo getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(Tipo tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public Set<ContactoInterno> getContactosInternos() {
		return contactosInternos;
	}

	public void setContactosInternos(Set<ContactoInterno> vendedores) {
		this.contactosInternos = vendedores;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public Set<Sucursal> getSucursales() {
		Set<Sucursal> sucursales = this.empresa.getSucursales();
		return sucursales;
	}

	public Set<Contacto> getContactos() {
		Set<Contacto> contactos = this.empresa.getContactos();
		return contactos;
	}

	public long getIdEmpresa() {
		return empresa.getId();
	}

	public void setIdEmpresa(long idEmpresa) {
	}

	public String getRazonSocial() {
		String razonSocial = this.empresa.getRazonSocial();
		return razonSocial.toUpperCase();
	}

	public void setRazonSocial(String razonSocial) {
		this.empresa.setRazonSocial(razonSocial);
	}
	
	public String getNombreFantasia() {
		return this.empresa.getNombre();
	}
	
	public void setNombreFantasia(String nombre) {
	}

	public String getRuc() {
		String ruc = this.empresa.getRuc();
		return ruc;
	}

	public void setRuc(String ruc) {
		this.empresa.setRuc(ruc);
	}
	
	public Date getFechaIngreso() {
		Date fechaIngreso = this.empresa.getFechaIngreso();
		return fechaIngreso;
	}

	public String getDescripcionEstado() {
		String descripcion = this.estadoCliente.getDescripcion();
		return descripcion;
	}

	public String getDescripcionCategoria() {
		String descripcion = this.categoriaCliente.getDescripcion();
		return descripcion;
	}

	public String getDescripcionTipoCliente() {
		String descripcion = this.tipoCliente.getDescripcion();
		return descripcion;
	}

	@SuppressWarnings("rawtypes")
	public String getDescripcionRubroEmpresa() {
		String out = "";
		Set<Tipo> sr = this.empresa.getRubroEmpresas();
		for (Iterator iterator = sr.iterator(); iterator.hasNext();) {
			Tipo r = (Tipo) iterator.next();
			out += "[" + r.getDescripcion() + "] ";

		}
		return out.trim();
	}
	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public void getRuc(String ruc) {
		ruc = this.empresa.getRuc();
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@Override
	public int compareTo(Object o) {

		Cliente cmp = (Cliente) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
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

	public Funcionario getCobrador() {
		return cobrador;
	}

	public void setCobrador(Funcionario cobrador) {
		this.cobrador = cobrador;
	}

	public ArticuloListaPrecio getListaPrecio() {
		return listaPrecio;
	}

	public void setListaPrecio(ArticuloListaPrecio listaPrecio) {
		this.listaPrecio = listaPrecio;
	}
}
