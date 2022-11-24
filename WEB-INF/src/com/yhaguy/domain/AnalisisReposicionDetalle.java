package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AnalisisReposicionDetalle extends Domain {
	
	private int ranking;
	private String codigoInterno;
	private String descripcion;
	private String familia;
	private double ventasUnidades;
	private double ventasImporte;
	private double pedidoReposicion;
	private double comprasUnidades;
	private double importacionUnidades;
	private double devoluciones;
	private double sugerido;
	private double aprobado;
	private long stock;
	private String observacion;
	private double promedio;
	
	private double ene;
	private double feb;
	private double mar;
	private double abr;
	private double may;
	private double jun;
	private double jul;
	private double ago;
	private double set;
	private double oct;
	private double nov;
	private double dic;
	
	private int cantClientes;
	private String ultProveedor;
	private double ultCosto;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public double getVentasUnidades() {
		return ventasUnidades;
	}

	public void setVentasUnidades(double ventasUnidades) {
		this.ventasUnidades = ventasUnidades;
	}

	public double getVentasImporte() {
		return ventasImporte;
	}

	public void setVentasImporte(double ventasImporte) {
		this.ventasImporte = ventasImporte;
	}

	public double getPedidoReposicion() {
		return pedidoReposicion;
	}

	public void setPedidoReposicion(double pedidoReposicion) {
		this.pedidoReposicion = pedidoReposicion;
	}

	public double getComprasUnidades() {
		return comprasUnidades;
	}

	public void setComprasUnidades(double comprasUnidades) {
		this.comprasUnidades = comprasUnidades;
	}

	public double getSugerido() {
		return sugerido;
	}

	public void setSugerido(double sugerido) {
		this.sugerido = sugerido;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public double getImportacionUnidades() {
		return importacionUnidades;
	}

	public void setImportacionUnidades(double importacionUnidades) {
		this.importacionUnidades = importacionUnidades;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public long getStock() {
		return stock;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}

	public double getDevoluciones() {
		return devoluciones;
	}

	public void setDevoluciones(double devoluciones) {
		this.devoluciones = devoluciones;
	}

	public double getPromedio() {
		return promedio;
	}

	public void setPromedio(double promedio) {
		this.promedio = promedio;
	}

	public String getFamilia() {
		return familia;
	}

	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public double getAprobado() {
		return aprobado;
	}

	public void setAprobado(double aprobado) {
		this.aprobado = aprobado;
	}

	public double getEne() {
		return ene;
	}

	public void setEne(double ene) {
		this.ene = ene;
	}

	public double getFeb() {
		return feb;
	}

	public void setFeb(double feb) {
		this.feb = feb;
	}

	public double getMar() {
		return mar;
	}

	public void setMar(double mar) {
		this.mar = mar;
	}

	public double getAbr() {
		return abr;
	}

	public void setAbr(double abr) {
		this.abr = abr;
	}

	public double getMay() {
		return may;
	}

	public void setMay(double may) {
		this.may = may;
	}

	public double getJun() {
		return jun;
	}

	public void setJun(double jun) {
		this.jun = jun;
	}

	public double getJul() {
		return jul;
	}

	public void setJul(double jul) {
		this.jul = jul;
	}

	public double getAgo() {
		return ago;
	}

	public void setAgo(double ago) {
		this.ago = ago;
	}

	public double getSet() {
		return set;
	}

	public void setSet(double set) {
		this.set = set;
	}

	public double getOct() {
		return oct;
	}

	public void setOct(double oct) {
		this.oct = oct;
	}

	public double getNov() {
		return nov;
	}

	public void setNov(double nov) {
		this.nov = nov;
	}

	public double getDic() {
		return dic;
	}

	public void setDic(double dic) {
		this.dic = dic;
	}

	public int getCantClientes() {
		return cantClientes;
	}

	public void setCantClientes(int cantClientes) {
		this.cantClientes = cantClientes;
	}

	public String getUltProveedor() {
		return ultProveedor;
	}

	public void setUltProveedor(String ultProveedor) {
		this.ultProveedor = ultProveedor;
	}

	public double getUltCosto() {
		return ultCosto;
	}

	public void setUltCosto(double ultCosto) {
		this.ultCosto = ultCosto;
	}
}
