package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.compras.gastos.subdiario.ArticuloGastoDTO;

@SuppressWarnings("serial")
public class OrdenPedidoGastoDetalleDTO extends DTO {
	
	private double importe;
	private String descripcion = "SIN DESC..";
	private MyArray departamento;
	private MyArray centroCosto;	
	private MyPair iva;
	
	private ArticuloGastoDTO articuloGasto = new ArticuloGastoDTO();
	
	@DependsOn("iva")
	public boolean isIva10() {
		return this.iva.getSigla().equals(Configuracion.SIGLA_IVA_10);
	}
	
	@DependsOn("iva")
	public boolean isExenta() {
		return this.iva.getSigla().equals(Configuracion.SIGLA_IVA_EXENTO);
	}
	
	@DependsOn({"importe", "iva"})
	public double getImpuesto() {
		if(this.isExenta())
			return 0;
		return this.getMisc().calcularIVA(this.importe, this.isIva10()? 10 : 5);
	}
	
	public double getImporte() {
		return importe;
	}
	
	public void setImporte(double importe) {
		this.importe = importe;
	}

	public MyArray getDepartamento() {
		return departamento;
	}
	
	public void setDepartamento(MyArray departamento) {
		this.departamento = departamento;
	}
	
	public MyArray getCentroCosto() {
		return centroCosto;
	}
	
	public void setCentroCosto(MyArray centroCosto) {
		this.centroCosto = centroCosto;
	}

	public MyPair getIva() {
		return iva;
	}

	public void setIva(MyPair iva) {
		this.iva = iva;
	}

	public ArticuloGastoDTO getArticuloGasto() {
		return articuloGasto;
	}

	public void setArticuloGasto(ArticuloGastoDTO articuloGasto) {
		this.articuloGasto = articuloGasto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}
}
