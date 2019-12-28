package com.yhaguy.gestion.reparto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RepartoEntrega;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;

@SuppressWarnings("serial")
public class RepartoDetalleDTO extends DTO {

	private Long idMovimiento;
	private String observacion = "";
	private double peso = 0;
	private double importeGs = 0;
	private MyArray tipoMovimiento = new MyArray();
 
	private MyArray detalle = new MyArray();
	private boolean entregado = true;
	
	private List<RepartoEntrega> entregas = new ArrayList<RepartoEntrega>();
	
	/**
	 * @return si es venta o no segun la sigla..
	 */
	public boolean isVenta() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_PEDIDO_VENTA)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
	}
	
	/**
	 * add entregas..
	 */
	public void addEntregas() throws Exception {
		if (this.isVenta()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			Venta vta = (Venta) rr.getObject(Venta.class.getName(), this.getIdMovimiento());
			if (vta != null) {
				for (VentaDetalle item : vta.getDetalles()) {
					RepartoEntrega ent = new RepartoEntrega();
					ent.setDetalle(item);
					ent.setCantidad(ent.getSaldo());
					this.getEntregas().add(ent);
				}
			}
		}		
	}
	
	@DependsOn("entregado")
	public String getStyle() {
		String style = "text-decoration: line-through; " + Misc.BACKGROUND_NARANJA;
		return this.isEntregado()? "" : style;
	}

	public Long getIdMovimiento() {
		return idMovimiento;
	}

	public void setIdMovimiento(Long idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyArray getDetalle() {
		return detalle;
	}

	public void setDetalle(MyArray datosDetalle) {
		this.detalle = datosDetalle;
	}

	public boolean isEntregado() {
		return entregado;
	}

	public void setEntregado(boolean entregado) {
		this.entregado = entregado;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public List<RepartoEntrega> getEntregas() {
		return entregas;
	}
	
	public Set<RepartoEntrega> getEntregas_() {
		Set<RepartoEntrega> out = new HashSet<RepartoEntrega>();
		out.addAll(this.entregas);
		return out;
	}

	public void setEntregas(List<RepartoEntrega> entregas) {
		this.entregas = entregas;
	}
}
