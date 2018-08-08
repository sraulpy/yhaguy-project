package com.yhaguy.gestion.compras.importacion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.csv.CSV;
import com.coreweb.extras.email.CuerpoCorreo;
import com.coreweb.extras.email.EnviarCorreo;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.articulos.ArticuloDTO;
import com.yhaguy.gestion.articulos.AssemblerArticulo;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresaMovimiento;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

@SuppressWarnings("static-access")
public class ImportacionPedidoCompraControlBody extends BodyApp {

	private ImportacionPedidoCompraDTO dto = new ImportacionPedidoCompraDTO();	
	
	private String filterNumero = "";
	private ImportacionPedidoCompra selectedImportacion;
	
	private ImportacionAplicacionAnticipoDTO selectedAnticipo;

	@Init(superclass = true)
	public void init() {		
	}

	@AfterCompose(superclass = true)
	public void afterCompose() throws Exception {
		seleccionarTab();
	}

	@Override
	public Assembler getAss() {
		return new AssemblerImportacionPedidoCompra();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (ImportacionPedidoCompraDTO) dto;
		this.prorrateado = this.dto.isCifProrrateado();

		if (this.dto.isImportacionAnulada() == true) {
			this.enmascarar();
			return;
		}

		if (this.dto.getImportacionFactura().size() > 0) {
			this.setSelectedImportacionFactura(this.dto.getImportacionFactura()
					.get(0));
			this.setSelectedRecepcion(this.dto.getImportacionFactura().get(0));
		} else {
			this.selectedImportacionFactura = new ImportacionFacturaDTO();
			this.selectedRecepcion = new ImportacionFacturaDTO();
		}
		this.setearValoresDespacho();
		this.desEnmascarar();
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		ImportacionPedidoCompraDTO out = new ImportacionPedidoCompraDTO();			
		tab1.setSelected(true);
		this.sugerirValores(out);
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return ImportacionPedidoCompra.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}	
	
	public Browser getBrowser(){
		return new ImportacionPedidoCompraBrowser();
	}
	
	public void clickCtrI() throws Exception{
		this.insertarItem();	
		BindUtils.postNotifyChange(null, null, this, "*");
	}

	Misc misc = this.m;			
	
	@Command
	@NotifyChange("*")
	public void importarDetalles(@BindingParam("comp") Popup comp) throws Exception {
		AssemblerImportacionPedidoCompra ass = new AssemblerImportacionPedidoCompra();
		ImportacionPedidoCompraDTO imp = (ImportacionPedidoCompraDTO) ass.domainToDto(this.selectedImportacion);
		for (ImportacionPedidoCompraDetalleDTO item : imp.getImportacionPedidoCompraDetalle()) {
			item.setId((long) -1);
		}
		this.dto.getImportacionPedidoCompraDetalle().addAll(imp.getImportacionPedidoCompraDetalle());
		Clients.showNotification("TOTAL ÍTEMS IMPORTADOS: " + imp.getImportacionPedidoCompraDetalle().size());
		comp.close();
		this.selectedImportacion = null;
	}
	
	/************************** ELIMINAR ITEM DETALLE ORDEN COMPRA ****************************/
	
	private List<ImportacionPedidoCompraDetalleDTO> selectedOrdenItems = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
	private String ordenItemsEliminar;

	@Command
	@NotifyChange("*")
	public void eliminarItem() {
		if (this.operacionValidaPedido(ELIMINAR_ITEM_ORDEN) == false) {
			return;
		}		
		if (this.confirmarEliminarItemOrden() == true) {
			for (ImportacionPedidoCompraDetalleDTO d : this.selectedOrdenItems) {
				this.dto.getImportacionPedidoCompraDetalle().remove(d);
			}
			this.selectedOrdenItems = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
		}
	}	
	
	private boolean confirmarEliminarItemOrden(){
		this.ordenItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ImportacionPedidoCompraDetalleDTO d : this.selectedOrdenItems) {
			this.ordenItemsEliminar = this.ordenItemsEliminar + "\n - " + d.getArticulo().getCodigoInterno();
		}		
		return this.mensajeSiNo(this.ordenItemsEliminar);
	}

	/******************************************************************************************/	
	
	
	/********************************** INSERTAR ITEM MANUAL **********************************/

	private ImportacionPedidoCompraDetalleDTO newDetalle = new ImportacionPedidoCompraDetalleDTO();
	
	@Command @NotifyChange("*")
	public void insertarItem() throws Exception {
		if (this.operacionValidaPedido(INSERTAR_ITEM_ORDEN) == false) {
			return;
		}		
		try {
			this.newDetalle = new ImportacionPedidoCompraDetalleDTO();
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Insertar Item");
			w.setWidth("450px");
			w.setHigth("310px");
			w.setDato(this);
			w.setCheckAC(new ValidadorInsertarItemOrdenCompra(this));
			w.show(Configuracion.INSERTAR_ITEM_PEDIDO_COMPRA_ZUL);
			if (w.isClickAceptar()) {
				this.newDetalle.setUltimoCostoDs(this.getUltimoCosto(this.newDetalle.getArticulo().getId()));
				this.dto.getImportacionPedidoCompraDetalle().add(this.newDetalle);
				this.selectedOrdenItems.add(newDetalle);
			}			
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}
	}	
	
	private double getUltimoCosto(long idArticulo){		
		double out = 0;
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			out = rr.getUltimoCostoFOBDs(idArticulo, 0); //arreglar!!!
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	/******************************************************************************************/	
	
	
	/********************************** UPLOAD DE ARCHIVOS CSV ********************************/
	
	private String nombreArchivoCSV;
	
	//Upload Pedido CSV
	@Listen("onUpload=#pedidoCompraUpload")
	public void uploadPedidoCompra(UploadEvent event){
		
		if (this.operacionValidaPedido(UPLOAD_PEDIDO_COMPRA) == false) {
			return;
		}
		
		String name = Configuracion.NRO_IMPORTACION_PEDIDO + "_" + misc.getIdUnique();
		String path = Configuracion.pathOrdenCompra;
		this.setPathCsvPedidoCompra(path + name + ".csv");
		this.setLinkCsvImportado(Configuracion.pathOrdenCompraGenerico + name + ".csv");
		this.nombreArchivoCSV = name;
		
		Media media = event.getMedia();
		boolean isText = "text/csv".equals(media.getContentType());
		
		InputStream file = new ByteArrayInputStream(isText? media.getStringData().getBytes() : media.getByteData());
		misc.uploadFile(path, name, ".csv", file);
		
		this.csvPedidoCompra();
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	//Upload Proforma CSV
	@Listen("onUpload=#proformaUpload")
	public void uploadProforma(UploadEvent event) {

		if (this.operacionValidaPedido(UPLOAD_PROFORMA) == false) {
			return;
		}

		String name = Configuracion.NRO_IMPORTACION_PEDIDO + "_"
				+ misc.getIdUnique();
		String path = Configuracion.pathProforma;
		this.setPathCsvPedidoCompra(path + name + ".csv");
		this.setLinkCsvImportado(Configuracion.pathProformaGenerico + name
				+ ".csv");
		this.nombreArchivoCSV = name;

		Media media = event.getMedia();
		boolean isText = "text/csv".equals(media.getContentType());
		
		InputStream file = isText ? new ByteArrayInputStream(media.getStringData()
				.getBytes()) : new ByteArrayInputStream(media.getByteData());

		misc.uploadFile(path, name, ".csv", file);
		this.csvProforma();
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	/******************************************************************************************/	
	

	/********************************* CSV PEDIDO COMPRA **************************************/
	
	/**
	 * En el evento onUpload() se guarda en el 'pathCsvPedidoCompra' la ruta completa donde
	 * se guardo el archivo y en 'linkCsvImportado' el link para acceder al mismo desde el browser.
	 * Se ejecuta la funcion csvPedidoCompra() que invoca al metodo start() de la clase CSV
	 * pasandole la ruta completa del archivo 'pathCsvPedidoCompra'. Si el formato del archivo es
	 * el correcto recorre el mismo y por cada fila genera un detalle en el Pedido Compra.
	 * Si encuentra articulos nuevos Solicita confirmacion para continuar, en caso que se confirme 
	 * crea un nuevo articulo en la BD con el estado 'Temporal' mediante la funcion nuevoArticulo().
	 * Si se importo mas de una vez verifica las diferencias..
 	 */

	private String pathCsvPedidoCompra = "";
	private String linkCsvImportado = "";	
	
	public void csvPedidoCompra() {	
		
		// Se declaran los parametros para pasar a la clase CSV
		String[][] cab = { { "Cliente", CSV.STRING }, { "Fecha", CSV.DATE },
				{ "Codigo", CSV.STRING }, { "Tipo de Compra", CSV.STRING } };

		String[][] cabDet = { { "codigo", CSV.STRING },
				{ "cantidad", CSV.NUMERICO }, { "articulo", CSV.STRING },
				{ "valor", CSV.NUMERICO }, { "total", CSV.NUMERICO } };

		RegisterDomain rr = RegisterDomain.getInstance();
		try {

			CSV csv = new CSV(cab, cabDet, this.getPathCsvPedidoCompra());
			List<ImportacionPedidoCompraDetalleDTO> detalles = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
			if (this.verificarCabeceraCSV(csv.getCabecera("Codigo")) == false) {
				this.mensajeError("El Proveedor seleccionado es distinto al ingresado en el archivo csv.. "
						+ "\n Favor verifique..");
				return;
			}

			csv.start();
			while (csv.hashNext()) {

				Articulo art = rr.getArticulo(csv.getDetalleString("codigo"));	
				ArticuloDTO articulo = null;

				if (art == null) {
										
					/*if (mensajeAgregar(Configuracion.TEXTO_ARTICULO_INEXISTENTE
							+ csv.getDetalleString("codigo") + "\n"	+ Configuracion.TEXTO_PREGUNTA_CONTINUAR) == true) {
						articulo = nuevoArticulo(csv.getDetalleString("codigo"), csv.getDetalleString("articulo"));
					} else {
						return;
					}*/
					this.mensajeError(Configuracion.TEXTO_ARTICULO_INEXISTENTE 
							+ csv.getDetalleString("codigo"));
					return;
					
				} else {									
					articulo = (ArticuloDTO) new AssemblerArticulo().domainToDto(art);
				}				
				
				ImportacionPedidoCompraDetalleDTO d = this.nvoItemOrden(
						articulo, csv.getDetalleDouble("cantidad"), (double) 0);
				detalles.add(d);				
			}
			this.diferenciasCSV = this.buscarDiferenciasCSV(detalles);
			if (diferenciasCSV.size() > 0) {
				this.showDiferenciasCSVPedidoCompra();
			} else {
				this.dto.setImportacionPedidoCompraDetalle(detalles);
			}
			this.addEventoAgendaLink( Configuracion.TEXTO_PEDIDO_COMPRA_CSV_IMPORTADO + this.nombreArchivoCSV, this.linkCsvImportado);

		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}

	private ImportacionPedidoCompraDetalleDTO nvoItemOrden(ArticuloDTO articulo, Double cantidad, double costoProformaDs) {

		ImportacionPedidoCompraDetalleDTO item = new ImportacionPedidoCompraDetalleDTO();
		item.setArticulo(articulo);
		item.setCantidad(cantidad.intValue());
		item.setCostoProformaDs(costoProformaDs);
		item.setCostoProformaGs(costoProformaDs * this.dto.getCambio());
		item.setUltimoCostoDs(this.getUltimoCosto(articulo.getId()));
		
		return item;
	}

	//Crea un nuevo articulo y lo setea como temporal..
	private ArticuloDTO nuevoArticulo(String codigoInterno, String descripcion)
			throws Exception {
		
		MyArray m = new MyArray();
		m.setId(new Long(1));
		MyPair mp = new MyPair();
		mp.setId(new Long(1));
		MyPair me = new MyPair();
		me.setId(Configuracion.ID_ESTADO_ARTICULO_TEMPORAL);

		ArticuloDTO out = new ArticuloDTO();
		out.setCodigoInterno(codigoInterno);
		out.setDescripcion(Configuracion.TEXTO_NUEVO_ARTICULO + descripcion);
		out.setArticuloPresentacion(m);
		out.setArticuloUnidadMedida(mp);
		out.setArticuloEstado(me);
		out.setArticuloFamilia(mp);
		out.setArticuloMarca(mp);
		out.setArticuloParte(mp);
		out.setArticuloLinea(mp);
		out.setArticuloUnidadMedida(mp);
		
		return out;
	}

	/******************************************************************************************/	
	

	/****************************** DIFERENCIAS CSV PEDIDO COMPRA *****************************/

	/**
	 * 1- Se importa el Pedido Compra CSV mas de una vez: 1.1- Si es un nuevo
	 * Pedido Pisa los datos anteriores. 1.2- Si no es un nuevo Pedido despliega
	 * las diferencias encontradas. 1.2.1- Las diferencias que despliega pueden
	 * ser: - Item que esta en el csv y no en el detalle del pedido. - Item que
	 * esta en el detalle del Pedido y no en el csv. - Item con cantidad
	 * diferente.
	 **/

	public static int MOTIVO_NUEVO = 1;
	public static int MOTIVO_CANTIDAD = 2;
	public static int MOTIVO_NO_ENCONTRADO = 3;
	
	private List<ImportacionPedidoCompraDetalleDTO> diferenciasCSV = new ArrayList<ImportacionPedidoCompraDetalleDTO>();	

	public List<ImportacionPedidoCompraDetalleDTO> buscarDiferenciasCSV(
			List<ImportacionPedidoCompraDetalleDTO> detalles) throws Exception {
		List<ImportacionPedidoCompraDetalleDTO> out = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
		if (this.dto.esNuevo() == false) {
			for (ImportacionPedidoCompraDetalleDTO csv : detalles) {
				boolean encontro = false;
				for (ImportacionPedidoCompraDetalleDTO dto : this.dto
						.getImportacionPedidoCompraDetalle()) {
					if (csv.getArticulo().getId() == dto.getArticulo().getId()) {
						encontro = true;
						if (misc.esIgual(csv.getCantidad(), dto.getCantidad()) == false) {
							csv.setMotivo(MOTIVO_CANTIDAD);
							csv.setCantidadSistema(dto.getCantidad());
							out.add(csv);
						}
						if (csv.getCostoProformaDs() > 0.001) {
							throw new Exception(Configuracion.TEXTO_ERROR_IMPORTAR_PEDIDO_COMPRA);
						}
					}
				}

				if (encontro == false) {
					csv.setMotivo(MOTIVO_NUEVO);
					csv.setCantidadSistema(csv.getCantidad());
					out.add(csv);
				}
			}

			for (ImportacionPedidoCompraDetalleDTO dto : this.dto
					.getImportacionPedidoCompraDetalle()) {
				boolean encontro = false;
				for (ImportacionPedidoCompraDetalleDTO csv : detalles) {
					if (csv.getArticulo().getId() == dto.getArticulo().getId()) {
						encontro = true;
					}
				}

				if (encontro == false) {
					dto.setMotivo(MOTIVO_NO_ENCONTRADO);
					dto.setCantidadSistema(dto.getCantidad());
					out.add(dto);
				}
			}
		}
		return out;
	}

	public void showDiferenciasCSVPedidoCompra(){
		try {
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Diferencias encontradas al Importar el Archivo csv");
			w.setWidth("1200px");
			w.setDato(this);
			w.setCheckAC(new ValidadorDiferenciasCSV(this));
			w.show(Configuracion.DIFERENCIAS_CSV_ZUL);
			if (w.isClickAceptar()) {
				for (ImportacionPedidoCompraDetalleDTO dif : this.diferenciasCSV) {
					if (dif.isChecked()) {						
						this.modificarDetalle(dif, dif.getMotivo());
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}		
	}
	
	public void modificarDetalle(ImportacionPedidoCompraDetalleDTO item, int motivo){
		if (motivo == MOTIVO_NUEVO) {
			item.setCantidad(item.getCantidadSistema());
			this.dto.getImportacionPedidoCompraDetalle().add(item);
		}
		if (motivo == MOTIVO_CANTIDAD || motivo == MOTIVO_NO_ENCONTRADO) {
			for (ImportacionPedidoCompraDetalleDTO detDTO : this.dto.getImportacionPedidoCompraDetalle()) {				
				if (detDTO.getArticulo().getId() == item.getArticulo().getId()) {
					detDTO.setCantidad(item.getCantidadSistema());	
					detDTO.setChecked(true);
				}
			}
		}
	}

	/******************************************************************************************/	
	

	/*************************************** CSV PROFORMA *************************************/
	
	/**
	 * En el evento OnUpload() se guarda en el 'pathCsvPedidoCompra' la ruta completa donde
	 * se guardo el archivo y en 'linkCsvImportado' el link para acceder al mismo desde el browser.
	 * Se ejecuta la funcion csvProforma() que invoca al metodo start() de la clase CSV
	 * pasandole la ruta completa del archivo 'pathCsvPedidoCompra'. Si el formato del archivo es
	 * el correcto recorre el mismo y por cada fila genera un detalle en el Pedido Compra.
	 * Si encuentra articulos nuevos Solicita confirmacion para continuar, en caso que se confirme 
	 * agrega el mismo al detalle. Si se importo mas de una vez verifica las diferencias..
 	 */
	
	public void csvProforma() {
		// Se declaran los parametros para pasar a la clase CSV
		String[][] cab = { { "Cliente", CSV.STRING }, { "Fecha", CSV.DATE },
				{ "Codigo", CSV.STRING }, { "Tipo de Compra", CSV.STRING } };

		String[][] cabDet = { { "codigo", CSV.STRING },
				{ "cantidad", CSV.NUMERICO }, { "articulo", CSV.STRING },
				{ "valor", CSV.NUMERICO }, { "total", CSV.NUMERICO } };

		RegisterDomain rr = RegisterDomain.getInstance();

		try {

			CSV csv = new CSV(cab, cabDet, this.getPathCsvPedidoCompra());
			List<ImportacionPedidoCompraDetalleDTO> detalles = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
			if (this.verificarCabeceraCSV(csv.getCabecera("Codigo")) == false) {
				this.mensajeError("El Proveedor seleccionado es distinto al ingresado en el archivo csv.. "
						+ "\n Favor verifique..");
				return;
			}
			Articulo art = null;
			csv.start();
			while (csv.hashNext()) {
				this.verificarCSVProforma(csv.getDetalleString("codigo"));
				art = rr.getArticuloByCodigoInterno(csv.getDetalleString("codigo"));
				ArticuloDTO articulo = (ArticuloDTO) new AssemblerArticulo().domainToDto(art);				
				ImportacionPedidoCompraDetalleDTO d = this.nvoItemOrden(articulo, 
						csv.getDetalleDouble("cantidad"), csv.getDetalleDouble("valor"));
				detalles.add(d);
			}

			//if (this.diferenciasCSVProforma(detalles) == true) { VERIFICAR!!!!
				csv.start();
				while (csv.hashNext()) {
					art = rr.getArticuloByCodigoInterno(csv.getDetalleString("codigo"));
					boolean encontro = false;
						for (ImportacionPedidoCompraDetalleDTO d : this.dto
								.getImportacionPedidoCompraDetalle()) {							
							if (art.getId().compareTo(d.getArticulo().getId()) == 0) {
								encontro = true;
								d.setCostoProformaDs(csv.getDetalleDouble("valor"));
								d.setCostoProformaGs(d.getCostoProformaDs()	* this.dto.getCambio());
							}
						}
						if (encontro == false) {
							
							ArticuloDTO articulo = (ArticuloDTO) new AssemblerArticulo().domainToDto(art);				
							ImportacionPedidoCompraDetalleDTO d = this.nvoItemOrden(articulo,
											csv.getDetalleDouble("cantidad"), csv.getDetalleDouble("valor"));
							this.dto.getImportacionPedidoCompraDetalle().add(d);
						}
				}

				this.addEventoAgendaLink( Configuracion.TEXTO_PROFORMA_CSV_IMPORTADO + this.nombreArchivoCSV, this.linkCsvImportado);

				this.actualizarEtapaActual(this.getDtoUtil().getImportacionEstadoProformaRecibida());
			//}

		} catch (Exception e) {
			mensajeError(e.getMessage());
		}
	}

	// Verifica los datos de cabecera al importar el Pedido Compra CSV
	public boolean verificarCabeceraCSV(String codigoProveedor) throws Exception {
		boolean out = true;
		if (this.dto.getProveedor().getCodigoEmpresa()
				.compareTo(codigoProveedor) != 0) {
			out = false;
		}
		return out;
	}

	// Verifica los datos al importar el archivo Proforma CSV
	public void verificarCSVProforma(String codigoInterno) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (rr.getArticuloByCodigoInterno(codigoInterno) == null) {
			throw new Exception(
					Configuracion.TEXTO_ERROR_IMPORTAR_CSV_PROFORMA_ITEM_BD
							+ codigoInterno);
		}
	}

	/******************************************************************************************/	
	

	/******************************* DIFERENCIAS CSV PROFORMA *********************************/

	/**
	 * Se importa el Csv Proforma mas de una vez: 1- Si es un nuevo Pedido pisa
	 * los datos anteriores. 2- Si no es un nuevo Pedido lanza una alerta
	 * solicitando continuar o no; -Si el item del csv no se encuentra en el
	 * Detalle del Pedido. -Si el item del Detalle Pedido no se encuentra en el
	 * csv.. -Si el costo Proforma del item no coincide.. -Si encuentra un item
	 * que no esta en la Base de Datos lanza una excepcion.
	 * */

	public boolean diferenciasCSVProforma(List<ImportacionPedidoCompraDetalleDTO> detalle) {
		String mensaje = "";
		if (this.dto.esNuevo() == false) {

			for (ImportacionPedidoCompraDetalleDTO csv : detalle) {
				boolean encontro = false;
				for (ImportacionPedidoCompraDetalleDTO dto : this.dto
						.getImportacionPedidoCompraDetalle()) {
					if (csv.getArticulo().getId() == dto.getArticulo().getId()) {
						encontro = true;
						if ((misc.esIgual(csv.getCostoProformaDs(),
								dto.getCostoProformaDs()) == false)
								&& (dto.getCostoProformaDs() != 0)) {
							// el costo Proforma del item no coincide..
							mensaje = mensaje
									+ Configuracion.TEXTO_NO_COINCIDE_COSTO_PROFORMA
									+ dto.getArticulo().getCodigoInterno()
									+ "\n";
						}
					}
				}

				if (encontro == false) {
					// item del csv no se encuentra en el Detalle Pedido..
					mensaje = mensaje + "- "
							+ Configuracion.TEXTO_ITEM_NO_ENCONTRADO_EN_DETALLE
							+ ": " + csv.getArticulo().getCodigoInterno()
							+ "\n";
				}
			}

			for (ImportacionPedidoCompraDetalleDTO dto : this.dto
					.getImportacionPedidoCompraDetalle()) {
				boolean encontro = false;
				for (ImportacionPedidoCompraDetalleDTO csv : detalle) {
					if (dto.getArticulo().getId() == csv.getArticulo().getId()) {
						encontro = true;
					}
				}

				if (encontro == false) {
					// item del Detalle Pedido no se encuentra en el csv..
					mensaje = mensaje + "- "
							+ Configuracion.TEXTO_ITEM_NO_ENCONTRADO_CSV
							+ " : " + dto.getArticulo().getCodigoInterno()
							+ "\n";
				}
			}
		}
		boolean out = true;

		if (mensaje.length() > 0) {
			out = mensajeAgregar(Configuracion.TEXTO_ALERTA_IMPORTAR_CSV_PROFORMA
					+ "\n"
					+ mensaje
					+ Configuracion.TEXTO_CSV_PROFORMA_PISAR_DATOS
					+ Configuracion.TEXTO_DESEA_CONTINUAR);
		}

		return out;
	}

	/******************************************************************************************/	
	
	
	/************************************* ENVIO DE CORREO ************************************/
	
	private String linkImportacionPedidoCompra = "";
	private String pathRealImportacionPedidoCompra = "";
	
	private CuerpoCorreo correo = new CuerpoCorreo(
			Configuracion.DPTO_IMPORTACION_MAIL_ADDRESS,
			Configuracion.DPTO_IMPORTACION_MAIL_PASSWD,
			Configuracion.TEXTO_CORREO_IMPORTACION);		
		
	@Command
	@NotifyChange("*")
	public void correo(){
		
		if (this.operacionValidaPedido(ENVIAR_CORREO) == false) {
			return;
		}		
		correo.setAsunto(this.getTipoPedido());		
		this.generarAdjuntoCorreo(false);			
		this.showCorreo();
	}
	
	public void showCorreo(){
		try {
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Envio de Correos");
			w.setWidth("750px");
			w.setHigth("500px");
			w.setDato(this);
			w.setCheckAC(new ValidadorEnviarCorreo(this));
			w.show(Configuracion.CORREO_IMPORTACION_ZUL);
			if (w.isClickAceptar()) {				
				this.enviarCorreo();
			} else {
				//si no envia borra el archivo
				this.m.borrarArchivo(this.pathRealImportacionPedidoCompra);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}

	// Genera el informe de Pedido Compra, guarda en un directorio en formato
	// PDF y de ahi adjunta al correo..
	public void generarAdjuntoCorreo(boolean mostrar){
		
		List<Object[]> data = new ArrayList<>();
		Object[] obj;		
		for (ImportacionPedidoCompraDetalleDTO d : this.dto.getImportacionPedidoCompraDetalle()) {
			obj = new Object[4];
			obj[0] = d.getArticulo().getCodigoInterno();
			obj[1] = d.getArticulo().getDescripcion();
			obj[2] = d.getCantidad();
			obj[3] = d.getCostoProformaDs();
			data.add(obj);	
		}
	}	
	
	public void enviarCorreo() {		
		String[] send = correo.getDestinatario().trim().split(";");
		String[] sendCC = correo.getDestinatarioCopia().trim().split(";");
		String[] sendCCO = correo.getDestinatarioCopiaOculta().trim().split(";");

		correo.setAdjunto(this.pathRealImportacionPedidoCompra);
		try {		
			EnviarCorreo enviarCorreo = new EnviarCorreo();			
			enviarCorreo.sendMessage(send, sendCC, sendCCO,
					correo.getAsunto(), correo.getCuerpo(),
					correo.getRemitente(), correo.getClave(), this.getNombreArchivo() + ".pdf",
					correo.getAdjunto());
			
			this.addEventoAgendaLink(Configuracion.TEXTO_CORREO_ENVIADO + correo.getAsunto(), this.linkImportacionPedidoCompra);
			
			this.actualizarEtapaActual(this.getTipoPedidoKey());
			this.m.mensajePopupTemporal(Configuracion.TEXTO_CORREO_ENVIADO_CORRECTAMENTE);			
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}	
	
	public String getTipoPedido() {
		String out = "";
		if (this.dto.isPedidoConfirmado()) {
			out = Configuracion.TEXTO_PEDIDO_CONFIRMADO;
		} else {
			out = Configuracion.TEXTO_SOLICITUD_COTIZACION;
		}
		return out;
	}	

	/******************************************************************************************/
	

	/*********************************** DERIVAR PEDIDO COMPRA ********************************/	

	private Window win;
	
	@Command
	public void abrirPropietariosPedidoCompra() {
		if (this.operacionValidaPedido(DERIVAR_PEDIDO) == false) {
			return;
		}
		this.selectedPropietarioPedidoCompra = null;
		win = (Window) Executions.createComponents(
				Configuracion.LISTA_PROPIETARIOS_PEDIDO_COMPRA_ZUL, this.mainComponent, null);
		win.doModal();
	}

	@Command
	@NotifyChange("*")
	public void selectPropietarioPedidoCompra() throws Exception {

		if (this.selectedPropietarioPedidoCompra
				.compareTo(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION) == 0) {
			this.dto.setPropietarioActual(Configuracion.PROPIETARIO_IMPORTACION_KEY);
		}

		if (this.selectedPropietarioPedidoCompra
				.compareTo(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION) == 0) {
			this.dto.setPropietarioActual(Configuracion.PROPIETARIO_VENTAS_KEY);
		}

		if (this.selectedPropietarioPedidoCompra
				.compareTo(Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION) == 0) {
			this.dto.setPropietarioActual(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
		}

		win.detach();
		this.addEventoAgenda(EVENTO_DERIVAR_ORDEN_COMPRA + this.dto.getPropietario());
	}

	public String getLabelImportacionStyle() {
		String out = "";
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_IMPORTACION_KEY) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}

	public String getLabelVentasStyle() {
		String out = "";
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_VENTAS_KEY) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}

	public String getLabelAdministracionStyle() {
		String out = "";
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}

	/******************************************************************************************/	
	
	
	/***************************** CONFIRMACION ORDEN COMPRA **********************************/

	// Comportamiento Checkbox Importacion (Habilitado/Desabilitado)
	public boolean getCheckboxImportacionEnabled() {
		boolean out = false;
		if ((this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_IMPORTACION_KEY)
				&& (this.dto.isConfirmadoVentas() == true)) {
			out = true;
		}
		return out;
	}

	// Comportamiento Checkbox Ventas (Habilitado/Desabilitado)
	public boolean getCheckboxVentasEnabled() {
		boolean out = false;
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_VENTAS_KEY) {
			out = true;
		}
		return out;
	}

	// Comportamiento Checkbox Administracion (Habilitado/Desabilitado)
	public boolean getCheckboxAdministracionEnabled() {
		boolean out = false;
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			out = true;
		}
		return out;
	}

	@Command @NotifyChange("*")
	public void confirmarPedidoCompra() throws Exception {
		
		if (this.operacionValidaPedido(CONFIRMAR_ORDEN_COMPRA) == false) {
			return;
		}
		
		try {
			if (mensajeSiNo("Desea Confirmar La Orden de Compra Nro.: "
					+ this.dto.getNumeroPedidoCompra() + "?")) {
				this.dto.setPropietarioActual(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
				this.dto.setPedidoConfirmado(true);
				this.dto.setEstado(this.getDtoUtil().getImportacionEstadoPendienteEnvio()); 
				this.addEventoAgenda(EVENTO_CONFIRMAR_ORDEN_COMPRA);
				this.actualizarEtapaActual(this.getDtoUtil().getImportacionEstadoPendienteEnvio());
				
				// Verifica que los datos esten correctos y graba en la BD al confirmar el Pedido			
				if (this.validarFormulario() == true) {
					this.grabarEventosAgenda();
					this.saveDTO(dto);
					this.mensajePopupTemporal(EVENTO_CONFIRMAR_ORDEN_COMPRA);
				}
						
			} else {
				this.dto.setConfirmadoImportacion(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}

	/******************************************************************************************/	
	

	/********************************** DETALLE PEDIDO COMPRA *********************************/

	
	public double getTotalProformaGs() {
		double total = 0;
		for (ImportacionPedidoCompraDetalleDTO d : this.dto
				.getImportacionPedidoCompraDetalle()) {
			total = total + d.getImporteProformaGs();
		}
		return total;
	}

	public double getTotalProformaDs() {
		double total = 0;
		for (ImportacionPedidoCompraDetalleDTO d : this.dto
				.getImportacionPedidoCompraDetalle()) {
			total = total + d.getImporteProformaDs();
		}
		return total;
	}

	@Command
	@NotifyChange({"totalProformaGs", "totalProformaDs", "itemsCostoFinal"})
	public void actualizarCostoProformaDs(
			@BindingParam("detalle") ImportacionPedidoCompraDetalleDTO detalle) {
		double costoGs = detalle.getCostoProformaGs();
		detalle.setCostoProformaDs(costoGs / this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, detalle, "costoProformaDs");
	}

	@Command
	@NotifyChange({"totalProformaGs", "totalProformaDs", "itemsCostoFinal"})
	public void actualizarCostoProformaGs(
			@BindingParam("detalle") ImportacionPedidoCompraDetalleDTO detalle) {
		double costoDs = detalle.getCostoProformaDs();
		detalle.setCostoProformaGs(costoDs * this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, detalle, "costoProformaGs");
	}

	/******************************************************************************************/	
	

	/***************************** ESTADO GENERAL PEDIDO COMPRA *******************************/

	@DependsOn({"dto.importacionAnulada", "dto.pedidoConfirmado"})
	public String getEstadoGeneralPedidoCompra() {
		String out = Configuracion.ESTADO_PEDIDO_COMPRA_ELABORACION;
		if (this.dto.isImportacionAnulada() == true) {
			out = Configuracion.ESTADO_PEDIDO_COMPRA_ANULADO;
			
		} else if (this.dto.isPedidoConfirmado() == true) {
			out = Configuracion.ESTADO_PEDIDO_COMPRA_CONFIRMADO;
		}
		return out;
	}

	/******************************************************************************************/	
	

	/************ ACTUALIZACION AUTOMATICA ETAPA ACTUAL DEL PEDIDO COMPRA *********************/

	public void actualizarEtapaActual(MyPair estado) {
		this.dto.setEstado(estado);
	}

	public MyPair getTipoPedidoKey() {
		MyPair out = null;
		if (this.dto.isPedidoConfirmado()) {
			out = this.getDtoUtil().getImportacionEstadoPedidoEnviado();
		} else {
			out = this.getDtoUtil().getImportacionEstadoSolicitandoCotizacion();
		}
		return out;
	}

	/******************************************************************************************/	
	
	
	/************************** ASIGNACION DEL TIPO DE IMPORTACION ****************************/
	
	private MyPair tipoSelected = new MyPair();
	private boolean prorrateado = false;	
	
	@Command @NotifyChange("*")
	public void asignarTipoImportacion(){
		if (this.operacionValidaPedido(MODIFICAR_TIPO_IMPORTACION) == false) {			
			this.tipoSelected = new MyPair();
			return;
		}
		this.dto.setTipo(this.tipoSelected);
	}
	
	@Command @NotifyChange("*")
	public void checkCifProrrateado(){
		if (this.operacionValidaPedido(MODIFICAR_TIPO_IMPORTACION) == false) {
			this.prorrateado = !this.prorrateado;
			return;
		}
		this.dto.setCifProrrateado(this.prorrateado);
	}
	
	/******************************************************************************************/	
	
	
	/**************************** RECALCULO DE COSTOS MONEDA LOCAL ****************************/
	
	/**
	 * Recalcula los costos de la Orden de Compra y las facturas
	 * Es usado cuando se realizan acciones que afectan a los costos
	 * ej: cuando se modifica la moneda..
	 */
	private void recalcularCostosMonedaLocal(){
		double tipoCambio = this.dto.getCambio();
				
		for (ImportacionPedidoCompraDetalleDTO item : this.dto.getImportacionPedidoCompraDetalle()) {
			double costoGs = item.getCostoProformaDs() * tipoCambio;
			item.setCostoProformaGs(costoGs);
		}
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				double costoGs = item.getCostoDs() * tipoCambio;
				double costoSPgs = item.getCostoSinProrrateoDs() * tipoCambio;
				item.setCostoGs(costoGs);
				item.setCostoSinProrrateoGs(costoSPgs);
			}
			
		}
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	/******************************************************************************************/	
	
	
	/************************************* VALIDACION PEDIDO **********************************/
	
	// Orden Compra
	private static int ELIMINAR_ITEM_ORDEN = 1;
	private static int INSERTAR_ITEM_ORDEN = 2;
	private static int UPLOAD_PEDIDO_COMPRA = 3;
	private static int UPLOAD_PROFORMA = 4;
	private static int ENVIAR_CORREO = 5;
	private static int DERIVAR_PEDIDO = 6;
	private static int CONFIRMAR_ORDEN_COMPRA = 7;
	private static int MODIFICAR_TIPO_IMPORTACION = 8;
	
		
	private boolean operacionValidaPedido(int operacion){
		boolean out = true;
		String mensaje = "No se puede realizar la operación debido a: \n";
		
		if (this.dto.isPedidoConfirmado() 
				&& (operacion != ENVIAR_CORREO)
					&& (operacion != MODIFICAR_TIPO_IMPORTACION)) {
			mensaje += "\n - La Orden de Compra ya fue confirmada.";
			out = false;			
		}
		
		if(operacion == ELIMINAR_ITEM_ORDEN){			
			if (this.selectedOrdenItems.size() == 0) {
				mensaje += "\n - No se ha seleccionado ningún ítem.";
				out = false;
			}			
		}
		
		if (operacion == INSERTAR_ITEM_ORDEN) {
			if (this.dto.getProveedor().esNuevo() == true) {
				mensaje += "\n - Debe seleccionar un Proveedor.";
				out = false;
			}
		}
		
		if (operacion == UPLOAD_PEDIDO_COMPRA) {
			if (this.dto.getProveedor().esNuevo() == true) {
				mensaje += "\n - Debe seleccionar un Proveedor.";
				out = false;
			}
		}
		
		if (operacion == UPLOAD_PROFORMA) {
			if (this.dto.getImportacionPedidoCompraDetalle().size() == 0) {
				mensaje += "\n - Debe ingresar al menos un ítem.";
				out = false;
			}			
		}
		
		if (operacion == ENVIAR_CORREO) {
			if (this.dto.esNuevo()) {
				mensaje += "\n - Debe Guardar los Datos antes de enviar el Correo.";
				out = false;
			}
			if (this.dto.getImportacionPedidoCompraDetalle().size() == 0) {
				mensaje = "\n - Debe ingresar al menos un item.";
				out = false;
			}			
		}
		
		if (operacion == DERIVAR_PEDIDO) {
			if (this.comprobarGrupo() == false) {
				mensaje += "\n - el Propietario actual es: " + this.dto.getPropietario();
				out = false;
			}
			if (this.dto.getImportacionPedidoCompraDetalle().size() == 0) {
				mensaje += "\n - Debe ingresar al menos un ítem.";
				out = false;
			}
		}
		
		if (operacion == CONFIRMAR_ORDEN_COMPRA) {
			
			if (this.dto.esNuevo() == true) {
				mensaje += "\n - Debe grabar la Importación.";
				out = false;
			}
			
			if (this.proformaCargada() == false) {
				mensaje += "\n - Aún no fue ingresado el costo de todos los ítems.";
				out = false;
			}
		}
		
		if (operacion == MODIFICAR_TIPO_IMPORTACION) {
			Object[] obj = this.verificarItemsDeReferencia();
			mensaje += (String) obj[1];
			out = (boolean) obj[0];						
		}
		
		if (out == false) {
			mensajeError(mensaje);
		}
		return out;
	}	
	
	private boolean proformaCargada(){
		boolean out = true;
		for (ImportacionPedidoCompraDetalleDTO d : this.dto.getImportacionPedidoCompraDetalle()) {
			if ((d.getCostoProformaDs() >= 0) && (d.getCostoProformaDs() <= 0.0001)) {
				out = false;
			}
		}
		return out;
	}
	
	
	/**
	 * Verifica si es posible la modificacion del tipo de Importacion
	 * siguiendo la sgte regla sobre las facturas:
	 * -Si se modifica a CIF prorrateado no debe existir items de gastos
	 * -Si se modifica a CIF no prorrateado no debe existir items de prorrateo
	 * -Si se modifica a FOB no debe existir items de prorrateo ni gastos
	 * -Si se modifica a CF no debe existir items de prorrateo ni gasto de flete
	 * @return 
	 * [0]:booleano que indica si es posible modificar
	 * [1]:String con el contenido del mensaje
	 */
	private Object[] verificarItemsDeReferencia(){
		boolean out = true;
		String msg = "- Se ha encontrado los sgts ítems de referencia:";
		
		List<ImportacionFacturaDTO> facs = this.dto.getImportacionFactura();
		Hashtable<Long, ImportacionFacturaDetalleDTO> facsTable = new Hashtable<>();
		
		if (facs.size() == 0) {
			return new Object[]{out, ""};
		}
		
		MyPair tipoSelected = this.tipoSelected;
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		MyPair tipoCF = utilDto.getTipoImportacionCF();
		boolean prorrateado = this.prorrateado;
		
		MyPair itemTipoGastoFlete = utilDto.getTipoCompraGastoFlete();
		MyPair itemTipoGastoSeguro = utilDto.getTipoCompraGastoSeguro();
		MyPair itemTipoProrrFlete = utilDto.getTipoCompraProrrateoFlete();
		MyPair itemTipoProrrSeguro = utilDto.getTipoCompraProrrateoSeguro();
		
		for (ImportacionFacturaDTO fac : facs) {
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				if (item.isGastoDescuento() == true) {
					facsTable.put(item.getTipoGastoDescuento().getId(), item);
				}
			}
		}
		
		ImportacionFacturaDetalleDTO itemGastoFlete = facsTable.get(itemTipoGastoFlete.getId());
		ImportacionFacturaDetalleDTO itemGastoSeguro = facsTable.get(itemTipoGastoSeguro.getId());
		ImportacionFacturaDetalleDTO itemProrrFlete = facsTable.get(itemTipoProrrFlete.getId());
		ImportacionFacturaDetalleDTO itemProrrSeguro = facsTable.get(itemTipoProrrSeguro.getId());
		
		//Si el tipo es CIF prorrateado
		if ((tipoSelected.compareTo(tipoCIF) == 0)
				&& (prorrateado == true)
					&& (itemGastoFlete != null || itemGastoSeguro != null)) {
			out = false;
			msg += "\n - ítems de Gastos.";
		}		
		
		//Si el tipo es CIF pero no prorrateado
		if ((tipoSelected.compareTo(tipoCIF) == 0)
				&& (prorrateado == false)
					&& (itemProrrFlete != null || itemProrrSeguro != null)) {
			out = false;
			msg += "\n - ítems de Prorrateo.";
		}
		
		//Si el tipo es FOB
		if ((tipoSelected.compareTo(tipoFOB) == 0)
				&& (itemProrrFlete != null || itemProrrSeguro != null
						|| itemGastoFlete != null || itemGastoSeguro != null)) {
			out = false;
			msg += "\n - ítems de Prorrateo y/o de Gastos";
		}
		
		//Si el tipo es CF
		if ((tipoSelected.compareTo(tipoCF) == 0)
				&& (itemProrrFlete != null || itemProrrSeguro != null
						|| itemGastoSeguro != null)) {
			out = false;
			msg += "\n - ítems de Prorrateo y/o de seguro";
		}
		
		return new Object[]{out, msg};
	}
	
	/******************************************************************************************/	
	
	
	/***************************************** AGENDA *****************************************/
	
	private static String EVENTO_DERIVAR_ORDEN_COMPRA = "La Orden de Compra fue derivada a: ";
	private static String EVENTO_CONFIRMAR_ORDEN_COMPRA = "Orden Compra Confirmada..";
	private static String EVENTO_IMPORTACION_ANULADA = "La Importación fue anulada - Motivo: ";

	
	@Override
	public int getCtrAgendaTipo(){
		return ControlAgendaEvento.COTIZACION;
	}
	
	@Override
	public String getCtrAgendaKey(){
		return this.dto.getNumeroPedidoCompra();
	}
	
	@Override
	public String getCtrAgendaTitulo(){
		return "[Orden Compra: " + this.getCtrAgendaKey() + "]";
	}
	
	@Override
	public boolean getAgendaDeshabilitado(){
		return this.dto.esNuevo();
	}

	/******************************************************************************************/
	
	
	/***************************************** IMPRIMIR ***************************************/
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showImprimir() {
		this.imprimir();
	}
	
	/******************************************************************************************/
	
	
	/*********************************** INFORMACION ADICIONAL ********************************/
	
	@Override
	public boolean getInformacionDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showInformacion() throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Información Adicional");
		wp.setHigth("400px");
		wp.setWidth("400px");
		wp.setDato(this);
		wp.setSoloBotonCerrar();
		wp.show(Configuracion.INFO_ADICIONAL_IMPORTACION_ZUL);
	}
	
	/******************************************************************************************/
	
	
	/*********************************** ANULAR IMPORTACION ***********************************/
	
	@Override
	public void showAnular() throws Exception {
		
		String motivo = this.getMotivoAnulacion();
		
		if (motivo.length() > 0) {
			this.dto.setEstado(this.getDtoUtil().getImportacionEstadoAnulado());
			this.dto.setReadonly();
			this.saveDTO(this.dto);
			this.enmascarar();
			this.getCtrAgenda().addDetalle(this.getCtrAgendaTipo(),
					this.getCtrAgendaKey(), 0, EVENTO_IMPORTACION_ANULADA + motivo,
					null);
			BindUtils.postNotifyChange(null, null, this.dto, "estado");
		}
	}
	
	/******************************************************************************************/
	
	/**
	 * Importacion en curso
	 */
	@Command
	public void setImportacionEnCurso() throws Exception {
		if (!this.mensajeSiNo("Desea volcar a Importación en Curso..?")) 
			return;
		this.dto.setAuxi(Configuracion.SIGLA_IMPORT_EN_CURSO);
		this.dto = (ImportacionPedidoCompraDTO) this.saveDTO(dto);
		this.mensajePopupTemporal("Volcado a Importación en Curso");
	}
	

	/****************************************** UTILES ****************************************/

	@Wire
	private Tab tab1;	
	@Wire
	private Tab tab2;	
	@Wire
	private Tab tab3;	
	@Wire
	private Tab tab4;	
	@Wire
	private Tab tab5;	
	@Wire
	private Tab tab6;		
	@Wire
	private Tabbox tbx;
	@Wire
	private Tabpanel tp4;
	
	private UtilDTO utilDto = this.getDtoUtil();
	private MyArray ordenCompraImportacion = utilDto.getTmOrdenCompraImportacion();
	
	private void seleccionarTab() throws Exception {
		
		if (this.operacionHabilitada("VerDatosGenerales") == true) {
			tab1.setSelected(true);
			
		} else if (this.operacionHabilitada("VerRecepcion") == true) {
			tab4.setSelected(true);
			tbx.setSelectedPanel(tp4);
		}
	}	
	
	@Command
	@NotifyChange({"totalProformaGs", "totalProformaDs", "totalImporteFactura", 
		"itemsCostoFinal", "totalCostoFinal"})
	public void notificarCambios() {
	}	
	
	private String emptyMsgFactura = "SELECCIONE UNA FACTURA...";

	public String getEmptyMsgFactura() {
		return emptyMsgFactura;
	}

	public void setEmptyMsgFactura(String emptyMsgFactura) {
		this.emptyMsgFactura = emptyMsgFactura;
	}
	
	public boolean getCheckmarkOrdenCompra(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getImportacionPedidoCompraDetalle().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckmarkFactura(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.selectedImportacionFactura.getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}
		
	private void sugerirValores(ImportacionPedidoCompraDTO dto) throws Exception{
		dto.setEstado(this.getDtoUtil().getImportacionEstadoConfirmado());
		dto.setTipoMovimiento(ordenCompraImportacion);
		dto.setMoneda(this.getDtoUtil().getMonedaDolaresConSimbolo());
		this.prorrateado = false;
	}
	
	@Command
	public void updateTipoCambio() {
		this.recalcularCostosMonedaLocal();
		BindUtils.postNotifyChange(null, null, this.dto, "cambio");
	}	
	
	//determina si el checkbox [cif prorrateado] es visible
	@DependsOn("dto.tipo")
	public boolean isCheckProrrateadoVisible(){
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		return this.dto.getTipo().compareTo(tipoCIF) == 0;
	}
	
	public String concatenar(String uno, String dos) {
		return uno + " " + dos;
	}
	
	/******************************************************************************************/
	
	
	/************************** ELIMINAR ITEM IMPORTACION FACTURA *****************************/
	
	private List<ImportacionFacturaDetalleDTO> selectedFacturaItems = new ArrayList<ImportacionFacturaDetalleDTO>();
	private String facturaItemsEliminar;	

	@Command
	@NotifyChange("*")
	public void eliminarItemFactura(){
		
		if (this.operacionValidaFactura(ELIMINAR_ITEM_FACTURA) == false) {
			return;
		}
		
		if (this.confirmarEliminarItemFactura() == true) {
			for (ImportacionFacturaDetalleDTO d : this.selectedFacturaItems) {
				this.selectedImportacionFactura.getDetalles().remove(d);
			}
			this.selectedFacturaItems = new ArrayList<ImportacionFacturaDetalleDTO>();
			this.recalcularCostosSinProrrateo();
		}		
	}
	
	private boolean confirmarEliminarItemFactura(){
		this.facturaItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ImportacionFacturaDetalleDTO d : this.selectedFacturaItems) {
			this.facturaItemsEliminar = this.facturaItemsEliminar + "\n - " + d.getArticulo().getCodigoInterno();
		}		
		return this.mensajeSiNo(this.facturaItemsEliminar);
	}
	
	/******************************************************************************************/	
	
	
	/******************************** INSERTAR ITEM FACTURA ***********************************/

	private ImportacionFacturaDetalleDTO nvoItem = new ImportacionFacturaDetalleDTO();		

	@Command
	@NotifyChange("*")
	public void insertarItemFactura(){
		
		if (this.operacionValidaFactura(INSERTAR_ITEM_FACTURA) == false) {
			return;
		}
		
		try {
			this.nvoItem = new ImportacionFacturaDetalleDTO();			
			WindowPopup w = new WindowPopup();
			w.setTitulo("Insertar Ítem");
			w.setModo(WindowPopup.NUEVO);
			w.setDato(this);
			w.setWidth("450px");
			w.setHigth("320px");
			w.setCheckAC(new ValidadorInsertarItemFactura(this));
			w.show(Configuracion.INSERTAR_ITEM_IMPORTACION_FACTURA_ZUL);
			if (w.isClickAceptar()) {
				this.selectedImportacionFactura.getDetalles().add(nvoItem);
				this.recalcularCostosSinProrrateo();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.mensajeError(e.getMessage());
		}
	}	
	
	/******************************************************************************************/	
	
	
	/*********************************** FACTURA SELECCIONADA *********************************/

	private ImportacionFacturaDTO selectedImportacionFactura = new ImportacionFacturaDTO();

	public ImportacionFacturaDTO getSelectedImportacionFactura() {
		return selectedImportacionFactura;
	}

	public void setSelectedImportacionFactura(
			ImportacionFacturaDTO selectedImportacionFactura) {
		this.selectedImportacionFactura = selectedImportacionFactura;
	}

	/******************************************************************************************/
	
	
	/************************************ NUEVA FACTURA ***************************************/
	
	private static String MSG_ADD_ITEMS = "AGREGUE ÍTEMS A LA FACTURA...";	 
	
	private ImportacionFacturaDTO nvaFactura = new ImportacionFacturaDTO();	

	@Command
	@NotifyChange("*")
	public void nuevaFactura(){
		
		if (this.operacionValidaFactura(AGREGAR_FACTURA) == false) {
			return;
		}
		
		try {
			this.nvaFactura = new ImportacionFacturaDTO();
			this.nvaFactura.setProveedor(this.dto.getProveedor());
			this.nvaFactura.setMoneda(this.dto.getMoneda());
			this.nvaFactura.setCondicionPago(this.dto.getProveedorCondicionPago());
			this.nvaFactura.setTipoMovimiento(this.dto.isCondicionContado() ? 
					getDtoUtil().getTmFacturaImportacionContado() : 
						getDtoUtil().getTmFacturaImportacionCredito()); 
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo(Configuracion.TEXTO_AGREGAR_NUEVA_FACTURA);
			w.setCheckAC(new ValidadorNuevaFactura(this));
			w.setWidth("400px");
			w.setHigth("340px");
			w.setDato(this);
			w.show(Configuracion.INSERTAR_NUEVA_FACTURA_ZUL);
			if (w.isClickAceptar()) {
				this.dto.getImportacionFactura().add(this.nvaFactura);
				this.setSelectedImportacionFactura(this.nvaFactura);
				this.emptyMsgFactura = MSG_ADD_ITEMS;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}		
	}	
	
	/******************************************************************************************/
	
	
	
	
	
	/****************************** UPLOAD DE ARCHIVO FACTURA CSV *****************************/
	
	//Upload Factura CSV
	@Listen("onUpload=#facturaImportacionUpload")
	public void uploadFactura(UploadEvent event){
		
		if (this.operacionValidaFactura(UPLOAD_FACTURA) == false) {
			return;
		}
		
		String name = Configuracion.NRO_IMPORTACION_FACTURA + "_" + misc.getIdUnique();
		String path = Configuracion.pathFacturaImportacion;
		this.setPathCsvFactura(path + name + ".csv");
		this.setLinkCsvFactura(Configuracion.pathFacturaImportacionGenerico + name + ".csv");
		this.nombreArchivoCSV = name;
		
		misc.uploadFile(path, name, "csv", event.getMedia().getStreamData());
		this.csvFactura();
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	/******************************************************************************************/	
	
	
	/************************************ FACTURA CSV *****************************************/
	
	private String pathCsvFactura = "";
	private String linkCsvFactura = "";	
	
	public void csvFactura(){		
		
		// Se declaran los parametros para pasar a la clase CSV
		String[][] cab = { { "Cliente", CSV.STRING }, { "Fecha", CSV.DATE },
						{ "Codigo", CSV.STRING }, { "Tipo de Compra", CSV.STRING },
						{"NroFactura", CSV.STRING}};

		String[][] cabDet = { { "codigo", CSV.STRING },
						{ "cantidad", CSV.NUMERICO }, { "articulo", CSV.STRING },
						{ "valor", CSV.NUMERICO }, { "total", CSV.NUMERICO } };

		RegisterDomain rr = RegisterDomain.getInstance();
		
		if (this.selectedImportacionFactura.getDetalles().size() > 0) {
			if (!this.mensajeSiNo("Esta Factura ya tiene ítems en el detalle, "
					+ "si continúa se eliminarán.. \n \n Desea continuar?")) {
				return;
			}
		}
				
		try {
			
			CSV csv = new CSV(cab, cabDet, this.getPathCsvFactura());
			if (this.verificarCabeceraCSV(csv.getCabecera("Codigo")) == false) {
				this.mensajeError("El Proveedor seleccionado es distinto al ingresado en el archivo csv.. "
						+ "\n Favor verifique..");
				return;
			}
			List<ImportacionFacturaDetalleDTO> detalle = new ArrayList<ImportacionFacturaDetalleDTO>();
			double total = 0;
			
			csv.start();
			while(csv.hashNext()){
				
				Articulo articulo = rr.getArticuloByCodigoInterno(csv.getDetalleString("codigo"));
				ArticuloDTO articuloDTO = null;
				
				if (articulo == null) {
					boolean continuar = mensajeAgregar(Configuracion.TEXTO_ARTICULO_INEXISTENTE
							+ csv.getDetalleString("codigo")
							+ "\n"
							+ Configuracion.TEXTO_PREGUNTA_CONTINUAR);
					if (continuar) {
						articuloDTO = this.nuevoArticulo(csv.getDetalleString("codigo"), 
								csv.getDetalleString("articulo"));
					} else {
						return;
					}
				} else {
					articuloDTO = (ArticuloDTO) new AssemblerArticulo().domainToDto(articulo);
				}
				ImportacionFacturaDetalleDTO d = setearImportacionFacturaDetalleDTO(articuloDTO, 
						csv.getDetalleDouble("cantidad"), csv.getDetalleDouble("valor"));
				total = total + d.getCostoDs();
				detalle.add(d);								
			}
			if (this.verificarTotalImportacionFactura(total)) {
				this.selectedImportacionFactura.setDetalles(detalle);
				this.addEventoAgendaLink(Configuracion.TEXTO_FACTURA_CSV + this.nombreArchivoCSV, linkCsvFactura);
			} 		
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}
	
	public boolean verificarTotalImportacionFactura(double total){
		
		if (misc.esIgual(total, this.selectedImportacionFactura.getTotalAsignadoDs()) == false) {
			return mensajeSiNo(Configuracion.TEXTO_TOTALES_NO_COINCIDEN + Configuracion.TEXTO_DESEA_CONTINUAR);
		} else {
			return true;
		}		
	}
	
	/******************************************************************************************/	
	
	
	/******************************* DETALLE IMPORTACION FACTURA ******************************/
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void notificarCostoDs(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Doublebox comp){
		double costoGs = item.getCostoGs();
		if (costoGs < 0) {
			Clients.showNotification("El costo no puede ser negativo..", "error", comp, null, 2000);
			item.setCostoGs(0);
			costoGs = 0;
		}
		item.setCostoDs(costoGs / this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, item, "costoDs");
		BindUtils.postNotifyChange(null, null, item, "costoGs");
	}
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void notificarCostoGs(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Doublebox comp){
		double costoDs = item.getCostoDs();
		if (costoDs < 0) {
			Clients.showNotification("El costo no puede ser negativo..", "error", comp, null, 2000);
			item.setCostoDs(0);
			costoDs = 0;
		}
		item.setCostoGs(costoDs * this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, item, "costoGs");
		BindUtils.postNotifyChange(null, null, item, "costoDs");		
	}
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void validarCantidad(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Intbox comp){
		if (item.getCantidad() < 0) {
			Clients.showNotification("La cantidad no puede ser negativa..", "error", comp, null, 2000);
			item.setCantidad(0);
		}
		BindUtils.postNotifyChange(null, null, item, "cantidad");
	}
	
	/**
	 * Retorna los sgtes valores de la factura seleccionada:
	 * [0]:Total Importe Gs.
	 * [1]:Total Importe Ds.
	 * [2]:Total SinProrrateo Gs.
	 * [3]:Total SinProrrateo Ds.
	 * [4]:Total itemsProrrateo Ds. (ProrrateoFlete + ProrrateoSeguro)
	 */
	public Double[] getTotalImporteFactura(){
		
		double totalGs = 0; double totalDs = 0;
		double totalSPgs = 0; double totalSPds = 0;
		double dctoGs = this.selectedImportacionFactura.getDescuentoGs();
		double dctoDs = this.selectedImportacionFactura.getDescuentoDs();
		double totalProrrateoDs = 0;
		
		for (ImportacionFacturaDetalleDTO item : this.selectedImportacionFactura.getDetalles()) {
			if (item.isValorProrrateo() == false) {
				totalGs += item.getImporteGsCalculado();
				totalDs += item.getImporteDsCalculado();				
				totalSPgs += item.getImporteSinProrrateoGs();
				totalSPds += item.getImporteSinProrrateoDs();
			} else {
				totalProrrateoDs += item.getImporteDsCalculado();
			}
		}
		
		if (this.dto.isCifProrrateado() == false) {
			totalProrrateoDs = 0;
		}
		
		Double[] out = {(totalGs - dctoGs), (totalDs - dctoDs), (totalSPgs - dctoGs), (totalSPds - dctoDs),
						 totalProrrateoDs};
		return out;
	}
	
	public ImportacionFacturaDetalleDTO setearImportacionFacturaDetalleDTO(
			ArticuloDTO articulo, Double cantidad, Double costoDs) {

		ImportacionFacturaDetalleDTO out = new ImportacionFacturaDetalleDTO();
		out.setArticulo(articulo);
		out.setCantidad(cantidad.intValue());
		out.setCostoDs(costoDs);
		out.setCostoGs(costoDs * this.dto.getCambio());
		
		return out;
	}
	
	/******************************************************************************************/
	
	
	/***************************** SUPRIMIR IMPORTACION FACTURA *******************************/
	
	@Command
	@NotifyChange("*")
	public void suprimirImportacionFactura(){
		
		if (this.operacionValidaFactura(SUPRIMIR_FACTURA) == false) {
			return;
		}
		
		String nroFactura = this.selectedImportacionFactura.getNumero();		
		if (this.mensajeSiNo("Desea eliminar la Factura Nro: \n" + nroFactura)) {
			this.dto.getImportacionFactura().remove(this.selectedImportacionFactura);					
			this.selectedImportacionFactura = new ImportacionFacturaDTO();
			this.addEventoAgenda("Se eliminó de la lista la Factura nro. " + nroFactura);	
		}
	}
	
	/******************************************************************************************/	
	
	
	/*********************** IMPORTACION FACTURA [GASTOS - DESCUENTOS] ************************/
	
	@Command @NotifyChange("*")
	public void agregarGastosDescuentos() throws Exception {
		
		if (this.operacionValidaFactura(ADD_GASTOS_DESCUENTOS) == false) {
			return;
		}
				
		this.nvoItem = new ImportacionFacturaDetalleDTO();
		this.nvoItem.setGastoDescuento(true);			
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setDato(this);
		w.setCheckAC(new ValidadorInsertarGastosDescuentos(this));
		w.setTitulo("Agregar Gastos/Descuentos");
		w.setWidth("400px");
		w.setHigth("280px");
		w.show(Configuracion.IMPORTACION_GASTOS_DESCTOS_ZUL);
		if (w.isClickAceptar()) {
						
			this.selectedImportacionFactura.getDetalles().add(this.nvoItem);
			
			if (this.nvoItem.isValorProrrateo() == true) {
				this.actualizarCostosSinProrrateo();
			} else {
				this.recalcularCostosSinProrrateo();
			}			
		}		
	}	
	
	public List<ArticuloDTO> getItemsGastoDescuento() throws Exception{
		List<ArticuloDTO> out = new ArrayList<ArticuloDTO>();
		RegisterDomain rr = RegisterDomain.getInstance();
		AssemblerArticulo assArt = new AssemblerArticulo();
		for (Articulo art : rr.getItemsGastoDescuento()) {
			ArticuloDTO a = (ArticuloDTO) assArt.domainToDto(art);
			out.add(a);
		}		
		return out;
	}
	
	@DependsOn("nvoItem.articulo")
	public List<MyPair> getTiposGastoDescuento(){
		List<MyPair> out = new ArrayList<MyPair>();
		
		List<MyPair> descuentos = utilDto.getCompraTiposDescuento();
		List<MyPair> gastos = utilDto.getCompraTiposGastos();
		List<MyPair> prorrateos = utilDto.getCompraTiposProrrateo();		
		
		ArticuloDTO art = this.nvoItem.getArticulo();
		
		if (art != null) {
			String codigo = art.getCodigoInterno();
			
			if (codigo.indexOf(Configuracion.CODIGO_ITEM_DESCUENTO_KEY) >= 0) {
				out = descuentos;
			}
			
			if (codigo.indexOf(Configuracion.CODIGO_ITEM_GASTO_KEY) >= 0) {
				out = gastos;
			}
			
			if (codigo.indexOf(Configuracion.CODIGO_ITEM_PRORRATEO_KEY) >= 0) {
				out = prorrateos;
			}
			
		}
		return out;
	}	
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void dolarizarDescuento(){
		double descGs = this.selectedImportacionFactura.getDescuentoGs();
		this.selectedImportacionFactura.setDescuentoDs(descGs / this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, this.selectedImportacionFactura, "descuentoDs");
	}
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void guaranizarDescuento(){
		double descDs = this.selectedImportacionFactura.getDescuentoDs();
		this.selectedImportacionFactura.setDescuentoGs(descDs * this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, this.selectedImportacionFactura, "descuentoGs");
	}	
	
	//actualiza los valores de la columna 'costo sin Prorrateo'	
	private void actualizarCostosSinProrrateo(){
		
		ImportacionFacturaDTO factura = this.selectedImportacionFactura;
		double totalDs = this.getTotalImporteFactura()[1];
		double valorProrrateo = this.getTotalImporteFactura()[4];
		double porcProrrateo = m.obtenerPorcentajeDelValor(valorProrrateo, totalDs);
		
		factura.setPorcProrrateo(porcProrrateo);
		
		for (ImportacionFacturaDetalleDTO item : factura.getDetalles()) {
			
			double valorProrrateoGs = m.obtenerValorDelPorcentaje(item.getCostoGs(), porcProrrateo);
			double valorProrrateoDs = m.obtenerValorDelPorcentaje(item.getCostoDs(), porcProrrateo);
			
			double valorSinProrrateoGs = item.getCostoGs() - valorProrrateoGs;
			double valorSinProrrateoDs = item.getCostoDs() - valorProrrateoDs;
			
			if (valorProrrateo == 0) {
				valorSinProrrateoGs = 0;
				valorSinProrrateoDs = 0;
			}
			
			item.setCostoSinProrrateoGs(valorSinProrrateoGs);
			item.setCostoSinProrrateoDs(valorSinProrrateoDs);
		}
	}
	
	/**
	 * Recalcula los valores de las columnas costo S/P
	 * Se usa cuando se realizan acciones que afectan al costo y 
	 * es necesario recalcular los valores..
	 * ej: cuando se elimina un item de la factura..
	 */
	private void recalcularCostosSinProrrateo(){		
		this.actualizarCostosSinProrrateo();
		BindUtils.postNotifyChange(null, null, this.selectedImportacionFactura, "*");
	}
	
	/******************************************************************************************/	
	
	
	
	
	/************************************ IMPORTAR ITEMS **************************************/
	
	private List<ImportacionFacturaDetalleDTO> itemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();
	private List<ImportacionFacturaDetalleDTO> selectedItemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();	

	@Command
	@NotifyChange("*")
	public void importarItems(){
		
		if (this.operacionValidaFactura(IMPORTAR_ITEMS) == false) {
			return;
		}
		
		if (this.selectedImportacionFactura.getDetalles().size() > 0) {
			if (!this.mensajeSiNo("Esta Factura ya tiene ítems en el detalle, "
					+ "si continúa se eliminarán.. \n \n Desea continuar?")) {
				return;
			}
		}
		
		try {
			this.exportarItems();
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setDato(this);
			w.setCheckAC(new ValidadorImportarItems(this));
			w.setTitulo("Importar Ítems de la Orden de Compra..");
			w.setWidth("950px");
			w.setHigth("500px");
			w.show(Configuracion.IMPORTAR_ITEMS_IMPORTACION_ZUL);
			if (w.isClickAceptar()) {
				this.selectedImportacionFactura.setDetalles(selectedItemsImportar);
			}
			this.selectedItemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void exportarItems(){
		this.itemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();
		for (ImportacionPedidoCompraDetalleDTO d : this.dto.getImportacionPedidoCompraDetalle()) {
			ImportacionFacturaDetalleDTO item = new ImportacionFacturaDetalleDTO();
			item.setArticulo(d.getArticulo());
			item.setCantidad(d.getCantidad());
			item.setCostoDs(d.getCostoProformaDs());
			item.setCostoGs(d.getCostoProformaGs());
			itemsImportar.add(item);
		}
	}
	
	/******************************************************************************************/
	
	
	/**************************** DERIVAR IMPORTACION FACTURA *********************************/
	
	private String selectedPropietarioImportacionFactura = "";	
	
	public List<String> getPropietariosImportacionFactura(){
		
		List<String> out = new ArrayList<String>();
		out.add(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_AUDITORIA_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION);
		
		return out;
	}
	
	private Window w;
	@Command
	public void abrirPropietariosImportacionFactura(){
		this.selectedPropietarioImportacionFactura = "";
		w = (Window) Executions.createComponents(
				Configuracion.LISTA_PROPIETARIOS_IMPORTACION_FACTURA_ZUL, this.mainComponent, null);
		w.doModal();
	}
	
	@Command
	@NotifyChange("*")
	public void derivarImportacionFactura(){
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_IMPORTACION_KEY);
		}
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_AUDITORIA_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_AUDITORIA_KEY);
		}
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_VENTAS_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_VENTAS_KEY);
		}
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
		}
		w.detach();
	}
	
	// Comportamiento Checkbox (Habilitado/Desabilitado)
	public boolean getCheckboxImportacionEnable() {
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_IMPORTACION_KEY) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckboxAuditoriaEnable(){
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_AUDITORIA_KEY) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckboxVentasEnable(){
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_VENTAS_KEY) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckboxAdministracionEnable(){
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			out = true;
		}
		return out;
	}
	
	/******************************************************************************************/	
	
	
	/******************************** DESCUENTOS POR ITEM *************************************/
	
	/**
	 * Si el descuento es ingresado como porcentaje: se calcula el valor en guaranies y dolares..
	 * Si el descuento es ingresado como valor: se toma el valor como dolares..
	 * */	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void descuentoPorItem(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Textbox comp){
		String desc = item.getTextoDescuento().trim();
		Double valorDesc = (double) 0;
		if (m.containsOnlyNumbers(desc) == true) {
			valorDesc = Double.parseDouble(desc);
			if (valorDesc < 0) {
				valorDesc = valorDesc * -1;
			}
			item.setDescuentoDs(valorDesc);
			item.setDescuentoGs(valorDesc * this.dto.getCambio());
		} else if (m.containsOnlyNumbersAndPercent(desc) == true) {
			valorDesc = Double.parseDouble(desc.replace("%", ""));
			if (valorDesc > 100) {
				m.mensajePopupTemporal("'"+desc+"' no es un valor de descuento correcto..", "error", comp);
				item.setTextoDescuento("0");
				item.setDescuentoDs(0);
				item.setDescuentoGs(0);
			} else {
				item.setDescuentoGs((item.getCostoGs() * valorDesc) / 100);
				item.setDescuentoDs((item.getCostoDs() * valorDesc) / 100);
			}			
		} else{
			m.mensajePopupTemporal("'"+desc+"' no es un valor de descuento correcto..", "error", comp);
			item.setTextoDescuento("0");
			item.setDescuentoDs(0);
			item.setDescuentoGs(0);
		}
		BindUtils.postNotifyChange(null, null, item, "descuentoGs");
		BindUtils.postNotifyChange(null, null, item, "descuentoDs");
	}
	
	/************************************************************************************************/	
	
	
	/*********************************** RECEPCION DE MERCADERIAS **********************************/
	
	private ImportacionFacturaDTO selectedRecepcion;	

	@Command
	@NotifyChange("*")
	public void confirmarRecepcion() {
		boolean continuar = true;

		if (this.operacionValidaFactura(CONFIRMAR_RECEPCION) == false) {
			return;
		}

		if (this.existeDiferencias() == true) {
			continuar = this
					.mensajeSiNo("Existen cantidades con diferencias..Desea continuar?");
		}

		if (continuar == true) {
			if (mensajeSiNo("Desea Confirmar la Recepción de la Factura Nro. "
					+ this.selectedRecepcion.getNumero())) {
				this.selectedRecepcion.setRecepcionConfirmada(true);
				this.addEventoAgenda("Se confirmó la Recepción de la Factura Nro. "
						+ this.selectedRecepcion.getNumero());
			}
		}
	}	
	
	/**
	 * @return booleano que indica si hay diferencias en la recepcion de mercaderias..
	 */
	private boolean existeDiferencias() {
		
		if (this.dto.getImportacionFactura().size() == 0) {
			return false;
		}	
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			if (this.existeDiferencias(fac) == true) {
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * @param factura..
	 * @return booleano que indica si hay diferencias en la recepcion de mercaderias..
	 */
	private boolean existeDiferencias(ImportacionFacturaDTO factura) {		
		
			for (ImportacionFacturaDetalleDTO item : factura.getDetalles()) {
				if (item.getDiferenciaCantidad() != 0) {
					return true;
				}
			}
		
		return false;
	}
	
	@Command
	public void imprimirPackingList() {
		ImportacionPackingList ipl = new ImportacionPackingList();
		
		ipl.setDatosReporte(this.getDatosPackingList());
		ipl.setProveedor(this.getDto().getProveedor().getRazonSocial());
		ipl.setNumeroOrden(this.getDto().getNumeroPedidoCompra());
		ipl.setFactura(this.selectedImportacionFactura.getNumero());
		
		//ipl.setApaisada();
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(ipl, this);		
	}
	
	private List<Object[]> getDatosPackingList() {
		List<Object[]> out = new ArrayList<>();
		List<ImportacionFacturaDetalleDTO> items = new ArrayList<ImportacionFacturaDetalleDTO>();
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				items.add(item);
			}
		}		
		
		for (ImportacionFacturaDetalleDTO item : items) {
			Object[] data = new Object[6];
			data[0] = item.getArticulo().getCodigoInterno();
			data[1] = item.getArticulo().getCodigoInterno();
			data[2] = item.getArticulo().getDescripcion();
			data[3] = "";
			data[4] = "";
			data[5] = "";
			out.add(data);
		}
		
		return out;
	}
	
	/***********************************************************************************************/	
	
	
	/************************************ RESUMEN GASTOS DESPACHO ***********************************/
	
	private static String MSG_ERROR_NEGATIVO = "El valor no puede ser negativo..";
	
	private double valorFleteGs = 0;
	private double valorFleteDs = 0;
	private double valorSeguroGs = 0;
	private double valorSeguroDs = 0;
 	
	@DependsOn("dto.tipo")
	public double getValorFleteGs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		
		if (tipo.compareTo(tipoFOB) == 0) {
			return valorFleteGs;
		} else {
			double valor = this.getValorFleteDs();
			return valor * cambio;
		}		
	}

	public void setValorFleteGs(double valorFleteGs) {
		this.valorFleteGs = valorFleteGs;
	}

	@DependsOn("dto.tipo")
	public double getValorFleteDs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		
		if (tipo.compareTo(tipoFOB) == 0) {
			return valorFleteDs;
		} else {
			return this.getValoresFromFacturas()[1];
		}		
	}

	public void setValorFleteDs(double valorFleteDs) {
		this.valorFleteDs = valorFleteDs;
	}

	@DependsOn("dto.tipo")
	public double getValorSeguroGs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			double valor = this.getValorSeguroDs();
			return valor * cambio;
		} else {
			return valorSeguroGs;
		}		
	}

	public void setValorSeguroGs(double valorSeguroGs) {
		this.valorSeguroGs = valorSeguroGs;
	}

	@DependsOn("dto.tipo")
	public double getValorSeguroDs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			return this.getValoresFromFacturas()[3];
		} else {
			return valorSeguroDs;
		}		
	}

	public void setValorSeguroDs(double valorSeguroDs) {
		this.valorSeguroDs = valorSeguroDs;
	}
	
	@DependsOn("dto.tipo")
	public double getValorFOBds(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			return this.getValorFOBdsCalculado();
		} else {
			return this.getValoresFromFacturas()[5];
		}		
	}
	
	@DependsOn("valorFOBds")	
	public double getValorFOBgs(){
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		return this.getValorFOBds() * cambio;
	}
	
	@DependsOn({"dto.tipo", "valorFleteDs", "valorSeguroDs"})
	public double getValorCIFds(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			return this.getValoresFromFacturas()[7];
		} else {
			return this.getValorCIFdsCalculado();
		}
	}
	
	@DependsOn("valorCIFds")
	public double getValorCIFgs(){
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		return this.getValorCIFds() * cambio;
	}
	
	@DependsOn({"dto.resumenGastosDespacho.totalGastosDs", 
				"dto.resumenGastosDespacho.totalGastosImprevistosDs",
				"valorFleteDs", "valorSeguroDs", "valorFOBds"})
	public double getCoeficienteGasto(){		
		ResumenGastosDespachoDTO despacho = this.dto.getResumenGastosDespacho();
		
		double gastosDs = despacho.getTotalGastosDs();
		double gastosImprevistosDs = despacho.getTotalGastosImprevistosDs();
		double flete = this.getValorFleteDs();
		double seguro = this.getValorSeguroDs();				
		
		double totalGastosDs = gastosDs + gastosImprevistosDs + flete + seguro;
		double valorFOBds = this.getValorFOBds();
		
		double coef = totalGastosDs / valorFOBds;
		
		return m.redondeoCuatroDecimales(coef);
	}
	
	/**
	 * Metodo que extrae de las facturas los valores para el 
	 * resumen de Gastos de Despacho...
	 * @return [0]:valorFleteGs  [1]:valorFleteDs [2]:valorSeguroGs 
	 * [3]:valorSeguroDs [4]:valorFOBgs [5]:valorFOBds
	 * [6]:valorCIFgs [7]:valorCIFds
	 */
	private Double[] getValoresFromFacturas(){
		
		double valorFleteGs = 0;
		double valorFleteDs = 0;
		double valorSeguroGs = 0;
		double valorSeguroDs = 0;
		double valorFOBgs = 0;
		double valorFOBds = 0;
		double valorCIFgs = 0;
		double valorCIFds = 0;
		
		MyPair tipoFlete = utilDto.getTipoCompraGastoFlete();
		MyPair tipoSeguro = utilDto.getTipoCompraGastoSeguro();
		MyPair tipoProrrFlete = utilDto.getTipoCompraProrrateoFlete();
		MyPair tipoProrrSeguro = utilDto.getTipoCompraProrrateoSeguro();
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				MyPair tipo = item.getTipoGastoDescuento();
				
				valorCIFgs += item.getImporteGsCalculado();
				valorCIFds += item.getImporteDsCalculado();
				
				if (item.isGastoDescuento() == true) {					
					
					if ((tipo.compareTo(tipoFlete) == 0)) {
						valorFleteGs += item.getImporteGastoDescuentoGs();
						valorFleteDs += item.getImporteGastoDescuentoDs();
					}
					
					if (tipo.compareTo(tipoSeguro) == 0) {
						valorSeguroGs += item.getImporteGastoDescuentoGs();
						valorSeguroDs += item.getImporteGastoDescuentoDs();
					}
					
					if (tipo.compareTo(tipoProrrFlete) == 0) {
						valorFleteGs += item.getImporteGsCalculado();
						valorFleteDs += item.getImporteDsCalculado();
					}
					
					if (tipo.compareTo(tipoProrrSeguro) == 0) {
						valorSeguroGs += item.getImporteGsCalculado();
						valorSeguroDs += item.getImporteDsCalculado();
					}
					
				} else {					
					valorFOBgs += item.getImporteGsCalculado();
					valorFOBds += item.getImporteDsCalculado();					
				}
			}
		}
		Double[] out = {valorFleteGs, valorFleteDs, valorSeguroGs, valorSeguroDs,
						valorFOBgs, valorFOBds, valorCIFgs, valorCIFds};
		return out;
	}
	
	//calcula el valor FOB en ds
	private double getValorFOBdsCalculado(){
		
		double valCIF = this.getValorCIFds();
		double valFlete = this.getValorFleteDs();
		double valSeguro = this.getValorSeguroDs();
		
		return valCIF - (valFlete + valSeguro);
	}
	
	//calcula el valor CIF en ds
	private double getValorCIFdsCalculado(){
		
		double valFOB = this.getValorFOBds();
		double valFlete = this.getValorFleteDs();
		double valSeguro = this.getValorSeguroDs();
		
		return valFOB + valFlete + valSeguro;
	}
			
	@Command @NotifyChange({"itemsCostoFinal", "valorFleteDs", "valorFleteGs"})
	public void dolarizarFlete(@BindingParam("comp") Doublebox comp){
		double valorGs = this.getValorFleteGs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorFleteGs(0);
			valorGs = 0;
		}
		double result = valorGs / this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorFleteDs(result);
	}
	
	@Command @NotifyChange({"itemsCostoFinal", "valorFleteDs", "valorFleteGs"})
	public void guaranizarFlete(@BindingParam("comp") Doublebox comp){
		double valorDs = this.getValorFleteDs();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorFleteDs(0);
			valorDs = 0;
		}
		double result = valorDs * this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorFleteGs(result);
	}
	
	@Command @NotifyChange({"itemsCostoFinal", "valorSeguroDs", "valorSeguroGs"})
	public void dolarizarSeguro(@BindingParam("comp") Doublebox comp){
		double valorGs = this.getValorSeguroGs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorSeguroGs(0);
			valorGs = 0;
		}
		double result = valorGs / this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorSeguroDs(result);
	}
	
	@Command @NotifyChange({"itemsCostoFinal", "valorSeguroDs", "valorSeguroGs"})
	public void guaranizarSeguro(@BindingParam("comp") Doublebox comp){
		double valorDs = this.getValorSeguroDs();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorSeguroDs(0);
			valorDs = 0;
		}
		double result = valorDs * this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorSeguroGs(result);
	}
	
	@Command
	public void dolarizarIVA(@BindingParam("comp") Doublebox comp){
		double valorGs = this.dto.getResumenGastosDespacho().getTotalIVAgs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalIVAgs(0);
			valorGs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalIVAds(valorGs / this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAds");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAgs");
	}
	
	@Command
	public void guaranizarIVA(@BindingParam("comp") Doublebox comp){
		double valorDs = this.dto.getResumenGastosDespacho().getTotalIVAds();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalIVAds(0);
			valorDs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalIVAgs(valorDs * this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAds");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAgs");
	}
	
	@Command
	@NotifyChange({"itemsCostoFinal"})
	public void dolarizarGasto(@BindingParam("comp") Doublebox comp){
		double valorGs = this.dto.getResumenGastosDespacho().getTotalGastosGs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalGastosGs(0);
			valorGs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalGastosDs(valorGs / this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosDs");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosGs");
	}
	
	@Command
	@NotifyChange({"itemsCostoFinal"})
	public void guaranizarGasto(@BindingParam("comp") Doublebox comp){
		double valorDs = this.dto.getResumenGastosDespacho().getTotalGastosDs();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalGastosDs(0);
			valorDs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalGastosGs(valorDs * this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosDs");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosGs");
	}
	
	@Command
	public void notificarValores(){
		
		ResumenGastosDespachoDTO resumen = this.dto.getResumenGastosDespacho();
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();		
		double valFleteDs = this.getValorFleteDs();
		double valSeguroDs = this.getValorSeguroDs();
		double valIVAds = this.dto.getResumenGastosDespacho().getTotalIVAds();
		double valGastoDs = this.dto.getResumenGastosDespacho().getTotalGastosDs();		
		
		this.setValorFleteGs(valFleteDs * cambio);
		this.setValorSeguroGs(valSeguroDs * cambio);
		resumen.setTotalIVAgs(valIVAds * cambio);
		resumen.setTotalGastosGs(valGastoDs * cambio);
		
		BindUtils.postNotifyChange(null, null, this, "valorCIFgs");
		BindUtils.postNotifyChange(null, null, this, "valorFOBgs");
		BindUtils.postNotifyChange(null, null, this, "valorFleteGs");
		BindUtils.postNotifyChange(null, null, this, "valorSeguroGs");
		BindUtils.postNotifyChange(null, null, resumen, "totalIVAgs");
		BindUtils.postNotifyChange(null, null, resumen, "totalGastosGs");
	}
	
	//para restringir la modificacion del tipo de cambio del despacho
	@DependsOn("dto.subDiarioConfirmado")
	public boolean isTipoCambioEditable(){
		
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return !this.dto.isSubDiarioConfirmado();
		}		
	}
	
	//para restringir la modificacion del valorFlete en despacho
	@DependsOn("dto.tipo")
	public boolean isValorFleteEditable(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return tipo.compareTo(tipoFOB) == 0;
		}		
	}
	
	//para restringir la modificacion del valorSeguro en despacho
	public boolean isValorSeguroEditable(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return tipo.compareTo(tipoCIF) != 0;
		}
	}
	
	//para setear los valores del despacho en el controlador (this)
	private void setearValoresDespacho(){
		ResumenGastosDespachoDTO desp = this.dto.getResumenGastosDespacho();
		this.setValorFleteDs(desp.getValorFleteDs());
		this.setValorFleteGs(desp.getValorFleteGs());
		this.setValorSeguroDs(desp.getValorSeguroDs());
		this.setValorSeguroGs(desp.getValorSeguroGs());
	}
	
	//para actualizar los valores del ResumenGastoDespachoDTO (se invoca al Grabar).
	private void actualizarValoresDespacho(){
		ResumenGastosDespachoDTO desp = this.dto.getResumenGastosDespacho();
		desp.setValorFleteDs(this.getValorFleteDs());
		desp.setValorFleteGs(this.getValorFleteGs());
		desp.setValorSeguroDs(this.getValorSeguroDs());
		desp.setValorSeguroGs(this.getValorSeguroGs());
		desp.setValorFOBds(this.getValorFOBds());
		desp.setValorFOBgs(this.getValorFOBgs());
		desp.setValorCIFds(this.getValorCIFds());
		desp.setValorCIFgs(this.getValorCIFgs());
		desp.setCoeficiente(this.getCoeficienteGasto());
	}
	
	/**
	 * actualiza el total importe..
	 */
	private void actualizarImporte() {
		double importeGs = 0;
		double importeDs = 0;
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			importeGs += fac.getTotalImporteGs();
			importeDs += fac.getTotalImporteDs();
		}
		this.dto.setTotalImporteGs(importeGs);
		this.dto.setTotalImporteDs(importeDs);
	}
	
	/**
	 * impresion de la compra..
	 */
	private void imprimir() {
		if (!(this.dto.getImportacionFactura().size() > 0)) {
			Clients.showNotification("SIN DATOS DE FACTURA..");
			return;
		}
		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (MyArray item : this.getItemsCostoFinal()) {
			Object[] obj1 = new Object[] { item.getPos1(), 
					item.getPos5(), 
					Utiles.getRedondeo((double) item.getPos3()), 
					misc.redondeoDosDecimales((double) item.getPos4()), 
					Utiles.getRedondeo((double) item.getPos8()),
					misc.redondeoDosDecimales((double) item.getPos9()) };
			data.add(obj1);
		}
		
		// ordena la lista segun codigo..
		Collections.sort(data, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String rs1 = (String) o1[0];
				String rs2 = (String) o2[0];
				return rs1.compareTo(rs2);
			}
		});

		ReporteYhaguy rep = new ReporteImportacion(this.dto);
		rep.setDatosReporte(data);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	}
	
	/***********************************************************************************************/	
	
	
	/*************************************** GASTOS IMPREVISTOS ************************************/
	
	private ImportacionGastoImprevistoDTO nvoGastoImprevisto = new ImportacionGastoImprevistoDTO();
	private List<ImportacionGastoImprevistoDTO> selectedGastosImprevistos = new ArrayList<ImportacionGastoImprevistoDTO>();
	private String gastosImprevistosEliminar;	

	@Command
	@NotifyChange("*")
	public void insertarGastoImprevisto(){
		
		if (this.operacionValidaFactura(AGREGAR_GASTO_IMPREVISTO) == false) {
			return;
		}
		
		try {
			this.nvoGastoImprevisto = new ImportacionGastoImprevistoDTO();
			
			WindowPopup w = new WindowPopup();
			w.setDato(this);
			w.setCheckAC(new ValidadorAgregarGastoImprevisto(this));
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Agregar Gasto Imprevisto");
			w.setWidth("400px");
			w.setHigth("260px");
			w.show(Configuracion.IMPORTACION_GASTOS_IMPREVISTOS_ZUL);
			if (w.isClickAceptar()) {
				this.dto.getGastosImprevistos().add(nvoGastoImprevisto);
				this.dto.getResumenGastosDespacho().setTotalGastosImprevistosDs(this.getTotalImporteGastoImprevisto()[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@Command
	@NotifyChange("*")
	public void eliminarItemGastoImprevisto(){
		
		if (this.operacionValidaFactura(ELIMINAR_GASTO_IMPREVISTO) == false) {
			return;
		}
		
		if (this.confirmarEliminarGastoImprevisto() == true) {
			for (ImportacionGastoImprevistoDTO d : this.selectedGastosImprevistos) {
				this.dto.getGastosImprevistos().remove(d);
				this.dto.getResumenGastosDespacho().setTotalGastosImprevistosDs(this.getTotalImporteGastoImprevisto()[1]);
			}
			this.selectedGastosImprevistos = new ArrayList<ImportacionGastoImprevistoDTO>();
		}		
	}
	
	private boolean confirmarEliminarGastoImprevisto(){
		this.ordenItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ImportacionGastoImprevistoDTO d : this.selectedGastosImprevistos) {
			this.gastosImprevistosEliminar = this.gastosImprevistosEliminar + "\n - " + d.getProveedor().getPos2();
		}		
		return this.mensajeSiNo(this.ordenItemsEliminar);
	}
	
	public boolean getCheckmarkGastoImprevisto(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getGastosImprevistos().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public Double[] getTotalImporteGastoImprevisto(){
		double totalGs = 0; double totalDs = 0;
		for (ImportacionGastoImprevistoDTO gasto : this.dto.getGastosImprevistos()) {
			totalGs = totalGs + gasto.getImporteGs();
			totalDs = totalDs + gasto.getImporteDs();
		}
		Double[] out = {totalGs, totalDs};
		return out;
	}
	
	/***********************************************************************************************/	
	

	/****************************************** COSTO FINAL ****************************************/
	
	public List<MyArray> getItemsCostoFinal(){
		List<MyArray> out = new ArrayList<MyArray>();	
		
		for (ImportacionFacturaDTO f : this.dto.getImportacionFactura()) {
			
			double coefAutomatico = this.getCoeficienteGasto();
			double coefManual = this.dto.getResumenGastosDespacho().getCoeficienteAsignado();
			double coefGasto = 0;
			
			if (coefManual > 0) {
				coefGasto = coefManual;
			} else {
				coefGasto = coefAutomatico;
			}			
			
			for (ImportacionFacturaDetalleDTO d : f.getDetalles()) {				
				
				double costoGs = d.getCostoGs();
				double costoDs = d.getCostoDs();
				
				long idArt = d.getArticulo().getId();
				int cant = d.getCantidad();				
				double valorGasto = coefGasto * costoDs;
				
				double costoFinalDs = this.getCostoFinal(costoDs, valorGasto, 0);
				double costoFinal = (costoFinalDs * this.dto.getResumenGastosDespacho().getTipoCambio());
				
				if (d.isGastoDescuento() == false) {
					MyArray mr = new MyArray();
					mr.setId(idArt);
					mr.setPos1(d.getArticulo().getCodigoInterno());
					mr.setPos2(d.getArticulo().getDescripcion());
					mr.setPos3(costoFinal);
					mr.setPos4(costoFinalDs);
					mr.setPos8(costoFinal * cant);
					mr.setPos9(costoFinalDs * cant);						
					mr.setPos5(new Integer(cant));
					mr.setPos6(costoGs);
					mr.setPos7(costoDs);
					out.add(mr);
				}			
			}			
		}	
		
		return out;
	}
	
	/**
	 * Calcula el costo final en base al costo + valorGasto + descuento
	 */
	private double getCostoFinal(double costoGravado, double valorGasto, double valorDescuento){
		return costoGravado + valorGasto - valorDescuento;
	}
	
	public String getItemsCostoFinalSize(){
		return this.getItemsCostoFinal().size() + " ítems";
	}
	
	public Object[] getTotalCostoFinal() {
		double totalGs = 0;
		double totalDs = 0;

		for (MyArray mr : this.getItemsCostoFinal()) {
			double importeGs = (double) mr.getPos8();
			double importeDs = (double) mr.getPos9();
			totalGs += importeGs;
			totalDs += importeDs;
		}

		return new Object[] { totalGs, totalDs };
	}
	
	/**
	 * @return el coeficiente utilizado para el costo final..
	 */
	public double getCoeficienteUtilizado() {
		
		if (this.dto.getResumenGastosDespacho().esNuevo() == true) {
			return 0;
		}
		
		double coefAutomatico = this.getCoeficienteGasto();
		double coefManual = this.dto.getResumenGastosDespacho().getCoeficienteAsignado();
		double coefGasto = 0;
		
		if (coefManual > 0) {
			coefGasto = coefManual;
		} else {
			coefGasto = coefAutomatico;
		}
		
		return coefGasto;
	}
	
	/***********************************************************************************************/	
	
	
	/************************************ VOLCADO DE COSTOS Y STOCK ********************************/
	
	private String linkReporteFinal;
	
	@Command @NotifyChange("*")
	public void confirmarImportacion() {		
		if (mensajeSiNo("Esta seguro de confirmar la Importación Nro.: " + this.dto.getNumeroPedidoCompra())) {
			try {
				this.generarDiferenciaTipoCambio();
				this.volcarImportacion();
				this.actualizarEtapaActual(this.getDtoUtil().getImportacionEstadoCerrado());
				this.dto.setAuxi("");
				this.dto.setReadonly();
				for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
					fac.setReadonly();
				}
				this.dto.setFechaCierre(new Date());
				this.dto.setImportacionConfirmada(true);
				this.setDto((ImportacionPedidoCompraDTO) this.saveDTO(this.dto));
				this.addEventoAgendaLink("Se confirmó la Importación..", linkReporteFinal); 
				this.mensajePopupTemporal("La Importación fue correctamente confirmada..");	
				this.setEstadoABMConsulta();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}		
	}	
	
	//MyArray item | pos1:idMovimiento - pos2:tipoMovimiento - pos3:cantidad - pos4:articulo
	//pos5:costoFOBgs - pos6:costoFOBds - pos7:costoFinalGs - pos8:costoFinalDs |
	private void volcarImportacion() throws Exception {
		ControlLogica ctr = new ControlLogica(null);
		
		for (MyArray m : this.getItemsCostoFinal()) {
			
			MyArray art = new MyArray();
			art.setId(m.getId());
			art.setPos1(m.getPos1());
			
			Integer cant = (Integer) m.getPos5();
			double costoFinalGs = (double) m.getPos3();
			
			ctr.actualizarArticuloDepositoCompra(this.dto.getId(), this.dto.getTipoMovimiento(), 
					art, cant.longValue(), costoFinalGs, 
						true, this.dto.getDeposito(), Configuracion.OP_SUMA);
			
			ControlArticuloCosto.addMovimientoCosto(art.getId(), costoFinalGs,
							this.dto.getFechaCierre(), this.dto.getId(),
							this.dto.getTipoMovimiento().getId(),
							this.getLoginNombre());
		}	
		this.actualizarCtaCte();
	}	
	
	/**
	 * actualizar Cta Cte..
	 */
	private void actualizarCtaCte() throws Exception {
		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);

		IiD empresa = this.dto.getProveedor().getEmpresa();
		MyPair moneda = new MyPair(this.dto.getMoneda().getId(), "");
		MyArray sucursal = new MyArray();
		sucursal.setId(this.getSucursal().getId());

		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			long idMovimientoOriginal = fac.getId();
			String nroComprobante = fac.getNumero();
			Date fechaEmision = fac.getFechaOriginal();
			double importeOriginal = fac.getTotalImporteDs();
			MyArray tipoMovimiento = fac.getTipoMovimiento();

			ctr.addCtaCteEmpresaMovimientoFacturaCredito(empresa,
					idMovimientoOriginal, nroComprobante, fechaEmision,
					(int) fac.getCondicionPago().getPos2(), 1,
					importeOriginal, 0, (importeOriginal - this.dto.getTotalAnticipoAplicadoDs()), moneda,
					tipoMovimiento, this.getDtoUtil().getCtaCteEmpresaCaracterMovProveedor(), sucursal, "", 
					this.dto.getResumenGastosDespacho().getTipoCambio());			
		}
		
		for (ImportacionAplicacionAnticipoDTO anticipo : this.dto.getAplicacionAnticipos()) {
			CtaCteEmpresaMovimientoDTO movim = anticipo.getMovimiento();
			movim.setSaldo(movim.getSaldo() + anticipo.getImporteDs());
			this.saveDTO(movim, new AssemblerCtaCteEmpresaMovimiento());
		}
	}
	
	@DependsOn({ "deshabilitado", "dto.importacionFactura", "dto.deposito" })
	public boolean isCerrarImportacionDisabled() {
		return this.isDeshabilitado() || this.dto.getImportacionFactura().size() == 0
				|| this.dto.getDeposito() == null;
	}
	
	/**
	 * genera el registro de diferencia tipo - cambio..
	 */
	private void generarDiferenciaTipoCambio() throws Exception {
		double dif_tc = this.getDiferenciaTipoCambio();
		if (dif_tc >= 1000 || dif_tc <= -1000) {
			RegisterDomain rr = RegisterDomain.getInstance();
			Recibo rec = new Recibo();
			rec.setCobrador("- - -");
			rec.setProveedor(rr.getProveedorById(this.dto.getProveedor().getId()));
			rec.setFechaEmision(new Date());
			rec.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
			rec.setNumero("REC-PAG-" + AutoNumeroControl.getAutoNumero("REC-PAG", 5));
			rec.setNombreUsuarioCarga(this.getLoginNombre());
			rec.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO));
			rec.setMotivoAnulacion("");
			rec.setNumeroPlanilla("- - -");
			rec.setNumeroRecibo("DIF. TIPO CAMBIO");
			rec.setFechaRecibo(new Date());
			rec.setEntregado(true);
			rec.setSucursal(rr.getSucursalAppById(2));
			rec.setTipoCambio(0);
			rec.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_RECIBO_PAGO));
			ReciboDetalle det = new ReciboDetalle();
			det.setAuxi(ReciboDetalle.TIPO_DIF_CAMBIO);
			det.setConcepto("DIFERENCIA POR TIPO DE CAMBIO");
			det.setMontoGs(dif_tc);
			rec.getDetalles().add(det);
			rr.saveObject(rec, this.getLoginNombre());
		}
	}
	
	//Genera el Reporte del Resultado Final de la Importacion..
	public void generarReporteFinal() {
			
		List<Object[]> data = new ArrayList<>();
		Object[] obj = new Object[6];		
		for (MyArray d : this.getItemsCostoFinal()) {
			obj = new Object[6];
			obj[0] = d.getPos1();
			obj[1] = d.getPos1();
			obj[2] = d.getPos2();
			obj[3] = d.getPos3();
			obj[4] = 0; //d.getCostoFinalDs();
			obj[5] = 0; //d.getCostoFinalGs();
			data.add(obj);	
		}
	}
	
	/***********************************************************************************************/	
	
	
	/***************************************** COMPROBAR GRUPO *************************************/	

	private boolean comprobarGrupo() {
		boolean out = false;
		
		if ((this.esGrupo(Configuracion.GRUPO_VENTAS)) && 
				(this.dto.getPropietario().compareTo(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION) == 0)) {
			out = true;
		}
		
		if ((this.esGrupo(Configuracion.GRUPO_IMPORTACION)) && 
				(this.dto.getPropietario().compareTo(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION) == 0)) {
			out = true;
		}
		
		if (this.esGrupo(Configuracion.GRUPO_GRUPO_ELITE) == true) {
			out = true;
		}
		
		return out;
	}
	
	/***********************************************************************************************/	
	
	
	/*************************************** VALIDACION FORMULARIO *********************************/
	
	@Override
	public boolean verificarAlGrabar() {
		try {
			String sigla = this.dto.getProveedor().getEmpresa().getSigla().trim();
			
			if (sigla.length() == 0) {
				sigla = Configuracion.NRO_IMPORTACION_PEDIDO;
			}
			
			if ((this.dto.esNuevo() == true) && (this.validarFormulario() == true)) {
				this.dto.setNumeroPedidoCompra(sigla + "-" + AutoNumeroControl.getAutoNumero(sigla, 5));
				
				this.grabarEventosAgenda();
			}
			
			if ((this.dto.esNuevo() == false) && (this.validarFormulario() == true)) {
				// Aqui graba en la BD los eventos de la agenda que se fueron cargando
				this.grabarEventosAgenda();
			}
			
			if (this.validarFormulario() == true) {				
				this.actualizarValoresDespacho();
				this.actualizarImporte();
			}
			
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}		
		return this.validarFormulario();
	}

	private String mensajeErrorVerificar = "";
	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeErrorVerificar;
	}
	
	public boolean validarFormulario(){
		boolean out = true;		
		this.mensajeErrorVerificar = "No se puede realizar la operación debido a: \n";
		
			if ((this.dto.esNuevo() == true) && (this.dto.getImportacionPedidoCompraDetalle().size() == 0)) {
				this.mensajeErrorVerificar += "\n - Debe ingresar al menos un ítem al Pedido..";
				out = false;
			}
			
			if (this.dto.getProveedor().esNuevo() == true) {
				this.mensajeErrorVerificar += "\n - Debe seleccionar un Proveedor..";
				out = false;
			}
			
			if (this.dto.getCambio() <= 0.001) {
				this.mensajeErrorVerificar += "\n - El Tipo de Cambio debe ser mayor a cero..";
				out = false;
			}
			
			if (this.dto.getProveedorCondicionPago().esNuevo() == true) {
				this.mensajeErrorVerificar += "\n - Debe seleccionar una Condición de Pago..";
				out = false;
			}
			
			if (this.dto.getTipo().esNuevo() == true) {
				this.mensajeErrorVerificar += "\n - Debe asignar un Tipo de Importación..";
				out = false;
			}		
		
		return out;
	}
	
	/***********************************************************************************************/	
	
	
	/********************************** VER COMPARATIVO COSTOS *************************************/
	
	private boolean comparativo = false;
	private boolean comparativoPrr = false;	

	@Command
	@NotifyChange("comparativo")
	public void verComparativo(){
		this.setComparativo(!this.comparativo);
	}
	
	@Command
	@NotifyChange("comparativoPrr")
	public void verComparativoProrrateo(){
		this.setComparativoPrr(!this.comparativoPrr);
	}
	
	/***********************************************************************************************/
	
	
	/********************************** SUGERIR VALORES POR DEFECTO ********************************/
	
	@Command
	@NotifyChange("*")
	public void sugerirValoresProveedor() throws Exception{
		
		if (this.dto.getProveedor().getCondicionPagos().size() > 0) {
			MyArray mc = this.dto.getProveedor().getCondicionPagos().get(0);
			this.dto.setProveedorCondicionPago(mc);
		}			
		this.dto.setMoneda(this.dto.getProveedor().getMonedaConSimbolo());				
	}	
	
	/***********************************************************************************************/	
	
	
	/*************************************** VALIDACION FACTURA ************************************/
	
	private static int UPLOAD_FACTURA = 8;
	private static int AGREGAR_FACTURA = 9;
	private static int SUPRIMIR_FACTURA = 10;
	private static int ELIMINAR_ITEM_FACTURA = 11;
	private static int INSERTAR_ITEM_FACTURA = 12;
	private static int IMPORTAR_ITEMS = 13;
	private static int ADD_GASTOS_DESCUENTOS = 14;
	private static int AGREGAR_GASTO_IMPREVISTO = 15;
	private static int ELIMINAR_GASTO_IMPREVISTO = 16;
	private static int CONFIRMAR_RECEPCION = 17;
	
	private boolean operacionValidaFactura(int operacion) {
		boolean out = true;
		String mensaje = "No se puede realizar la operación debido a: \n";
		
			if (operacion == UPLOAD_FACTURA || operacion == SUPRIMIR_FACTURA
					|| operacion == ADD_GASTOS_DESCUENTOS || operacion == INSERTAR_ITEM_FACTURA
						|| operacion == IMPORTAR_ITEMS) {
				if (this.selectedImportacionFactura.getNumero().length() == 0) {
					out = false;
					mensaje = "Debe agregar o seleccionar una Factura para realizar esta operación";
				}
			}
			
			if (operacion == AGREGAR_FACTURA || operacion == AGREGAR_GASTO_IMPREVISTO) {
				if (this.dto.isPedidoConfirmado() == false) {
					out = false;
					mensaje = "La Orden de Compra aun no fue Confirmada..";
				}
			}
			
			if (operacion == ELIMINAR_ITEM_FACTURA) {
				if (this.selectedFacturaItems.size() == 0) {
					out = false;
					mensaje = "Debe seleccionar al menos un ítem..";
				}
			}
			
			if (operacion == ELIMINAR_GASTO_IMPREVISTO) {
				if (this.selectedGastosImprevistos.size() == 0) {
					mensaje = "Debe seleccionar al menos un ítem..";
					out = false;
				}
			}
			
			if (operacion == CONFIRMAR_RECEPCION) {
				
				if (this.dto.getImportacionFactura().size() == 0) {
					mensaje = "Esta Importación aún no tiene Facturas cargadas..";
					out = false;
				}
				
				if (this.selectedRecepcion.esNuevo() == true) {
					mensaje = "La Factura seleccionada aun no fue guardada..";
					out = false;
				}
			}
			
			if (out == false) {
				mensajeError(mensaje);
			}		
		return out;
	}	
	
	/***********************************************************************************************/	
	
	@Command
	@NotifyChange("*")
	public void aplicarAnticipo(@BindingParam("comp") Popup comp) {
		if(this.selectedAnticipo == null) return;
		this.dto.getAplicacionAnticipos().add(this.selectedAnticipo);
		this.selectedAnticipo = null;
		comp.close();
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn("filterNumero")
	public List<ImportacionPedidoCompra> getImportaciones() throws Exception {
		if (this.filterNumero.isEmpty()) {
			return new ArrayList<ImportacionPedidoCompra>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getImportaciones_(this.filterNumero);
	}
	
	@DependsOn({ "dto.proveedor", "dto.moneda" })
	public List<ImportacionAplicacionAnticipoDTO> getAnticiposPendientes() throws Exception {
		List<ImportacionAplicacionAnticipoDTO> out = new ArrayList<ImportacionAplicacionAnticipoDTO>();
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		List<CtaCteEmpresaMovimiento> movims = rr.getPagosAnticipadosConSaldo(desde, new Date(),
				this.dto.getProveedor().getEmpresa().getId(), this.dto.getMoneda().getId());
		for (CtaCteEmpresaMovimiento movim : movims) {
			if (movim.getTipoMovimiento().getSigla().equals(Configuracion.SIGLA_TM_ANTICIPO_PAGO)) {
				CtaCteEmpresaMovimientoDTO mvdto = (CtaCteEmpresaMovimientoDTO) new AssemblerCtaCteEmpresaMovimiento().domainToDto(movim);
				ImportacionAplicacionAnticipoDTO ant = new ImportacionAplicacionAnticipoDTO();
				ant.setMovimiento(mvdto);
				ant.setImporteDs(mvdto.getSaldo() * -1);
				out.add(ant);
			}
		}
		return out;
	}
	
	@DependsOn({ "dto" })
	public double getDiferenciaTipoCambio() throws Exception {
		if(this.dto.getImportacionFactura().size() == 0 || this.dto.getResumenGastosDespacho().esNuevo()) return 0;
		double totalFacGs = this.dto.getImporteGsTcDespacho();
		double dif = 0;
		
		RegisterDomain rr = RegisterDomain.getInstance();		
		for (ImportacionAplicacionAnticipoDTO anticipo : this.dto.getAplicacionAnticipos()) {
			Recibo ant = rr.getOrdenPagoById(anticipo.getMovimiento().getIdMovimientoOriginal());
			dif += (ant.getTotalImporteGs() - totalFacGs);
		}		
		return dif;
	}
	
	public ImportacionPedidoCompraDTO getDto() {
		return dto;
	}

	public void setDto(ImportacionPedidoCompraDTO dto) {		
		this.dto = dto;
	}
	
	public List<ImportacionPedidoCompraDetalleDTO> getSelectedOrdenItems() {
		return selectedOrdenItems;
	}

	public void setSelectedOrdenItems(
			List<ImportacionPedidoCompraDetalleDTO> selectedOrdenItems) {
		this.selectedOrdenItems = selectedOrdenItems;
	}

	public String getOrdenItemsEliminar() {
		return ordenItemsEliminar;
	}

	public void setOrdenItemsEliminar(String ordenItemsEliminar) {
		this.ordenItemsEliminar = ordenItemsEliminar;
	}
	
	public ImportacionPedidoCompraDetalleDTO getNewDetalle() {
		return newDetalle;
	}

	public void setNewDetalle(ImportacionPedidoCompraDetalleDTO newDetalle) {
		this.newDetalle = newDetalle;
	}

	public String getPathCsvPedidoCompra() {
		return pathCsvPedidoCompra;
	}

	public void setPathCsvPedidoCompra(String pathCsvPedidoCompra) {
		this.pathCsvPedidoCompra = pathCsvPedidoCompra;
	}

	public String getLinkCsvImportado() {
		return linkCsvImportado;
	}

	public void setLinkCsvImportado(String linkCsvImportado) {
		this.linkCsvImportado = linkCsvImportado;
	}
	
	public List<ImportacionPedidoCompraDetalleDTO> getDiferenciasCSV() {
		return diferenciasCSV;
	}

	public void setDiferenciasCSV(List<ImportacionPedidoCompraDetalleDTO> diferenciasCSV) {
		this.diferenciasCSV = diferenciasCSV;
	}
	
	public CuerpoCorreo getCorreo() {
		return correo;
	}

	public void setCorreo(CuerpoCorreo correo) {
		this.correo = correo;
	}

	public String getNombreArchivo() {
		return this.dto.getNumeroPedidoCompra();
	}
	
	public String getLinkImportacionPedidoCompra() {
		return linkImportacionPedidoCompra;
	}

	public void setLinkImportacionPedidoCompra(String linkImportacionPedidoCompra) {
		this.linkImportacionPedidoCompra = linkImportacionPedidoCompra;
	}	
	
	public String getPathRealImportacionPedidoCompra() {
		return pathRealImportacionPedidoCompra;
	}

	public void setPathRealImportacionPedidoCompra(
			String pathRealImportacionPedidoCompra) {
		this.pathRealImportacionPedidoCompra = pathRealImportacionPedidoCompra;
	}
	
	private String selectedPropietarioPedidoCompra = "";

	public String getSelectedPropietarioPedidoCompra() {
		return selectedPropietarioPedidoCompra;
	}

	public void setSelectedPropietarioPedidoCompra(
			String selectedPropietarioPedidoCompra) {
		this.selectedPropietarioPedidoCompra = selectedPropietarioPedidoCompra;
	}

	public List<String> getPropietariosPedidoCompra() {

		List<String> out = new ArrayList<String>();
		out.add(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION);

		return out;
	}
	

	public MyPair getTipoSelected() {
		return tipoSelected;
	}

	public void setTipoSelected(MyPair tipoSelected) {
		this.tipoSelected = tipoSelected;
	}
	
	public boolean isProrrateado() {
		return prorrateado;
	}

	public void setProrrateado(boolean prorrateado) {
		this.prorrateado = prorrateado;
	}
	
	public List<ImportacionFacturaDetalleDTO> getSelectedFacturaItems() {
		return selectedFacturaItems;
	}

	public void setSelectedFacturaItems(
			List<ImportacionFacturaDetalleDTO> selectedFacturaItems) {
		this.selectedFacturaItems = selectedFacturaItems;
	}

	public String getFacturaItemsEliminar() {
		return facturaItemsEliminar;
	}

	public void setFacturaItemsEliminar(String facturaItemsEliminar) {
		this.facturaItemsEliminar = facturaItemsEliminar;
	}
	
	public ImportacionFacturaDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(ImportacionFacturaDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}
	
	public ImportacionFacturaDTO getNvaFactura() {
		return nvaFactura;
	}

	public void setNvaFactura(ImportacionFacturaDTO nvaFactura) {
		this.nvaFactura = nvaFactura;
	}
	
	public String getPathCsvFactura() {
		return pathCsvFactura;
	}

	public void setPathCsvFactura(String pathCsvFactura) {
		this.pathCsvFactura = pathCsvFactura;
	}

	public String getLinkCsvFactura() {
		return linkCsvFactura;
	}

	public void setLinkCsvFactura(String linkCsvFactura) {
		this.linkCsvFactura = linkCsvFactura;
	}
	
	public List<ImportacionFacturaDetalleDTO> getItemsImportar() {
		return itemsImportar;
	}

	public void setItemsImportar(List<ImportacionFacturaDetalleDTO> itemsImportar) {
		this.itemsImportar = itemsImportar;
	}

	public List<ImportacionFacturaDetalleDTO> getSelectedItemsImportar() {
		return selectedItemsImportar;
	}

	public void setSelectedItemsImportar(
			List<ImportacionFacturaDetalleDTO> selectedItemsImportar) {
		this.selectedItemsImportar = selectedItemsImportar;
	}
	
	public String getSelectedPropietarioImportacionFactura() {
		return selectedPropietarioImportacionFactura;
	}

	public void setSelectedPropietarioImportacionFactura(
			String selectedPropietarioImportacionFactura) {
		this.selectedPropietarioImportacionFactura = selectedPropietarioImportacionFactura;
	}

	public String getLabelImportacionBorder() {		
		return getLabelBorder(Configuracion.PROPIETARIO_IMPORTACION_KEY);
	}	
	
	public String getLabelAdministracionBorder() {
		return getLabelBorder(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
	}
	
	public String getLabelVentasBorder(){
		return getLabelBorder(Configuracion.PROPIETARIO_VENTAS_KEY);
	}
	
	public String getLabelAuditoriaBorder(){
		return getLabelBorder(Configuracion.PROPIETARIO_AUDITORIA_KEY);
	}
	
	public String getLabelBorder(int propietarioKey){
		String out = "";
		if (this.selectedImportacionFactura.getPropietarioActual() == propietarioKey) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}
	
	public ImportacionFacturaDTO getSelectedRecepcion() {
		return selectedRecepcion;
	}

	public void setSelectedRecepcion(ImportacionFacturaDTO selectedRecepcion) {
		this.selectedRecepcion = selectedRecepcion;		
	}
	
	public ImportacionGastoImprevistoDTO getNvoGastoImprevisto() {
		return nvoGastoImprevisto;
	}

	public void setNvoGastoImprevisto(
			ImportacionGastoImprevistoDTO nvoGastoImprevisto) {
		this.nvoGastoImprevisto = nvoGastoImprevisto;
	}

	public List<ImportacionGastoImprevistoDTO> getSelectedGastosImprevistos() {
		return selectedGastosImprevistos;
	}

	public void setSelectedGastosImprevistos(
			List<ImportacionGastoImprevistoDTO> selectedGastosImprevistos) {
		this.selectedGastosImprevistos = selectedGastosImprevistos;
	}

	public String getGastosImprevistosEliminar() {
		return gastosImprevistosEliminar;
	}

	public void setGastosImprevistosEliminar(String gastosImprevistosEliminar) {
		this.gastosImprevistosEliminar = gastosImprevistosEliminar;
	}
	
	public boolean isComparativo() {
		return comparativo;
	}

	public void setComparativo(boolean comparativo) {
		this.comparativo = comparativo;
	}
	
	public boolean isComparativoPrr() {
		return comparativoPrr;
	}

	public void setComparativoPrr(boolean comparativoPrr) {
		this.comparativoPrr = comparativoPrr;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public ImportacionPedidoCompra getSelectedImportacion() {
		return selectedImportacion;
	}

	public void setSelectedImportacion(ImportacionPedidoCompra selectedImportacion) {
		this.selectedImportacion = selectedImportacion;
	}

	public ImportacionAplicacionAnticipoDTO getSelectedAnticipo() {
		return selectedAnticipo;
	}

	public void setSelectedAnticipo(ImportacionAplicacionAnticipoDTO selectedAnticipo) {
		this.selectedAnticipo = selectedAnticipo;
	}
	
	/***********************************************************************************************/
	
}



//Validador Insertar Item Orden de Compra..
class ValidadorInsertarItemOrdenCompra implements VerificaAceptarCancelar{
	
	private ImportacionPedidoCompraDetalleDTO newDetalle = new ImportacionPedidoCompraDetalleDTO();
	private String mensajeError = "";	

	public ValidadorInsertarItemOrdenCompra(ImportacionPedidoCompraControlBody ctr){
		this.newDetalle = ctr.getNewDetalle();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.newDetalle.getArticulo().esNuevo()) {
			this.mensajeError = this.mensajeError + "\n - " + Configuracion.TEXTO_ERROR_ITEM_NO_SELECCIONADO;
			out = false;
		}
		
		if (this.newDetalle.getCantidad() <= 0.001) {
			this.mensajeError = this.mensajeError + "\n - " + Configuracion.TEXTO_ERROR_ITEM_CANTIDAD;
			out = false;
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
		return "Error de validación al Cancelar..";
	}	
}



//Validador Diferencias CSV
class ValidadorDiferenciasCSV implements VerificaAceptarCancelar{

	private List<ImportacionPedidoCompraDetalleDTO> diferenciasCSV = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
	private String mensajeError = "";	

	public ValidadorDiferenciasCSV(ImportacionPedidoCompraControlBody ctr){
		this.diferenciasCSV = ctr.getDiferenciasCSV();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		for (ImportacionPedidoCompraDetalleDTO d : this.diferenciasCSV) {
			if (d.isChecked() && d.getCantidadSistema() <= 0.001) {
				this.mensajeError = this.mensajeError + "\n - " + Configuracion.TEXTO_ERROR_ITEM_CANTIDAD;
				out = false;
			}
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
		return "Error de validación al Cancelar..";
	}
}


//Validar Envio de Correo
class ValidadorEnviarCorreo implements VerificaAceptarCancelar{
	
	private CuerpoCorreo correo;
	private Misc misc = new Misc();
	private String mensajeError = "";
	
	public ValidadorEnviarCorreo(ImportacionPedidoCompraControlBody ctr){
		this.correo = ctr.getCorreo();
	}

	@Override
	public boolean verificarAceptar() {		
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if ((correo.getDestinatario().trim().length() == 0) 
				&& (correo.getDestinatarioCopia().trim().length() == 0)
					&& (correo.getDestinatarioCopiaOculta().trim().length() == 0)) {
			this.mensajeError = this.mensajeError + "\n - Debe ingresar una dirección de correo..";
			out = false;
		}
		
		if (correo.getDestinatario().trim().length() == 0) {
			this.mensajeError = this.mensajeError + "\n - Debe ingresar el Destinatario..";
			out = false;
		}
		
		String send = correo.getDestinatario();
		String sendCC = correo.getDestinatarioCopia();
		String sendCCO = correo.getDestinatarioCopiaOculta();
		
		String[] arraySend = null;
		String[] arraySendCC = null;
		String[] arraySendCCO = null;
		
		if (send.trim().length() > 0) {
			arraySend = correo.getDestinatario().trim().split(";");
		}
		
		if (sendCC.trim().length() > 0) {
			arraySendCC = correo.getDestinatarioCopia().trim().split(";");
		}
		
		if (sendCCO.trim().length() > 0) {
			arraySendCCO = correo.getDestinatarioCopiaOculta().trim().split(";");
		}

		String[] correos = misc.concatenarArraysDeString(arraySend, arraySendCC, arraySendCCO, null, null);
		boolean correosCheck = true;
		String correosMsg = "";
		
		if (correos.length > 0) {
			correosCheck = (boolean) misc.chequearMultipleCorreos(correos)[0];
			correosMsg = (String) misc.chequearMultipleCorreos(correos)[1];
		}	
		
		if (correosCheck == false) {
			this.mensajeError = mensajeError + "Las sgtes direcciones de Correo son "
					+ "inválidas: \n" + correosMsg;
			out = false;
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
		return "Error de validación al Cancelar..";
	}	
}


//Validar Nueva Factura
class ValidadorNuevaFactura implements VerificaAceptarCancelar{
	
	private ImportacionFacturaDTO nvaFactura;
	private List<ImportacionFacturaDTO> facturas;
	private String mensajeError = "";
	
	public ValidadorNuevaFactura(ImportacionPedidoCompraControlBody ctr){
		this.nvaFactura = ctr.getNvaFactura();
		this.facturas = ctr.getDto().getImportacionFactura();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.nvaFactura.getNumero().length() == 0) {
			this.mensajeError = this.mensajeError + "\n - El número no puede quedar vacío..";
			out = false;
		}			
		if (this.nvaFactura.getFechaOriginal() == null) {
			this.mensajeError = this.mensajeError + "\n - La fecha no puede quedar vacía..";
			out = false;
		}
		if (this.nvaFactura.getTotalAsignadoDs() <= 0.001) {
			this.mensajeError = this.mensajeError + "\n - El Total asignado debe ser mayor a cero..";
			out = false;
		}
		for (ImportacionFacturaDTO f: this.facturas) {
			if (f.getNumero().compareTo(this.nvaFactura.getNumero()) == 0) {
				this.mensajeError = this.mensajeError + "\n - Número de Factura duplicada: "+this.nvaFactura.getNumero();
				out = false;
			}
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
		return "Error de validación al Cancelar..";
	}
}


//Validar Insertar Item Factura
class ValidadorInsertarItemFactura implements VerificaAceptarCancelar{
	
	private ImportacionFacturaDetalleDTO nvoItem;
	private String mensajeError = "";
	
	public ValidadorInsertarItemFactura(ImportacionPedidoCompraControlBody ctr){
		this.nvoItem = ctr.getNvoItem();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;		
		this.mensajeError = "No se puede realizar la operación debido a: \n";

		if (this.nvoItem.getArticulo().esNuevo() == true) {
			this.mensajeError = this.mensajeError + "\n - Debe seleccionar un artículo..";
			out = false;
		}
		if (this.nvoItem.getCantidad() <= 0) {
			this.mensajeError = this.mensajeError + "\n - La cantidad debe ser mayor a cero..";
			out = false;
		}
		if (this.nvoItem.getCostoDs() <= 0.001) {
			this.mensajeError = this.mensajeError + "\n - El costo debe ser mayor a cero..";
			out = false;
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
		return "Error de validación al Cancelar..";
	}
}


//Validar Importar Items
class ValidadorImportarItems implements VerificaAceptarCancelar{

	private List<ImportacionFacturaDetalleDTO> selectedItemsImportar;
	private String mensajeError = "";
	
	public ValidadorImportarItems(ImportacionPedidoCompraControlBody ctr){
		this.selectedItemsImportar = ctr.getSelectedItemsImportar();
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;		
		this.mensajeError = "No se puede realizar la operación debido a: \n";

		if (this.selectedItemsImportar.size() == 0) {
			//this.mensajeError = this.mensajeError + "\n - Debe seleccionar al menos un ítem..";
			//out = false;
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
		return "Error de validación al Cancelar..";
	}
}


//Validar Insertar Gastos-Descuentos
class ValidadorInsertarGastosDescuentos implements VerificaAceptarCancelar {

	private ImportacionFacturaDetalleDTO nvoItem;
	private ImportacionPedidoCompraControlBody ctr;
	private String mensajeError = "";
	
	public ValidadorInsertarGastosDescuentos(ImportacionPedidoCompraControlBody ctr){
		this.nvoItem = ctr.getNvoItem();
		this.ctr = ctr;
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		MyPair tipo = this.nvoItem.getTipoGastoDescuento();	
		double valorDs = this.nvoItem.getImporteGastoDescuentoDs();
		
		if (tipo.esNuevo() == true) {
			this.mensajeError += "\n - Debe seleccionar el ítem de Gasto o Descuento..";
			out = false;
		}
		
		if ((valorDs >= 0) && (valorDs <= 0.001)) {
			this.mensajeError += "\n - El valor no debe ser cero..";
			out = false;
		}		
		
		if (this.existeItem(tipo) == true) {
			this.mensajeError += "\n - Ya existe un ítem del mismo tipo en la Factura..";
			out = false;
		}
		
		if (this.importeMayorQueFactura() == true) {
				this.mensajeError += "\n - El valor del item supera al Importe total de la Factura..";
				out = false;
		}
		
		if ((this.nvoItem.isValorProrrateo() == true)
				&& (this.nvoItem.getImporteDsCalculado() <= 0)) {
			this.mensajeError += "\n - El valor del prorrateo no debe ser negativo..";
			out = false;
		}
		
		if (this.tipoCorrecto() == false) {
			this.mensajeError += "\n - El tipo de Importación es: " + this.getDescripcionTipo();
			out = false;
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
		return "Error de validación al Cancelar..";
	}
	
	//verifica si ya existe un item segun el idArticulo en la factura
	private boolean existeItem(MyPair tipo){
		ImportacionFacturaDTO fac = ctr.getSelectedImportacionFactura();
		
		for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {			
			if (item.getTipoGastoDescuento().compareTo(tipo) == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	//verifica si el valor del item supera al total de la factura
	private boolean importeMayorQueFactura(){
		
		double importeItem = this.nvoItem.getImporteGsCalculado();
		double importeFact = this.ctr.getTotalImporteFactura()[0];
		
		return importeItem >= importeFact;
	}	
	
	//verifica si es correcto la insercion del item segun el tipo de Importacion
	private boolean tipoCorrecto(){
		boolean out = true;		
		
		MyPair tipo = this.ctr.getDto().getTipo();
		MyPair tipoCIF = this.ctr.getDtoUtil().getTipoImportacionCIF();
		MyPair tipoFOB = this.ctr.getDtoUtil().getTipoImportacionFOB();
		MyPair tipoCF = this.ctr.getDtoUtil().getTipoImportacionCF();
		boolean prorrateado = this.ctr.getDto().isCifProrrateado();
		
		MyPair tipoItem = this.nvoItem.getTipoGastoDescuento();
		MyPair tipoItemGastoFlete = this.ctr.getDtoUtil().getTipoCompraGastoFlete();
		MyPair tipoItemGastoSeguro = this.ctr.getDtoUtil().getTipoCompraGastoSeguro();
		MyPair tipoItemProrrFlete = this.ctr.getDtoUtil().getTipoCompraProrrateoFlete();
		MyPair tipoItemProrrSeguro = this.ctr.getDtoUtil().getTipoCompraProrrateoSeguro();
		
		if ((tipoItem.compareTo(tipoItemGastoFlete) == 0)
				&& ((tipo.compareTo(tipoFOB) == 0) 
						|| (tipo.compareTo(tipoCIF) == 0 && prorrateado == true))) {
			out = false;
		}
		
		if ((tipoItem.compareTo(tipoItemGastoSeguro) == 0)
				&& ((tipo.compareTo(tipoFOB) == 0)
						|| (tipo.compareTo(tipoCF) == 0)
							|| (tipo.compareTo(tipoCIF) == 0 && prorrateado == true))) {
			out = false;
		}
		
		if ((tipoItem.compareTo(tipoItemProrrFlete) == 0 
				|| tipoItem.compareTo(tipoItemProrrSeguro) == 0)
					&& ((tipo.compareTo(tipoFOB) == 0)
						|| (tipo.compareTo(tipoCF) == 0)
							|| (tipo.compareTo(tipoCIF) == 0 && prorrateado == false))) {
			out = false;
		}
		
		return out;
	}
	
	private String getDescripcionTipo(){
		String add = "";
		
		MyPair tipo = this.ctr.getDto().getTipo();
		MyPair tipoCIF = this.ctr.getDtoUtil().getTipoImportacionCIF();
		boolean prorrateado = this.ctr.getDto().isCifProrrateado();
		
		if ((tipo.compareTo(tipoCIF) == 0)
				&& (prorrateado == true)) {
			add = " Prorrateado";
		}
		
		if ((tipo.compareTo(tipoCIF) == 0)
				&& (prorrateado == false)) {
			add = " Sin Prorratear";
		}
		
		return tipo.getText() + add;
	}
}


//Validar Agregar Gasto Imprevisto
class ValidadorAgregarGastoImprevisto implements VerificaAceptarCancelar{

	private ImportacionGastoImprevistoDTO nvoGastoImprevisto;
	private Misc misc = new Misc();
	private String mensajeError = "";
	
	public ValidadorAgregarGastoImprevisto(ImportacionPedidoCompraControlBody ctr){
		this.nvoGastoImprevisto = ctr.getNvoGastoImprevisto();
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.nvoGastoImprevisto.getProveedor().esNuevo() == true) {
			this.mensajeError = this.mensajeError + "\n - Debe seleccionar el Proveedor..";
			out = false;
		}
		
		if (misc.esIgual(this.nvoGastoImprevisto.getImporteDs(), 0) == true) {
			this.mensajeError = this.mensajeError + "\n - El importe debe ser mayor a cero..";
			out = false;			
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
		return "Error de validación al Cancelar..";
	}
}

/**
 * Reporte de Importacion..
 */
class ReporteImportacion extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30, false);
	static DatosColumnas col4 = new DatosColumnas("Costo Gs.", TIPO_DOUBLE_GS, 30, false); 
	static DatosColumnas col5 = new DatosColumnas("Costo USD", TIPO_DOUBLE_DS, 30, false); 
	static DatosColumnas col6 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true); 
	static DatosColumnas col7 = new DatosColumnas("Importe USD", TIPO_DOUBLE_DS, 30, true);
	
	public ReporteImportacion(ImportacionPedidoCompraDTO importacion) {
		this.importacion = importacion;
	}
	
	static {
		cols.add(col1);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Compra Importacion");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();
		String factura = this.importacion.getImportacionFactura().get(0).getNumero();
		String proveedor = this.importacion.getProveedor().getRazonSocial();
		String tipoCambio = Utiles.getNumberFormat(this.importacion.getResumenGastosDespacho().getTipoCambio());
		String coeficiente = Utiles.getNumberFormatDs_(this.importacion.getResumenGastosDespacho().getCoeficiente());

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Nro. Factura", factura))
				.add(this.textoParValor("Tipo Cambio Gs.", tipoCambio))
				.add(this.textoParValor("Coeficiente", coeficiente)));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", proveedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}