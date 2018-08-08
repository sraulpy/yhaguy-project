package com.yhaguy.gestion.caja.principal;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Caja;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Talonario;
import com.yhaguy.inicio.AccesoDTO;

public class CajaControlBody extends BodyApp {
	
	final static String[] ATT_FUNCIONARIO = { "empresa.nombre" };
	final static String[] COLUMNAS = { "Apellido y Nombre" };

	final static String[] ATT_TALONARIO = { "numero", "bocaExpedicion",
			"puntoExpedicion", "desde", "hasta", "sucursal.nombre" };
	final static String[] COLS_TALONARIO = { "Número", "Boca", "Punto",
			"Desde", "Hasta", "Sucursal" };
	
	final static int TALONARIO_VENTA = 1;
	final static int TALONARIO_NC = 2;
	final static int TALONARIO_AUTOFACTURA = 3;
	final static int TALONARIO_RECIBOS = 4;
	final static int TALONARIO_RETENCIONES = 5;
	
	private CajaDTO dto = new CajaDTO();
	private AccesoDTO accesosUsuario = (AccesoDTO) this.getAtributoSession(Configuracion.ACCESO); 
	
	private String filterFuncionario = "";
	
	private MyArray selectedSupervisor;

	@Init(superclass=true)
	public void initCajaControlBody(){
	}
	
	@AfterCompose(superclass=true)
	public void afterComposeCajaControlBody(){
	}	

	@Override
	public Assembler getAss() {
		return new AssemblerCaja();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (CajaDTO) dto;
		selectedSupervisor = null;
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		CajaDTO newDto = new CajaDTO();
		newDto.setSucursal(this.accesosUsuario.getSucursalOperativa());
		newDto.setCreador(this.accesosUsuario.getFuncionario());
		newDto.setEstado(this.getDtoUtil().getCajaEstadoActivo());
		newDto.setTipo(this.getDtoUtil().getCajaTipoMovimientosVarios());
		txResponsable.focus();
		selectedSupervisor = null;
		return newDto;
	}

	@Override
	public String getEntidadPrincipal() {
		return Caja.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		List<DTO> l = this.getAllDTOs(this.getEntidadPrincipal());
		return l;
	}	
	
	public Browser getBrowser(){
		return new CajaBrowser();
	}
	
	@Wire
	private Textbox txResponsable;
	
	/*************************************************/
	
	
	/******************* COMANDOS ********************/

	@Command
	@NotifyChange("*")
	public void buscarSupervisor() throws Exception{
		BuscarElemento b = new BuscarElemento();
		b.setClase(Funcionario.class);
		b.setAtributos(ATT_FUNCIONARIO);
		b.setNombresColumnas(COLUMNAS);
		b.setTitulo("Buscar Funcionario");
		b.setWidth("600px");
		b.addOrden("empresa.nombre");
		b.addWhere("id != " + this.dto.getResponsable().getId());
		b.show(this.filterFuncionario);
		if (b.isClickAceptar()) {
			//falta validar que el seleccionado sea un supervisor
			if (this.itemDuplicado(b.getSelectedItem())) {
				this.mensajeError(Configuracion.TEXTO_ERROR_ITEM_DUPLICADO);
			} else {
				this.dto.getSupervisores().add(b.getSelectedItem());
			}			
		}
		this.filterFuncionario = "";
	}
	
	
	@Command
	@NotifyChange("*")
	public void buscarResponsable() throws Exception{
		BuscarElemento b = new BuscarElemento();
		b.setClase(Funcionario.class);
		b.setAtributos(ATT_FUNCIONARIO);
		b.setNombresColumnas(COLUMNAS);
		b.setTitulo("Buscar Funcionario");
		b.setWidth("600px");
		b.addOrden("empresa.nombre");
		b.show((String) this.dto.getResponsable().getPos1());
		if (b.isClickAceptar()) {
			//falta validar que el seleccionado sea un cajero
			this.dto.setResponsable(b.getSelectedItem());
		}
	}	
	
	@Command
	@NotifyChange("*")
	public void removerSupervisor(){
		if (this.mensajeSiNo(Configuracion.TEXTO_BORRAR_ITEM_SELECCIONADO
				+ "\n" + this.selectedSupervisor.getPos1())) {
			this.dto.getSupervisores().remove(this.selectedSupervisor);
			this.selectedSupervisor = null;
		}
	}
	
	@Command
	@NotifyChange("*")
	public void buscarTalonario(@BindingParam("tipo") int tipo) throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Talonario.class);
		b.setAtributos(ATT_TALONARIO);
		b.setNombresColumnas(COLS_TALONARIO);
		b.setAnchoColumnas(new String[]{"", "70px", "70px", "70px", "70px", ""});
		b.setTitulo("Talonarios");
		b.setWidth("600px");
		b.show("%");
		if (b.isClickAceptar()) {
			this.setTalonario(tipo, b.getSelectedItem());
		}
	}
	
	/**
	 * @param item duplicado..
	 */
	private boolean itemDuplicado(MyArray item){
		boolean out = false;
		for (MyArray m : this.dto.getSupervisores()) {
			if (m.getId() == item.getId()) {
				out = true;
			}
		}
		return out;
	}
	
	/**
	 * seteo del talonario..
	 */
	private void setTalonario(int tipo, MyArray talonario) {
		switch (tipo) {
		case TALONARIO_VENTA:
			this.dto.setTalonarioVentas(talonario);
			break;

		case TALONARIO_NC:
			this.dto.setTalonarioNotasCredito(talonario);
			break;
			
		case TALONARIO_AUTOFACTURA:
			this.dto.setTalonarioAutoFacturas(talonario);
			break;
			
		case TALONARIO_RECIBOS:
			this.dto.setTalonarioRecibos(talonario);
			break;
			
		case TALONARIO_RETENCIONES:
			this.dto.setTalonarioRetenciones(talonario);
			break;
		}
	}
			
	/*************************************************/	
	
	
	/****************** VALIDACIONES *****************/
	
	private String mensajeError = Configuracion.TEXTO_ERROR_VALIDACION;
	
	final static String ERROR_SUPERVISORES = "Debe asignar al menos un Supervisor..";
	final static String ERROR_TIPO = "Debe asignar un Tipo de Caja..";
	final static String ERROR_DESCRIPCION = "Debe especificar una descripción..";
	final static String ERROR_RESPONSABLE = "Debe asignar un Responsable..";
	final static String ERROR_TALONARIO_VTA = "Debe asigbar un Talonario de Ventas..";
	final static String ERROR_TALONARIO_NCV = "Debe asigbar un Talonario de Notas de Crédito..";
	
	@Override
	public boolean verificarAlGrabar() {
		try {
			if ((this.dto.esNuevo()) && (this.validarFormulario() == true)) {
				this.dto.setNumero(Configuracion.NRO_CAJA
						+ "-"
						+ AutoNumeroControl.getAutoNumero(
								Configuracion.NRO_CAJA, 5));
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
		return this.validarFormulario();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return mensajeError;
	}
	
	private boolean validarFormulario(){
		boolean out = true;
		this.mensajeError = "No se puede completar la operación debido a:";
		
		if (this.dto.getResponsable().esNuevo()) {
			out = false;
			this.mensajeError += "\n - " + ERROR_RESPONSABLE;
		}
		
		if (this.dto.getDescripcion().trim().length() == 0) {
			out = false;
			this.mensajeError += "\n - " + ERROR_DESCRIPCION;
		}
		
		if (this.dto.getTipo().esNuevo()) {
			out = false;
			this.mensajeError += "\n - " + ERROR_TIPO;
		}
		
		if (this.dto.getSupervisores().size() == 0) {
			out = false;
			this.mensajeError += "\n - " + ERROR_SUPERVISORES;
		}
		
		if(this.dto.getTalonarioVentas() == null){
			out = false;
			this.mensajeError += ERROR_TALONARIO_VTA;
		}
		
		if(this.dto.getTalonarioNotasCredito() == null){
			out = false;
			this.mensajeError += ERROR_TALONARIO_NCV;
		}
		
		return out;
	}
	
	/*************************************************/
	
	public CajaDTO getDto() {
		return dto;
	}

	public void setDto(CajaDTO dto) {
		this.dto = dto;
	}

	public String getFilterFuncionario() {
		return filterFuncionario;
	}

	public void setFilterFuncionario(String filterFuncionario) {
		this.filterFuncionario = filterFuncionario;
	}
	
	public MyArray getSelectedSupervisor() {
		return selectedSupervisor;
	}

	public void setSelectedSupervisor(MyArray selectedSupervisor) {
		this.selectedSupervisor = selectedSupervisor;
	}
}
