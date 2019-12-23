package com.yhaguy.domain;

import java.util.List;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class RepartoEntrega extends Domain {

	private long cantidad;
	
	private VentaDetalle detalle;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return el saldo..
	 */
	public long getSaldo() throws Exception {
		long sum = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> det = rr.getRepartoEntregas(this.detalle.getId());
		for (Object[] item : det) {
			long ent = (long) item[1];
			sum += ent;
		}
		return this.detalle.getCantidad() - sum;
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
	}

	public VentaDetalle getDetalle() {
		return detalle;
	}

	public void setDetalle(VentaDetalle detalle) {
		this.detalle = detalle;
	}
}
