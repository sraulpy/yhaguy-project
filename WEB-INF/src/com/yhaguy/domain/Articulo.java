package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Articulo extends Domain {

	private String descripcion = ""; 
	private String codigoInterno = ""; 
	private String codigoProveedor = "";
	private String codigoOriginal = ""; 
	private String codigoBarra = ""; 
	private String observacion = ""; 
	private String urlImagen = "";
	private String urlEspecificacion = "";
	private double peso;
	private double volumen; 
	private double costoGs;
	private Date fechaAlta; 
	private boolean importado; 
	private boolean servicio;
	long prioridad = 0; 
	boolean completo = false;
	private Tipo articuloEstado; 
	private Tipo articuloFamilia; 
	private Tipo articuloMarca; 
	private Tipo articuloParte; 						
	private Tipo articuloLinea; 
	private Tipo articuloUnidadMedida; 
	
	private long stock = 0;

	private Set<ProveedorArticulo> proveedorArticulos = new HashSet<ProveedorArticulo>();

	// en este dato ponemos toda la info extra que tengamos de los artículos
	// cómo tener marca y modelo era complicado de mantener, la idea es poner toda
	// la infor con un texto con tokens
	private Set<ArticuloInformacionExtra> articuloInformacionExtras = new HashSet<ArticuloInformacionExtra>();

	
	// Cuanto viene en caja, bolsa, juego, pallets, etc. teniendo como
	// referencia la Unidad de Medida de Venta
	private ArticuloPresentacion articuloPresentacion;
	
	private Set<ArticuloUbicacion> ubicaciones = new HashSet<ArticuloUbicacion>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return si el proveedor esta definido para este articulo..
	 */
	public boolean isProveedor(long idProveedor) {
		if (this.proveedorArticulos.size() == 0) {
			return false;
		}
		for (ProveedorArticulo proveedor : this.proveedorArticulos) {
			if (proveedor.getProveedor().getId().longValue() == idProveedor) {
				return true;
			}
		}
		return false;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public String getCodigoOriginal() {
		return codigoOriginal;
	}

	public void setCodigoOriginal(String codigoOriginal) {
		this.codigoOriginal = codigoOriginal;
	}
		
	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getUrlImagen() {
		return urlImagen;
	}

	public void setUrlImagen(String urlImagen) {
		this.urlImagen = urlImagen;
	}

	public String getUrlEspecificacion() {
		return urlEspecificacion;
	}

	public void setUrlEspecificacion(String urlEspecificacion) {
		this.urlEspecificacion = urlEspecificacion;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public double getVolumen() {
		return volumen;
	}

	public void setVolumen(double volumen) {
		this.volumen = volumen;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
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

	public Tipo getArticuloEstado() {
		return articuloEstado;
	}

	public void setArticuloEstado(Tipo articuloEstado) {
		this.articuloEstado = articuloEstado;
	}

	public Tipo getArticuloFamilia() {
		return articuloFamilia;
	}

	public void setArticuloFamilia(Tipo articuloFamilia) {
		this.articuloFamilia = articuloFamilia;
	}
		
	public Tipo getArticuloMarca() {
		return articuloMarca;
	}

		
	public void setArticuloMarca(Tipo articuloMarca) {
		this.articuloMarca = articuloMarca;
	}

	public Tipo getArticuloParte() {
		return articuloParte;
	}

	public void setArticuloParte(Tipo articuloParte) {
		this.articuloParte = articuloParte;
	}

	public Tipo getArticuloLinea() {
		return articuloLinea;
	}

	public void setArticuloLinea(Tipo articuloLinea) {
		this.articuloLinea = articuloLinea;
	}

	public Tipo getArticuloUnidadMedida() {
		return articuloUnidadMedida;
	}

	public void setArticuloUnidadMedida(Tipo articuloUnidadMedida) {
		this.articuloUnidadMedida = articuloUnidadMedida;
	}	
	
	public ArticuloPresentacion getArticuloPresentacion() {
		return articuloPresentacion;
	}

	public void setArticuloPresentacion(
			ArticuloPresentacion articuloPresentacion) {
		this.articuloPresentacion = articuloPresentacion;
	}

	public Set<ProveedorArticulo> getProveedorArticulos() {
		return proveedorArticulos;
	}

	public Set<ArticuloInformacionExtra> getArticuloInformacionExtras() {
		return articuloInformacionExtras;
	}

	public void setArticuloInformacionExtras(
			Set<ArticuloInformacionExtra> articuloInformacionExtras) {
		this.articuloInformacionExtras = articuloInformacionExtras;
	}

	public void setProveedorArticulos(Set<ProveedorArticulo> proveedorArticulos) {
		this.proveedorArticulos = proveedorArticulos;
	}

	public boolean isImportado() {
		return importado;
	}

	public void setImportado(boolean importado) {
		this.importado = importado;
	}

	public Set<ArticuloUbicacion> getUbicaciones() {
		return ubicaciones;
	}

	public void setUbicaciones(Set<ArticuloUbicacion> ubicaciones) {
		this.ubicaciones = ubicaciones;
	}

	public String getCodigoProveedor() {
		return codigoProveedor;
	}

	public void setCodigoProveedor(String codigoProveedor) {
		this.codigoProveedor = codigoProveedor;
	}

	public boolean isServicio() {
		return servicio;
	}

	public void setServicio(boolean servicio) {
		this.servicio = servicio;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public long getStock() {
		return stock;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}
}
