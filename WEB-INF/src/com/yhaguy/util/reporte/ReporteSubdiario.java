package com.yhaguy.util.reporte;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;

import com.coreweb.extras.reporte.Templates;
import com.coreweb.util.Misc;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SubDiario;
import com.yhaguy.gestion.contabilidad.subdiario.AssemblerSubDiario;
import com.yhaguy.gestion.contabilidad.subdiario.SubDiarioDTO;
import com.yhaguy.gestion.contabilidad.subdiario.SubDiarioDetalleDTO;

//import com.saraki.gestion.curriculum.CurriculumVitaeDTO;

public class ReporteSubdiario extends ReporteYhaguy {

	String descAncho = "200";
	Date desde = new Date();
	Date hasta = new Date();
	Misc misc = new Misc();

	SubDiarioDTO sd = new SubDiarioDTO();
	List<SubDiarioDTO> listaSD = new ArrayList<SubDiarioDTO>();

	public List<SubDiarioDTO> getListaSD() {
		return listaSD;
	}

	public void setListaSD(List<SubDiarioDTO> listaSD) {
		this.listaSD = listaSD;
	}

	public SubDiarioDTO getSd() {
		return sd;
	}

	public void setSd(SubDiarioDTO sd) {
		this.sd = sd;
	}

	public String getDesde() {
		return (this.misc.dateToString(desde, this.misc.DD_MM_YYYY ));
		
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public String getHasta() {
		return (this.misc.dateToString(hasta, this.misc.DD_MM_YYYY ));
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	@Override
	public void informacionReporte() {
		try {

			// titulo del reporte
			this.setTitulo("Reporte de Subdiario");
			this.setBody(this.getCabecera());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ComponentBuilder getCabecera() throws Exception {

		Misc m = new Misc();
		VerticalListBuilder out = null;
		out = cmp.verticalList();

		HorizontalListBuilder f0 = cmp.horizontalList();
		HorizontalListBuilder f1 = cmp.horizontalList();
		// HorizontalListBuilder f2 = cmp.horizontalList();

		f0.add(this.textoNegrita("Fecha:"+ this.getDesde()+" / "+ this.getHasta()));
		//f0.add(this.textoNegrita("Fecha desde "+ this.getDesde()+" hasta "+ this.getHasta()));
		// f0.add(this.textoNegrita("SISTEMA CONTABLE"));
		// f0.add(this.textoParValor("Fecha", "csad"));//obtener fecha de hoy
		out.add(f0);
		out.add(cmp.line());

		f1.add(this.textoNegrita("NUMERO/FECHA"));
		// f1.add(this.textoNegrita("DESCRIPCION"));
		f1.add(this.texto("DESCRIPCION", NEGRITA + WIDTH + descAncho));

		f1.add(this.textoNegritaDerecha("DEBE"));
		f1.add(this.textoNegritaDerecha("HABER"));
		out.add(f1);
		out.add(cmp.line());

		List<SubDiarioDetalleDTO> seccion;

		for (SubDiarioDTO sdItem : listaSD) {

			HorizontalListBuilder f2 = cmp.horizontalList();
			// f2.add(this.textoNegrita(sdItem.getNumero()+"  "+sdItem.getFecha()));
			f2.add(this.textoNegrita(sdItem.getNumero()));
			f2.add(this.texto(sdItem.getDescripcion(), NEGRITA + WIDTH
					+ descAncho));
			f2.add(this.texto(""));
			f2.add(this.texto(""));
			out.add(f2);
			// out.add(cmp.line());

			seccion = new ArrayList<SubDiarioDetalleDTO>();
			seccion = sdItem.getDetalles();
			out.add(this.seccionCV(seccion));
			out.add(cmp.line());

		}

		return out;
	}

	private ComponentBuilder seccionCV(List<SubDiarioDetalleDTO> listaItems)
			throws Exception {

		VerticalListBuilder out = null;
		out = cmp.verticalList();

		// out.add(cmp.verticalGap(20));

		for (SubDiarioDetalleDTO item : listaItems) {
			out.add(this.getSudItem(item));
		}
		out.add(cmp.verticalGap(20));

		return out;
	}

	public HorizontalListBuilder getSudItem(SubDiarioDetalleDTO item) {
		String itemString = "";

		HorizontalListBuilder f = cmp.horizontalList();

		if (item.isEsHaber() == true) {
			f.add(this.texto("  " + item.getCuenta().getPos4().toString()));
			f.add(this.texto("  " + item.getCuenta().getPos2().toString(),
					WIDTH + descAncho));
			f.add(this.texto(""));
			f.add(this.textoAlineadoDerecha("	" + item.getHaber()));
		} else {
			f.add(this.texto("" + item.getCuenta().getPos4().toString()));
			f.add(this.texto("" + item.getCuenta().getPos2().toString(), WIDTH
					+ descAncho));
			f.add(this.textoAlineadoDerecha("	" + item.getDebe()));
			f.add(this.texto(""));
		}
		return f;
	}

	public static void main(String[] args) throws Exception {

		List<Object[]> data = new ArrayList<>();

		Object[] obj = new Object[6];

		SubDiarioDTO sd = null;

		List<SubDiarioDTO> listaSD = new ArrayList<SubDiarioDTO>();

		RegisterDomain rr = RegisterDomain.getInstance();

		List<SubDiario> listaSDdom = rr.getObjects(SubDiario.class.getName());
		// SubDiario sdDom = (SubDiario) rr
		// .getObject(SubDiario.class.getName(), 1);
		AssemblerSubDiario ass = new AssemblerSubDiario();
		System.out.println("-------->" + listaSDdom.size());
		int c = 0;
		for (int i = 0; i < listaSDdom.size(); i++) {
			System.out.println("->" + c++ + " ---- "
					+ listaSDdom.get(i).getNumero());
			sd = (SubDiarioDTO) ass.domainToDto((SubDiario) listaSDdom.get(i));
			listaSD.add(sd);
		}
		// sd = (SubDiarioDTO) ass.domainToDto(sdDom);

		ReporteSubdiario dr = new ReporteSubdiario();
		// dr.setSd(sd);
		dr.setListaSD(listaSD);

		dr.ejecutar(true);
	}

}
