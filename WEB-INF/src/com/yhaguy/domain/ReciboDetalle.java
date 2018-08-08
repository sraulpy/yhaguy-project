package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ReciboDetalle extends Domain {
	
	public static final String TIPO_DIF_CAMBIO = "DIF.T.C.";
	
	private String concepto;
	private double montoGs;
	private double montoDs;
	
	private CtaCteEmpresaMovimiento movimiento;
	
	private Venta venta;
	
	private boolean selected = false;

	public String getMontoGs_() {
		return Utiles.getNumberFormat(montoGs);
	}
	
	public double getMontoGs() {
		return Math.rint(montoGs * 1) / 1;
	}
	
	/**
	 * @return la venta.. 
	 */
	public Venta getVenta() throws Exception {
		if (this.movimiento == null) {
			return null;
		}
		if (this.venta != null) {
			return venta;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta out = (Venta) rr.getObject(Venta.class.getName(), this.movimiento.getIdMovimientoOriginal());
		this.venta = out;
		return out;
	}
	
	/**
	 * @return el gasto.. 
	 */
	public Gasto getGasto() throws Exception {
		if (this.movimiento != null && this.movimiento.isGasto()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			Gasto out = (Gasto) rr.getObject(Gasto.class.getName(), this.movimiento.getIdMovimientoOriginal());
			return out;
		}
		return null;
	}
	
	/**
	 * @return el importe segun el proveedor..
	 */
	public double getImporteByProveedor(long idProveedor) throws Exception {
		if (this.getVenta() != null) {
			return this.getVenta().getImporteByProveedor(idProveedor);
		}
		return 0;
	}
	
	/**
	 * @return el importe de la factura de venta aplicada..
	 */
	public double getImporteVenta() throws Exception {
		if (this.getVenta() != null) {
			return this.getVenta().getTotalImporteGs();
		}
		return 0;
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = montoDs;
	}

	public CtaCteEmpresaMovimiento getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(CtaCteEmpresaMovimiento movimiento) {
		this.movimiento = movimiento;
	}

	@Override
	public int compareTo(Object o) {
		ReciboDetalle cmp = (ReciboDetalle) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);
		if (isOk) {
			return 0;
		} else {
			return -1;
		}
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}	
}
