package com.yhaguy.gestion.articulos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.ArticuloPresentacion;
import com.yhaguy.domain.ArticuloSubMarca;

@SuppressWarnings({ "serial", "unused" })
public class ArticuloDTO extends DTO {

	static final String ESTADO_ACTIVO = "ACTIVO";
	static final String ESTADO_INACTIVO = "INACTIVO";
	
	private String descripcion = "";
	private String codigoInterno = "";
	private String codigoProveedor = "";
	private String codigoOriginal = "";
	private String codigoBarra = "";
	private String observacion = "";
	private String urlImagen = "";
	private String urlEspecificacion = "";
	private String ochentaVeinte = "";
	private String abc = "";
	private double peso = 0;
	private double volumen;
	Date fechaAlta = new Date();
	private Date fechaUltimaCompra;
	private double precioUltimaCompra;
	private Date fechaUltimaVenta;
	private int cantUltimaVenta;
	private double precioUltimaVenta;
	private long prioridad = 0;
	private boolean completo;
	private boolean importado = true;
	private boolean servicio = false;
	private boolean estado = true;
	private String estado_ = ESTADO_ACTIVO;
	private String referencia = "";
	private int maximo = 0;
	private int minimo = 0;
	private double porcentajeDescuento = 0;
	private boolean restriccionCosto = true;
	private boolean promocion = false;
	private String descripcionPromocion = "";
	
	private double precioGs = 0; // mayorista gs
	private double precioDs = 0; // mayorista ds
	private double precioMinoristaGs = 0; // minorista
	private double precioListaGs = 0;	// autocentro
	private double precioTransportadora = 0;
	private double precioBaterias = 0;
	private double precioPromocion = 0;
	private double precioMayoristaContadoGs = 0;
	
	private String disenho = "";
	private int ancho = 0;
	private double alto = 0.0;
	private double aro = 0.0;
	private String medida = "";
	private String pisada = "";
	private int unidadesCaja = 0;
	private double consumoCarga = 0;
	
	private MyPair articuloEstado = new MyPair();
	private MyPair articuloFamilia = new MyPair();
	private MyPair articuloMarca = new MyPair();
	private MyPair articuloParte = new MyPair();
	private MyPair articuloLinea = new MyPair();
	private MyPair articuloUnidadMedida = new MyPair();
	
	private MyArray familia;	
	private MyArray proveedor = new MyArray();
	
	private MyPair articuloGrupo = new MyPair();
	private MyPair articuloSubGrupo = new MyPair();
	private MyPair articuloModelo = new MyPair();
	private MyPair articuloAplicacion = new MyPair();
	private MyPair articuloAPI = new MyPair();
	private MyPair articuloProcedencia = new MyPair();
	private MyPair articuloIndiceCarga = new MyPair();
	private MyPair articuloLado = new MyPair();

	private List<MyArray> articuloInformacionExtras = new ArrayList<MyArray>();
	private List<ProveedorArticuloDTO> proveedorArticulos = new ArrayList<ProveedorArticuloDTO>();
	private List<MyArray> referenciasDeleted = new ArrayList<MyArray>();
	private List<MyArray> referencias = new ArrayList<MyArray>();
	private List<MyArray> ubicaciones = new ArrayList<MyArray>();
	private List<MyArray> precios = new ArrayList<MyArray>();
	
	private ArticuloMarca marca;
	private ArticuloSubMarca articuloSubMarca;
	private ArticuloFamilia familia_;
	private ArticuloPresentacion presentacion;
	
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
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "articulos/" + this.getId() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "articulos/" + this.getId() + ".png";
	}
	
	/**
	 * @return los estados..
	 */
	public List<String> getEstados() {
		List<String> out = new ArrayList<String>();
		out.add(ESTADO_ACTIVO);
		out.add(ESTADO_INACTIVO);
		return out;
	}
	
	@DependsOn("estado")
	public String getEstado_() {
		return this.estado ? ESTADO_ACTIVO : ESTADO_INACTIVO;
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

	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
	}

	public MyArray getFamilia() {
		return familia;
	}

	public void setFamilia(MyArray familia) {
		this.familia = familia;
	}

	public ArticuloMarca getMarca() {
		return marca;
	}

	public void setMarca(ArticuloMarca marca) {
		this.marca = marca;
	}

	public ArticuloFamilia getFamilia_() {
		return familia_;
	}

	public void setFamilia_(ArticuloFamilia familia_) {
		this.familia_ = familia_;
	}

	public MyPair getArticuloGrupo() {
		return articuloGrupo;
	}

	public void setArticuloGrupo(MyPair articuloGrupo) {
		this.articuloGrupo = articuloGrupo;
	}

	public MyPair getArticuloSubGrupo() {
		return articuloSubGrupo;
	}

	public void setArticuloSubGrupo(MyPair articuloSubGrupo) {
		this.articuloSubGrupo = articuloSubGrupo;
	}

	public MyPair getArticuloModelo() {
		return articuloModelo;
	}

	public void setArticuloModelo(MyPair articuloModelo) {
		this.articuloModelo = articuloModelo;
	}

	public MyPair getArticuloAplicacion() {
		return articuloAplicacion;
	}

	public void setArticuloAplicacion(MyPair articuloAplicacion) {
		this.articuloAplicacion = articuloAplicacion;
	}

	public MyPair getArticuloAPI() {
		return articuloAPI;
	}

	public void setArticuloAPI(MyPair articuloAPI) {
		this.articuloAPI = articuloAPI;
	}

	public MyPair getArticuloProcedencia() {
		return articuloProcedencia;
	}

	public void setArticuloProcedencia(MyPair articuloProcedencia) {
		this.articuloProcedencia = articuloProcedencia;
	}

	public MyPair getArticuloIndiceCarga() {
		return articuloIndiceCarga;
	}

	public void setArticuloIndiceCarga(MyPair articuloIndiceCarga) {
		this.articuloIndiceCarga = articuloIndiceCarga;
	}

	public MyPair getArticuloLado() {
		return articuloLado;
	}

	public void setArticuloLado(MyPair articuloLado) {
		this.articuloLado = articuloLado;
	}

	public String getDisenho() {
		return disenho;
	}

	public void setDisenho(String disenho) {
		this.disenho = disenho;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public double getAlto() {
		return alto;
	}

	public void setAlto(double alto) {
		this.alto = alto;
	}

	public double getAro() {
		return aro;
	}

	public void setAro(double aro) {
		this.aro = aro;
	}

	public String getMedida() {
		return medida;
	}

	public void setMedida(String medida) {
		this.medida = medida;
	}

	public String getPisada() {
		return pisada;
	}

	public void setPisada(String pisada) {
		this.pisada = pisada;
	}

	public ArticuloPresentacion getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(ArticuloPresentacion presentacion) {
		this.presentacion = presentacion;
	}

	public int getUnidadesCaja() {
		return unidadesCaja;
	}

	public void setUnidadesCaja(int unidadesCaja) {
		this.unidadesCaja = unidadesCaja;
	}

	public ArticuloSubMarca getArticuloSubMarca() {
		return articuloSubMarca;
	}

	public void setArticuloSubMarca(ArticuloSubMarca articuloSubMarca) {
		this.articuloSubMarca = articuloSubMarca;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public void setEstado_(String estado_) {
		this.estado_ = estado_;
		this.estado = estado_.equals(ESTADO_ACTIVO) ? true : false;
		BindUtils.postNotifyChange(null, null, this, "estado");
	}

	public String getOchentaVeinte() {
		return ochentaVeinte;
	}

	public void setOchentaVeinte(String ochentaVeinte) {
		this.ochentaVeinte = ochentaVeinte;
	}

	public String getAbc() {
		return abc;
	}

	public void setAbc(String abc) {
		this.abc = abc;
	}

	public int getMaximo() {
		return maximo;
	}

	public void setMaximo(int maximo) {
		this.maximo = maximo;
	}

	public int getMinimo() {
		return minimo;
	}

	public void setMinimo(int minimo) {
		this.minimo = minimo;
	}

	public double getPrecioGs() {
		return precioGs;
	}

	public void setPrecioGs(double precioGs) {
		this.precioGs = precioGs;
	}

	public double getPrecioDs() {
		return precioDs;
	}

	public void setPrecioDs(double precioDs) {
		this.precioDs = precioDs;
	}

	public double getPrecioMinoristaGs() {
		return precioMinoristaGs;
	}

	public void setPrecioMinoristaGs(double precioMinoristaGs) {
		this.precioMinoristaGs = precioMinoristaGs;
	}

	public double getPrecioListaGs() {
		return precioListaGs;
	}

	public void setPrecioListaGs(double precioListaGs) {
		this.precioListaGs = precioListaGs;
	}

	public Date getFechaUltimaCompra() {
		return fechaUltimaCompra;
	}

	public void setFechaUltimaCompra(Date fechaUltimaCompra) {
		this.fechaUltimaCompra = fechaUltimaCompra;
	}

	public double getPrecioUltimaCompra() {
		return precioUltimaCompra;
	}

	public void setPrecioUltimaCompra(double precioUltimaCompra) {
		this.precioUltimaCompra = precioUltimaCompra;
	}

	public Date getFechaUltimaVenta() {
		return fechaUltimaVenta;
	}

	public void setFechaUltimaVenta(Date fechaUltimaVenta) {
		this.fechaUltimaVenta = fechaUltimaVenta;
	}

	public int getCantUltimaVenta() {
		return cantUltimaVenta;
	}

	public void setCantUltimaVenta(int cantUltimaVenta) {
		this.cantUltimaVenta = cantUltimaVenta;
	}

	public double getPrecioUltimaVenta() {
		return precioUltimaVenta;
	}

	public void setPrecioUltimaVenta(double precioUltimaVenta) {
		this.precioUltimaVenta = precioUltimaVenta;
	}

	public boolean isRestriccionCosto() {
		return restriccionCosto;
	}

	public void setRestriccionCosto(boolean restriccionCosto) {
		this.restriccionCosto = restriccionCosto;
	}

	public double getPorcentajeDescuento() {
		return porcentajeDescuento;
	}

	public void setPorcentajeDescuento(double porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}

	public boolean isPromocion() {
		return promocion;
	}

	public void setPromocion(boolean promocion) {
		this.promocion = promocion;
	}

	public String getDescripcionPromocion() {
		return descripcionPromocion;
	}

	public void setDescripcionPromocion(String descripcionPromocion) {
		this.descripcionPromocion = descripcionPromocion;
	}

	public double getPrecioTransportadora() {
		return precioTransportadora;
	}

	public void setPrecioTransportadora(double precioTransportadora) {
		this.precioTransportadora = precioTransportadora;
	}

	public double getConsumoCarga() {
		return consumoCarga;
	}

	public void setConsumoCarga(double consumoCarga) {
		this.consumoCarga = consumoCarga;
	}

	public double getPrecioBaterias() {
		return precioBaterias;
	}

	public void setPrecioBaterias(double precioBaterias) {
		this.precioBaterias = precioBaterias;
	}

	public double getPrecioPromocion() {
		return precioPromocion;
	}

	public void setPrecioPromocion(double precioPromocion) {
		this.precioPromocion = precioPromocion;
	}

	public double getPrecioMayoristaContadoGs() {
		return precioMayoristaContadoGs;
	}

	public void setPrecioMayoristaContadoGs(double precioMayoristaContadoGs) {
		this.precioMayoristaContadoGs = precioMayoristaContadoGs;
	}
}
