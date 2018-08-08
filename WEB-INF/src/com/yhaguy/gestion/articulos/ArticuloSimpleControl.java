package com.yhaguy.gestion.articulos;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.SoloViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.gestion.empresa.AssemblerProveedor;
import com.yhaguy.gestion.empresa.ProveedorDTO;

public class ArticuloSimpleControl extends SoloViewModel {

	static String[] attProveedor = { "empresa.codigoEmpresa",
			"empresa.razonSocial", "empresa.ruc" };
	static String[] columnas = { "Código", "Razón Social", "Ruc" };
	
	private ArticuloControlBody dato;	

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) ArticuloControlBody dato){
		this.dato = dato;
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return this.dato.getAliasFormularioCorriente();
	}
	
	@Command
	@NotifyChange("*")
	public void buscarProveedor() throws Exception {
		BuscarElemento be = new BuscarElemento();
		be.setTitulo("Buscar Proveedor");
		be.setClase(Proveedor.class);
		be.setAtributos(attProveedor);
		be.setNombresColumnas(columnas);
		be.setAnchoColumnas(new String[]{"100px", "", "100px"});
		be.setWidth("750px");
		be.setAssembler(new AssemblerProveedor());
		be.show("");		
		if (be.isClickAceptar()) {
			ProveedorDTO prov = (ProveedorDTO) be.getSelectedItemDTO();
			this.dato.getNvoProveedorArticulo().setProveedor(prov);
		}
	}		
	
	/*********************** GET/SET ***********************/
	
	public ArticuloControlBody getDato() {
		return dato;
	}

	public void setDato(ArticuloControlBody dato) {
		this.dato = dato;
	}	
}
