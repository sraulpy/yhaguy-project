package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;


@SuppressWarnings("serial")
public class HistoricoMovimientoArticulo extends Domain {
	
	private Date fecha;
	private String codigo;
	private String codigoProveedor;
	private String descripcion;
	private String referencia;
	private String codigoOriginal;
	private String estado;
	private String articulo;
	private String familia;
	private String marca;
	private String linea;
	private String grupo;
	private String ochentaVeinte;
	private String abc;
	private String aplicacion;
	private String modelo;
	private String peso;
	private String volumen;
	private String proveedor;
	private String subGrupo;
	private String parte;
	private String subMarca;
	private String procedencia;
	private int unidadesCaja;
	private long cantidad;
	private long cantidadImportacion;
	private long stock1;
	private long stock2;
	private long stock3;
	private long stock4;
	private long stock5;
	private long stock6;
	private long stock7;
	private long stock8;
	private long stock9;
	private long stock10;
	private long stockGral;
	private long stockMinimo;
	private long stockMaximo;
	private String fechaUltimaImport;
	private String fechaUltimaCompraLocal;
	private String fechaUltimaVenta;
	private String proveedorUltimaCompra;
	private String proveedorUltimaImport;
	private double costoFob;
	private double coeficiente;
	private double tipoCambio;
	private double costoGs;
	private double mayoristaGs;
	private double clienteGral;
	private double clienteMesVigente;
	private long enero;
	private long febrero;
	private long marzo;
	private long abril;
	private long mayo;
	private long junio;
	private long julio;
	private long agosto;
	private long setiembre;
	private long octubre;
	private long noviembre;
	private long diciembre;
	private long total;
	
	private double enero_;
	private double febrero_;
	private double marzo_;
	private double abril_;
	private double mayo_;
	private double junio_;
	private double julio_;
	private double agosto_;
	private double setiembre_;
	private double octubre_;
	private double noviembre_;
	private double diciembre_;
	
	private double _enero;
	private double _febrero;
	private double _marzo;
	private double _abril;
	private double _mayo;
	private double _junio;
	private double _julio;
	private double _agosto;
	private double _setiembre;
	private double _octubre;
	private double _noviembre;
	private double _diciembre;
	
	private double total_;
	
	private double litraje;
	
	private int maximo;
	private int minimo;
	private long cantCliente;
	private int cantClienteVigente;
	
	private double costoFobGs;
	private double costoFobDs;
	private double ultimoCostoGs;
	private double costoPromedioGs;
	
	public double getUltimoCostoGs() {
		return ultimoCostoGs;
	}

	public void setUltimoCostoGs(double ultimoCostoGs) {
		this.ultimoCostoGs = ultimoCostoGs;
	}

	public double getCostoPromedioGs() {
		return costoPromedioGs;
	}

	public void setCostoPromedioGs(double costoPromedioGs) {
		this.costoPromedioGs = costoPromedioGs;
	}

	private double precioMinorista;
	private double precioLista;
	
	private String ruc;
	private String vendedor;
	private String rubro;
	private String zona;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigoProveedor() {
		return codigoProveedor;
	}

	public void setCodigoProveedor(String codigoProveedor) {
		this.codigoProveedor = codigoProveedor;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getArticulo() {
		return articulo;
	}

	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}

	public String getFamilia() {
		return familia;
	}

	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getAplicacion() {
		return aplicacion;
	}

	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getVolumen() {
		return volumen;
	}

	public void setVolumen(String volumen) {
		this.volumen = volumen;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
	}

	public long getStock1() {
		return stock1;
	}

	public void setStock1(long stock1) {
		this.stock1 = stock1;
	}

	public long getStock2() {
		return stock2;
	}

	public void setStock2(long stock2) {
		this.stock2 = stock2;
	}

	public long getStock3() {
		return stock3;
	}

	public void setStock3(long stock3) {
		this.stock3 = stock3;
	}

	public long getStock4() {
		return stock4;
	}

	public void setStock4(long stock4) {
		this.stock4 = stock4;
	}

	public long getStock5() {
		return stock5;
	}

	public void setStock5(long stock5) {
		this.stock5 = stock5;
	}

	public long getStock6() {
		return stock6;
	}

	public void setStock6(long stock6) {
		this.stock6 = stock6;
	}

	public long getStock7() {
		return stock7;
	}

	public void setStock7(long stock7) {
		this.stock7 = stock7;
	}

	public long getStock8() {
		return stock8;
	}

	public void setStock8(long stock8) {
		this.stock8 = stock8;
	}

	public long getStockGral() {
		return stockGral;
	}

	public void setStockGral(long stockGral) {
		this.stockGral = stockGral;
	}

	public long getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(long stockMinimo) {
		this.stockMinimo = stockMinimo;
	}

	public long getStockMaximo() {
		return stockMaximo;
	}

	public void setStockMaximo(long stockMaximo) {
		this.stockMaximo = stockMaximo;
	}

	public String getFechaUltimaVenta() {
		return fechaUltimaVenta;
	}

	public void setFechaUltimaVenta(String fechaUltimaVenta) {
		this.fechaUltimaVenta = fechaUltimaVenta;
	}

	public String getProveedorUltimaCompra() {
		return proveedorUltimaCompra;
	}

	public void setProveedorUltimaCompra(String proveedorUltimaCompra) {
		this.proveedorUltimaCompra = proveedorUltimaCompra;
	}

	public double getCostoFob() {
		return costoFob;
	}

	public void setCostoFob(double costoFob) {
		this.costoFob = costoFob;
	}

	public double getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(double coeficiente) {
		this.coeficiente = coeficiente;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public double getMayoristaGs() {
		return mayoristaGs;
	}

	public void setMayoristaGs(double mayoristaGs) {
		this.mayoristaGs = mayoristaGs;
	}

	public double getClienteGral() {
		return clienteGral;
	}

	public void setClienteGral(double clienteGral) {
		this.clienteGral = clienteGral;
	}

	public double getClienteMesVigente() {
		return clienteMesVigente;
	}

	public void setClienteMesVigente(double clienteMesVigente) {
		this.clienteMesVigente = clienteMesVigente;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public long getEnero() {
		return enero;
	}

	public void setEnero(long enero) {
		this.enero = enero;
	}

	public long getFebrero() {
		return febrero;
	}

	public void setFebrero(long febrero) {
		this.febrero = febrero;
	}

	public long getMarzo() {
		return marzo;
	}

	public void setMarzo(long marzo) {
		this.marzo = marzo;
	}

	public long getAbril() {
		return abril;
	}

	public void setAbril(long abril) {
		this.abril = abril;
	}

	public long getMayo() {
		return mayo;
	}

	public void setMayo(long mayo) {
		this.mayo = mayo;
	}

	public long getJunio() {
		return junio;
	}

	public void setJunio(long junio) {
		this.junio = junio;
	}

	public long getJulio() {
		return julio;
	}

	public void setJulio(long julio) {
		this.julio = julio;
	}

	public long getAgosto() {
		return agosto;
	}

	public void setAgosto(long agosto) {
		this.agosto = agosto;
	}

	public long getSetiembre() {
		return setiembre;
	}

	public void setSetiembre(long setiembre) {
		this.setiembre = setiembre;
	}

	public long getOctubre() {
		return octubre;
	}

	public void setOctubre(long octubre) {
		this.octubre = octubre;
	}

	public long getNoviembre() {
		return noviembre;
	}

	public void setNoviembre(long noviembre) {
		this.noviembre = noviembre;
	}

	public long getDiciembre() {
		return diciembre;
	}

	public void setDiciembre(long diciembre) {
		this.diciembre = diciembre;
	}

	public long getTotal() {
		return total;
	}

	public double getLitraje() {
		return litraje;
	}

	public void setLitraje(double litraje) {
		this.litraje = litraje;
	}

	public double getEnero_() {
		return enero_;
	}

	public void setEnero_(double enero_) {
		this.enero_ = enero_;
	}

	public double getFebrero_() {
		return febrero_;
	}

	public void setFebrero_(double febrero_) {
		this.febrero_ = febrero_;
	}

	public double getMarzo_() {
		return marzo_;
	}

	public void setMarzo_(double marzo_) {
		this.marzo_ = marzo_;
	}

	public double getAbril_() {
		return abril_;
	}

	public void setAbril_(double abril_) {
		this.abril_ = abril_;
	}

	public double getMayo_() {
		return mayo_;
	}

	public void setMayo_(double mayo_) {
		this.mayo_ = mayo_;
	}

	public double getJunio_() {
		return junio_;
	}

	public void setJunio_(double junio_) {
		this.junio_ = junio_;
	}

	public double getJulio_() {
		return julio_;
	}

	public void setJulio_(double julio_) {
		this.julio_ = julio_;
	}

	public double getAgosto_() {
		return agosto_;
	}

	public void setAgosto_(double agosto_) {
		this.agosto_ = agosto_;
	}

	public double getSetiembre_() {
		return setiembre_;
	}

	public void setSetiembre_(double setiembre_) {
		this.setiembre_ = setiembre_;
	}

	public double getOctubre_() {
		return octubre_;
	}

	public void setOctubre_(double octubre_) {
		this.octubre_ = octubre_;
	}

	public double getNoviembre_() {
		return noviembre_;
	}

	public void setNoviembre_(double noviembre_) {
		this.noviembre_ = noviembre_;
	}

	public double getDiciembre_() {
		return diciembre_;
	}

	public void setDiciembre_(double diciembre_) {
		this.diciembre_ = diciembre_;
	}

	public double getTotal_() {
		return total_;
	}

	public void setTotal_(double total_) {
		this.total_ = total_;
	}

	public double get_enero() {
		return _enero;
	}

	public void set_enero(double _enero) {
		this._enero = _enero;
	}

	public double get_febrero() {
		return _febrero;
	}

	public void set_febrero(double _febrero) {
		this._febrero = _febrero;
	}

	public double get_marzo() {
		return _marzo;
	}

	public void set_marzo(double _marzo) {
		this._marzo = _marzo;
	}

	public double get_abril() {
		return _abril;
	}

	public void set_abril(double _abril) {
		this._abril = _abril;
	}

	public double get_mayo() {
		return _mayo;
	}

	public void set_mayo(double _mayo) {
		this._mayo = _mayo;
	}

	public double get_junio() {
		return _junio;
	}

	public void set_junio(double _junio) {
		this._junio = _junio;
	}

	public double get_julio() {
		return _julio;
	}

	public void set_julio(double _julio) {
		this._julio = _julio;
	}

	public double get_agosto() {
		return _agosto;
	}

	public void set_agosto(double _agosto) {
		this._agosto = _agosto;
	}

	public double get_setiembre() {
		return _setiembre;
	}

	public void set_setiembre(double _setiembre) {
		this._setiembre = _setiembre;
	}

	public double get_octubre() {
		return _octubre;
	}

	public void set_octubre(double _octubre) {
		this._octubre = _octubre;
	}

	public double get_noviembre() {
		return _noviembre;
	}

	public void set_noviembre(double _noviembre) {
		this._noviembre = _noviembre;
	}

	public double get_diciembre() {
		return _diciembre;
	}

	public void set_diciembre(double _diciembre) {
		this._diciembre = _diciembre;
	}

	public String getCodigoOriginal() {
		return codigoOriginal;
	}

	public void setCodigoOriginal(String codigoOriginal) {
		this.codigoOriginal = codigoOriginal;
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

	public double getCostoFobGs() {
		return costoFobGs;
	}

	public void setCostoFobGs(double costoFobGs) {
		this.costoFobGs = costoFobGs;
	}

	public double getCostoFobDs() {
		return costoFobDs;
	}

	public void setCostoFobDs(double costoFobDs) {
		this.costoFobDs = costoFobDs;
	}

	public long getCantCliente() {
		return cantCliente;
	}

	public void setCantCliente(long cantCliente) {
		this.cantCliente = cantCliente;
	}

	public int getCantClienteVigente() {
		return cantClienteVigente;
	}

	public void setCantClienteVigente(int cantClienteVigente) {
		this.cantClienteVigente = cantClienteVigente;
	}

	public String getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(String subGrupo) {
		this.subGrupo = subGrupo;
	}

	public String getParte() {
		return parte;
	}

	public void setParte(String parte) {
		this.parte = parte;
	}

	public String getSubMarca() {
		return subMarca;
	}

	public void setSubMarca(String subMarca) {
		this.subMarca = subMarca;
	}

	public String getProcedencia() {
		return procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia;
	}

	public int getUnidadesCaja() {
		return unidadesCaja;
	}

	public void setUnidadesCaja(int unidadesCaja) {
		this.unidadesCaja = unidadesCaja;
	}

	public long getStock9() {
		return stock9;
	}

	public void setStock9(long stock9) {
		this.stock9 = stock9;
	}

	public long getStock10() {
		return stock10;
	}

	public void setStock10(long stock10) {
		this.stock10 = stock10;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getVendedor() {
		return vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public String getRubro() {
		return rubro;
	}

	public void setRubro(String rubro) {
		this.rubro = rubro;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public double getPrecioMinorista() {
		return precioMinorista;
	}

	public void setPrecioMinorista(double precioMinorista) {
		this.precioMinorista = precioMinorista;
	}

	public double getPrecioLista() {
		return precioLista;
	}

	public void setPrecioLista(double precioLista) {
		this.precioLista = precioLista;
	}

	public String getFechaUltimaCompraLocal() {
		return fechaUltimaCompraLocal;
	}

	public void setFechaUltimaCompraLocal(String fechaUltimaCompraLocal) {
		this.fechaUltimaCompraLocal = fechaUltimaCompraLocal;
	}

	public long getCantidadImportacion() {
		return cantidadImportacion;
	}

	public void setCantidadImportacion(long cantidadImportacion) {
		this.cantidadImportacion = cantidadImportacion;
	}

	public String getProveedorUltimaImport() {
		return proveedorUltimaImport;
	}

	public void setProveedorUltimaImport(String proveedorUltimaImport) {
		this.proveedorUltimaImport = proveedorUltimaImport;
	}

	public String getFechaUltimaImport() {
		return fechaUltimaImport;
	}

	public void setFechaUltimaImport(String fechaUltimaImport) {
		this.fechaUltimaImport = fechaUltimaImport;
	}
}
