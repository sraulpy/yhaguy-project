package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.coreweb.extras.browser.Browser2;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VentaFiscal;

public class BrowserLibroVenta extends Browser2 {

	String query = "Select vf.emision, vf.condicion, vf.numero, vf.timbrado, vf.razonSocial, vf.ruc, vf.importeGs,"
			+ " vf.moneda, vf.tipoCambio, vf.tipoIva, vf.gravada, vf.iva, vf.exenta"
			+ " from VentaFiscal vf  ";

	@Override
	public String getCabeceraZulUrl() {
		// TODO Auto-generated method stub
		return "/yhaguy/gestion/contabilidad/subdiario/visor_LibroVenta.zul";
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser fecha = new ColumnaBrowser();
		fecha.setCampo("vf.emision");
		fecha.setComponente("getFechaFromato");
		fecha.setTitulo("Fecha");
		fecha.setWidthColumna("80px");
		fecha.setWidthComponente("50px");

		ColumnaBrowser tipo = new ColumnaBrowser();
		tipo.setCampo("vf.condicion");
		tipo.setTitulo("Tipo");
		tipo.setWidthColumna("80px");
		tipo.setWidthComponente("50px");

		ColumnaBrowser numero = new ColumnaBrowser();
		numero.setCampo("vf.numero");
		numero.setTitulo("Número");
		numero.setWidthColumna("80px");
		numero.setWidthComponente("50px");

		ColumnaBrowser timbrado = new ColumnaBrowser();
		timbrado.setCampo("vf.timbrado");
		timbrado.setTitulo("Timbrado");
		timbrado.setWidthColumna("80px");
		timbrado.setWidthComponente("50px");

		ColumnaBrowser proveedor = new ColumnaBrowser();
		proveedor.setCampo("vf.razonSocial");
		proveedor.setTitulo("Proveedor");
		proveedor.setWidthColumna("150px");
		proveedor.setWidthComponente("120px");

		ColumnaBrowser ruc = new ColumnaBrowser();
		ruc.setCampo("vf.ruc");
		ruc.setTitulo("Ruc");
		ruc.setWidthColumna("90px");
		ruc.setWidthComponente("60px");

		ColumnaBrowser importeGs = new ColumnaBrowser();
		importeGs.setCampo("vf.importeGs");
		importeGs.setTitulo("Importe Gs");
		importeGs.setComponente("getGuaraniComp");
		importeGs.setWidthColumna("120px");
		importeGs.setWidthComponente("90px");

		ColumnaBrowser moneda = new ColumnaBrowser();
		moneda.setCampo("vf.moneda");
		moneda.setTitulo("Moneda");
		moneda.setWidthColumna("80px");
		moneda.setWidthComponente("50px");

		ColumnaBrowser tipoCambio = new ColumnaBrowser();
		tipoCambio.setCampo("vf.tipoCambio");
		tipoCambio.setComponente("getGuaraniComp");
		tipoCambio.setTitulo("Tipo Cambio");
		tipoCambio.setWidthColumna("100px");
		tipoCambio.setWidthComponente("70px");

		ColumnaBrowser tipoIva = new ColumnaBrowser();
		tipoIva.setCampo("vf.tipoIva");
		tipoIva.setTitulo("Tipo Iva");
		tipoIva.setWidthColumna("80px");
		tipoIva.setWidthComponente("50px");

		ColumnaBrowser gravada = new ColumnaBrowser();
		gravada.setCampo("vf.gravada");
		gravada.setComponente("getGuaraniComp");
		gravada.setTitulo("Gravada");
		gravada.setWidthColumna("120px");
		gravada.setWidthComponente("90px");

		ColumnaBrowser iva = new ColumnaBrowser();
		iva.setCampo("vf.iva");
		iva.setComponente("getGuaraniComp");
		iva.setTitulo("Iva");
		iva.setWidthColumna("100px");
		iva.setWidthComponente("70px");

		ColumnaBrowser exenta = new ColumnaBrowser();
		exenta.setCampo("vf.exenta");
		exenta.setComponente("getGuaraniComp");
		exenta.setTitulo("Exenta");
		exenta.setWidthColumna("100px");
		exenta.setWidthComponente("70px");

		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(fecha);
		columnas.add(tipo);
		columnas.add(numero);
		columnas.add(timbrado);
		columnas.add(proveedor);
		columnas.add(ruc);
		columnas.add(importeGs);
		columnas.add(moneda);
		columnas.add(tipoCambio);
		columnas.add(tipoIva);
		columnas.add(gravada);
		columnas.add(iva);
		columnas.add(exenta);

		return columnas;
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public void setingInicial() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTituloBrowser() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public HtmlBasedComponent getFechaFromato(Object obj, Object[] datos) {
		Label l = new Label();
		l.setValue(this.m.dateToString((Date) obj, this.m.DD_MM_YYYY));
		return l;
	}

	public HtmlBasedComponent getDolarComp(Object obj, Object[] datos) {
		return getStyle(obj, datos, false);
	}

	public HtmlBasedComponent getGuaraniComp(Object obj, Object[] datos) {
		return getStyle(obj, datos, true);
	}

	public HtmlBasedComponent getStyle(Object obj, Object[] datos,
			boolean moneda) {
		Textbox l = new Textbox();
		l.setReadonly(true);
		l.setInplace(true);
		l.setStyle("text-align:right");
		double valor = (double) obj;
		if (moneda == true) {
			l.setValue(this.m.formatoGs(valor));
		} else if (moneda == false) {
			l.setValue(this.m.formatoDs(valor, 30, false));
		}
		return l;
	}

	public static void main(String[] args) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String query = "Select vf.emision, vf.condicion, vf.numero, vf.timbrado, vf.razonSocial, vf.ruc, vf.importeGs, vf.importeDs,"
				+ " vf.moneda, vf.tipoCambio, vf.tipoIva, vf.gravada, vf.iva, vf.exenta"
				+ " from VentaFiscal vf  ";

		VentaFiscal vf = new VentaFiscal();
		vf.setEmision(new Date());
		vf.setCondicion("Contado");
		vf.setNumero("100");
		vf.setTimbrado("100");
		vf.setRazonSocial("Yhaguy");
		vf.setRuc("4568-9");
		vf.setImporteGs(1000000);
		vf.setImporteDs(5000);
		vf.setMoneda("Dolar");
		vf.setTipoCambio(4850);
		vf.setIva(100000);
		vf.setGravada(900000);
		vf.setTipoIva("10%");

		rr.saveObject(vf, "pepe");

		System.out.println(query);
		List<Object[]> obj = rr.hql(query);
		for (Object oj[] : obj) {
			// System.out.println("fecha: "+oj[0]+"\nTipo: "+oj[1]+"\nNumero: "+oj[2]+"\nTimbrado: "+oj[3]+"\nProveedor: "+oj[4]+"\nRuc: "+oj[5]+"\nImporte: "+oj[6]);
			System.out.println("fecha: " + oj[0] + "\nTipo: " + oj[1]
					+ "\nNumero: " + oj[2] + "\nTimbrado: " + oj[3]
					+ "\nImporte: " + oj[4]);
		}
	}

}
