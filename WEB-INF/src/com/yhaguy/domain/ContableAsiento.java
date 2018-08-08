package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ContableAsiento extends Domain {

	private Date fecha;
	private String numero;
	private String descripcion;

	private Set<ContableAsientoDetalle> detalles = new HashSet<ContableAsientoDetalle>();

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return el total del debe..
	 */
	public double getTotalDebe() {
		double out = 0;
		for (ContableAsientoDetalle det : this.detalles) {
			out += det.getDebe();
		}
		return out;
	}
	
	/**
	 * @return el total del haber..
	 */
	public double getTotalHaber() {
		double out = 0;
		for (ContableAsientoDetalle det : this.detalles) {
			out += det.getHaber();
		}
		return out;
	}
	
	public List<ContableAsientoDetalle> getDetallesOrdenadoPorDebe() {
		List<ContableAsientoDetalle> detail = new ArrayList<ContableAsientoDetalle>();
		detail.addAll(this.detalles);
		Collections.sort(detail, new OrdenarDetalles(OrdenarDetalles.ORDENAR_POR_DEBE));
		return detail;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<ContableAsientoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<ContableAsientoDetalle> detalles) {
		this.detalles = detalles;
	}
	
	/**
	 * ordena los detalles..
	 */
	class OrdenarDetalles implements Comparator<ContableAsientoDetalle> {
		
		static final int ORDENAR_POR_NROLINEA = 1;
		static final int ORDENAR_POR_DEBE = 2;
		
		int orden;
		
		public OrdenarDetalles(int orden) {
			this.orden = orden;
		}

		@Override
		public int compare(ContableAsientoDetalle o1, ContableAsientoDetalle o2) {			
			int nro1 = o1.getDebe() > 0? 1:0;
			int nro2 = o2.getDebe() > 0? 1:0;
			return nro2 - nro1;
		}
		
	}
}
