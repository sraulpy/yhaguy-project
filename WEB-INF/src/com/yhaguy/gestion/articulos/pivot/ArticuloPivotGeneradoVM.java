package com.yhaguy.gestion.articulos.pivot;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.domain.ArticuloPivot;
import com.yhaguy.domain.ArticuloPivotDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class ArticuloPivotGeneradoVM extends SimpleViewModel {
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private ArticuloPivot selectedItem;
	
	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void imprimir() {
		this.imprimir_();
	}
	
	/**
	 * impresion de la compra..
	 */
	private void imprimir_() {
		List<Object[]> data = new ArrayList<Object[]>();

		for (ArticuloPivotDetalle item : this.selectedItem.getDetalles()) {
			Object[] obj1 = new Object[] { item.getArticulo().getCodigoInterno(),
					item.getArticulo().getDescripcion(), item.getCantidad() };
			data.add(obj1);
		}

		ReporteYhaguy rep = new ReporteEnlaceGenerado(this.selectedItem);
		rep.setDatosReporte(data);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	}

	/**
	 * GET'S / SET'S
	 */
	@DependsOn({ "filterFechaAA", "filterFechaMM", "filterFechaDD" })
	public List<ArticuloPivot> getEnlaces() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulosPivotGenerados(this.getFilterFecha());
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaAA.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaAA.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaAA;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaAA;
		return this.filterFechaAA + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}

	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}

	public ArticuloPivot getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ArticuloPivot selectedItem) {
		this.selectedItem = selectedItem;
	}
}

/**
 * Reporte de Enlace generado..
 */
class ReporteEnlaceGenerado extends ReporteYhaguy {
	
	ArticuloPivot enlace;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cant.", TIPO_INTEGER, 15, false);
	
	public ReporteEnlaceGenerado(ArticuloPivot enlace) {
		this.enlace = enlace;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Orden de Compra");
		this.setDirectorio("compras/locales");
		this.setNombreArchivo("CompraPivot-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		
		String proveedor = this.enlace.getProveedor().getRazonSocial();

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Proveedor", proveedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
