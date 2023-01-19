package com.yhaguy.gestion.empresa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.templateABM.Body;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.FuncionarioAnticipo;
import com.yhaguy.domain.FuncionarioDescuento;
import com.yhaguy.domain.FuncionarioDocumento;
import com.yhaguy.domain.FuncionarioPeriodoVacaciones;
import com.yhaguy.domain.FuncionarioPremio;
import com.yhaguy.domain.Identificaciones;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaDTO;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class FuncionarioControlBody extends Body {
	
	static final String PATH = Configuracion.pathFuncionarios;
	
	private String filterCedula;
	private String filterNombres;
	private String filterApellidos;
	
	private Identificaciones selectedIdentificaciones;

	private FuncionarioDTO dto = new FuncionarioDTO();
	private String msjErr = "";
	
	private FuncionarioPeriodoVacaciones nvoPeriodo;
	private FuncionarioDocumento documento;
	private FuncionarioDescuento descuento;
	private FuncionarioAnticipo anticipo;
	private FuncionarioPremio premio;

	@Init(superclass = true)
	public void initFuncionarioControlBody() {
		this.nvoPeriodo = new FuncionarioPeriodoVacaciones();
		this.filterCedula = "";
		this.filterNombres = "";
		this.filterApellidos = "";
	}

	@AfterCompose(superclass = true)
	public void afterComposeFuncionarioControlBody() {
	}

	@Override
	public Assembler getAss() {
		return new AssemblerFuncionario();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.selectedAcceso = null;
		this.dto = (FuncionarioDTO) dto;
		Clients.evalJavaScript("setImage('" + this.dto.getUrlImagen() + "')");
	}

	@Override
	public DTO nuevoDTO() {
		
		FuncionarioDTO funDto = new FuncionarioDTO();
		funDto.getEmpresa().setPais(this.getDtoUtil().getPaisParaguay());
		funDto.getEmpresa().setTipoPersona(this.getDtoUtil().getTipoPersonaFisica());
		funDto.getEmpresa().setRegimenTributario(this.getDtoUtil().getRegimenTributarioNoExenta());

		CtaCteEmpresaDTO cuentaClienteFuncionario = new CtaCteEmpresaDTO();
		cuentaClienteFuncionario.setEstadoComoCliente(this.getDtoUtil().getCtaCteEmpresaEstadoActivo());
		cuentaClienteFuncionario.setCondicionPagoCliente(this.getDtoUtil().getCondicionPagoContado());
		cuentaClienteFuncionario.setLineaCredito(this.getDtoUtil().getCtaCteLineaCreditoDefault());
		cuentaClienteFuncionario.setFechaAperturaCuentaCliente(new Date());

		cuentaClienteFuncionario.setEstadoComoProveedor(this.getDtoUtil().getCtaCteEmpresaEstadoSinCuenta());
		cuentaClienteFuncionario.setFechaAperturaCuentaProveedor(null);
		
		funDto.getEmpresa().setCtaCteEmpresa(cuentaClienteFuncionario);
		funDto.getEmpresa().setMoneda(this.getDtoUtil().getMonedaGuarani());
		funDto.getEmpresa().getMonedas().add(this.getDtoUtil().getMonedaGuarani());
		
		return funDto;
	}

	@Override
	public String getEntidadPrincipal() {
		return Funcionario.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}

	@Override
	public Browser getBrowser() {
		return new FuncionarioBrowser();
	}

	public FuncionarioDTO getDto() {
		return dto;
	}

	public void setDto(FuncionarioDTO dto) {
		this.dto = dto;
	}

	AccesoDTO selectedAcceso = new AccesoDTO();

	public AccesoDTO getSelectedAcceso() {
		return this.selectedAcceso;
	}

	public void setSelectedAcceso(AccesoDTO selectedAcceso) {
		this.selectedAcceso = selectedAcceso;
	}

	private MyArray selectedSucursalHab = null;
	private List<MyArray> sucursalesAnhadir = new ArrayList<MyArray>();
	private List<MyArray> selectedSucursalesAnhadir = new ArrayList<MyArray>();

	public MyArray getSelectedSucursalHab() {
		return selectedSucursalHab;
	}

	public void setSelectedSucursalHab(MyArray selectedSucursalHab) {
		this.selectedSucursalHab = selectedSucursalHab;
	}

	public List<MyArray> getSucursalesAnhadir() {
		return sucursalesAnhadir;
	}

	public void setSucursalesAnhadir(List<MyArray> sucursalesAnhadir) {
		this.sucursalesAnhadir = sucursalesAnhadir;
	}

	public List<MyArray> getSelectedSucursalesAnhadir() {
		return selectedSucursalesAnhadir;
	}

	public void setSelectedSucursalesAnhadir(List<MyArray> selectedSucursalesAnhadir) {
		this.selectedSucursalesAnhadir = selectedSucursalesAnhadir;
	}

	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO) super.getDtoUtil();
		return u;
	}

	public List<MyArray> obtenerSucursalesSinAnhadir() throws Exception {

		List<MyArray> sucursalesAcceso = this.selectedAcceso.getSucursales();
		List<MyArray> sucursales = this.getDtoUtil().getSucursales();
		List<MyArray> sucursalesSinAnhadir = new ArrayList<MyArray>();
		sucursalesSinAnhadir.addAll(this.getDtoUtil().getSucursales());

		for (MyArray n : sucursales) {
			for (MyArray m : sucursalesAcceso) {
				if (m.getId().compareTo(n.getId()) == 0) {
					sucursalesSinAnhadir.remove(n);
				}
			}
		}

		return sucursalesSinAnhadir;
	}

	@Command()
	@NotifyChange("*")
	public void agregarSucursalHab() {

		try {

			if (this.selectedAcceso == null) {
				this.msjErr = "- Seleccione primero un acceso.";
				this.mensajeError(this.msjErr);
				return;
			}

			this.sucursalesAnhadir = obtenerSucursalesSinAnhadir();
			WindowPopup win = new WindowPopup();
			win.setDato(this);
			win.setHigth("580px");
			win.setWidth("850px");
			win.setTitulo("Agregar Sucursales Habilitadas");
			win.setModo(WindowPopup.NUEVO);
			win.setCheckAC(new ValidadorPopupSucursales(this));
			win.show(Configuracion.FUNCIONARIO_POPUP_SUCURSALES_ZUL);

			if (win.isClickAceptar()) {

				this.getSelectedAcceso().getSucursales().addAll(this.selectedSucursalesAnhadir);

			}

			// Limpiar los seleccionados
			this.selectedSucursalesAnhadir = new ArrayList<MyArray>();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	@Command()
	@NotifyChange("*")
	public void eliminarSucursalHab() {
		if (this.selectedSucursalHab != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la sucursal?")) {

				((List) this.selectedAcceso.getSucursales()).remove(this.selectedSucursalHab);

			}
			this.setSelectedSucursalHab(null);
		} else {

			this.msjErr = "- Seleccione la sucursal habilitada que desee eliminar.";
			this.mensajeError(this.msjErr);
			return;

		}
	}
	
	@Command
	@NotifyChange("*")
	public void addPeriodo(@BindingParam("comp") Popup comp) throws Exception {
		for (FuncionarioPeriodoVacaciones p : this.dto.getPeriodos()) {
			if (p.isVigente()) {
				Clients.showNotification("YA EXISTE UN PERIODO VIGENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Funcionario f = rr.getFuncionarioById(this.dto.getId());
		f.getPeriodos().add(this.nvoPeriodo);
		rr.saveObject(f, this.getLoginNombre());
		comp.close();
		
		this.dto = (FuncionarioDTO) this.getDTOById(Funcionario.class.getName(), f.getId());
	}
	
	@Command
	@NotifyChange("dto")
	public void selectIdentificaciones() throws Exception {
		this.dto.setNombre(this.selectedIdentificaciones.getPer_nombres() + " " + this.selectedIdentificaciones.getPer_apellidos());
		this.dto.setCi(this.selectedIdentificaciones.getPer_nrodocumento());
		this.dto.setFechaCumpleanhos(Utiles.getFecha(this.selectedIdentificaciones.getPer_fecha_nac(), "yyyy-MM-dd hh:mm:ss"));
	}
	
	@Command
	@NotifyChange("documento")
	public void addDocumento(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		Funcionario f = rr.getFuncionarioById(this.dto.getId());
		this.documento = new FuncionarioDocumento();
		this.documento.setFuncionario(f);
		pop.open(comp, "after_start");
	}

	@Command 
	@NotifyChange("*")
	public void uploadFile(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = Utiles.getDateToString(new Date(), "dd_MM_yyyy_hh_mm_ss");
			InputStream file_ = new ByteArrayInputStream(file.getByteData());
			String format = "." + file.getFormat();
			misc.uploadFile(PATH, name, format, file_);
			
			RegisterDomain rr = RegisterDomain.getInstance();
			this.documento.setAuxi(Configuracion.pathFuncionariosGenerico + name + format);
			this.documento.setDescripcion(this.documento.getDescripcion().toUpperCase());
			rr.saveObject(this.documento, this.getLoginNombre());
			
			this.documento = null;
			
			Clients.showNotification("DOCUMENTO CORRECTAMENTE SUBIDO");
			
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command
	@NotifyChange("descuento")
	public void openDescuento(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		Funcionario f = rr.getFuncionarioById(this.dto.getId());
		this.descuento = new FuncionarioDescuento();
		this.descuento.setFuncionario(f);
		pop.open(comp, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void addDescuento(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		
		if (this.descuento.getDescripcion().trim().isEmpty()) {
			Clients.showNotification("DEBE INGRESAR EL CONCEPTO", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 0);
			return;
		}
		
		if (this.descuento.getImporteGs() <= 0) {
			Clients.showNotification("DEBE INGRESAR EL IMPORTE", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 0);
			return;
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		this.descuento.setDescripcion(this.descuento.getDescripcion().toUpperCase());
		pop.close();
		rr.saveObject(this.descuento, this.getLoginNombre());
	}
	
	@Command
	@NotifyChange("*")
	public void deleteDescuento(@BindingParam("item")FuncionarioDescuento item) throws Exception {
		if (this.mensajeSiNo("Desea eliminar el ítem seleccionado?")) {
			RegisterDomain rr = RegisterDomain.getInstance();
			rr.deleteObject(item);
			Clients.showNotification("ITEM ELIMINADO");
		}		
	}
	
	@Command
	@NotifyChange("anticipo")
	public void openAnticipo(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		Funcionario f = rr.getFuncionarioById(this.dto.getId());
		this.anticipo = new FuncionarioAnticipo();
		this.anticipo.setFuncionario(f);
		this.anticipo.setAnho(Integer.parseInt(Utiles.getDateToString(new Date(), "yyyy")));
		this.anticipo.setMes(Integer.parseInt(Utiles.getDateToString(new Date(), "MM")));
		pop.open(comp, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void deleteAnticipo(@BindingParam("item")FuncionarioAnticipo item) throws Exception {
		if (this.mensajeSiNo("Desea eliminar el ítem seleccionado?")) {
			RegisterDomain rr = RegisterDomain.getInstance();
			rr.deleteObject(item);
			Clients.showNotification("ITEM ELIMINADO");
		}		
	}
	
	@Command
	@NotifyChange("*")
	public void addAnticipo(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		
		if (this.anticipo.getDescripcion().trim().isEmpty()) {
			Clients.showNotification("DEBE INGRESAR EL CONCEPTO", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 0);
			return;
		}
		
		if (this.anticipo.getImporteGs() <= 0) {
			Clients.showNotification("DEBE INGRESAR EL IMPORTE", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 0);
			return;
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		this.anticipo.setDescripcion(this.anticipo.getDescripcion().toUpperCase());
		pop.close();
		rr.saveObject(this.anticipo, this.getLoginNombre());
	}

	
	@Command
	@NotifyChange("premio")
	public void openPremio(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		Funcionario f = rr.getFuncionarioById(this.dto.getId());
		this.premio = new FuncionarioPremio();
		this.premio.setFuncionario(f);
		this.premio.setAnho(Integer.parseInt(Utiles.getDateToString(new Date(), "yyyy")));
		this.premio.setMes(Integer.parseInt(Utiles.getDateToString(new Date(), "MM")));
		pop.open(comp, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void deletePremio(@BindingParam("item") FuncionarioPremio item) throws Exception {
		if (this.mensajeSiNo("Desea eliminar el ítem seleccionado?")) {
			RegisterDomain rr = RegisterDomain.getInstance();
			rr.deleteObject(item);
			Clients.showNotification("ITEM ELIMINADO");
		}		
	}
	
	@Command
	@NotifyChange("*")
	public void addPremio(@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) throws Exception {
		
		if (this.premio.getDescripcion().trim().isEmpty()) {
			Clients.showNotification("DEBE INGRESAR EL CONCEPTO", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 0);
			return;
		}
		
		if (this.premio.getImporteGs() <= 0) {
			Clients.showNotification("DEBE INGRESAR EL IMPORTE", Clients.NOTIFICATION_TYPE_ERROR, comp, null, 0);
			return;
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		this.premio.setDescripcion(this.premio.getDescripcion().toUpperCase());
		pop.close();
		rr.saveObject(this.premio, this.getLoginNombre());
	}


	List<MyArray> users = new ArrayList<MyArray>();
	MyArray selectedUser = null;
	MyArray selectedDepartamento = null;
	
	/**
	 * @return los estados civiles..
	 */
	public List<String> getEstadosCiviles() {
		List<String> out = new ArrayList<String>();
		out.add(Funcionario.ESTADO_CIVIL_CASADO);
		out.add(Funcionario.ESTADO_CIVIL_SOLTERO);
		return out;
	}
	
	/**
	 * @return los grados academicos..
	 */
	public List<String> getGradosAcademicos() {
		List<String> out = new ArrayList<String>();
		out.add(Funcionario.BACHILLER);
		out.add(Funcionario.LICENCIADO);
		out.add(Funcionario.MAGISTER);
		out.add(Funcionario.DOCTORADO);
		out.add(Funcionario.INGENIERO);
		out.add(Funcionario.SIN_ESTUDIOS);
		return out;
	}
	
	@DependsOn({ "filterCedula", "filterNombres", "filterApellidos" })
	public List<Identificaciones> getIdentificaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getIdentificaciones(this.filterCedula, this.filterNombres, this.filterApellidos);
	}
	
	@DependsOn("dto")
	public List<FuncionarioDocumento> getDocumentos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioDocumentos(this.dto.getId());
	}
	
	@DependsOn("dto")
	public List<FuncionarioDescuento> getDescuentos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioDescuentos(this.dto.getId());
	}
	
	@DependsOn("dto")
	public List<FuncionarioAnticipo> getAnticipos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioAnticipos(this.dto.getId());
	}
	
	@DependsOn("dto")
	public List<FuncionarioPremio> getPremios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioPremios(this.dto.getId());
	}

	public List<MyArray> getUsers() {
		return users;
	}

	public void setUsers(List<MyArray> users) {
		this.users = users;
	}

	public MyArray getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(MyArray selectedUser) {
		this.selectedUser = selectedUser;
	}

	public MyArray getSelectedDepartamento() {
		return selectedDepartamento;
	}

	public void setSelectedDepartamento(MyArray selectedDepartamento) {
		this.selectedDepartamento = selectedDepartamento;
	}

	@Command
	@NotifyChange("*")
	public void agregarUsuario() throws Exception {

		try {

			this.users = obtenerUsuariosSinAcceso();
			WindowPopup win = new WindowPopup();
			win.setDato(this);
			win.setHigth("580px");
			win.setWidth("850px");
			win.setTitulo("Agregar Acceso Usuario");
			win.setModo(WindowPopup.NUEVO);
			win.setCheckAC(new ValidadorPopupUsuario(this));
			win.show(Configuracion.FUNCIONARIO_POPUP_USUARIO_ZUL);

			if (win.isClickAceptar()) {

				// Crea un nuevo acceso al sistema
				AccesoDTO nuevoAcceso = new AccesoDTO();

				// Asigna el usuario del sistema y a que departamento pertenece
				nuevoAcceso.setUsuario(this.selectedUser);
				nuevoAcceso.setDepartamento(this.selectedDepartamento);

				// Crea un my array para poder asignarle el usuario
				MyArray funcionario = new MyArray();
				funcionario.setId(this.dto.getId());
				funcionario.setPos1(this.dto.getNombre());

				// Asigna el usuario
				nuevoAcceso.setFuncionario(funcionario);

				// Asignar sucursal operatva
				this.dto.getAccesos().add(nuevoAcceso);

			}

			this.selectedDepartamento = null;
			this.selectedUser = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<MyArray> obtenerUsuariosSinAcceso() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();

		List<MyArray> usuariosSinAcceso = new ArrayList<MyArray>();
		List<Object[]> usuariosConAcceso = rr.getUsuariosConAcceso();
		List<Object[]> usuarios = rr.getTodosUsuarios();

		// transformar usuarios a myArray y cargar la lista
		List<MyArray> usuariosMA = new ArrayList<MyArray>();

		for (Object[] usuario : usuarios) {
			MyArray usr = new MyArray();
			usr.setId((long) usuario[0]);
			usr.setPos1(usuario[1]);
			usr.setPos2(usuario[2]);
			usuariosMA.add(usr);
		}

		for (Object[] usuario : usuariosConAcceso) {
			MyArray usr = new MyArray();
			usr.setId((long) usuario[0]);
			usr.setPos1(usuario[1]);
			usr.setPos2(usuario[2]);
			usuariosMA.remove(usr);
		}

		usuariosSinAcceso = usuariosMA;

		return usuariosSinAcceso;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarAcceso() {
		if (this.selectedAcceso != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el acceso del funcionario?")) {
				// verificar que no este asociado a ningun objeto
				this.getDto().getAccesos().remove(this.selectedAcceso);
			}

			this.setSelectedAcceso(null);
		} else {

			this.msjErr = "- Seleccione el acceso de usuario que desee eliminar.";
			this.mensajeError(this.msjErr);
			return;

		}
	}

	@Override
	public boolean verificarAlGrabar() {
		this.msjErr = "No se puede realizar la operación debido a:";

		boolean out = true;

		if (this.dto.getNombre().trim().length() == 0) {
			out = false;
			this.msjErr += "\n - Campo nombre vacio.";
		}

		if (this.dto.getCi().trim().length() == 0) {
			out = false;
			this.msjErr += "\n - Campo número de documento vacio.";
		}

		if (this.dto.getDireccion().trim().length() == 0) {
			out = false;
			this.msjErr += "\n - Campo dirección vacio.";
		}

		if (this.dto.getTelefono().trim().length() == 0) {
			out = false;
			this.msjErr += "\n - Campo teléfono vacio.";
		}

		if (this.dto.getFuncionarioEstado() == null || this.dto.getFuncionarioEstado().esNuevo()) {
			out = false;
			this.msjErr += "\n - Debe seleccionar un estado para el funcionario.";
		}

		if (this.dto.getFuncionarioCargo() == null || this.dto.getFuncionarioCargo().esNuevo()) {
			out = false;
			this.msjErr += "\n - Debe seleccionar un cargo para el funcionario.";
		}

		return out;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.msjErr;
	}

	public FuncionarioPeriodoVacaciones getNvoPeriodo() {
		return nvoPeriodo;
	}

	public void setNvoPeriodo(FuncionarioPeriodoVacaciones nvoPeriodo) {
		this.nvoPeriodo = nvoPeriodo;
	}

	public String getFilterCedula() {
		return filterCedula;
	}

	public void setFilterCedula(String filterCedula) {
		this.filterCedula = filterCedula;
	}

	public String getFilterNombres() {
		return filterNombres;
	}

	public void setFilterNombres(String filterNombres) {
		this.filterNombres = filterNombres;
	}

	public String getFilterApellidos() {
		return filterApellidos;
	}

	public void setFilterApellidos(String filterApellidos) {
		this.filterApellidos = filterApellidos;
	}

	public Identificaciones getSelectedIdentificaciones() {
		return selectedIdentificaciones;
	}

	public void setSelectedIdentificaciones(Identificaciones selectedIdentificaciones) {
		this.selectedIdentificaciones = selectedIdentificaciones;
	}

	public FuncionarioDocumento getDocumento() {
		return documento;
	}

	public void setDocumento(FuncionarioDocumento documento) {
		this.documento = documento;
	}

	public FuncionarioDescuento getDescuento() {
		return descuento;
	}

	public void setDescuento(FuncionarioDescuento descuento) {
		this.descuento = descuento;
	}

	public FuncionarioAnticipo getAnticipo() {
		return anticipo;
	}

	public void setAnticipo(FuncionarioAnticipo anticipo) {
		this.anticipo = anticipo;
	}

	public FuncionarioPremio getPremio() {
		return premio;
	}

	public void setPremio(FuncionarioPremio premio) {
		this.premio = premio;
	}
}

class ValidadorPopupUsuario implements VerificaAceptarCancelar {

	private String mensajeError = "";
	private FuncionarioControlBody funcionarioControl;

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public ValidadorPopupUsuario(FuncionarioControlBody ctr) {
		this.funcionarioControl = ctr;
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a:";

		if (funcionarioControl.getSelectedUser() == null || funcionarioControl.getSelectedUser().esNuevo()) {
			out = false;
			this.mensajeError += "\n- Debe seleccionar un usuario del sistema de la lista.";
		}

		if (funcionarioControl.getSelectedDepartamento() == null
				|| funcionarioControl.getSelectedDepartamento().esNuevo()) {
			out = false;
			this.mensajeError += "\n- Debe seleccionar un departamento.";
		}

		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar";
	}
}

class ValidadorPopupSucursales implements VerificaAceptarCancelar {

	private String mensajeError = "";
	private FuncionarioControlBody funcionarioControl;

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public ValidadorPopupSucursales(FuncionarioControlBody ctr) {
		this.funcionarioControl = ctr;
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a:";

		if (funcionarioControl.getSelectedSucursalesAnhadir().size() <= 0) {
			out = false;
			this.mensajeError += "\n- Debe seleccionar una o mas sucursales de la lista.";
		}

		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar";
	}
}
