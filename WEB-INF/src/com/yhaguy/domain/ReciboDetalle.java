package com.yhaguy.domain;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.coreweb.domain.Domain;
import com.yhaguy.util.ConnectDB;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ReciboDetalle extends Domain {
	
	public static final String TIPO_DIF_CAMBIO = "DIF.T.C.";
	public static final String TIPO_CTA_CONTABLE = "C.T.";
	
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
	 * @return venta.sucursal.id
	 */
	public long getIdSucursalVenta() throws Exception {
		if (this.movimiento == null) {
			return 0;
		}
		if (this.venta != null) {
			return venta.getSucursal().getId();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Object[] vta = rr.getVentaLazy(this.movimiento.getIdMovimientoOriginal());
		long out = vta != null ? (long) vta[3] : 0;
		return out;
	}
	
	/**
	 * @return los codigos de items de venta migrada.. 
	 */
	public List<Object[]> getDetalleVentaMigracion() throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		if (this.getVenta() != null) {
			return out;
		}
		if (this.movimiento != null) {
			ConnectDB conn = ConnectDB.getInstance();
			ResultSet result = conn.getDetalleMovimiento(this.movimiento.getIdMovimientoOriginal() + "");
			if (result != null) {
				while (result.next()) {
					String cod = (String) result.getObject(1);
					double importe = (double) result.getObject(2);
					out.add(new Object[]{ cod, importe });
				}
			}
		}		
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
	 * @return el importe segun sucursal..
	 */
	public double getImporteBySucursalGs(long idSucursal) throws Exception {
		long idSucVta = this.getIdSucursalVenta();
		if (idSucVta == idSucursal) {
			return this.getMontoGs();
		}
		return 0;
	}
	
	/**
	 * @return el importe segun sucursal..
	 */
	public double getImporteBySucursalDs(long idSucursal) throws Exception {
		long idSucVta = this.getIdSucursalVenta();
		if (idSucVta == idSucursal) {
			return this.getMontoDs();
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
