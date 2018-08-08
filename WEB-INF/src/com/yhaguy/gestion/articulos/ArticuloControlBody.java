package com.yhaguy.gestion.articulos;

import java.io.IOException;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.MyArray;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Barcode;

public class ArticuloControlBody extends BodyApp {
	
	static final String ZUL_ADD_PROVEEDOR = "/yhaguy/gestion/articulos/articuloProveedor.zul";
	static final String ZUL_VER_IMAGEN = "/yhaguy/gestion/articulos/imagenArticulo.zul";
	
	static String[] attArticulo = { "codigoInterno", "descripcion" };
	static String[] colArticulo = { "Código", "Descripción" };
	
	static String[] attUbicacion = { "estante", "fila", "columna" };
	static String[] colUbicacion = { "Estante", "Fila", "Columna" };

	private ArticuloDTO dto = new ArticuloDTO();
	
	private String mensajeError;
	private String codigoInterno;
	
	private UtilDTO utilDto = this.getDtoUtil();
	private ProveedorArticuloDTO nvoProveedorArticulo;	
	private List<ProveedorArticuloDTO> selectedsArtProveedor;
	
	private MyArray articuloReferencia;	
	private List<MyArray> selectedArtReferencia;
	private List<MyArray> selectedUbicaciones;
	
	@Wire
	private Button verImg;

	@Init(superclass = true)
	public void init() {
		Clients.evalJavaScript("setImage('" + this.dto.getUrlImagen_() + "')");
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
		this.verImg.setDisabled(false);
	}

	@Override
	public Assembler getAss() {
		return new AssemblerArticulo();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (ArticuloDTO) dto;
		this.codigoInterno = this.dto.getCodigoInterno();
		Clients.evalJavaScript("setImage('" + this.dto.getUrlImagen_() + "')");
	}

	@Override
	public DTO nuevoDTO() {
		ArticuloDTO out = new ArticuloDTO();
		this.sugerirValores(out);
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return Articulo.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}
	
	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.NORMAL;
	}

	@Override
	public String getCtrAgendaKey() {
		return this.dto.getCodigoInterno();
	}

	@Override
	public String getCtrAgendaTitulo() {
		return "[Artículo: " + this.getCtrAgendaKey() + "]";
	}

	@Override
	public boolean getAgendaDeshabilitado() throws Exception {
		return true;
	}

	@Override
	public boolean verificarAlGrabar() {
		try {
			boolean out = this.validarFormulario();
			if (out == true) {
				Barcode.generarBarcode(this.dto.getCodigoInterno(), this.dto.getDescripcion());
			}
			return out;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}
	
	@Override
	public Browser getBrowser() {
		return new ArticuloBrowser();
	}
	
	/********************** COMANDOS **********************/

	@Command
	@NotifyChange("*")
	public void asignarProveedor() throws Exception {
		this.addProveedor();
	}	
	
	@Command
	@NotifyChange("*")
	public void deleteArtProveedor() {
		this.eliminarArtProveedor();
	}
	
	@Command
	@NotifyChange("*")
	public void asignarReferencia() throws Exception {
		this.buscarArticulo();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteReferencia() {
		this.eliminarReferencia();
	}
	
	@Command
	@NotifyChange("*")
	public void asignarUbicacion() throws Exception {
		this.buscarUbicaciones();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteUbicacion() {
		this.eliminarUbicacion();
	}	
	
	@Command
	public void verImagen() throws Exception {
		this.showImage();
	}
	
	@Listen("onUpload=#upImg")
	public void uploadImage(UploadEvent event) throws IOException {
		this.subirImagen(event);
	}
	
	/******************************************************/
	
	
	/********************** FUNCIONES *********************/
	
	/**
	 * setea valores por defecto..
	 */
	private void sugerirValores(ArticuloDTO articulo) {
		articulo.setArticuloEstado(this.utilDto.getArticuloEstadoActivo());
		articulo.setArticuloLinea(this.utilDto.getArticuloLinea().get(0));
		articulo.setArticuloParte(this.utilDto.getArticuloParte().get(0));
		articulo.setArticuloMarca(this.utilDto.getArticuloMarca().get(0));
		articulo.setArticuloFamilia(this.utilDto.getArticuloFamilia().get(0));
		articulo.setArticuloUnidadMedida(this.utilDto.getArticuloUnidadMedida().get(0));
	}
	
	/**
	 * asigna un proveedor al articulo..
	 */
	private void addProveedor() throws Exception {
		this.nvoProveedorArticulo = new ProveedorArticuloDTO();
		
		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Asignar Proveedor..");		
		wp.setHigth("250px");		
		wp.setWidth("450px");
		wp.setCheckAC(new validadorAddProveedor());
		wp.setDato(this);
		wp.show(ZUL_ADD_PROVEEDOR);
		if (wp.isClickAceptar()) {
			this.dto.getProveedorArticulos().add(this.nvoProveedorArticulo);
		}
		this.nvoProveedorArticulo = null;
	}
	
	/**
	 * elimina las asignaciones de proveedor..
	 */
	private void eliminarArtProveedor() {
		if (this.mensajeSiNo("Desea eliminar los ítems seleccionados..") == false)
			return;
		for (ProveedorArticuloDTO item : this.selectedsArtProveedor) {
			this.dto.getProveedorArticulos().remove(item);
		}
		this.selectedsArtProveedor = null;
	}
	
	/**
	 * asignacion de articulo referencia..
	 */
	public void buscarArticulo() throws Exception {
		BuscarElemento be = new BuscarElemento();
		be.setTitulo("Buscar Artículo");
		be.setClase(Articulo.class);
		be.setAtributos(attArticulo);
		be.setNombresColumnas(colArticulo);
		be.setAnchoColumnas(new String[]{"150px", ""});
		be.setWidth("750px");
		be.show("");		
		if (be.isClickAceptar()) {
			this.articuloReferencia = be.getSelectedItem();
			this.articuloReferencia.setPos3((long)-1);	
			this.dto.getReferencias().add(this.articuloReferencia);
		}
	}
	
	/**
	 * eliminar asignacion de referencias..
	 */
	private void eliminarReferencia() {
		if(this.mensajeSiNo("Desea eliminar los ítems seleccionados..") == false)
			return;
		this.dto.getReferenciasDeleted().addAll(this.selectedArtReferencia);
		this.dto.getReferencias().removeAll(this.selectedArtReferencia);		
		this.selectedArtReferencia = null;
	}
	
	/**
	 * asignacion de articulo referencia..
	 */
	public void buscarUbicaciones() throws Exception {
		BuscarElemento be = new BuscarElemento();
		be.setTitulo("Buscar Ubicación");
		be.setClase(ArticuloUbicacion.class);
		be.setAtributos(attUbicacion);
		be.setNombresColumnas(colUbicacion);
		be.setWidth("600px");
		be.show("");		
		if (be.isClickAceptar()) {
			this.dto.getUbicaciones().add(be.getSelectedItem());
		}
	}
	
	/**
	 * eliminar asignacion de ubicacion..
	 */
	private void eliminarUbicacion() {
		if (this.mensajeSiNo("Desea eliminar los ítems seleccionados..") == false)
			return;
		this.dto.getUbicaciones().removeAll(this.selectedUbicaciones);
		this.selectedUbicaciones = null;
	}
	
	/**
	 * upload de la imagen..
	 */
	@SuppressWarnings("static-access")
	private void subirImagen(UploadEvent event) throws IOException {
		String fileName = this.dto.getCodigoInterno();
		String folder = Configuracion.PATH_IMAGENES_ARTICULOS;
		String format = "." + event.getMedia().getFormat();

		if (this.m.uploadFile(folder, fileName, event, this.m.TIPO_IMAGEN) == true) {
			this.dto.setUrlImagen(fileName + format);
		}
		BindUtils.postNotifyChange(null, null, this, "*");
		Clients.showNotification("Imagen correctamente subida..");
	}
	
	/**
	 * muestra la imagen en un modal..
	 */
	private void showImage() throws Exception {
		WindowPopup w = new WindowPopup();
		w.setDato(this);
		w.setModo(WindowPopup.NUEVO);
		w.setWidth("800px");
		w.setHigth("550px");
		w.setSoloBotonCerrar();
		w.setTitulo("Imagen del Artículo..");
		w.show(ZUL_VER_IMAGEN);
	}
	
	/**
	 * @return la validacion de datos antes de guardar..
	 */
	private boolean validarFormulario() throws Exception {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";

		if ((this.dto.getCodigoInterno() == null)
				|| (this.dto.getCodigoInterno().isEmpty())) {
			this.mensajeError += "\n - Debe ingresar el código interno..";
			out = false;
		}

		if ((this.dto.getDescripcion() == null)
				|| (this.dto.getDescripcion().isEmpty())) {
			this.mensajeError += "\n - Debe ingresar la descripción..";
			out = false;
		}

		if ((this.dto.esNuevo())
				&& (this.isCodigoDuplicado(this.dto.getCodigoInterno()))) {
			this.mensajeError += "\n - Ya existe un artículo con el mismo Código Interno..";
			out = false;
		}
		
		if ((this.dto.esNuevo() == false)
				&& (this.dto.getCodigoInterno().equals(this.codigoInterno) == false)
				&& (this.isCodigoDuplicado(this.dto.getCodigoInterno()))) {
			this.mensajeError += "\n - Ya existe un artículo con el mismo Código Interno..";
			out = false;
		}

		if (out == true && this.dto.esNuevo() == true) {
			this.addEventoAgenda("Se creó nuevo artículo.");
			this.grabarEventosAgenda();
		}
		return out;
	}
	
	/******************************************************/
	
	/**
	 * Validador asignacion de proveedor..
	 */
	class validadorAddProveedor implements VerificaAceptarCancelar {
		
		private String mensaje;

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if (nvoProveedorArticulo.getProveedor() == null) {
				out = false;
				this.mensaje += "\n - Debe asignar el proveedor..";
			}
			
			if (nvoProveedorArticulo.getDescripcionArticuloProveedor().isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar la descripción..";
			}
			
			if (nvoProveedorArticulo.getCodigoFabrica().isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar el código..";
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
	 * Validador asignar referencia..
	 */
	class ValidadorAddReferencia implements VerificaAceptarCancelar {

		private String mensaje;
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if (articuloReferencia.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe seleccionar un artículo..";
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
	
	/******************************************************/

	
	/*********************** GET/SET **********************/
	
	/**
	 * @return true si ya existe el codigo en la bd..
	 */
	private boolean isCodigoDuplicado(String codigo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticuloByCodigoInterno(codigo) != null;
	}
	
	@DependsOn({"deshabilitado", "selectedsArtProveedor"})
	public boolean isDeleteArtProvDisabled() {
		return this.isDeshabilitado() || this.selectedsArtProveedor == null
				|| this.selectedsArtProveedor.size() == 0;
	}
	
	@DependsOn({"deshabilitado", "selectedArtReferencia"})
	public boolean isDeleteArtRefDisabled() {
		return this.isDeshabilitado() || this.selectedArtReferencia == null
				|| this.selectedArtReferencia.size() == 0;
	}
	
	@DependsOn({"deshabilitado", "selectedUbicaciones"})
	public boolean isDeleteUbicacionDisabled() {
		return this.isDeshabilitado() || this.selectedUbicaciones == null
				|| this.selectedUbicaciones.size() == 0;
	}
	
	public String getTipoFamilia() {
		return Configuracion.SIGLA_ARTICULO_FAMILIA_DEFAULT;
	}

	public String getTipoParte() {
		return Configuracion.SIGLA_ARTICULO_PARTE_DEFAULT;
	}

	public String getTipoMarca() {
		return Configuracion.SIGLA_ARTICULO_MARCA_DEFAULT;
	}

	public String getTipoLinea() {
		return Configuracion.SIGLA_ARTICULO_LINEA_DEFAULT;
	}

	public ArticuloDTO getDto() {
		return dto;
	}

	public void setDto(ArticuloDTO dto) {
		this.dto = dto;
	}

	public ProveedorArticuloDTO getNvoProveedorArticulo() {
		return nvoProveedorArticulo;
	}

	public void setNvoProveedorArticulo(ProveedorArticuloDTO nvoProveedorArticulo) {
		this.nvoProveedorArticulo = nvoProveedorArticulo;
	}

	public List<ProveedorArticuloDTO> getSelectedsArtProveedor() {
		return selectedsArtProveedor;
	}

	public void setSelectedsArtProveedor(
			List<ProveedorArticuloDTO> selectedsArtProveedor) {
		this.selectedsArtProveedor = selectedsArtProveedor;
	}

	public MyArray getArticuloReferencia() {
		return articuloReferencia;
	}

	public void setArticuloReferencia(MyArray articuloReferencia) {
		this.articuloReferencia = articuloReferencia;
	}

	public List<MyArray> getSelectedArtReferencia() {
		return selectedArtReferencia;
	}

	public void setSelectedArtReferencia(List<MyArray> selectedArtReferencia) {
		this.selectedArtReferencia = selectedArtReferencia;
	}

	public List<MyArray> getSelectedUbicaciones() {
		return selectedUbicaciones;
	}

	public void setSelectedUbicaciones(List<MyArray> selectedUbicaciones) {
		this.selectedUbicaciones = selectedUbicaciones;
	}
}
