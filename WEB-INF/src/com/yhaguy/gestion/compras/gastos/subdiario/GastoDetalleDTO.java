package com.yhaguy.gestion.compras.gastos.subdiario;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class GastoDetalleDTO extends DTO{

	private String observacion = "SIN DESC..";
	private int cantidad = 0;
	private double montoGs = 0;
	private double montoDs = 0;
	private double montoIva = 0;
	
	private ArticuloGastoDTO articuloGasto = new ArticuloGastoDTO();
	private MyArray centroCosto = new MyArray();
	private MyArray tipoIva = new MyArray();
	
	
	@DependsOn({"montoGs", "cantidad"})
	public double getImporteGs(){
		return montoGs * cantidad;
	}
	
	@DependsOn({"montoDs", "cantidad"})
	public double getImporteDs(){	
		return getMisc().redondeoDosDecimales(montoDs * cantidad);
	}
	
	@DependsOn({"tipoIva", "montoGs", "cantidad"})
	public double getIvaCalculado(){		
		if (tipoIva.esNuevo() == true) {
			this.setMontoIva(0);
			return 0;
		} else {
			int iva = Integer.parseInt(tipoIva.getPos2() + "");
			double ivaCalculado = getMisc().calcularIVA(this.getMontoGs(), iva) * this.getCantidad();;			
			this.setMontoIva(ivaCalculado);
			return ivaCalculado;
		}		
	}
	
	@DependsOn("tipoIva")
	public boolean isIva10() {
		String sigla = (String) this.tipoIva.getPos2();
		return sigla.compareTo(Configuracion.SIGLA_IVA_10) == 0;
	}
	
	@DependsOn("tipoIva")
	public boolean isIva5() {
		String sigla = (String) this.tipoIva.getPos2();
		return sigla.compareTo(Configuracion.SIGLA_IVA_5) == 0;
	}
	
	@DependsOn("tipoIva")
	public boolean isExenta() {
		String sigla = (String) this.tipoIva.getPos2();
		return sigla.compareTo(Configuracion.SIGLA_IVA_EXENTO) == 0;
	}
	
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion.toUpperCase();
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getMontoGs() {
		return montoGs;
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = getMisc().redondeoDosDecimales(montoDs);
	}

	public ArticuloGastoDTO getArticuloGasto() {
		return articuloGasto;
	}

	public void setArticuloGasto(ArticuloGastoDTO articuloGasto) {
		this.articuloGasto = articuloGasto;
	}

	public MyArray getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(MyArray centroCosto) {
		this.centroCosto = centroCosto;
	}

	public MyArray getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(MyArray tipoIva) {
		this.tipoIva = tipoIva;
	}

	public double getMontoIva() {
		return montoIva;
	}

	public void setMontoIva(double montoIva) {
		this.montoIva = getMisc().redondeo(montoIva);
	}	
}
