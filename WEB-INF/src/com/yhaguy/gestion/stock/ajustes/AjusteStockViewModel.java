package com.yhaguy.gestion.stock.ajustes;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.Config;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class AjusteStockViewModel extends BodyApp {
	
	static final String ZUL_INSERT_ITEM = "/yhaguy/gestion/stock/insertarAjuste.zul";
	static final String KEY_NRO = "AJT";
	
	private AjusteStockDTO dto = new AjusteStockDTO();
	private AjusteStockDetalleDTO nvoDetalle;
	private String mensaje;
	
	private List<AjusteStockDetalleDTO> selectedItems;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public Assembler getAss() {
		return new AjusteStockAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (AjusteStockDTO) dto;
		if (this.dto.getEstadoComprobante().getSigla()
				.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO)) {
			this.enmascararAnulados(true);
		} else {
			this.enmascararAnulados(false);
		}
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		AjusteStockDTO out = new AjusteStockDTO();
		this.sugerirValores(out);
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return AjusteStock.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}
	
	@Override
	public Browser getBrowser() {
		return new AjusteStockBrowser();
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return (this.dto.esNuevo())
				|| (this.dto.isConfirmado() == false);
	}
	
	@Override
	public void showImprimir() {
		this.imprimir();
	}
	
	@Override
	public boolean verificarAlGrabar() {
		try {
			return this.validarFormulario();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensaje;
	}
	
	/*****************************************************/
	
	
	/********************** COMANDOS *********************/
	
	@Command
	@NotifyChange("*")
	public void insertItem() throws Exception {
		this.insertarItem();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteItem() {
		this.eliminarItem();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() throws Exception {
		this.confirmarAjuste();
	}
	
	/*****************************************************/
	
	
	/********************** FUNCIONES ********************/
	
	/**
	 * sugiere valores por defecto..
	 */
	private void sugerirValores(AjusteStockDTO dto) {
		dto.setSucursal(this.getSucursal());
		dto.setEstadoComprobante(this.getEstadoComprobantePendiente());
	}
	
	/**
	 * inserta el item al detalle..
	 */
	private void insertarItem() throws Exception {
		this.nvoDetalle = new AjusteStockDetalleDTO();
		
		WindowPopup wp = new WindowPopup();
		wp.setCheckAC(new ValidadorInsertarItem());
		wp.setDato(this);
		wp.setHigth("360px");
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
	 * elimina los items seleccionados..
	 */
	private void eliminarItem() {
		if (this.mensajeSiNo("Desea eliminar los ítems seleccionados..") == false)
			return;
		this.dto.getDetalles().removeAll(this.selectedItems);
		this.selectedItems = null;
	}
	
	/**
	 * confirma el ajuste..
	 */
	private void confirmarAjuste() throws Exception {
		if(this.mensajeSiNo("Desea Confirmar el Ajuste..") == false)
			return;
		this.dto.setEstadoComprobante(this.getEstadoComprobanteCerrado());
		this.dto.setActualizarStock(true);
		this.dto.setAutorizadoPor(this.getUs().getNombre());
		this.dto.setReadonly();
		this.dto = (AjusteStockDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		Clients.showNotification("Ajuste Confirmado..");
	}
	
	/**
	 * impresion del ajuste..
	 */
	private void imprimir() {
		List<Object[]> data = new ArrayList<Object[]>();

		for (AjusteStockDetalleDTO item : this.dto.getDetalles()) {
			Object[] obj1 = new Object[] { item.getArticulo().getPos1(),
					item.getArticulo().getPos2(), item.getArticulo().getPos3(),
					item.getArticulo().getPos4(), item.getCantidad() };
			data.add(obj1);
		}

		ReporteYhaguy rep = new AjusteStockReporte(this.dto);
		rep.setDatosReporte(data);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * @return validacion de datos del formulario..
	 */
	private boolean validarFormulario() throws Exception {
		boolean out = true;
		this.mensaje = "No se puede completar la operación debido a: \n";
		
		if (this.dto.getDescripcion().isEmpty()) {
			this.mensaje += "\n - Debe ingresar la descripción..";
			out = false;
		
		}
		
		if (this.dto.getDetalles().size() == 0) {
			this.mensaje += "\n - Debe ingresar al menos un ítem..";
			out = false;
		}
		
		if(out == true && this.dto.esNuevo()){
			this.dto.setNumero(AutoNumeroControl.getAutoNumeroKey(KEY_NRO, 7));
		}
		
		return out;
	}
	
	/*****************************************************/
	
	
	/******************** VALIDACIONES *******************/
	
	/**
	 * Validador insertar item..
	 */
	class ValidadorInsertarItem implements VerificaAceptarCancelar {

		private String mensaje;
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if (nvoDetalle.getArticulo().esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe seleccionar un artículo..";
			}
			
			if(this.isDuplicado()){
				out = false;
				this.mensaje += "\n - No se permiten ítems duplicados..";
			};
			
			if (nvoDetalle.getCantidad() == 0) {
				out = false;
				this.mensaje += "\n - La cantidad no puede ser cero..";
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
		
		/**
		 * @return true si el item ya existe en el detalle.
		 */
		private boolean isDuplicado() {
			long idArt = nvoDetalle.getArticulo().getId();
			for (AjusteStockDetalleDTO item : dto.getDetalles()) {
				long idArt_ = item.getArticulo().getId();
				if(idArt == idArt_)
					return true;
			}			
			return false;
		}
		
	}
	
	/*****************************************************/
	
	
	/********************** GET/SET **********************/
	
	@DependsOn({ "dto.tipoMovimiento", "dto.deposito", "dto.descripcion" })
	public boolean isDetalleVisible() {
		return (this.dto.getTipoMovimiento() != null)
				&& (this.dto.getDeposito() != null)
				&& (this.dto.getDescripcion().isEmpty() == false);
	}
	
	@DependsOn({ "deshabilitado", "selectedItems" })
	public boolean isDeleteItemDisabled() {
		return this.isDeshabilitado() || this.selectedItems == null
				|| this.selectedItems.size() == 0;
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isConfirmarDisabled() {
		return this.isDeshabilitado() || this.dto.esNuevo();
	}
	
	@DependsOn({ "deshabilitado", "dto.detalles" })
	public boolean isCheckmarkVisible() {
		return (this.isDeshabilitado() == false)
				&& (this.dto.getDetalles().size() > 0);
	}
	
	/**
	 * @return el costo del articulo a ajustar..
	 */
	public double getCostoGs(long idArticulo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);		
		return art.getCostoGs();
	}
	
	/**
	 * @return tipos de movimientos de ajuste..
	 */
	public List<MyArray> getTiposAjuste() {
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(this.getDtoUtil().getTmAjustePositivo());
		out.add(this.getDtoUtil().getTmAjusteNegativo());
		return out;
	}

	public AjusteStockDTO getDto() {
		return dto;
	}

	public void setDto(AjusteStockDTO dto) {
		this.dto = dto;
	}

	public AjusteStockDetalleDTO getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(AjusteStockDetalleDTO nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}

	public List<AjusteStockDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<AjusteStockDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}
}

/**
 * Browser..
 */
class AjusteStockBrowser extends Browser {

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser numero = new ColumnaBrowser();
		ColumnaBrowser fecha = new ColumnaBrowser();
		ColumnaBrowser tipo = new ColumnaBrowser();
		ColumnaBrowser sucursal = new ColumnaBrowser();
		ColumnaBrowser deposito = new ColumnaBrowser();
		ColumnaBrowser estado = new ColumnaBrowser();
		
		numero.setCampo("numero");
		numero.setTitulo("Número");
		numero.setWidthColumna("110px");
		
		fecha.setCampo("fecha");
		fecha.setTitulo("Fecha");
		fecha.setTipo(Config.TIPO_DATE);
		fecha.setComponente(Browser.LABEL_DATE);
		
		tipo.setCampo("tipoMovimiento.descripcion");
		tipo.setTitulo("Tipo Movimiento");
		
		sucursal.setCampo("sucursal.descripcion");
		sucursal.setTitulo("Sucursal");
		
		deposito.setCampo("deposito.descripcion");
		deposito.setTitulo("Depósito");
		deposito.setWidthColumna("110px");
		
		estado.setCampo("estadoComprobante.descripcion"); 	
		estado.setTitulo("Estado");	
		estado.setWidthColumna("110px");
		
		List<ColumnaBrowser> out = new ArrayList<ColumnaBrowser>();
		out.add(numero);
		out.add(fecha);
		out.add(tipo);
		out.add(sucursal);
		out.add(deposito);
		out.add(estado);
		
		return out;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return AjusteStock.class;
	}

	@Override
	public void setingInicial() {
		this.addOrden("id");
		this.setWidthWindows("900px");
		this.setHigthWindows("80%");
	}

	@Override
	public String getTituloBrowser() {
		return "Ajustes de Stock";
	}	
}

/**
 * Reporte de Presupuesto..
 */
class AjusteStockReporte extends ReporteYhaguy {
	
	private AjusteStockDTO ajuste;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Código Proveedor", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Código Original", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30, false);
	
	public AjusteStockReporte(AjusteStockDTO ajuste) {
		this.ajuste = ajuste;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Ajuste de Stock");
		this.setDirectorio("ajustes");
		this.setNombreArchivo("Ajuste-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.ajuste.getNumero();
		String sucursal = this.ajuste.getSucursal().getText();
		String tipo = (String) this.ajuste.getTipoMovimiento().getPos1();
		String autorizadoPor = this.ajuste.getAutorizadoPor();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Sucursal", sucursal)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Tipo Movimiento", tipo)));
		
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Autorizado por", autorizadoPor)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
