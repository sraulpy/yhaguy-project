package com.yhaguy.gestion.notacredito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class NotaCreditoDetalleDTO extends DTO {

	private int cantidad = 0;
	
	private double costoGs = 0;
	private double montoGs = 0;
	private double montoDs = 0;
	
	private double importeDs = 0;	
	
	private MyPair tipoIva;
	
	private MyPair tipoDetalle = new MyPair();
	
	private MyArray articulo = new MyArray();
	private MyArray venta = new MyArray();
	private MyArray gasto = new MyArray();
	private MyArray compra = new MyArray();
	private MyArray importacion = new MyArray();
	
	private MyPair deposito;
	
	public NotaCreditoDetalleDTO() {
	}
	
	public NotaCreditoDetalleDTO(MyPair tipoIva) {
		this.tipoIva = tipoIva;
	}
	
	@DependsOn("tipoIva")
	public boolean isIva10() {
		String sigla = (String) this.tipoIva.getSigla();
		return sigla.equals(Configuracion.SIGLA_IVA_10);
	}
	
	@DependsOn("tipoIva")
	public boolean isIva5() {
		String sigla = (String) this.tipoIva.getSigla();
		return sigla.equals(Configuracion.SIGLA_IVA_5);
	}
	
	@DependsOn("tipoIva")
	public boolean isExenta() {
		String sigla = (String) this.tipoIva.getSigla();
		return sigla.equals(Configuracion.SIGLA_IVA_EXENTO);
	}
	
	@DependsOn("tipoIva")
	public double getIva10() {
		if (this.isIva10() == false)
			return 0;
		return this.getMisc().calcularIVA(this.getImporteGs(), 10);
	}

	@DependsOn("tipoIva")
	public double getIva5() {
		if (this.isIva5() == false)
			return 0;
		return this.getMisc().calcularIVA(this.getImporteGs(), 5);
	}
	
	@DependsOn("compra")
	public boolean isFacturaCompra() {
		return !this.compra.esNuevo();
	}
	
	@DependsOn("importacion")
	public boolean isFacturaImportacion() {
		return !this.importacion.esNuevo();
	}
	
	@DependsOn("gasto")
	public boolean isFacturaGasto() {
		return !this.gasto.esNuevo();
	}

	/**
	 * Retorna la descripcion de acuerdo al tipo de detalle..
	 */
	public String getDescripcion() {
		String out = "";
		
		if (isDetalleFactura() == false) {
			out += this.articulo.getPos1();
			
		} else if (isDetalleVenta() == true) {
			out += Utiles.getDateToString((Date) this.venta.getPos3(), Utiles.DD_MM_YYYY) 
					+ " - " + this.venta.getPos1() + " - " + this.venta.getPos2() ;
			
		} else if (isDetalleGasto() == true) {
			out += this.gasto.getPos1() + " - " + this.gasto.getPos2();
			
		} else if (isDetalleCompra() == true) {
			out += this.compra.getPos1() + " - " + this.compra.getPos2();
		
		} else if (isDetalleImportacion() == true) {
			out += this.importacion.getPos1() + " - " + this.importacion.getPos2();
		}
		
		return out;
	}
	
	/**
	 * Retorna true si es un detalle de tipo factura
	 */
	public boolean isDetalleFactura() {
		return this.tipoDetalle.getSigla().compareTo(
				Configuracion.SIGLA_TIPO_NC_DETALLE_FACTURA) == 0;
	}
	
	/**
	 * Retorna true si es un detalle de tipo factura venta..
	 */
	public boolean isDetalleVenta() {
		String out = (String) this.venta.getPos1();
		return out.length() > 0;
	}
	
	/**
	 * Retorna true si es un detalle de tipo factura gasto..
	 */
	public boolean isDetalleGasto() {
		String out = (String) this.gasto.getPos1();
		return out.length() > 0;
	}
	
	/**
	 * Retorna true si es un detalle de tipo factura compra..
	 */
	public boolean isDetalleCompra() {
		String out = (String) this.compra.getPos1();
		return out.length() > 0;
	}
	
	/**
	 * Retorna true si es un detalle de tipo factura compra..
	 */
	public boolean isDetalleImportacion() {
		String out = (String) this.importacion.getPos1();
		return out.length() > 0;
	}
	
	/**
	 * Para enlazar con la Cta Cte pos1: id de la factura - pos2: el MyArray tipoMovimiento..
	 */
	public MyArray getDatosCtaCte() {
		MyArray out = new MyArray();
		
		if (this.isDetalleVenta()) {
			MyPair tm = (MyPair) this.venta.getPos4();
			out.setPos1(this.venta.getId());
			out.setPos2(this.MypairToMyArray(tm));
		}
		
		return out;
	}
	
	/**
	 * Retorna los artículos contenidos en la Venta..
	 */
	public List<MyArray> getArticulosVenta() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<VentaDetalle> items = rr.getDetallesVenta(this.venta.getId());
		
		for (VentaDetalle item : items) {
			MyArray art = new MyArray();
			art.setId(item.getArticulo().getId());
			art.setPos1(item.getArticulo().getCodigoInterno());
			art.setPos2(item.getArticulo().getDescripcion());
			art.setPos3((int) item.getCantidad());
			art.setPos4(item.getPrecioGs());
			art.setPos5(item.getPrecioGs());
			art.setPos6((int) 0);
			art.setPos7((double) 0);
			art.setPos8(item.getDescuentoUnitarioGs());
			out.add(art);
		}
		
		return out;
	}
	
	/**
	 * Retorna los artículos contenidos en la Compra..
	 */
	public List<MyArray> getArticulosCompra() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CompraLocalFacturaDetalle> items = rr.getDetallesCompra(this.compra.getId());
		
		for (CompraLocalFacturaDetalle item : items) {
			MyArray art = new MyArray();
			art.setId(item.getArticulo().getId());
			art.setPos1(item.getArticulo().getCodigoInterno());
			art.setPos2(item.getArticulo().getDescripcion());
			art.setPos3(item.getCantidad());
			art.setPos4(item.getCostoGs());
			art.setPos5(item.getCostoDs());
			art.setPos6((int) 0);
			art.setPos7((double) 0);
			art.setPos8((double) 0);
			out.add(art);
		}		
		return out;
	}
	
	public int getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	public double getMontoGs() {
		return montoGs;
	}
	
	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}
	
	public double getMontoDs() {
		return montoDs;
	}
	
	public void setMontoDs(double montoDs) {
		this.montoDs = this.getMisc().redondeoDosDecimales(montoDs);
		this.importeDs = montoDs * cantidad;
	}
	
	@DependsOn({"montoGs", "cantidad"})
	public double getImporteGs() {
		return this.montoGs * this.cantidad;
	}	
	
	public void setImporteGs(double importeGs) {
	}
	
	@DependsOn({"montoDs", "cantidad"})
	public double getImporteDs() {
		return importeDs;
	}
	
	/**
	 * @return el codigo del articulo..
	 */
	public String getCodigoInterno() {
		return (String) this.articulo.getPos2();
	}
	
	/**
	 * @return la descripcion del articulo..
	 */
	public String getDescripcionArticulo() {
		return (String) this.articulo.getPos1();
	}
	
	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}
	
	public MyPair getTipoDetalle() {
		return tipoDetalle;
	}
	
	public void setTipoDetalle(MyPair tipoDetalle) {
		this.tipoDetalle = tipoDetalle;
	}
	
	public MyArray getArticulo() {
		return articulo;
	}
	
	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}
	
	public MyArray getVenta() {
		return venta;
	}
	
	public void setVenta(MyArray venta) {
		this.venta = venta;
	}
	
	public MyArray getGasto() {
		return gasto;
	}
	
	public void setGasto(MyArray gasto) {
		this.gasto = gasto;
	}
	
	public MyArray getCompra() {
		return compra;
	}
	
	public void setCompra(MyArray compra) {
		this.compra = compra;
	}	
	
	public MyPair getDeposito() {
		return deposito;
	}

	public void setDeposito(MyPair deposito) {
		this.deposito = deposito;
	}
	
	/**
	 * @return un myarray a partir de un mypair..
	 */
	private MyArray MypairToMyArray(MyPair mypair) {
		MyArray out = new MyArray();
		out.setId(mypair.getId());		
		out.setPos1(mypair.getText());
		return out;
	}

	public MyPair getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(MyPair tipoIva) {
		this.tipoIva = tipoIva;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public MyArray getImportacion() {
		return importacion;
	}

	public void setImportacion(MyArray importacion) {
		this.importacion = importacion;
	}
}
