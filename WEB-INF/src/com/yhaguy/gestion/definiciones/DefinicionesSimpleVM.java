package com.yhaguy.gestion.definiciones;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;

@SuppressWarnings("unchecked")
public class DefinicionesSimpleVM extends SoloViewModel {

	private DefinicionesViewModel dato;

	private MyPair selectedTipoMovimiento;
	private MyArray selectedBanco;

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) DefinicionesViewModel dato) {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	/******************* COMANDOS ********************/

	@Command
	@NotifyChange("*")
	public void abrirVentanaTimbrado() {

		WindowTimbrado w = new WindowTimbrado();
		w.setIdProveedor(Configuracion.ID_PROVEEDOR_YHAGUY_MRA);
		w.setTimbrado("%");
		w.show(WindowPopup.NUEVO, w);

		if (w.isClickAceptar()) {
			this.dato.getSelectedTalonario().setPos6(w.getSelectedTimbrado());
			this.dato.getSelectedTalonario().setPos7(w.getSelectedTimbrado().getPos2());
		}

		BindUtils.postNotifyChange(null, null, this.dato.getSelectedTalonario(), "*");
	}

	@Command
	@NotifyChange("*")
	public void selectTipoMovimiento() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(TipoMovimiento.class);
		b.setAtributos(new String[] { "descripcion" });
		b.setTitulo("Tipos de Movimientos");
		b.setNombresColumnas(new String[] { "Descripción" });
		b.addWhere("tipoComprobante.sigla = '" + Configuracion.SIGLA_TIPO_COMPROBANTE_LEGAL + "'");
		b.show("%");
		if (b.isClickAceptar()) {
			MyArray tm = b.getSelectedItem();
			MyPair _tm = new MyPair(tm.getId(), (String) tm.getPos1());
			List<MyPair> tms = (List<MyPair>) this.dato.getSelectedTalonario().getPos10();
			tms.add(_tm);
		}
	}

	@Command
	@NotifyChange("*")
	public void deleteTipoMovimiento() {
		List<MyPair> tms = (List<MyPair>) this.dato.getSelectedTalonario().getPos10();
		tms.remove(this.selectedTipoMovimiento);
		this.selectedTipoMovimiento = null;
	}

	@Command
	public void validarDesde(@BindingParam("comp") Component comp) {
		long desde = (long) this.dato.getSelectedTalonario().getPos4();
		long hasta = (long) this.dato.getSelectedTalonario().getPos5();
		if (desde >= hasta && hasta > 0) {
			this.dato.getSelectedTalonario().setPos4((long) 0);
			Clients.showNotification("Valor no válido..", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 2000);
		}
		BindUtils.postNotifyChange(null, null, this.dato.getSelectedTalonario(), "pos4");
	}

	@Command
	public void validarHasta(@BindingParam("comp") Component comp) {
		long desde = (long) this.dato.getSelectedTalonario().getPos4();
		long hasta = (long) this.dato.getSelectedTalonario().getPos5();
		if (hasta <= desde) {
			this.dato.getSelectedTalonario().setPos5((long) 0);
			Clients.showNotification("Valor no válido..", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 2000);
		}
		BindUtils.postNotifyChange(null, null, this.dato.getSelectedTalonario(), "pos5");
	}

	/*************************************************/

	/******************** GET/SET ********************/

	public DefinicionesViewModel getDato() {
		return dato;
	}

	public void setDato(DefinicionesViewModel dato) {
		this.dato = dato;
	}

	public MyPair getSelectedTipoMovimiento() {
		return selectedTipoMovimiento;
	}

	public void setSelectedTipoMovimiento(MyPair selectedTipoMovimiento) {
		this.selectedTipoMovimiento = selectedTipoMovimiento;
	}

	@Command
	public void copiarDescricionBanco() throws Exception {
		this.dato.getSelectedBanco().setPos1(((MyPair) this.dato.getSelectedBanco().getPos7()).getText());
	}

	public MyArray getSelectedBanco() {
		return selectedBanco;
	}

	public void setSelectedBanco(MyArray selectedBanco) {
		this.selectedBanco = selectedBanco;
	}

}
