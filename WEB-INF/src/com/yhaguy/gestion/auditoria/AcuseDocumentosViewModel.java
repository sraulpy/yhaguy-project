package com.yhaguy.gestion.auditoria;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.domain.AcuseDocumento;
import com.yhaguy.domain.AcuseDocumentoDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

public class AcuseDocumentosViewModel extends SimpleViewModel {
	
	static final String ZUL_INSERT_ITEM = "/yhaguy/gestion/auditoria/addacuse.zul";
	static final String KEY_NRO = "ACUSE";
	
	private AcuseDocumento nvoAcuse;
	private AcuseDocumento selectedItem;
	
	private Window win;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addAcuse() throws Exception {
		this.addAcuse_();
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void openAcuse(@BindingParam("acuse") AcuseDocumento acuse,
			@BindingParam("popup") Popup popup,
			@BindingParam("comp") Component comp) {
		this.selectedItem = acuse;
		popup.open(comp, "start_before");
	}
	
	@Command
	@NotifyChange("*")
	public void setDevuelto(@BindingParam("devuelto") boolean devuelto, 
			@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedItem.setDevuelto(devuelto);
		rr.saveObject(this.selectedItem, this.getLoginNombre());
		this.selectedItem = null;
		comp.close();
		Clients.showNotification("REGISTRO ACTUALIZADO..");
	}
	
	@Command
	public void imprimirItem() throws Exception {
		this.imprimirAcuse();
	}
	
	/**
	 * Despliega el Reporte de Acuse Recibo..
	 */
	private void imprimirAcuse() throws Exception {		
		String source = ReportesViewModel.SOURCE_ACUSE;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new AcuseDataSource(this.selectedItem);
		params.put("title", "Acuse Recibo de Documentos");
		params.put("Fecha", Utiles.getDateToString(this.selectedItem.getFecha(), Utiles.DD_MM_YYYY));
		params.put("NroAcuse", this.selectedItem.getNumero());
		params.put("TipoDocumento", this.selectedItem.getTipoMovimiento());
		params.put("Motivo", this.selectedItem.getMotivo());
		params.put("Receptor", this.selectedItem.getReceptor());
		params.put("Usuario", getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions.createComponents(
				ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * add combustible..
	 */
	private void addAcuse_() throws Exception {
		this.nvoAcuse = new AcuseDocumento();
		this.nvoAcuse.setNumero(KEY_NRO + "-" + AutoNumeroControl.getAutoNumero(KEY_NRO, 5, true));
		this.nvoAcuse.setFecha(new Date());
		WindowPopup wp = new WindowPopup();
		wp.setCheckAC(new ValidadorInsertarAcuse());
		wp.setDato(this);
		wp.setHigth("430px");
		wp.setWidth("380px");
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Agregar Acuse Recibo de Documentos");
		wp.show(ZUL_INSERT_ITEM);
		if (wp.isClickAceptar()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			rr.saveObject(this.nvoAcuse, this.getLoginNombre());
			AutoNumeroControl.getAutoNumero(KEY_NRO, 5);
			this.selectedItem = this.nvoAcuse;
			this.imprimirAcuse();
		}
	}
	
	/**
	 * validador insertar acuse..
	 */
	class ValidadorInsertarAcuse implements VerificaAceptarCancelar {
		
		String mensaje = "";

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a:";
			
			if (nvoAcuse.getDetalles().size() == 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar los documentos..";			
			}
			
			if (nvoAcuse.getReceptor() == null) {
				out = false;
				this.mensaje += "\n - Debe ingresar el receptor..";
			}
			
			if (nvoAcuse.getMotivo() == null) {
				out = false;
				this.mensaje += "\n - Debe ingresar el motivo..";
			}			
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return this.mensaje;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "Error al cancelar..";
		}		
	}
	
	/**
	 * DataSource del Acuse..
	 */
	class AcuseDataSource implements JRDataSource {

		List<AcuseDocumentoDetalle> detalle = new ArrayList<AcuseDocumentoDetalle>();

		public AcuseDataSource(AcuseDocumento acuse) {
			this.detalle.addAll(acuse.getDetalles());
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			AcuseDocumentoDetalle item = this.detalle.get(index);
			if ("NroFactura".equals(fieldName)) {
				value = item.getNumeroDocumento();
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < detalle.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}

	/**
	 * GETS / SETS
	 */	
	public List<AcuseDocumento> getAcusesDocumentos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getAcusesDocumentos();
	}
	
	public AcuseDocumento getNvoAcuse() {
		return nvoAcuse;
	}

	public void setNvoAcuse(AcuseDocumento nvoAcuse) {
		this.nvoAcuse = nvoAcuse;
	}

	public AcuseDocumento getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(AcuseDocumento selectedItem) {
		this.selectedItem = selectedItem;
	}
	
}
