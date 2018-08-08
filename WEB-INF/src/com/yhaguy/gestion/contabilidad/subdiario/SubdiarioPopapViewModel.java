package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.AutonumeroYhaguy;
import com.yhaguy.Configuracion;

public class SubdiarioPopapViewModel extends SoloViewModel {

	private SubdiarioSimpleControl dato;
	String mensaje = "";
	private SubDiarioDetalleDTO subDiarioBorrar;
	private double balanceado;
	
	@Init(superclass = true)
	public void initSubdiarioPopapViewModel(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) SubdiarioSimpleControl dato)
			throws Exception {
		this.dato = dato;
		this.balanceado= 0;
		this.numeroProvisorio();
		this.calcularBalanceo(this.dato.getSubDiarioNuevoEditar().getDetalles());
	}

	@AfterCompose(superclass = true)
	public void afterComposeSubdiarioPopapViewModel() {

	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	@NotifyChange("*")
	@Command
	public void numeroProvisorio() throws Exception {

		if (this.dato.getSubDiarioNuevoEditar().esNuevo() == true) {

			String numero = AutonumeroYhaguy.getNumeroSubDiarioProvisorio(
					this.dato.getSubDiarioNuevoEditar().getSucursal(),
					(Date) this.dato.getSubDiarioNuevoEditar().getFecha());
			this.dato.getSubDiarioNuevoEditar().setNumero(numero);
		}

	}

	@NotifyChange("*")
	@Command
	public void agregarSubDiarioDetalle() throws Exception {
		this.dato.setSubDiarioDetalleNuevo(new SubDiarioDetalleDTO());
		this.dato.setDebe(false);
		this.dato.setHaber(false);
		String pathPopapZul = "/yhaguy/gestion/contabilidad/subdiario/subDiarioDetallesPopup.zul";
		WindowPopup wp = new WindowPopup();
		wp.setTitulo("Nuevo Subdiario Detalle");
		wp.setModo(WindowPopup.NUEVO);
		wp.setDato(this.dato);
		wp.setWidth("400px");
		wp.setHigth("300px");
		wp.setCheckAC(new SubdiarioDetalleNuevoVerificaAceptarCancelar(
				this.dato.getSubDiarioDetalleNuevo()));
		wp.show(pathPopapZul);

		if (wp.isClickAceptar()) {
			this.dato.getSubDiarioDetalleNuevo().setImporte(
					this.dato.getSubDiarioDetalleNuevo().getImporte()
							* this.dato.getSubDiarioDetalleNuevo().getTipo());
			this.dato.getSubDiarioNuevoEditar().getDetalles()
					.add(this.dato.getSubDiarioDetalleNuevo());
			this.calcularBalanceo(this.dato.getSubDiarioNuevoEditar().getDetalles());
					

		}
	}

	@NotifyChange("*")
	@Command
	public void borrarSubDiarioDetalle() {

		if (this.subDiarioBorrar != null) {
			
			if (this.dato.getSubDiarioNuevoEditar().getDetalles()
					.remove(this.subDiarioBorrar) == true) {
				this.calcularBalanceo(this.dato.getSubDiarioNuevoEditar().getDetalles());
			}
		} else {
			this.mensajeInfo("Debe de seleccionar un item para borrar!");
		}

	}
	
	public void calcularBalanceo(List<SubDiarioDetalleDTO> lista){
		this.balanceado=0;
		if(lista.size() > 0){
			for(SubDiarioDetalleDTO sd : lista){
				this.balanceado += sd.getImporte();
			}
		}
	}

	public boolean verificarSubDiarioDetalle() {
		boolean cargar = true;
		this.mensaje = "";
		if (this.dato.getSubDiarioDetalleNuevo().getDescripcion() == "") {
			this.mensaje = this.mensaje + "\n\n - Añadir una descripción.";
			cargar = false;
		}
		if (this.dato.getSubDiarioDetalleNuevo().getCuenta().toString()
				.length() == 1) {
			this.mensaje = this.mensaje + "\n - Seleccionar una cuenta.";
			cargar = false;
		}
		if (this.dato.getSubDiarioDetalleNuevo().getTipo() == 0) {
			this.mensaje = this.mensaje + "\n - Asignar el tipo.";
			cargar = false;
		}
		if (this.dato.getSubDiarioDetalleNuevo().getImporte() == 0) {
			this.mensaje = this.mensaje + "\n - Añadir el importe.";
			cargar = false;
		}
		if (!cargar) {
			this.mensaje = "Verificar las siguientes anotaciones:"
					+ this.mensaje;
		}

		return cargar;
	}

	// GET and SET

	public SubdiarioSimpleControl getDato() {
		return dato;
	}

	public void setDato(SubdiarioSimpleControl dato) {
		this.dato = dato;
	}

	public SubDiarioDetalleDTO getSubDiarioBorrar() {
		return subDiarioBorrar;
	}

	public void setSubDiarioBorrar(SubDiarioDetalleDTO subDiarioBorrar) {
		this.subDiarioBorrar = subDiarioBorrar;
	}

	public double getBalanceado() {
		return balanceado;
	}

	public void setBalanceado(double balanceado) {
		this.balanceado = balanceado;
	}
	
	

}

class SubdiarioDetalleNuevoVerificaAceptarCancelar implements
		VerificaAceptarCancelar {

	String mensaje = "";

	SubDiarioDetalleDTO subDiarioDetalle = null;

	public SubdiarioDetalleNuevoVerificaAceptarCancelar(
			SubDiarioDetalleDTO subDiarioDetalle) {
		this.subDiarioDetalle = subDiarioDetalle;
	}

	@Override
	public boolean verificarAceptar() {

		this.mensaje = "Verificar las siguientes anotaciones:";
		boolean cargar = true;
		if (this.subDiarioDetalle.getDescripcion() == "") {
			this.mensaje += "\n\n - Añadir una descripción.";
			cargar = false;
		}
		if (this.subDiarioDetalle.getCuenta().toString().length() == 1) {
			this.mensaje += "\n - Seleccionar una cuenta.";
			cargar = false;
		}
		if (this.subDiarioDetalle.getTipo() == 0) {
			this.mensaje += "\n - Asignar el tipo.";
			cargar = false;
		}
		if (this.subDiarioDetalle.getImporte() == 0) {
			this.mensaje += "\n - Añadir el importe.";
			cargar = false;
		}

		return cargar;
	}

	@Override
	public String textoVerificarAceptar() {
		// TODO Auto-generated method stub
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		// TODO Auto-generated method stub
		return null;
	}

}
