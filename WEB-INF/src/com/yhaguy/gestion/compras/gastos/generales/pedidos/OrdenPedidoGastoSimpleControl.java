package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.SoloViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.gestion.compras.gastos.subdiario.ArticuloGastoDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.AssemblerArticuloGasto;

public class OrdenPedidoGastoSimpleControl extends SoloViewModel {
	
	static final String[] ATRIBUTOS = {"cuentaContable.codigo", "cuentaContable.descripcion"};
	static final String[] ANCHOS = {"120px", ""};
	static final String[] COLUMNAS = {"Código", "Descripción"};

	private OrdenPedidoGastoControlBody dato;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) 
			OrdenPedidoGastoControlBody dato) {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_ORDEN_PEDIDO_GASTO_ABM;
	}

	/********************************************************/

	/**
	 * Seleccion del item de gasto..
	 */
	@Command
	@NotifyChange("*")
	public void selectItemDeGasto(@BindingParam("filtro") int filtro)
			throws Exception {
		// esto es porque se accede de dos archivos .zul a este comando..
		int filtro_ = filtro;
		if(filtro > 1)
			filtro_ = filtro - 2;
		
		BuscarElemento b = new BuscarElemento();
		b.setClase(ArticuloGasto.class);
		b.setAssembler(new AssemblerArticuloGasto());
		b.setAtributos(ATRIBUTOS);
		b.setAnchoColumnas(ANCHOS);
		b.setNombresColumnas(COLUMNAS);
		b.setTitulo("Ítems de Gastos");
		b.setWidth("800px");
		b.show(this.getFiltro(filtro), filtro_);
		if (b.isClickAceptar()) {
			if(filtro > 1) {
				this.dato.getGastoDetalle().setArticuloGasto(
						(ArticuloGastoDTO) b.getSelectedItemDTO());
			} else {
				this.dato.getNvoDetalle().setArticuloGasto(
						(ArticuloGastoDTO) b.getSelectedItemDTO());
			}			
		}
	}
	
	
	/********************************************************/
	
	/**
	 * @return el valor del filtro..
	 */
	private String getFiltro(int filtro) {
		String codigo = "";
		String descripcion = "";
		
		if (filtro > 1) {
			
			codigo = (String) this.dato.getGastoDetalle().getArticuloGasto()
					.getCuentaContable().getPos1();
			descripcion = (String) this.dato.getGastoDetalle().getArticuloGasto()
					.getCuentaContable().getPos2();
		} else {
			
			codigo = (String) this.dato.getNvoDetalle().getArticuloGasto()
					.getCuentaContable().getPos1();
			descripcion = (String) this.dato.getNvoDetalle().getArticuloGasto()
					.getCuentaContable().getPos2();
		}		
		
		switch (filtro) {
		case 0:
			return codigo;
		case 1:
			return descripcion;
		case 2:
			return codigo;
		case 3:
			return descripcion;
		}
		return "";
	}
	
	public OrdenPedidoGastoControlBody getDato() {
		return dato;
	}

	public void setDato(OrdenPedidoGastoControlBody dato) {
		this.dato = dato;
	}

}
