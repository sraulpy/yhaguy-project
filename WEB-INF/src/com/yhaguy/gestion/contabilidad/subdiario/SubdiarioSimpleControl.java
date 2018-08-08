package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Domain;
import com.coreweb.util.MyPair;
import com.yhaguy.AutonumeroYhaguy;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SubDiario;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.reporte.ReporteSubdiario;

public class SubdiarioSimpleControl extends SimpleViewModel {

	private List<SubDiarioDTO> subdiarios = new ArrayList<SubDiarioDTO>();
	private Date fechaDesde = new Date();
	private Date fechaHasta = new Date();
	private SubDiarioDTO subDiarioNuevoEditar;
	private SubDiarioDetalleDTO subDiarioDetalleNuevo;
	private AccesoDTO acceso = null;
	private String mensaje = "";

	private boolean debe = false;
	private boolean haber = false;

	private boolean habilitarImprimir = false;

	private boolean nuevoSD = true;


	@Init(superclass = true)
	public void initSubdiarioSimpleControl() {
		this.setAss(new AssemblerSubDiario());
	}

	@AfterCompose(superclass = true)
	public void afterComposeSubdiarioSimpleControl() {
	}

	public AccesoDTO getAcceso() {
		if (this.acceso == null) {
			Session s = Sessions.getCurrent();
			this.acceso = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		}
		return acceso;
	}

	// busca subdiarios con los parametros fecha desde y hasta
	@NotifyChange("*")
	@Command
	public void buscarSubdiarios() throws WrongValueException, Exception {

		// System.out.println("*******************************");
		// System.out.println(fechaDesde + "" + fechaHasta);
		ArrayList<SubDiarioDTO> sd = this.buscarSubDiarios(fechaDesde,
				fechaHasta);
		// System.out.println("hay:"+sd.size());
		this.setSubdiarios(sd);
		this.setHabilitarImprimir(true);

	}

	// se encarga de cargar la lista subdiarios segun parametros de fecha
	public ArrayList<SubDiarioDTO> buscarSubDiarios(Date desde, Date hasta)
			throws Exception {

		ArrayList<SubDiarioDTO> sd = new ArrayList<SubDiarioDTO>();
		RegisterDomain rr = RegisterDomain.getInstance();

		// consultar base de datos segun parametros y retorna la lista de
		// subdiarios de tipo domain
		List<SubDiario> list = rr.getSubdiariosByFecha(desde, hasta);

		// recorre la lista y cada item convierte a dto y agreaga a la lista
		// subdiarios
		for (SubDiario item : list) {
			SubDiarioDTO d = (SubDiarioDTO) this.getAss().domainToDto(item);
			sd.add(d);
		}
		return sd;
	}

	// crea un nuevo sub diario manualmente

	@NotifyChange("*")
	@Command
	public void crearNuevoSubdiarios() throws Exception {
		SubDiarioDTO sd = new SubDiarioDTO();
		sd.setSucursal(this.getAcceso().getSucursalOperativa());
		this.nuevoSD = false;
		this.nuevoEditarSubdiarios(sd);
	}

	@NotifyChange("*")
	@Command
	public void editarSubdiarios(@BindingParam("subd") SubDiarioDTO subDiario)
			throws Exception {
		this.nuevoSD = true;
		this.nuevoEditarSubdiarios(subDiario);
	}

	public void nuevoEditarSubdiarios(SubDiarioDTO sdp) throws Exception {

		this.subDiarioNuevoEditar = sdp;
		String titulo = "Editar Subdiario " + sdp.getNumero();
		if (this.subDiarioNuevoEditar.isEsNuevo() == true) {
			titulo = "Nuevo Subdiario";
		}

		String pathPopapZul = "/yhaguy/gestion/contabilidad/subdiario/subDiarioPopup.zul";

		WindowPopup wp = new WindowPopup();
		wp.setTitulo(titulo);
		wp.setModo(WindowPopup.NUEVO);
		wp.setDato(this);
		wp.setWidth("820px");
		wp.setHigth("600px");
		wp.setCheckAC(new SubdiarioNuevoVerificaAceptarCancelar(
				this.subDiarioNuevoEditar));
		wp.show(pathPopapZul);
		if (wp.isClickAceptar()) {
			this.subDiarioNuevoEditar
					.setAuxi(Configuracion.SUBDIARIO_CARGA_MANUAL);
			this.subDiarioNuevoEditar.setConfirmado(true);
			if (this.subDiarioNuevoEditar.isEsNuevo() == true) {
				MyPair suc = this.getAcceso().getSucursalOperativa();
				String numero = AutonumeroYhaguy.getNumeroSubDiario(suc,
						(Date) this.subDiarioNuevoEditar.getFecha());
				this.subDiarioNuevoEditar.setNumero(numero);
			}
			AssemblerSubDiario ass = new AssemblerSubDiario();
			Domain d = ass.dtoToDomain(this.subDiarioNuevoEditar);

			this.saveDTO(this.subDiarioNuevoEditar, ass);
			this.buscarSubdiarios();
		}

	}

	@Command
	@NotifyChange("*")
	public void reporteSubdiario() {
		ReporteSubdiario rSD = new ReporteSubdiario();
		rSD.setListaSD(this.getSubdiarios());
		rSD.setDesde(this.getFechaDesde());
		rSD.setDesde(this.getFechaHasta());
		ViewPdf pdf = new ViewPdf();
		pdf.showReporte(rSD, this);

	}

	public List<SubDiarioDTO> getSubdiarios() {
		return subdiarios;
	}

	public void setSubdiarios(List<SubDiarioDTO> subdiarios) {
		this.subdiarios = subdiarios;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public SubDiarioDTO getSubDiarioNuevoEditar() {
		return subDiarioNuevoEditar;
	}

	public void setSubDiarioNuevoEditar(SubDiarioDTO subDiarioNuevoEditar) {
		this.subDiarioNuevoEditar = subDiarioNuevoEditar;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public SubDiarioDetalleDTO getSubDiarioDetalleNuevo() {
		return subDiarioDetalleNuevo;
	}

	public void setSubDiarioDetalleNuevo(
			SubDiarioDetalleDTO subDiarioDetalleNuevo) {
		this.subDiarioDetalleNuevo = subDiarioDetalleNuevo;
	}

	public boolean isDebe() {
		return debe;
	}

	public void setDebe(boolean debe) {
		this.debe = debe;
	}

	public boolean isHaber() {
		return haber;
	}

	public void setHaber(boolean haber) {
		this.haber = haber;
	}

	public boolean isNuevoSD() {
		return nuevoSD;
	}

	public void setNuevoSD(boolean nuevoSD) {
		this.nuevoSD = nuevoSD;
	}

	public boolean isHabilitarImprimir() {
		return habilitarImprimir;
	}

	public void setHabilitarImprimir(boolean habilitarImprimir) {
		this.habilitarImprimir = habilitarImprimir;
	}

}

class SubdiarioNuevoVerificaAceptarCancelar implements VerificaAceptarCancelar {

	String mensaje = "";

	SubDiarioDTO subDiario = null;

	public SubdiarioNuevoVerificaAceptarCancelar(SubDiarioDTO subDiario) {
		this.subDiario = subDiario;
	}

	@Override
	public boolean verificarAceptar() {
		this.mensaje = "Verificar las siguientes anotaciones:";
		boolean cargar = true;
		if (this.subDiario.getFecha().toString().length() == 1) {
			this.mensaje += "\n\n - Seleccionar una fecha.";
			cargar = false;
		}
		if (this.subDiario.getDescripcion() == "") {
			this.mensaje += "\n - Introducir una descripción.";
			cargar = false;
		}
		if (this.subDiario.getDetalles().size() == 0) {
			this.mensaje += "\n - Agregar los detalles del subdiario.";
			cargar = false;
		}
		if (!debeIgualHaber()) {
			this.mensaje += "\n - El asiento no balancea.";
			cargar = false;
		}

		return cargar;
	}

	public boolean debeIgualHaber() {
		double importe = (double) 0;
		for (SubDiarioDetalleDTO sd : this.subDiario.getDetalles()) {
			importe += sd.getImporte();
		}
		if (importe == (double) 0) {
			return true;
		} else {
			return false;
		}
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
