package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Articulo extends Domain {
	
	public static final String COD_VALVULAS = "VAL 413R";
	public static final String COD_DESCUENTO_BAT_USADA = "@DESC. BATERIA USADA";

	private String descripcion = ""; 
	private String codigoInterno = ""; 
	private String codigoProveedor = "";
	private String codigoOriginal = ""; 
	private String codigoBarra = ""; 
	private String observacion = ""; 
	private String referencia = "";
	private String numeroParte = "";
	private String ochentaVeinte = "";
	private String abc = "";
	private String urlImagen = "";
	private String urlEspecificacion = "";
	private double peso;
	private double volumen; 
	private double costoGs;
	private double costoDs;
	private double precioGs; // mayorista gs
	private double precioDs; // mayorista ds
	private double precioMinoristaGs; // minorista
	private double precioListaGs;	// autocentro
	private double precioTransportadora;
	private Date fechaAlta; 
	private Date fechaUltimaCompra;
	private int cantUltimaCompra;
	private double precioUltimaCompra;
	private Date fechaUltimaVenta;
	private int cantUltimaVenta;
	private double precioUltimaVenta;
	private boolean importado; 
	private boolean servicio;
	long prioridad = 0; 
	boolean completo = false;
	private boolean estado;
	private boolean restriccionCosto = true;
	private boolean promocion = false;
	private int maximo;
	private int minimo;
	private double porcentajeDescuento;
	private String descripcionPromocion;
	
	private String disenho;
	private int ancho;
	private int unidadesCaja;
	private double alto;
	private double aro;
	private double consumoCarga;
	private String medida;
	private String pisada;
	
	private Tipo articuloEstado; 
	private Tipo articuloFamilia; 
	private Tipo articuloMarca; 
	private Tipo articuloParte; 
	private Tipo articuloUnidadMedida; 
	
	private ArticuloFamilia familia;
	private ArticuloMarca marca;
	private ArticuloSubMarca articuloSubMarca;
	private ArticuloGrupo articuloGrupo;
	private ArticuloLinea articuloLinea;
	private ArticuloAplicacion articuloAplicacion;
	private ArticuloModelo articuloModelo;
	private ArticuloSubGrupo articuloSubGrupo;
	private ArticuloAPI articuloAPI;
	private ArticuloProcedencia articuloProcedencia;
	private ArticuloIndicecarga articuloIndiceCarga;
	private ArticuloLado articuloLado;
	private ArticuloPresentacion presentacion;
		
	private long stock = 0;
	
	private Proveedor proveedor;

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
		if (this.proveedor != null && this.proveedor.getId().longValue() == idProveedor) {
			return true;
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
	
	public List<ProveedorArticulo> getProveedorArticulos_() {
		List<ProveedorArticulo> out = new ArrayList<ProveedorArticulo>();
		out.addAll(this.proveedorArticulos);
		return out;
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

	public double getCostoDs() {
		return costoDs;
	}

	public void setCostoDs(double costoDs) {
		this.costoDs = costoDs;
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

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getNumeroParte() {
		return numeroParte;
	}

	public void setNumeroParte(String numeroParte) {
		this.numeroParte = numeroParte;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
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

	public ArticuloFamilia getFamilia() {
		return familia;
	}

	public void setFamilia(ArticuloFamilia familia) {
		this.familia = familia;
	}

	public ArticuloMarca getMarca() {
		return marca;
	}

	public void setMarca(ArticuloMarca marca) {
		this.marca = marca;
	}

	public ArticuloGrupo getArticuloGrupo() {
		return articuloGrupo;
	}

	public void setArticuloGrupo(ArticuloGrupo articuloGrupo) {
		this.articuloGrupo = articuloGrupo;
	}

	public ArticuloAplicacion getArticuloAplicacion() {
		return articuloAplicacion;
	}

	public void setArticuloAplicacion(ArticuloAplicacion articuloAplicacion) {
		this.articuloAplicacion = articuloAplicacion;
	}

	public ArticuloModelo getArticuloModelo() {
		return articuloModelo;
	}

	public void setArticuloModelo(ArticuloModelo articuloModelo) {
		this.articuloModelo = articuloModelo;
	}

	public ArticuloSubGrupo getArticuloSubGrupo() {
		return articuloSubGrupo;
	}

	public void setArticuloSubGrupo(ArticuloSubGrupo articuloSubGrupo) {
		this.articuloSubGrupo = articuloSubGrupo;
	}

	public ArticuloAPI getArticuloAPI() {
		return articuloAPI;
	}

	public void setArticuloAPI(ArticuloAPI articuloAPI) {
		this.articuloAPI = articuloAPI;
	}

	public ArticuloProcedencia getArticuloProcedencia() {
		return articuloProcedencia;
	}

	public void setArticuloProcedencia(ArticuloProcedencia articuloProcedencia) {
		this.articuloProcedencia = articuloProcedencia;
	}

	public ArticuloIndicecarga getArticuloIndiceCarga() {
		return articuloIndiceCarga;
	}

	public void setArticuloIndiceCarga(ArticuloIndicecarga articuloIndiceCarga) {
		this.articuloIndiceCarga = articuloIndiceCarga;
	}

	public ArticuloLado getArticuloLado() {
		return articuloLado;
	}

	public void setArticuloLado(ArticuloLado articuloLado) {
		this.articuloLado = articuloLado;
	}

	public ArticuloLinea getArticuloLinea() {
		return articuloLinea;
	}

	public void setArticuloLinea(ArticuloLinea articuloLinea) {
		this.articuloLinea = articuloLinea;
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

	public Date getFechaUltimaCompra() {
		return fechaUltimaCompra;
	}

	public void setFechaUltimaCompra(Date fechaUltimaCompra) {
		this.fechaUltimaCompra = fechaUltimaCompra;
	}

	public int getCantUltimaCompra() {
		return cantUltimaCompra;
	}

	public void setCantUltimaCompra(int cantUltimaCompra) {
		this.cantUltimaCompra = cantUltimaCompra;
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
}
