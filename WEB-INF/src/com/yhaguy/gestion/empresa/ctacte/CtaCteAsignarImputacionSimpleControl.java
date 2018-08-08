package com.yhaguy.gestion.empresa.ctacte;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Doublebox;

import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.empresa.EmpresaControlBody;

public class CtaCteAsignarImputacionSimpleControl extends SoloViewModel {

	EmpresaControlBody empCtrlBdy;
	
	CtaCteEmpresaMovimientoSimpleControl movCtrlBdy;
	//Lista movimientos a imputar
	private List<CtaCteEmpresaDetalleImputacion> movimientosPendientes = new ArrayList<CtaCteEmpresaDetalleImputacion>();
	//Movimientos a imputar seleccionados
	private List<CtaCteEmpresaDetalleImputacion> selectedMovs = new ArrayList<CtaCteEmpresaDetalleImputacion>();
	
	//Monto total aplicable
	private double montoAplicableMonedaOriginal = 0;
	private double montoAplicableMonedaLocal = 0;
	
	//Monto total imputaciones aplicadas
	private double impTotalMonedaOriginal = 0;
	private double impTotalMonedaLocal = 0;

	@AfterCompose(superclass = true)
	public void afterCtaCteEmpresaMovimientoSimpleControl(){
	}

	@Init(superclass = true)
	public void initCtaCteEmpresaMovimientoSimpleControl(
			@ExecutionArgParam("dato") CtaCteEmpresaMovimientoSimpleControl dato)
			throws Exception {
		this.movCtrlBdy = dato;
		this.empCtrlBdy = dato.empCtrlBdy;
		this.movCtrlBdy.setAsigImpSimCtrl(this);;
		this.montoAplicableMonedaOriginal = this.getDto().getSaldo();
		this.montoAplicableMonedaLocal = this.montoAplicableMonedaOriginal * this.getTipoCambio();
		this.buscarMovimientosPendientes();
	}

	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_CTA_CTE_EMPRESA_ASIGNAR_IMPUTACION;
	}

	public CtaCteEmpresaMovimientoDTO getDto() {
		return this.empCtrlBdy.getSelectedMov();
	}

	public void setDto(CtaCteEmpresaMovimientoDTO dto) {
		this.empCtrlBdy.setSelectedMov(dto);
	}

	public List<CtaCteEmpresaDetalleImputacion> getMovimientosPendientes() {
		return movimientosPendientes;
	}

	public void setMovimientosPendientes(
			List<CtaCteEmpresaDetalleImputacion> movimientosPendientes) {
		this.movimientosPendientes = movimientosPendientes;
	}

	public List<CtaCteEmpresaDetalleImputacion> getSelectedMovs() {
		return selectedMovs;
	}

	public void setSelectedMovs(
			List<CtaCteEmpresaDetalleImputacion> selectedMovs) {
		this.selectedMovs = selectedMovs;
	}

	/**
	 * Tipo de cambio correspondiente a la moneda utilizada en el movimiento "quienImputa"
	 * Si la empresa(Tercero) realizo la operacion del movimiento en caracter de cliente
	 * entonces se utiliza el tipo de cambio compraBCP y si de lo contrario el tercero
	 * realizo la operacion en caracter de proveedor entonces se utiliza el tipo de cambio
	 * ventaBCP
	 * @return
	 * @throws Exception
	 */
	public double getTipoCambio() throws Exception {
		double cambio = 1;
		
		MyPair caracter = this.empCtrlBdy.getCaracterMovimientoPorTipoControlInstanciado();
		
		if(caracter.compareTo(empCtrlBdy.getDtoUtil().getCtaCteEmpresaCaracterMovCliente()) == 0)
			cambio =  empCtrlBdy.getDtoUtil().getCambioCompraBCP(this.getDto().getMoneda());
		
		else if(caracter.compareTo(empCtrlBdy.getDtoUtil().getCtaCteEmpresaCaracterMovProveedor()) == 0)
			cambio =  empCtrlBdy.getDtoUtil().getCambioVentaBCP(this.getDto().getMoneda());
	
		return cambio;
	}

	public double getMontoAplicableMonedaOriginal() {
		return montoAplicableMonedaOriginal;
	}

	public void setMontoAplicableMonedaOriginal(double montoAplicableMonedaOriginal) {
		this.montoAplicableMonedaOriginal = montoAplicableMonedaOriginal;
	}
	
	public double getMontoAplicableMonedaLocal() {
		return montoAplicableMonedaLocal;
	}

	public void setMontoAplicableMonedaLocal(double montoAplicableMonedaLocal) {
		this.montoAplicableMonedaLocal = montoAplicableMonedaLocal;
	}

	public double getImpTotalMonedaOriginal() {
		return impTotalMonedaOriginal;
	}

	public void setImpTotalMonedaOriginal(double impTotalMonedaOriginal) {
		this.impTotalMonedaOriginal = impTotalMonedaOriginal;
	}

	public double getImpTotalMonedaLocal() {
		return impTotalMonedaLocal;
	}

	public void setImpTotalMonedaLocal(double impTotalMonedaLocal) {
		this.impTotalMonedaLocal = impTotalMonedaLocal;
	}

	/**
	 * Metodo utilizado para recargar los datos correspondiente al dato
	 * seleccionado.
	 * 
	 * @throws Exception
	 */
	public void recargarMovimiento() throws Exception {
		long idMovimiento = empCtrlBdy.getSelectedMov().getId();
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento movimientoDom = rr
				.getCtaCteEmpresaMovimientoById(idMovimiento);
		CtaCteEmpresaMovimientoDTO movimientoDto = (CtaCteEmpresaMovimientoDTO) new AssemblerCtaCteEmpresaMovimiento()
				.domainToDto(movimientoDom);
		empCtrlBdy.setSelectedMov(movimientoDto);
	}

	/**
	 * Obtiene los movimientos pendientes a los cuales seran aplicadas
	 * las operaciones correspondientes(En este caso asignar imputacion anticipo)
	 * @throws Exception
	 */
	private void buscarMovimientosPendientes() throws Exception {

		this.movimientosPendientes = new ArrayList<CtaCteEmpresaDetalleImputacion>();
		List<CtaCteEmpresaMovimientoDTO> pendientes = empCtrlBdy
				.getControlCtaCte()
				.getCtaCteEmpresaMovimientosPendientes(
						this.empCtrlBdy.getDtoEmp(),
						this.empCtrlBdy.getCaracterMovimientoPorTipoControlInstanciado(),
						(long) 1);

		for (CtaCteEmpresaMovimientoDTO pendiente : pendientes) {

			CtaCteEmpresaDetalleImputacion detalleImp = new CtaCteEmpresaDetalleImputacion();

			if (pendiente.getMoneda().getSigla()
					.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0) {

				// detalleImp.setTipoCambio(empCtrlBdy.getDtoUtil().getCambioCompraBCP(pendiente.getMoneda()));

				if (pendiente.getTipoCaracterMovimiento().compareTo(
						empCtrlBdy.getDtoUtil()
								.getCtaCteEmpresaCaracterMovCliente()) == 0)
					detalleImp.setTipoCambio(empCtrlBdy.getDtoUtil()
							.getCambioCompraBCP(pendiente.getMoneda()));

				else if (pendiente.getTipoCaracterMovimiento().compareTo(
						empCtrlBdy.getDtoUtil()
								.getCtaCteEmpresaCaracterMovProveedor()) == 0)
					detalleImp.setTipoCambio(empCtrlBdy.getDtoUtil()
							.getCambioVentaBCP(pendiente.getMoneda()));

				detalleImp.setSaldoGs(pendiente.getSaldo());
				detalleImp.setSaldoDs(pendiente.getSaldo()
						/ detalleImp.getTipoCambio());
				detalleImp.setStyleSaldoGs("font-weight:bold");
				detalleImp.setFormat(this.getCnv().getMonedaLocal());
			} else {
				// detalleImp.setTipoCambio(empCtrlBdy.getDtoUtil().getCambioCompraBCP(pendiente.getMoneda()));

				if (pendiente.getTipoCaracterMovimiento().compareTo(
						empCtrlBdy.getDtoUtil()
								.getCtaCteEmpresaCaracterMovCliente()) == 0)
					detalleImp.setTipoCambio(empCtrlBdy.getDtoUtil()
							.getCambioCompraBCP(pendiente.getMoneda()));

				else if (pendiente.getTipoCaracterMovimiento().compareTo(
						empCtrlBdy.getDtoUtil()
								.getCtaCteEmpresaCaracterMovProveedor()) == 0)
					detalleImp.setTipoCambio(empCtrlBdy.getDtoUtil()
							.getCambioVentaBCP(pendiente.getMoneda()));

				detalleImp.setSaldoDs(pendiente.getSaldo());
				detalleImp.setSaldoGs(pendiente.getSaldo()
						* detalleImp.getTipoCambio());
				detalleImp.setStyleSaldoDs("font-weight:bold");
				detalleImp.setFormat(this.getCnv().getMonedaExtranjera());
			}
			
			detalleImp.setMovimiento(pendiente);
			this.movimientosPendientes.add(detalleImp);
		}
	}

	/**
	 * Habilita la edicion de montos cuando el movimiento de la lista es 
	 * seleccionado
	 */
	@Command
	@NotifyChange("datosMovimientosPendientes")
	public void habilitarMontos() {
		for (CtaCteEmpresaDetalleImputacion item : this.movimientosPendientes) {
			if (this.selectedMovs.contains(item) == true) {
				item.setSelected(true);
			} else {
				item.setMontoGs(0);
				item.setMontoDs(0);
				item.setSelected(false);
			}
			BindUtils.postNotifyChange(null, null, item, "selected");
			BindUtils.postNotifyChange(null, null, item, "montoGs");
			BindUtils.postNotifyChange(null, null, item, "montoDs");
		}
	}

	@Command
	@NotifyChange({ "datosMovimientosPendientes", "cancelarTodo" })
	public void cambiarAmonedaLocal(
			@BindingParam("item") CtaCteEmpresaDetalleImputacion item,
			@BindingParam("comp") Doublebox comp) {
		double tc = item.getTipoCambio();
		double montoDs = item.getMontoDs();
		double montoGs = montoDs * tc;
		double saldoGs = item.getSaldoGs();

		if (montoGs > saldoGs) {
			item.setMontoDs(item.getMontoGs() / tc);
			m.mensajePopupTemporal("El monto ingresado supera al saldo..","error", comp);
			
		} else {
			item.setMontoGs(montoGs);
		}
		BindUtils.postNotifyChange(null, null, item, "montoGs");
	}

	@Command
	@NotifyChange({ "datosMovimientosPendientes", "cancelarTodo" })
	public void cambiarAmonedaExtranjera(
			@BindingParam("item") CtaCteEmpresaDetalleImputacion item,
			@BindingParam("comp") Doublebox comp) {
		double montoGs = item.getMontoGs();
		double saldoGs = item.getSaldoGs();
		double tc = item.getTipoCambio();

		if (montoGs > saldoGs) {
			
			item.setMontoGs(item.getMontoDs() * tc);
			m.mensajePopupTemporal("El monto ingresado supera al saldo..","error", comp);
			
		}else{
			
			item.setMontoDs(montoGs / tc);
	
		}
		BindUtils.postNotifyChange(null, null, item, "montoDs");
	}

	public void actualizarSaldosMovimientosCtaCte(CtaCteEmpresaDetalleImputacion detalleImp) {
		
		double importe = 0;
		double saldo = 0;

		if (detalleImp.getMovimiento().getMoneda().getSigla().compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0) {
			importe = detalleImp.getMontoGs();
		} else {
			importe = detalleImp.getMontoDs();
		}
		
		saldo = detalleImp.getMovimiento().getSaldo() - importe;
		detalleImp.getMovimiento().setSaldo(saldo);
	}

	@Command
	public void actualizarCtaCte() throws Exception {
		
		this.calcularImpTotalMonedaLocal();
		this.calcularImpTotalMonedaOriginal();
		
		if(this.impTotalMonedaOriginal > (this.montoAplicableMonedaOriginal*-1)){
			
			this.mensajeError("El monto total supera al monto aplicable \nNo se han aplicado los cambios");
			return;
		}
			
		this.getDto().setSaldo(this.montoAplicableMonedaOriginal + this.impTotalMonedaOriginal);
	
		for (CtaCteEmpresaDetalleImputacion imputaciones : selectedMovs) {
			actualizarSaldosMovimientosCtaCte(imputaciones);
		}
		
		this.empCtrlBdy.getControlCtaCte().asignarImputacion(
				this.empCtrlBdy.getDtoEmp(), this.getDto(),
				this.getSelectedMovs());
		
		this.mensajePopupTemporal("Imputacion Aplicada");

	}
	
	/**
	 *	Calcula el total de las imputaciones aplicadas en Gs.
	 */
	public void calcularImpTotalMonedaLocal(){
		double importeTotalGs = 0;
		for (CtaCteEmpresaDetalleImputacion imputaciones : selectedMovs) {
			importeTotalGs += imputaciones.getMontoGs();
		}
		this.impTotalMonedaLocal =  importeTotalGs;
	}
	
	/**
	 *	Calcula el total de las imputaciones aplicadas en la moneda del
	 *	movimiento "quienImputa".
	 */
	public void calcularImpTotalMonedaOriginal() throws Exception{
		this.impTotalMonedaOriginal =  this.impTotalMonedaLocal / this.getTipoCambio();
	}
	
	public Object[] getDatosMovimientosPendientes() throws Exception{
	
		this.calcularImpTotalMonedaLocal();
		this.calcularImpTotalMonedaOriginal();
		
		return new Object[]{this.impTotalMonedaOriginal,this.impTotalMonedaLocal};
	}

}
