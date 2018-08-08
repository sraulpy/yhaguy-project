package com.yhaguy.gestion.tesaka;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Retencion;
import com.yhaguy.gestion.caja.recibos.AssemblerRecibo;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.caja.recibos.ReciboDetalleDTO;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class TesakaViewModel extends SimpleViewModel {
	
	private ReciboDTO selectedPago = new ReciboDTO();
	private List<ReciboDetalleDTO> selectedItems;
	
	private int selectedPeriodo = 0;
	private String selectedTipoRetencion = Retencion.EMITIDAS;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	
	/******************* COMANDOS *******************/
	
	@Command
	@NotifyChange("*")
	public void buscarPagos(@BindingParam("posFiltro") int posFiltro) throws Exception {
		String filtro = "";
		
		switch (posFiltro) {
		case 0:
			filtro = this.selectedPago.getNumero();
			break;
		case 1:
			filtro = (String) this.selectedPago.getProveedor().getPos3();
			break;
		case 2:
			filtro = (String) this.selectedPago.getProveedor().getPos2();
			break;
		}
		this.buscarPagos(filtro, posFiltro);
	}
	
	@Command
	@NotifyChange("selectedPago")
	public void generarArchivo() throws Exception {

		if (this.mensajeSiNo(this.getMensajeConfirmacion()) == true) {
			String nroPago = this.selectedPago.getNumero();
			List<CtaCteEmpresaMovimientoDTO> movims = new ArrayList<CtaCteEmpresaMovimientoDTO>();
			for (ReciboDetalleDTO pago : this.selectedItems) {
				movims.add(pago.getMovimiento());
			}
			TesakaParser.generarArchivoRetenciones(movims, nroPago);
			TesakaParser.setTesakaMovimiento(movims, this.getLoginNombre());
			String tesaka = TesakaParser.setTesakaPago(
					this.selectedPago.getId(), this.getUs().getNombre());
			this.selectedPago.setTesaka(tesaka);
			Clients.showNotification("Archivo Generado..");
		}
	}
	
	@Command
	public void downloadFile() {
		try {
			Filedownload.save(
					TesakaParser.PATH_FILE + this.selectedPago.getNumero()
							+ ".json", null);
		} catch (Exception e) {
			Clients.showNotification("Archivo no encontrado..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	
	/******************* FUNCIONES *******************/

	/**
	 * busqueda de los pagos..
	 */
	private void buscarPagos(String filtro, int posFiltro) throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Recibo.class);
		b.setAnchoColumnas(new String[]{ "140px", "140px", ""});
		b.setAssembler(new AssemblerRecibo());
		b.setAtributos(new String[] { "numero", "proveedor.empresa.ruc", "proveedor.empresa.razonSocial" });		
		b.setHeight("400px");
		b.setWidth("800px");
		b.setNombresColumnas(new String[] { "Número", "Ruc", "Razón Social" });
		b.setTitulo("Órdenes de Pago");
		b.addWhere("c.tipoMovimiento.sigla = '" + Configuracion.SIGLA_TM_RECIBO_PAGO + "'");
		b.show(filtro, posFiltro);
		if (b.isClickAceptar()) {
			this.selectedPago = (ReciboDTO) b.getSelectedItemDTO();
			this.selectedItems = null;
			if (!this.isCheckmarkVisible()) {
				this.selectedItems = new ArrayList<ReciboDetalleDTO>();
				for (ReciboDetalleDTO item : this.selectedPago.getDetalles()) {
					if(item.getMovimiento().isTesaka())
						this.selectedItems.add(item);
				}
			}
		}
	}
	
	
	@Command 
	@NotifyChange("selectedPeriodo")
	public void uploadFile(@BindingParam("file") Media file) {
		try {
			TesakaParser.uploadArchivoRetencion(file, this.selectedPeriodo,
					this.getLoginNombre(), this.selectedTipoRetencion);
			Clients.showNotification("Archivo correctamente importado..");
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command
	public void imprimir() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Retencion> rets = rr.getRetenciones(this.selectedPeriodo, this.selectedTipoRetencion);
		List<Object[]> data = new ArrayList<Object[]>();

		for (Retencion ret : rets) {
			Object[] obj = new Object[] { ret.getFecha(), ret.getNumero(),
					ret.getRuc(), ret.getRazonSocial(), ret.getNumeroFactura(),
					ret.getNumeroOrdenPago(), ret.getImporteGs() };
			data.add(obj);
		}

		ReporteYhaguy rep = new ReporteRetenciones(
				this.getMes(this.selectedPeriodo), this.selectedTipoRetencion);
		rep.setDatosReporte(data);
		rep.setA4();
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/***************** GETTER/SETTER *****************/
	
	@DependsOn("selectedItems")
	public boolean isGenerarArchivoDisabled() {
		return (this.selectedItems == null) || (this.selectedItems.size() == 0);
	}
	
	@DependsOn("selectedPago")
	public boolean isCheckmarkVisible() {
		return this.selectedPago.getTesaka() == null
				|| this.selectedPago.getTesaka().isEmpty();
	}
	
	/**
	 * @return los periodos..
	 */
	public List<Integer> getPeriodos() {
		List<Integer> out = new ArrayList<Integer>();
		out.add(1);out.add(2);out.add(3);
		out.add(4);out.add(5);out.add(6);
		out.add(7);out.add(8);out.add(9);
		out.add(10);out.add(11);out.add(12);
		return out;
	}
	
	/**
	 * @return el mes segun el periodo..
	 */
	public String getMes(int mes) {
		return this.m.getMesEnLetras(mes).toUpperCase();
	}
	
	/**
	 * @return los tipos de retenciones..
	 */
	public List<String> getTiposRetenciones() {
		List<String> out = new ArrayList<String>();
		out.add(Retencion.EMITIDAS);
		out.add(Retencion.RECIBIDAS);
		return out;
	}
	
	/**
	 * @return el mensaje de confirmacion para generar el archivo de retencion..
	 */
	private String getMensajeConfirmacion() {
		String out = "Se generara el archivo de retencion para las facturas seleccionadas: \n\n";
		for (ReciboDetalleDTO item : this.selectedItems) {
			out += ("- " + item.getMovimiento().getNroComprobante().replace("(1/1)", "") + " \n");
		}
		out += "\n Desea continuar..?";
		return out;
	}
	
	public ReciboDTO getSelectedPago() {
		return selectedPago;
	}

	public void setSelectedPago(ReciboDTO selectedPago) {
		this.selectedPago = selectedPago;
	}

	public List<ReciboDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<ReciboDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public int getSelectedPeriodo() {
		return selectedPeriodo;
	}

	public void setSelectedPeriodo(int selectedPeriodo) {
		this.selectedPeriodo = selectedPeriodo;
	}

	public String getSelectedTipoRetencion() {
		return selectedTipoRetencion;
	}

	public void setSelectedTipoRetencion(String selectedTipoRetencion) {
		this.selectedTipoRetencion = selectedTipoRetencion;
	}
}

/**
 * Reporte de Retenciones..
 */
class ReporteRetenciones extends ReporteYhaguy {
	
	private String periodo;	
	private String tipoRetencion;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 30);
	static DatosColumnas col4 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Factura", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Orden Pago", TIPO_STRING, 40);
	static DatosColumnas col7 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true);
	
	public ReporteRetenciones(String periodo, String tipoRetencion) {
		this.periodo = periodo;
		this.tipoRetencion = tipoRetencion.toUpperCase();
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Retenciones Tesaka");
		this.setDirectorio("tesaka");
		this.setNombreArchivo("Ret-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Periodo", this.periodo))
				.add(this.textoParValor("Tipo", this.tipoRetencion)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
