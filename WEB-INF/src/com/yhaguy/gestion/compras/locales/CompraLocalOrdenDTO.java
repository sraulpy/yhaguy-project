package com.yhaguy.gestion.compras.locales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.empresa.ProveedorDTO;

@SuppressWarnings("serial")
public class CompraLocalOrdenDTO extends DTO{
	
	private String numero = "...";
	private Date fechaCreacion = new Date();
	private double tipoCambio = 0;
	private String observacion = "Sin obs..";
	private boolean autorizado = false;
	private boolean cerrado = false;
	private boolean recepcionado = false;
	private String autorizadoPor = "...";
	private String numeroFactura = "- - -";
	
	private ProveedorDTO proveedor = new ProveedorDTO();
	private MyArray condicionPago = new MyArray();
	private MyArray moneda = new MyArray();
	private MyPair sucursal = new MyPair();
	private MyPair deposito;
	private CompraLocalFacturaDTO factura;

	private MyArray tipoMovimiento = new MyArray();
	private List<CompraLocalOrdenDetalleDTO> detalles = new ArrayList<CompraLocalOrdenDetalleDTO>();
	private List<CompraLocalFacturaDTO> facturas = new ArrayList<CompraLocalFacturaDTO>();
	private List<CompraLocalGastoDTO> resumenGastos = new ArrayList<CompraLocalGastoDTO>(); 
	
	
	@DependsOn("autorizado")
	public String getEstado(){
		String out = "NO AUTORIZADO";
		if (this.autorizado == true) {
			out = "AUTORIZADO";
		}
		return out;
	}
	
	/**
	 * @return true si es una compra en moneda local..
	 */
	public boolean isMonedaLocal() {
		String sigla = (String) this.moneda.getPos2();
		return sigla.equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	@DependsOn("detalles")
	public List<CompraLocalOrdenDetalleDTO> getDetallesPresupuesto() {
		List<CompraLocalOrdenDetalleDTO> out = new ArrayList<CompraLocalOrdenDetalleDTO>();
		for (CompraLocalOrdenDetalleDTO item : this.getDetalles()) {
			if(item.isPresupuesto())
			out.add(item);
		}
		return out;
	}
	
	@DependsOn("detalles")
	public List<CompraLocalOrdenDetalleDTO> getDetallesOrdenCompra() {
		List<CompraLocalOrdenDetalleDTO> out = new ArrayList<CompraLocalOrdenDetalleDTO>();
		for (CompraLocalOrdenDetalleDTO item : this.getDetalles()) {
			if(item.isOrdenCompra())
			out.add(item);
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalImporteGs() {
		double out = 0;		
		for (CompraLocalOrdenDetalleDTO item : this.getDetalles()) {
			out += item.getImporteGs();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva10() {
		double out = 0;		
		for (CompraLocalOrdenDetalleDTO item : this.getDetalles()) {
			if(item.isIva10())
				out += item.getImporteIva();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva5() {
		double out = 0;		
		for (CompraLocalOrdenDetalleDTO item : this.getDetalles()) {
			if(item.isIva5())
				out += item.getImporteIva();
		}		
		return out;
	}
	
	@DependsOn("resumenGastos")
	public double getTotalResumenGastos() {
		double out = 0;		
		for (CompraLocalGastoDTO gasto : this.getResumenGastos()) {
			out += gasto.getMontoGs();
		}		
		return out;
	}
	
	/**
	 * @return el detalle ordenado..
	 */
	public List<CompraLocalOrdenDetalleDTO> getDetalles() {
		Collections.sort(this.detalles,
				new Comparator<CompraLocalOrdenDetalleDTO>() {
					@Override
					public int compare(CompraLocalOrdenDetalleDTO o1,
							CompraLocalOrdenDetalleDTO o2) {
						long id1 = o1.getId().longValue();
						long id2 = o2.getId().longValue();
						if (id1 < 0) {
							return 1;
						}
						return (int) (id1 - id2);
					}
				});
		return detalles;
	}
	
	public MyPair getDeposito() {
		return deposito;
	}

	public void setDeposito(MyPair deposito) {
		this.deposito = deposito;
	}
	
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	public double getTipoCambio() {		
		return tipoCambio;
	}
	
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	public String getObservacion() {
		return observacion;
	}
	
	public void setObservacion(String observacion) {
		this.observacion = observacion.toUpperCase();
	}
	
	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public ProveedorDTO getProveedor() {
		return proveedor;
	}
	
	public void setProveedor(ProveedorDTO proveedor) {
		this.proveedor = proveedor;
	}
	
	public MyArray getCondicionPago() {
		return condicionPago;
	}
	
	public void setCondicionPago(MyArray condicionPago) {
		this.condicionPago = condicionPago;
	}
	
	public MyArray getMoneda() {
		return moneda;
	}
	
	public void setMoneda(MyArray moneda) {
		this.moneda = moneda;
	}	
	
	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public void setDetalles(List<CompraLocalOrdenDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public List<CompraLocalFacturaDTO> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<CompraLocalFacturaDTO> facturas) {
		this.facturas = facturas;
	}

	public List<CompraLocalGastoDTO> getResumenGastos() {
		return resumenGastos;
	}

	public void setResumenGastos(List<CompraLocalGastoDTO> resumenGastos) {
		this.resumenGastos = resumenGastos;
	}

	public String getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(String autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}

	public boolean isRecepcionado() {
		return recepcionado;
	}

	public void setRecepcionado(boolean recepcionConfirmada) {
		this.recepcionado = recepcionConfirmada;
	}

	public CompraLocalFacturaDTO getFactura() {
		return factura;
	}

	public void setFactura(CompraLocalFacturaDTO factura) {
		this.factura = factura;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
}
