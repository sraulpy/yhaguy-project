package com.yhaguy.gestion.stock.inventarios;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.Articulo;

public class InventarioSimpleVM extends SoloViewModel {
	
	static final String[] ANCHOS_COL = { "120px", "120px", "120px", "" };
	static final String[] ATRIBUTOS = { "codigoInterno", "codigoProveedor",
			"codigoOriginal", "descripcion" };
	static final String[] NOMBRES_COL = { "Código Interno", "Código Proveedor",
			"Código Original", "Descripción" };

	private InventarioViewModel dato;
	
	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) InventarioViewModel dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void buscarArticulo(@BindingParam("filtro") int filtro)
			throws Exception {
		String filter = this.getFilter(filtro);
		
		BuscarElemento b = new BuscarElemento();	
		b.setClase(Articulo.class);
		b.setTitulo("Buscar Artículo");
		b.setHeight("400px");
		b.setWidth("800px");
		b.setAtributos(ATRIBUTOS);
		b.setNombresColumnas(NOMBRES_COL);
		b.setAnchoColumnas(ANCHOS_COL);		
		b.show(filter, filtro);
		if (b.isClickAceptar()) {
			this.dato.getNvoDetalle().setArticulo(b.getSelectedItem());
			this.dato.getNvoDetalle().setCostoGs(this.dato.getCostoGs(b.getSelectedItem().getId()));
		}
	}
	
	@Command
	public void asignarSigno(@BindingParam("item") InventarioDetalleDTO item) {
		if(item.getCantidad() < 0)
			item.setCantidad(item.getCantidad() * -1);
		int signo = 1;
		item.setCantidad(item.getCantidad() * signo);
		BindUtils.postNotifyChange(null, null, item, "cantidad");
	}

	/************************ GET/SET ************************/
	
	private String getFilter(int filtro) {
		MyArray articulo = this.getDato().getNvoDetalle().getArticulo();
		switch (filtro) {
		case 0:
			return (String) articulo.getPos1();

		case 1:
			return (String) articulo.getPos2();

		case 2:
			return (String) articulo.getPos3();

		case 3:
			return (String) articulo.getPos4();
		}
		return "";
	}
	
	public InventarioViewModel getDato() {
		return dato;
	}

	public void setDato(InventarioViewModel dato) {
		this.dato = dato;
	}
}
