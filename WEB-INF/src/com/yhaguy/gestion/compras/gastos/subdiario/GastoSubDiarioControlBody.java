package com.yhaguy.gestion.compras.gastos.subdiario;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;

import com.yhaguy.BodyApp;
import com.yhaguy.gestion.contabilidad.subdiario.SubDiarioDetalleDTO;

public abstract class GastoSubDiarioControlBody extends BodyApp{

	@Init(superclass=true)
	public void initGastoSubDiarioControlBody(){		
	}
	
	@AfterCompose(superclass=true)
	public void afterComposeGastoSubDiarioControlBody(){
	}
	
	public abstract void agregarSubDiariosDetalle() throws Exception;
	
	public abstract double getTotalSubDiarioDetalle();
	
	public abstract void poblarDetallesTemporales();
	
	public abstract SubDiarioDetalleDTO crearSubDiarioDetalle(
			int tipo, String descripcion, String alias, double importe, boolean editable);
	
	public abstract void confirmarSubDiario();
	
	/**
	 * Para habilitar/deshabilitar las acciones en el formulario..
	 */
	public abstract boolean isDisabled();
	
	/**
	 * Para habilitar/deshabilitar la accion confirmar sub-diario..
	 */
	public abstract boolean isConfirmarDisabled();
	
	/**
	 * Para habilitar/deshabilitar la accion eliminar item del sub-diario..
	 */
	public abstract boolean isEliminarItemDisabled();
	
}
