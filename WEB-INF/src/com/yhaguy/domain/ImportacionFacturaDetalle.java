package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ImportacionFacturaDetalle extends Domain {	
	
	private double costoGs;
	private double costoDs;
	private double costoSinProrrateoGs;
	private double costoSinProrrateoDs;
	private String textoDescuento;
	private double descuentoGs;
	private double descuentoDs;
	private double importeGastoDescuentoGs;
	private double importeGastoDescuentoDs;
	private boolean gastoDescuento;
	private boolean valorProrrateo;
	private Tipo tipoGastoDescuento;
	private int cantidad;
	private int cantidad_acum;
	private int cantidadRecibida;
	
	private int conteo1;
	private int conteo2;
	private int conteo3;
	
	private double precioFinalGs;
	private double minoristaGs;
	private double listaGs;
	private double transportadoraGs;
	
	private boolean verificado;
	private double costoFinalGs;
	private double costoPromedioGs;
	
	private Articulo articulo;
	
	/**
	 * @return el importe del item en gs.
	 */
	public double getImporteGs() {
		if (this.isGastoDescuento() == true) {
			return importeGastoDescuentoGs;
		} else {
			double out = (costoGs * cantidad) - (descuentoGs * cantidad);
			return Utiles.redondeoCuatroDecimales(out);
		}		
	}
	
	/**
	 * @return el importe del item en ds.
	 */	
	public double getImporteDs() {
		if (this.isGastoDescuento() == true) {
			return importeGastoDescuentoDs;
		} else {
			double out = (costoDs * cantidad) - (descuentoDs * cantidad);
			return Utiles.redondeoCuatroDecimales(out);
		}		
	}
	
	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public double getCostoDs() {
		return costoDs;
	}

	public void setCostoDs(double costoDs) {
		this.costoDs = costoDs;
	}
          	
	public String getTextoDescuento() {
		return textoDescuento;
	}

	public void setTextoDescuento(String textoDescuento) {
		this.textoDescuento = textoDescuento;
	}

	public double getDescuentoGs() {
		return descuentoGs;
	}

	public void setDescuentoGs(double descuentoGs) {
		this.descuentoGs = descuentoGs;
	}

	public double getDescuentoDs() {
		return descuentoDs;
	}

	public void setDescuentoDs(double descuentoDs) {
		this.descuentoDs = descuentoDs;
	}

	public double getImporteGastoDescuentoGs() {
		return importeGastoDescuentoGs;
	}

	public void setImporteGastoDescuentoGs(double importeGastoDescuentoGs) {
		this.importeGastoDescuentoGs = importeGastoDescuentoGs;
	}

	public double getImporteGastoDescuentoDs() {
		return importeGastoDescuentoDs;
	}

	public void setImporteGastoDescuentoDs(double importeGastoDescuentoDs) {
		this.importeGastoDescuentoDs = importeGastoDescuentoDs;
	}

	public boolean isGastoDescuento() {
		return gastoDescuento;
	}

	public void setGastoDescuento(boolean gastoDescuento) {
		this.gastoDescuento = gastoDescuento;
	}

	public Tipo getTipoGastoDescuento() {
		return tipoGastoDescuento;
	}

	public void setTipoGastoDescuento(Tipo tipoGastoDescuento) {
		this.tipoGastoDescuento = tipoGastoDescuento;
	}

	public int getCantidad() {
		return cantidad;
	}
	
	public long getCantidad_() {
		Long out = new Long(this.cantidad);
		return out.longValue();
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(int cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}	

	public boolean isValorProrrateo() {
		return valorProrrateo;
	}

	public void setValorProrrateo(boolean valorProrrateo) {
		this.valorProrrateo = valorProrrateo;
	}

	public double getCostoSinProrrateoGs() {
		return costoSinProrrateoGs;
	}

	public void setCostoSinProrrateoGs(double costoSinProrrateoGs) {
		this.costoSinProrrateoGs = costoSinProrrateoGs;
	}

	public double getCostoSinProrrateoDs() {
		return costoSinProrrateoDs;
	}

	public void setCostoSinProrrateoDs(double costoSinProrrateoDs) {
		this.costoSinProrrateoDs = costoSinProrrateoDs;
	}
	
	@Override
	public int compareTo(Object o) {
		ImportacionFacturaDetalle cmp = (ImportacionFacturaDetalle) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);
		
		if (isOk) {
			return 0;
		} else {
			return -1;	
		}		
	}

	public double getPrecioFinalGs() {
		return precioFinalGs;
	}

	public void setPrecioFinalGs(double precioFinalGs) {
		this.precioFinalGs = precioFinalGs;
	}

	public double getMinoristaGs() {
		return minoristaGs;
	}

	public void setMinoristaGs(double minoristaGs) {
		this.minoristaGs = minoristaGs;
	}

	public double getListaGs() {
		return listaGs;
	}

	public void setListaGs(double listaGs) {
		this.listaGs = listaGs;
	}

	public boolean isVerificado() {
		return verificado;
	}

	public void setVerificado(boolean verificado) {
		this.verificado = verificado;
	}

	public int getConteo1() {
		return conteo1;
	}

	public void setConteo1(int conteo1) {
		this.conteo1 = conteo1;
	}

	public int getConteo2() {
		return conteo2;
	}

	public void setConteo2(int conteo2) {
		this.conteo2 = conteo2;
	}

	public int getConteo3() {
		return conteo3;
	}

	public void setConteo3(int conteo3) {
		this.conteo3 = conteo3;
	}

	public int getCantidad_acum() {
		return cantidad_acum;
	}

	public void setCantidad_acum(int cantidad_acum) {
		this.cantidad_acum = cantidad_acum;
	}

	public double getTransportadoraGs() {
		return transportadoraGs;
	}

	public void setTransportadoraGs(double transportadoraGs) {
		this.transportadoraGs = transportadoraGs;
	}

	public double getCostoFinalGs() {
		return costoFinalGs;
	}

	public void setCostoFinalGs(double costoFinalGs) {
		this.costoFinalGs = costoFinalGs;
	}

	public double getCostoPromedioGs() {
		return costoPromedioGs;
	}

	public void setCostoPromedioGs(double costoPromedioGs) {
		this.costoPromedioGs = costoPromedioGs;
	}
}
