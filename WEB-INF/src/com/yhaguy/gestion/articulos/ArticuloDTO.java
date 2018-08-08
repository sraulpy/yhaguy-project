package com.yhaguy.gestion.articulos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class ArticuloDTO extends DTO {

	private String descripcion = "";
	private String codigoInterno = "";
	private String codigoProveedor = "";
	private String codigoOriginal = "";
	private String codigoBarra = "";
	private String observacion = "";
	private String urlImagen = "";
	private String urlEspecificacion = "";
	private double peso = 0;
	private double volumen;
	Date fechaAlta = new Date();
	private long prioridad = 0;
	private boolean completo;
	private boolean importado = true;
	private boolean servicio = false;
	
	private MyPair articuloEstado = new MyPair();
	private MyPair articuloFamilia = new MyPair();
	private MyPair articuloMarca = new MyPair();
	private MyPair articuloParte = new MyPair();
	private MyPair articuloLinea = new MyPair();
	private MyPair articuloUnidadMedida = new MyPair();

	private List<MyArray> articuloInformacionExtras = new ArrayList<MyArray>();
	private List<ProveedorArticuloDTO> proveedorArticulos = new ArrayList<ProveedorArticuloDTO>();
	private List<MyArray> referenciasDeleted = new ArrayList<MyArray>();
	private List<MyArray> referencias = new ArrayList<MyArray>();
	private List<MyArray> ubicaciones = new ArrayList<MyArray>();
	private List<MyArray> precios = new ArrayList<MyArray>();
	
	@Override
	public String toString() {
		String out = "";
		out += this.codigoInterno + " - " + this.descripcion + " ("
				+ this.getId() + ")";
		return out;
	}

	public String getUrlImagenWeb() {
		if ((this.getUrlImagen() == null)
				|| (this.getUrlImagen().trim().length() == 0)) {
			return Configuracion.IMAGEN_ARTICULO_DEFAULT;
		}
		String out = Configuracion.URL_IMAGENES_ARTICULOS + this.getUrlImagen();
		return out;
	}
	
	/**
	 * @return la url de la imagen..
	 */
	public String getUrlImagen_() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "articulos/" + this.getId() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "articulos/" + this.getId() + ".png";
	}

	public List<MyArray> getReferencias() {
		return referencias;
	}

	public void setReferencias(List<MyArray> articulosReferencias) {
		this.referencias = articulosReferencias;
	}

	public List<ProveedorArticuloDTO> getProveedorArticulos() {
		return proveedorArticulos;
	}

	public void setProveedorArticulos(
			List<ProveedorArticuloDTO> proveedorArticulos) {
		this.proveedorArticulos = proveedorArticulos;
	}

	private MyArray articuloPresentacion = new MyArray();

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno.toUpperCase();
	}

	public String getCodigoOriginal() {
		return codigoOriginal;
	}

	public void setCodigoOriginal(String codigoOriginal) {
		this.codigoOriginal = codigoOriginal.toUpperCase();
	}

	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra.toUpperCase();
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion.toUpperCase();
	}

	public String getUrlImagen() {
		return urlImagen;
	}

	public void setUrlImagen(String urlImagen) {
		this.urlImagen = urlImagen;
	}

	public String getUrlEspecificacion() {
		if (((urlEspecificacion == null) || (urlEspecificacion.trim().length() == 0))) {
			urlEspecificacion = Configuracion.IMAGEN_ARTICULO_DEFAULT;
		}
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

	public MyPair getArticuloEstado() {
		return articuloEstado;
	}

	public void setArticuloEstado(MyPair articuloEstado) {
		this.articuloEstado = articuloEstado;
	}

	public MyPair getArticuloFamilia() {
		return articuloFamilia;
	}

	public void setArticuloFamilia(MyPair articuloFamilia) {
		this.articuloFamilia = articuloFamilia;
	}

	public MyPair getArticuloMarca() {
		return articuloMarca;
	}

	public void setArticuloMarca(MyPair articuloMarca) {
		this.articuloMarca = articuloMarca;
	}

	public MyPair getArticuloParte() {
		return articuloParte;
	}

	public void setArticuloParte(MyPair articuloParte) {
		this.articuloParte = articuloParte;
	}

	public MyPair getArticuloLinea() {
		return articuloLinea;
	}

	public void setArticuloLinea(MyPair articuloLinea) {
		this.articuloLinea = articuloLinea;
	}

	public MyPair getArticuloUnidadMedida() {
		return articuloUnidadMedida;
	}

	public void setArticuloUnidadMedida(MyPair articuloUnidadMedida) {
		this.articuloUnidadMedida = articuloUnidadMedida;
	}

	public MyArray getArticuloPresentacion() {
		return articuloPresentacion;
	}

	public void setArticuloPresentacion(MyArray articuloPresentacion) {
		this.articuloPresentacion = articuloPresentacion;
	}

	public List<MyArray> getArticuloInformacionExtras() {
		return articuloInformacionExtras;
	}

	public void setArticuloInformacionExtras(
			List<MyArray> articuloInformacionExtras) {
		this.articuloInformacionExtras = articuloInformacionExtras;
	}

	public boolean isImportado() {
		return importado;
	}

	public void setImportado(boolean importado) {
		this.importado = importado;
	}

	public List<MyArray> getReferenciasDeleted() {
		return referenciasDeleted;
	}

	public void setReferenciasDeleted(List<MyArray> oldReferencias) {
		this.referenciasDeleted = oldReferencias;
	}

	public List<MyArray> getUbicaciones() {
		return ubicaciones;
	}

	public void setUbicaciones(List<MyArray> ubicaciones) {
		this.ubicaciones = ubicaciones;
	}

	public List<MyArray> getPrecios() {
		return precios;
	}

	public void setPrecios(List<MyArray> precios) {
		this.precios = precios;
	}

	public String getCodigoProveedor() {
		return codigoProveedor;
	}

	public void setCodigoProveedor(String codigoProveedor) {
		this.codigoProveedor = codigoProveedor.toUpperCase();
	}

	public boolean isServicio() {
		return servicio;
	}

	public void setServicio(boolean servicio) {
		this.servicio = servicio;
	}

}
