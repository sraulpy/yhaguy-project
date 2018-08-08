package com.yhaguy.gestion.caja.periodo;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.venta.VentaDTO;
import com.yhaguy.gestion.venta.VentaDetalleDTO;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class VentaDataSource implements JRDataSource, Cloneable {
	
	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	VentaDTO venta;
	
	public VentaDataSource() {
	}
	
	public VentaDataSource clone() {
	    return new VentaDataSource(this.venta); 
	}

	public VentaDataSource(VentaDTO venta) {
		this.venta = venta;
		
		if (this.venta.isVentaConDescuento()) {
			VentaDetalleDTO itemPrint = null;
			for (VentaDetalleDTO item : this.venta.getDetalles()) {
				if (item.isImpresionDescuento()) {
					itemPrint = item;
				}
			}
			this.venta.getDetalles().remove(itemPrint);
			MyArray art = new MyArray();
			art.setPos1("DESCUENTO");
			art.setPos4("DESCUENTO");
			VentaDetalleDTO item = new VentaDetalleDTO();
			item.setArticulo(art);
			item.setCantidad(1);
			item.setPrecioGs(venta.getTotalDescuentoGs() * -1);
			item.setTipoIVA(new MyPair());
			item.setImpresionDescuento(true);				
			this.venta.getDetalles().add(item);
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		VentaDetalleDTO det = this.venta.getDetalles().get(this.index);

		if ("Cantidad".equals(fieldName)) {
			value = String.valueOf(det.getCantidad());
		} else if ("Codigo".equals(fieldName)) {
			value = this.getMaxLength(det.getCodigoInterno(), 15);
		} else if ("Descripcion".equals(fieldName)) {
			value = this.getMaxLength(det.getDescripcionArticulo(), 28);
		} else if ("Precio".equals(fieldName)) {
			value = FORMATTER.format(det.getPrecioGs());
		} else if ("ImporteExenta".equals(fieldName)) {
			value = FORMATTER.format(0.0);
		} else if ("ImporteIva5".equals(fieldName)) {
			value = FORMATTER.format(0.0);
		} else if ("ImporteIva10".equals(fieldName)) {
			value = FORMATTER.format(det.getImporteGsSinDescuento());
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.venta.getDetalles().size() - 1) {
			index++;
			return true;
		}
		return false;
	}
	
	/**
	 * @return longitud maxima de un string..
	 */
	private String getMaxLength(String string, int max) {
		if (string.length() > max)
			return string.substring(0, max) + "...";
		return string;
	}
}
