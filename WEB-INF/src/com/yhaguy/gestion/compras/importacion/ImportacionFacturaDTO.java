package com.yhaguy.gestion.compras.importacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.empresa.ProveedorDTO;

@SuppressWarnings("serial")
public class ImportacionFacturaDTO extends DTO{
		
	private String numero = "";	
	private Date fechaOriginal;
	private Date fechaCreacion = new Date();	
	private String observacion = "";	
	private boolean confirmadoImportacion = false;
	private boolean confirmadoAuditoria = false;
	private boolean confirmadoVentas = false;
	private boolean confirmadoAdministracion = false;
	private int propietarioActual = Configuracion.PROPIETARIO_IMPORTACION_KEY;	
	private double descuentoGs = 0;
	private double descuentoDs = 0;
	private double totalAsignadoGs = 0;
	private double totalAsignadoDs = 0;
	private double porcProrrateo = 0;
	private boolean facturaVerificada = false;
	private boolean recepcionConfirmada = false;
	
	private ProveedorDTO proveedor = new ProveedorDTO();
	private MyArray tipoMovimiento = new MyArray();
	private MyArray condicionPago = new MyArray();
	private MyArray moneda = new MyArray();
	private List<ImportacionFacturaDetalleDTO> detalles = new ArrayList<ImportacionFacturaDetalleDTO>();
	
	/**
	 * @return el importe total en gs..
	 */
	public double getTotalImporteGs() {
		double out = 0;
		for (ImportacionFacturaDetalleDTO det : this.detalles) {
			out += det.getImporteGsCalculado();
		}
		return out;
	}
	
	/**
	 * @return el importe total en gs..
	 */
	public double getTotalImporteDs() {
		double out = 0;
		for (ImportacionFacturaDetalleDTO det : this.detalles) {
			out += det.getImporteDsCalculado();
		}
		return out;
	}
	
	public String getDetalleSize(){
		return this.detalles.size() + " ítems";
	}
	
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public Date getFechaOriginal() {
		return fechaOriginal;
	}
	
	public void setFechaOriginal(Date fechaOriginal) {
		this.fechaOriginal = fechaOriginal;
	}
	
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	public String getObservacion() {
		return observacion;
	}
	
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public boolean isConfirmadoImportacion() {
		return confirmadoImportacion;
	}
	
	public void setConfirmadoImportacion(boolean confirmadoImportacion) {
		this.confirmadoImportacion = confirmadoImportacion;
	}
	
	public boolean isConfirmadoAuditoria() {
		return confirmadoAuditoria;
	}
	
	public void setConfirmadoAuditoria(boolean confirmadoAuditoria) {
		this.confirmadoAuditoria = confirmadoAuditoria;
	}
	
	public boolean isConfirmadoVentas() {
		return confirmadoVentas;
	}
	
	public void setConfirmadoVentas(boolean confirmadoVentas) {
		this.confirmadoVentas = confirmadoVentas;
	}
	
	public boolean isConfirmadoAdministracion() {
		return confirmadoAdministracion;
	}
	
	public void setConfirmadoAdministracion(boolean confirmadoAdministracion) {
		this.confirmadoAdministracion = confirmadoAdministracion;
	}
	
	public int getPropietarioActual() {
		return propietarioActual;
	}
	
	public void setPropietarioActual(int propietarioActual) {
		this.propietarioActual = propietarioActual;
	}
	
	public double getDescuentoGs() {
		return descuentoGs;
	}
	
	public void setDescuentoGs(double descuentoGs) {
		this.descuentoGs = this.getMisc().redondeoCuatroDecimales(descuentoGs);
	}
	
	public double getDescuentoDs() {
		return descuentoDs;
	}
	
	public void setDescuentoDs(double descuentoDs) {
		this.descuentoDs = this.getMisc().redondeoCuatroDecimales(descuentoDs);
	}
	
	public double getTotalAsignadoGs() {
		return totalAsignadoGs;
	}
	
	public void setTotalAsignadoGs(double totalAsignadoGs) {
		this.totalAsignadoGs = this.getMisc().redondeoCuatroDecimales(totalAsignadoGs);
	}
	
	public double getTotalAsignadoDs() {
		return totalAsignadoDs;
	}
	
	public void setTotalAsignadoDs(double totalAsignadoDs) {
		this.totalAsignadoDs = this.getMisc().redondeoCuatroDecimales(totalAsignadoDs);
	}
	
	public boolean isFacturaVerificada() {
		return facturaVerificada;
	}
	
	public void setFacturaVerificada(boolean facturaVerificada) {
		this.facturaVerificada = facturaVerificada;
	}
	
	public boolean isRecepcionConfirmada() {
		return recepcionConfirmada;
	}
	
	public void setRecepcionConfirmada(boolean recepcionConfirmada) {
		this.recepcionConfirmada = recepcionConfirmada;
	}
	
	public ProveedorDTO getProveedor() {
		return proveedor;
	}
	
	public void setProveedor(ProveedorDTO proveedor) {
		this.proveedor = proveedor;
	}
	
	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}
	
	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
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
	
	public List<ImportacionFacturaDetalleDTO> getDetalles() {
		return detalles;
	}
	
	public void setDetalles(List<ImportacionFacturaDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public double getPorcProrrateo() {
		return porcProrrateo;
	}

	public void setPorcProrrateo(double porcProrrateo) {
		this.porcProrrateo = porcProrrateo;
	}
}
