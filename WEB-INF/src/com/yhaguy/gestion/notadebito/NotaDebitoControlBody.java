package com.yhaguy.gestion.notadebito;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.domain.NotaDebito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;

public class NotaDebitoControlBody extends BodyApp {
	
	static final String ZUL_INSERT_ITEM = "/yhaguy/gestion/notadebito/insertarItem.zul";
	
	private NotaDebitoDTO dto = new NotaDebitoDTO();
	private NotaDebitoDetalleDTO nvoDetalle;
	
	private List<NotaDebitoDetalleDTO> selectedItems;
	private String mensaje = "";
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public boolean verificarAlGrabar() {
		return this.isDatosValidos();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensaje;
	}

	@Override
	public Assembler getAss() {
		return new NotaDebitoAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (NotaDebitoDTO) dto;		
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		NotaDebitoDTO nvo = new NotaDebitoDTO();
		this.sugerirValores(nvo);
		return nvo;
	}

	@Override
	public String getEntidadPrincipal() {
		return NotaDebito.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}
	
	@Override
	public Browser getBrowser() {
		return new NotaDebitoBrowser();
	}
	
	@Command
	@NotifyChange("*")
	public void insertItem() throws Exception {
		this.insertarItem();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteItems() {
		this.deleteItems_();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() {
		this.confirmar_();
	}
	
	/**
	 * inserta el item al detalle..
	 */
	private void insertarItem() throws Exception {
		this.nvoDetalle = new NotaDebitoDetalleDTO();
		MyArray iva10 = this.getDtoUtil().getTipoIva10();
		this.nvoDetalle.setTipoIva(new MyPair(iva10.getId(), (String) iva10.getPos1()));
		
		WindowPopup wp = new WindowPopup();
		wp.setCheckAC(new ValidadorInsertarItem());
		wp.setDato(this);
		wp.setHigth("260px");
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Insertar ítem");
		wp.setWidth("400px");
		wp.show(ZUL_INSERT_ITEM);
		if (wp.isClickAceptar()) {
			this.dto.getDetalles().add(this.nvoDetalle);
		}
		this.nvoDetalle = null;
	}
	
	/**
	 * eliminar items..
	 */
	private void deleteItems_() {
		if (this.mensajeSiNo("Desea eliminar los ítems seleccionados..")) {
			this.dto.getDetalles().removeAll(this.selectedItems);
			this.selectedItems = null;
		}		
	}
	
	/**
	 * sugiere valores por defecto
	 */
	private void sugerirValores(NotaDebitoDTO dto) {
		dto.setTipoMovimiento(this.getDtoUtil().getTmNotaDebitoVenta());
		dto.setSucursal(this.getSucursal());
		dto.setEstadoComprobante(this.getEstadoComprobantePendiente());
	}
	
	/**
	 * confirmacion del registro..
	 */
	private void confirmar_() {
		if (!this.mensajeSiNo("Desea confirmar el registro..")) {
			return;
		}
		try {
			this.dto.setEstadoComprobante(getEstadoComprobanteCerrado());
			this.dto.setReadonly();
			this.dto = (NotaDebitoDTO) this.saveDTO(this.dto);
			this.setEstadoABMConsulta();
			ControlCuentaCorriente.addNotaDebitoVenta(this.dto, this.getLoginNombre());
			
		} catch (Exception e) {
			e.printStackTrace();
			this.mensajePopupTemporalWarning("Hubo un error al intentar confirmar..");
		}
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		RegisterDomain rr = RegisterDomain.getInstance();
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		
		try {
			
			if (this.dto.getDetalles().size() == 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar ítems al detalle..";
			}
			
			if (rr.getNotaDebitoByNumero(this.dto.getNumero(), this.dto.getTimbrado()) != null) {
				out = false;
				this.mensaje += "\n - Ya existe una nota de debito con el mismo numero y timbrado..";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
		return out;
	}

	
	/**
	 * validador insertar item..
	 */
	class ValidadorInsertarItem implements VerificaAceptarCancelar {
		
		private String mensaje = "";

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede realizar la operación debido a: \n";
			
			if (nvoDetalle.getDescripcion().isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar la descripcion..";
			}
			
			if (nvoDetalle.getImporteGs() == 0) {
				out = false;
				this.mensaje += "\n - Debe ingresar el importe..";
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
			return "";
		}
		
	}
	
	/**
	 * browser notas de debito..
	 */
	class NotaDebitoBrowser extends Browser {
		
		@Override
		public void setingInicial() {
			this.setWidthWindows("900px");
			this.setHigthWindows("80%");
			this.addOrden("numero");
		}
		
		@Override
		public List<ColumnaBrowser> getColumnasBrowser() {
			// TODO Auto-generated method stub

			ColumnaBrowser col1 = new ColumnaBrowser();
			ColumnaBrowser col2 = new ColumnaBrowser();
			ColumnaBrowser col3 = new ColumnaBrowser();
			ColumnaBrowser col4 = new ColumnaBrowser();
			ColumnaBrowser col5 = new ColumnaBrowser();

			col1.setCampo("numero"); 	
			col1.setTitulo("Número");
			col1.setWidthColumna("150px");
			
			col2.setCampo("timbrado"); 	
			col2.setTitulo("Timbrado");
			col2.setWidthColumna("100px");
			
			col3.setCampo("fecha"); 	
			col3.setTitulo("Fecha");
			col3.setTipo(Config.TIPO_DATE);
			col3.setComponente(Browser.LABEL_DATE);
			col3.setWidthColumna("170px");
			
			col4.setCampo("cliente.empresa.razonSocial"); 	
			col4.setTitulo("Cliente");	
			
			col5.setCampo("estadoComprobante.descripcion"); 	
			col5.setTitulo("Estado");	
			col5.setWidthColumna("100px");
			
			List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
			columnas.add(col1);
			columnas.add(col2);
			columnas.add(col3);
			columnas.add(col4);
			columnas.add(col5);
			
			return columnas;
		}	

		@SuppressWarnings("rawtypes")
		@Override
		public Class getEntidadPrincipal() {
			return NotaDebito.class;
		}

		@Override
		public String getTituloBrowser() {
			return "Notas de Débito";
		}	
	}
	
	/**
	 * GETS / SETS
	 */	
	@DependsOn({ "dto.cliente", "dto.numero", "dto.timbrado" })
	public boolean isDetalleVisible() {
		return (this.dto.getCliente().esNuevo() == false)
					&& (!this.dto.getNumero().isEmpty())
					&& (!this.dto.getTimbrado().isEmpty());
	}
	
	@DependsOn({ "deshabilitado", "dto.detalles" })
	public boolean isCheckmarkVisible() {
		return (this.isDeshabilitado() == false)
				&& (this.dto.getDetalles().size() > 0);
	}
	
	// Retorna true si se puede editar el cliente..
	public boolean isClienteEditable() {
		boolean out = false;
		if ((this.isDeshabilitado() == false)
				&& (this.dto.getDetalles().size() == 0)) {
			out = true;
		}
		return out;
	}
	
	public NotaDebitoDTO getDto() {
		return dto;
	}

	public void setDto(NotaDebitoDTO dto) {
		this.dto = dto;
	}

	public List<NotaDebitoDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<NotaDebitoDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public NotaDebitoDetalleDTO getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(NotaDebitoDetalleDTO nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}
