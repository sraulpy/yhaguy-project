package com.yhaguy.gestion.empresa.ctacte;

import java.util.Date;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SoloViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.gestion.empresa.EmpresaControlBody;
import com.yhaguy.gestion.empresa.EmpresaDTO;

public class CtaCteEmpresaMasInforSimpleControl  extends SoloViewModel{
	private EmpresaControlBody dato;	

	@AfterCompose(superclass = true)
	public void afterCtaCteEmpresaMovimientoSimpleControl() {
	}

	@Init(superclass = true)
	public void initCtaCteEmpresaMovimientoSimpleControl(
			@ExecutionArgParam("dato") EmpresaControlBody dato) {
		this.dato=  dato;
	}
	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_CTA_CTE_EMPRESA_MAS_INFORMACION;
	}

	public EmpresaControlBody getDato() {
		return dato;
	}

	public void setDato(EmpresaControlBody dto) {
		this.dato = dto;
	}
	
	/**
	 * Obtener el monto de la linea de credito del Cliente
	 * @return
	 */
	public double getLineaCredito(){
		double lineaCredito = 0;
		MyArray linea = dato.getControlCtaCte().getLineaCreditoCliente(dato.getDtoEmp());
		lineaCredito = (double)linea.getPos2();
		return lineaCredito;
	}
	
	/**
	 * Obtener el credito desponible en la cuenta del Cliente
	 * @return
	 * @throws Exception
	 */
	public double getLineaCreditoDisponible() throws Exception{
		double lineaCreditoDisponible = dato.getControlCtaCte().getCreditoDisponibleLineaCreditoCliente(dato.getDtoEmp());
		return lineaCreditoDisponible;
	}
	
	/**
	 * Obtener el saldo pendiente de la Cta. Cte. ya sea de 
	 * Clientes o Proveedores.
	 * Obs.: Si el saldo es negativo en el caso de la Cta. Cte. sea
	 * de un cliente es a favor de dicho cliente. En cambio si el saldo es negativo
	 * pero en la Cta. Cte. de un proveedor entonces es saldo a favor de
	 * la empresa y no del proveedor
	 * @return
	 * @throws Exception
	 */
	
	public double getSaldoPendienteCtaCteEmpresa() throws Exception {
		double	saldo = dato.getControlCtaCte().getSaldoPendienteEmpresa(
				dato.getDtoEmp(),
				dato.getCaracterMovimientoPorTipoControlInstanciado());
		
		if(saldo > 0)
			return saldo;
		
		return 0;
	}
	
	/**
	 * Obtiene el saldo a favor del cliente o la empresa.
	 * Obs.: Si el saldo es negativo en el caso de la Cta. Cte. sea
	 * de un cliente es a favor de dicho cliente. En cambio si el saldo es negativo
	 * pero en la Cta. Cte. de un proveedor entonces es saldo a favor de
	 * la empresa y no del proveedor.
	 * @return
	 * @throws Exception
	 */
	public double getSaldoFavorCtaCteEmpresa() throws Exception{
		double	saldo = dato.getControlCtaCte().getSaldoPendienteEmpresa(
				dato.getDtoEmp(),
				dato.getCaracterMovimientoPorTipoControlInstanciado());
		
		if(saldo < 0 )
			return saldo * -1;
		
		return 0;
		
	}
	
}