package com.yhaguy.gestion.stock.inventarios;

import java.util.ArrayList;
import java.util.Date;
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
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class InventarioViewModel extends BodyApp {
	
	static final String ZUL_INSERT_ITEM = "/yhaguy/gestion/stock/insertarInventario.zul";
	static final String KEY_NRO = "INVENTARIO";
	
	private InventarioDTO dto = new InventarioDTO();
	private InventarioDetalleDTO nvoDetalle;
	private String mensaje;
	
	private List<InventarioDetalleDTO> selectedItems;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public Assembler getAss() {
		return new InventarioAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (InventarioDTO) dto;
		if (this.dto.getEstadoComprobante().getSigla()
				.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO)) {
			this.enmascararAnulados(true);
		} else {
			this.enmascararAnulados(false);
		}
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		InventarioDTO out = new InventarioDTO();
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
		return new InventarioBrowser();
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
		this.confirmarInventario();
	}
	
	/*****************************************************/
	
	
	/********************** FUNCIONES ********************/
	
	/**
	 * sugiere valores por defecto..
	 */
	private void sugerirValores(InventarioDTO dto) {
		dto.setSucursal(this.getSucursal());
		dto.setEstadoComprobante(this.getEstadoComprobantePendiente());
		dto.setTipoMovimiento(this.getDtoUtil().getTmInventario());
	}
	
	/**
	 * inserta el item al detalle..
	 */
	private void insertarItem() throws Exception {
		this.nvoDetalle = new InventarioDetalleDTO();
		
		WindowPopup wp = new WindowPopup();
		wp.setCheckAC(new ValidadorInsertarItem());
		wp.setDato(this);
		wp.setHigth("360px");
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Insertar ítem");
		wp.setWidth("400px");
		wp.show(ZUL_INSERT_ITEM);
		if (wp.isClickAceptar()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			long sistema = rr.getStockDisponible(this.nvoDetalle.getArticulo().getId(), this.dto.getDeposito().getId());
			this.nvoDetalle.setCantidadSistema(Long.valueOf(sistema).intValue());
			boolean registrado = false;
			for (InventarioDetalleDTO item : this.dto.getDetalles()) {
				if (item.getArticulo().getId().longValue() == this.nvoDetalle.getArticulo().getId().longValue()) {
					registrado = true;
					item.setCantidad(item.getCantidad() + this.nvoDetalle.getCantidad());
					item.setCantidadSistema(Long.valueOf(sistema).intValue());
				}
			}
			if (!registrado) {
				this.dto.getDetalles().add(this.nvoDetalle);
			}			
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
	private void confirmarInventario() throws Exception {
		if(this.mensajeSiNo("Desea Confirmar el Inventario..") == false)
			return;
		this.dto.setEstadoComprobante(this.getEstadoComprobanteCerrado());
		this.dto.setAutorizadoPor(this.getUs().getNombre());
		this.dto.setReadonly();
		this.dto = (InventarioDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		Clients.showNotification("Inventario Confirmado..");
	}
	
	/**
	 * impresion del ajuste..
	 */
	private void imprimir() {
		List<Object[]> data = new ArrayList<Object[]>();

		for (InventarioDetalleDTO item : this.dto.getDetalles()) {
			Object[] obj1 = new Object[] { item.getArticulo().getPos1(), item.getCantidad(), item.getCantidadSistema(),
					item.getDiferencia() };
			data.add(obj1);
		}

		ReporteYhaguy rep = new InventarioReporte(this.dto);
		rep.setTitulo("Inventario de Mercaderías");
		rep.setDatosReporte(data);

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
		
		if(out == true && this.dto.esNuevo()) {
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
			for (InventarioDetalleDTO item : dto.getDetalles()) {
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
	 * @return los contadores..
	 */
	public List<String> getContadores() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Funcionario func : rr.getFuncionariosDeposito()) {
			out.add(func.getRazonSocial().toUpperCase());
		}
		return out;
	}
	
	/**
	 * @return el costo del articulo a ajustar..
	 */
	public double getCostoGs(long idArticulo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);		
		return art.getCostoGs();
	}

	public InventarioDTO getDto() {
		return dto;
	}

	public void setDto(InventarioDTO dto) {
		this.dto = dto;
	}

	public InventarioDetalleDTO getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(InventarioDetalleDTO nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}

	public List<InventarioDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<InventarioDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}
}

/**
 * Browser..
 */
class InventarioBrowser extends Browser {

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser numero = new ColumnaBrowser();
		ColumnaBrowser fecha = new ColumnaBrowser();
		ColumnaBrowser tipo = new ColumnaBrowser();
		ColumnaBrowser sucursal = new ColumnaBrowser();
		ColumnaBrowser deposito = new ColumnaBrowser();
		ColumnaBrowser contador = new ColumnaBrowser();
		ColumnaBrowser estado = new ColumnaBrowser();
		
		numero.setCampo("numero");
		numero.setTitulo("Número");
		
		fecha.setCampo("fecha");
		fecha.setTitulo("Fecha");
		fecha.setWidthColumna("130px");
		fecha.setTipo(Config.TIPO_DATE);
		fecha.setComponente(Browser.LABEL_DATE);
		fecha.setValue(Utiles.getDateToString(new Date(), "yyyy-MM-"));
		
		tipo.setCampo("tipoMovimiento.descripcion");
		tipo.setTitulo("Tipo Movimiento");
		tipo.setWhere("tipoMovimiento.sigla = '" + Configuracion.SIGLA_TM_INVENTARIO_MERCADERIAS + "'");
		
		sucursal.setCampo("sucursal.descripcion");
		sucursal.setTitulo("Sucursal");
		
		deposito.setCampo("deposito.descripcion");
		deposito.setTitulo("Depósito");
		deposito.setWidthColumna("120px");
		
		contador.setCampo("orden");
		contador.setTitulo("Contador");
		
		estado.setCampo("estadoComprobante.descripcion"); 	
		estado.setTitulo("Estado");	
		estado.setWidthColumna("90px");
		
		List<ColumnaBrowser> out = new ArrayList<ColumnaBrowser>();
		out.add(numero);
		out.add(fecha);
		out.add(tipo);
		out.add(contador);
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
		this.setWidthWindows("1100px");
		this.setHigthWindows("80%");
	}

	@Override
	public String getTituloBrowser() {
		return "Inventarios";
	}	
}

/**
 * Reporte de Presupuesto..
 */
class InventarioReporte extends ReporteYhaguy {
	
	private InventarioDTO ajuste;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30, false);
	static DatosColumnas col3 = new DatosColumnas("Sistema", TIPO_INTEGER, 30, false);
	static DatosColumnas col4 = new DatosColumnas("Diferencia", TIPO_INTEGER, 30, false);
	
	public InventarioReporte(InventarioDTO ajuste) {
		this.ajuste = ajuste;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Inventario de Mercaderías");
		this.setDirectorio("ajustes");
		this.setNombreArchivo("Inventario-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.ajuste.getNumero();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.textoParValor("Número", numero))
				.add(this.textoParValor("Depósito", this.ajuste.getDeposito().getText().toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Contador", this.ajuste.getOrden().toUpperCase()))
				.add(this.textoParValor("Confirmado por", this.ajuste.getAutorizadoPor().toUpperCase())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
