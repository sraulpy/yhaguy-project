package com.yhaguy.gestion.caja.periodo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.notacredito.NotaCreditoDTO;
import com.yhaguy.gestion.notacredito.NotaCreditoDetalleDTO;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class NotaCreditoDataSource implements JRDataSource, Cloneable {
	
	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	NotaCreditoDTO notacredito;
	MyPair iva10;

	public NotaCreditoDataSource() {
	}
	
	public NotaCreditoDataSource(NotaCreditoDTO notacredito, MyPair iva10) {
		this.notacredito = notacredito;
		this.iva10 = iva10;
	}
	
	public NotaCreditoDataSource clone() {
	    return new NotaCreditoDataSource(this.notacredito, this.iva10); 
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		NotaCreditoDetalleDTO det = this.getDetalles().get(this.index);
		if (this.notacredito.isMotivoDescuento())
			det = this.getItemDescuento();

		if ("Cantidad".equals(fieldName)) {
			value = String.valueOf(det.getCantidad());
		} else if ("Codigo".equals(fieldName)) {
			value = this.getMaxLength(det.getCodigoInterno(), 15);
		} else if ("Descripcion".equals(fieldName)) {
			value = this.getMaxLength(det.getDescripcionArticulo(), 23);
		} else if ("Precio".equals(fieldName)) {
			value = FORMATTER.format(det.getMontoGs());
		} else if ("ImporteExenta".equals(fieldName)) {
			value = FORMATTER.format(det.isExenta() ? det.getImporteGs()
					: 0);
		} else if ("ImporteIva5".equals(fieldName)) {
			value = FORMATTER.format(det.isIva5() ? det.getImporteGs() : 0);
		} else if ("ImporteIva10".equals(fieldName)) {
			value = FORMATTER
					.format(det.isIva10() ? det.getImporteGs() : 0);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		System.out.println("---- INDEX: " + index);
		System.out.println("---- NOTACREDITO: " + notacredito);
		System.out.println("---- DETALLES: " + getDetalles());
		if (index < this.getDetalles().size() - 1) {
			index++;
			return true;
		}
		return false;
	}

	/**
	 * @return el string con longitud maxima..
	 */
	private String getMaxLength(String string, int max) {
		if (string.length() > max)
			return string.substring(0, max) + "...";
		return string;
	}

	/**
	 * @return los detalles segun el motivo..
	 */
	private List<NotaCreditoDetalleDTO> getDetalles() {
		if (this.notacredito.isMotivoDescuento())
			return this.notacredito.getDetallesFacturas();
		return this.notacredito.getDetallesArticulos();
	}

	/**
	 * @return el item descuento concedido..
	 */
	private NotaCreditoDetalleDTO getItemDescuento() {
		double importeGs = (double) this.notacredito.getImportesFacturas()[0];

		MyArray art = new MyArray("DESCUENTO CONCEDIDO", "@DESCUENTO");
		NotaCreditoDetalleDTO out = new NotaCreditoDetalleDTO(this.iva10);
		out.setArticulo(art);
		out.setCantidad(1);
		out.setMontoGs(importeGs);
		out.setImporteGs(importeGs);
		return out;
	}
}
