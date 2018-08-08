package com.yhaguy.gestion.compras.importacion;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.gestion.articulos.ArticuloDTO;
import com.yhaguy.gestion.articulos.AssemblerArticulo;

public class ImportacionSimpleControl extends SoloViewModel {

	private ImportacionPedidoCompraControlBody dato = new ImportacionPedidoCompraControlBody();
	private UtilDTO utilDto = (UtilDTO) this.getDtoUtil();

	public ImportacionPedidoCompraControlBody getDato() {
		return dato;
	}

	public void setDato(ImportacionPedidoCompraControlBody dato) {
		this.dato = dato;
	}

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) ImportacionPedidoCompraControlBody dato) {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireEventListeners(view, this);
	}

	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_IMPORTACION_ABM;
	}

	/************************************ BUSCAR ARTICULO *************************************/

	private static String[] attArticulo = { "codigoInterno", "descripcion" };
	private static String[] columnas = { "Código", "Descripción" };

	static int BUSQUEDA_POR_CODIGO = 0;
	static int BUSQUEDA_POR_DESCRIPCION = 1;

	@Command
	public void buscarArticulo(@BindingParam("tipo") Object tipo,
			@BindingParam("busqueda") int busqueda) throws Exception {

		String find = "";
		String cod = "";
		String des = "";

		if (tipo instanceof ImportacionFacturaDetalleDTO) {
			cod = this.dato.getNvoItem().getArticulo().getCodigoInterno();
			des = this.dato.getNvoItem().getArticulo().getDescripcion();

		} else {
			cod = this.dato.getNewDetalle().getArticulo().getCodigoInterno();
			des = this.dato.getNewDetalle().getArticulo().getDescripcion();
		}

		find = (busqueda == BUSQUEDA_POR_CODIGO) ? cod : des;

		BuscarElemento b = new BuscarElemento();
		b.setClase(Articulo.class);
		b.setTitulo("Buscar Artículo");
		b.setAtributos(attArticulo);
		b.setNombresColumnas(columnas);
		b.setWidth("800px");
		b.setAssembler(new AssemblerArticulo());
		b.addWhere("c.articuloEstado.sigla = '" + Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO + "'");
		b.show(find, busqueda);
		
		if ((b.isClickAceptar()) && (tipo instanceof ImportacionPedidoCompraDetalleDTO)) {
			this.dato.getNewDetalle().setArticulo((ArticuloDTO) b.getSelectedItemDTO());
			
		} else if ((b.isClickAceptar()) && (tipo instanceof ImportacionFacturaDetalleDTO)) {
			this.dato.getNvoItem().setArticulo((ArticuloDTO) b.getSelectedItemDTO());
		}
		BindUtils.postNotifyChange(null, null, this.dato.getNewDetalle(), "articulo");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "articulo");
	}

	/******************************************************************************************/

	public String getNombreArchivoAdjunto() {
		return dato.getNombreArchivo() + ".pdf";
	}

	@Command
	public void notificarItemGs(@BindingParam("comp") Doublebox comp) {
		double costoDs = this.dato.getNewDetalle().getCostoProformaDs();
		if (costoDs < 0) {
			m.mensajePopupTemporal("El costo no puede ser negativo..", "error",
					comp);
			this.dato.getNewDetalle().setCostoProformaDs(0);
			costoDs = 0;
		}
		this.dato.getNewDetalle().setCostoProformaGs(
				costoDs * this.dato.getDto().getCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNewDetalle(),
				"costoProformaGs");
		BindUtils.postNotifyChange(null, null, this.dato.getNewDetalle(),
				"costoProformaDs");
	}

	@Command
	public void notificarItemDs(@BindingParam("comp") Doublebox comp) {
		double costoGs = this.dato.getNewDetalle().getCostoProformaGs();
		if (costoGs < 0) {
			m.mensajePopupTemporal("El costo no puede ser negativo..", "error",
					comp);
			this.dato.getNewDetalle().setCostoProformaGs(0);
			costoGs = 0;
		}
		this.dato.getNewDetalle().setCostoProformaDs(
				costoGs / this.dato.getDto().getCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNewDetalle(),
				"costoProformaDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNewDetalle(),
				"costoProformaGs");
	}

	@Command
	public void validarCantidad(@BindingParam("comp") Intbox comp) {
		if (this.dato.getNewDetalle().getCantidad() < 0) {
			m.mensajePopupTemporal("La cantidad no puede ser negativa..",
					"error", comp);
			this.dato.getNewDetalle().setCantidad(0);
		}
		BindUtils.postNotifyChange(null, null, this.dato.getNewDetalle(),
				"cantidad");
	}

	/************************************** FACTURA ****************************************/

	@Command
	public void dolarizarTotal(@BindingParam("comp") Doublebox comp) {
		double totalGs = this.dato.getNvaFactura().getTotalAsignadoGs();
		if (totalGs < 0) {
			m.mensajePopupTemporal("El total no puede ser negativo..", "error",
					comp);
			this.dato.getNvaFactura().setTotalAsignadoGs(0);
			totalGs = 0;
		}
		this.dato.getNvaFactura().setTotalAsignadoDs(
				totalGs / this.dato.getDto().getCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(),
				"totalAsignadoDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(),
				"totalAsignadoGs");
	}

	@Command
	public void guaranizarTotal(@BindingParam("comp") Doublebox comp) {
		double totalDs = this.dato.getNvaFactura().getTotalAsignadoDs();
		if (totalDs < 0) {
			m.mensajePopupTemporal("El total no puede ser negativo..", "error",
					comp);
			this.dato.getNvaFactura().setTotalAsignadoDs(0);
			totalDs = 0;
		}
		this.dato.getNvaFactura().setTotalAsignadoGs(
				totalDs * this.dato.getDto().getCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(),
				"totalAsignadoDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(),
				"totalAsignadoGs");
	}

	@Command
	public void validarCantidadItemFactura(@BindingParam("comp") Intbox comp) {
		if (this.dato.getNvoItem().getCantidad() < 0) {
			m.mensajePopupTemporal("La cantidad no puede ser negativa..",
					"error", comp);
			this.dato.getNvoItem().setCantidad(0);
		}
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"cantidad");
	}

	@Command
	public void notificarFacturaItemDs(@BindingParam("comp") Doublebox comp) {
		double costoGs = this.dato.getNvoItem().getCostoGs();
		if (costoGs < 0) {
			m.mensajePopupTemporal("El costo no puede ser negativo..", "error",
					comp);
			this.dato.getNvoItem().setCostoGs(0);
			costoGs = 0;
		}
		this.dato.getNvoItem().setCostoDs(
				costoGs / this.dato.getDto().getCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"costoDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"costoGs");
	}

	@Command
	public void notificarFacturaItemGs(@BindingParam("comp") Doublebox comp) {
		double costoDs = this.dato.getNvoItem().getCostoDs();
		if (costoDs < 0) {
			m.mensajePopupTemporal("El costo no puede ser negativo..", "error",
					comp);
			this.dato.getNvoItem().setCostoDs(0);
			costoDs = 0;
		}
		this.dato.getNvoItem().setCostoGs(
				costoDs * this.dato.getDto().getCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"costoDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"costoGs");
	}

	/************************************** DESCUENTO ***********************************/

	@Wire
	private Doublebox porc;

	@Command
	public void notificarTipoDcto() {

		this.dato.getNvoItem().setImporteGastoDescuentoDs(0);
		this.dato.getNvoItem().setImporteGastoDescuentoGs(0);
		porc.setValue(0);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"importeGastoDescuentoDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"importeGastoDescuentoGs");
	}

	@Command
	public void calcularDescuento(@BindingParam("porc") double porc) {
		double descDs = (this.dato.getTotalImporteFactura()[1] * porc) / 100;
		this.dato.getNvoItem().setImporteGastoDescuentoDs(descDs);
		this.notificarGastoDctoGs(porc);
	}

	@Command
	public void notificarGastoDctoDs() {
		double gdescGs = this.dato.getNvoItem().getImporteGastoDescuentoGs();
		if (this.dato.getNvoItem().getArticulo().getCodigoInterno()
				.compareTo(Configuracion.CODIGO_ITEM_DESCUENTO_KEY) == 0) {
			if (gdescGs > 0) {
				gdescGs = gdescGs * -1;
			}
		} else {
			if (gdescGs < 0) {
				gdescGs = gdescGs * -1;
			}
		}
		this.dato.getNvoItem().setImporteGastoDescuentoDs(
				gdescGs / this.dato.getDto().getCambio());
		this.dato.getNvoItem().setImporteGastoDescuentoGs(gdescGs);
		this.porc.setValue(0);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"importeGastoDescuentoDs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"importeGastoDescuentoGs");
	}

	@Command
	public void notificarGastoDctoGs() {
		this.notificarGastoDctoGs(0);
	}

	public void notificarGastoDctoGs(double porc) {
		double gdescDs = this.dato.getNvoItem().getImporteGastoDescuentoDs();
		if (this.dato.getNvoItem().getArticulo().getCodigoInterno()
				.compareTo(Configuracion.CODIGO_ITEM_DESCUENTO_KEY) == 0) {
			if (gdescDs > 0) {
				gdescDs = gdescDs * -1;
			}
		} else {
			if (gdescDs < 0) {
				gdescDs = gdescDs * -1;
			}
		}
		this.dato.getNvoItem().setImporteGastoDescuentoGs(
				gdescDs * this.dato.getDto().getCambio());
		this.dato.getNvoItem().setImporteGastoDescuentoDs(gdescDs);
		this.porc.setValue(porc);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"importeGastoDescuentoGs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(),
				"importeGastoDescuentoDs");
	}

	@Command
	public void notificarProrrateo() {

		ImportacionFacturaDetalleDTO item = this.dato.getNvoItem();
		MyPair tipoSelected = item.getTipoGastoDescuento();
		MyPair tipoProrrateoFlete = utilDto.getTipoCompraProrrateoFlete();
		MyPair tipoProrrateoSeguro = utilDto.getTipoCompraProrrateoSeguro();

		if ((tipoSelected.compareTo(tipoProrrateoFlete) == 0)
				|| (tipoSelected.compareTo(tipoProrrateoSeguro) == 0)) {
			item.setValorProrrateo(true);
		} else {
			item.setValorProrrateo(false);
		}
		BindUtils.postNotifyChange(null, null, item, "valorProrrateo");
	}

	@Command
	public void dolarizarGastoImprevisto(@BindingParam("comp") Doublebox comp) {
		double valorGs = this.dato.getNvoGastoImprevisto().getImporteGs();
		double cambio = this.dato.getDto().getResumenGastosDespacho()
				.getTipoCambio();
		if (valorGs < 0) {
			m.mensajePopupTemporal("El importe no puede ser negativo..",
					"error", comp);
			this.dato.getNvoGastoImprevisto().setImporteGs(0);
			valorGs = 0;
		}
		this.dato.getNvoGastoImprevisto().setImporteDs(valorGs / cambio);
		BindUtils.postNotifyChange(null, null,
				this.dato.getNvoGastoImprevisto(), "importeDs");
		BindUtils.postNotifyChange(null, null,
				this.dato.getNvoGastoImprevisto(), "importeGs");
	}

	@Command
	public void guaranizarGastoImprevisto(@BindingParam("comp") Doublebox comp) {
		double valorDs = this.dato.getNvoGastoImprevisto().getImporteDs();
		double cambio = this.dato.getDto().getResumenGastosDespacho()
				.getTipoCambio();
		if (valorDs < 0) {
			m.mensajePopupTemporal("El importe no puede ser negativo..",
					"error", comp);
			this.dato.getNvoGastoImprevisto().setImporteDs(0);
			valorDs = 0;
		}
		this.dato.getNvoGastoImprevisto().setImporteGs(valorDs * cambio);
		BindUtils.postNotifyChange(null, null,
				this.dato.getNvoGastoImprevisto(), "importeDs");
		BindUtils.postNotifyChange(null, null,
				this.dato.getNvoGastoImprevisto(), "importeGs");
	}
}
