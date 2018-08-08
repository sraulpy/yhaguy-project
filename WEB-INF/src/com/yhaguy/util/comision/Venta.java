package com.yhaguy.util.comision;

import java.util.Comparator;

import com.coreweb.util.Misc;

public class Venta implements Comparator{

	private String id = "idVenta";
	private String idVendedor = "";
	private String idUsuario = "";
	private String nombreVendedor = "nombre vendedor no definido";
	private String nombreUsuario = "nombre usuario no definido";
	private String fecha = "dd.mm.aaaa";
	private String nroFactura = "nroFactura";
	private String idArticulo = "idArticulo";
	private String articulo = "articulo";
	private String idMarca = "idMarca";
	private String marca = "marca";
	private String idProveedor = "idProveedor";
	private String proveedor = "proveedor";
	private boolean esCompartida = false;
	private String esCompartidaTexto = "No";
	private String compartidaCon = "compartidaCon";
	private double importe = 0.0;
	private double porcentaje = 0.0;
	private double comision = 0.0;
	private boolean contado = false;
	private String contadoTexto = "Credito";
	private boolean cobrado = false;
	private boolean esVentaExterna = false;
	private long idTipoMovimiento = 0;
	private boolean esVentaDelMes = false;
	private String nroRecibo = "nroRecibo";
	private String fechaRecibo = "--.--.----";
	
	private double cantidad = 0;
	private double precioIva = 0;
	private double montoTotalRecibo = 0;
	private double montoDetalleRecibo = 0;
	private double debe = 0;
	private String parcial = " ";
	private double grabadoOriginal = 0;
	private double porcentajeParcial = 0;
	private String idPersona = "idPersona";
	private String persona = "Persona";
	

	public String toString() {
		String out = "";
		out = id + " " + fecha + " " + nroFactura + " (" + idVendedor + ") ["
				+ idUsuario + "] " + articulo;
		return out;
	}

	
	public double getGrabadoOriginal() {
		return grabadoOriginal;
	}


	public void setGrabadoOriginal(double grabadoOriginal) {
		this.grabadoOriginal = grabadoOriginal;
	}


	public double getPorcentajeParcial() {
		return porcentajeParcial;
	}


	public void setPorcentajeParcial(double porcentajeParcial) {
		this.porcentajeParcial = porcentajeParcial;
	}


	public double getMontoTotalRecibo() {
		return montoTotalRecibo;
	}
	
	public double getMontoDetalleRecibo() {
		return montoDetalleRecibo;
	}


	public void setMontoTotalRecibo(double montoTotalRecibo) {
		this.montoTotalRecibo = montoTotalRecibo;
	}
	

	public void setMontoDetalleRecibo(double montoDetalleRecibo) {
		this.montoDetalleRecibo = montoDetalleRecibo;
	}



	public double getDebe() {
		return debe;
	}



	public void setDebe(double debe) {
		this.debe = debe;
	}



	public String getNroRecibo() {
		return nroRecibo;
	}



	public void setNroRecibo(String nroRecibo) {
		this.nroRecibo = nroRecibo;
	}



	public String getFechaRecibo() {
		return fechaRecibo;
	}



	public void setFechaRecibo(String fechaRecibo) {
		this.fechaRecibo = fechaRecibo;
	}



	public boolean isEsVentaDelMes() {
		return esVentaDelMes;
	}


	public long getIdTipoMovimiento() {
		return idTipoMovimiento;
	}



	public void setIdTipoMovimiento(long idTipoMovimiento) {
		this.idTipoMovimiento = idTipoMovimiento;
	}


	public String getTipoMovimiento(){
		String out = "";
		out = "Venta";
		if (this.idTipoMovimiento == 13){
			out = "NDC";
		}
		return out;
	}

	public boolean isEsVentaExterna() {
		return esVentaExterna;
	}

	public void setEsVentaExterna(boolean esVentaExterna) {
		this.esVentaExterna = esVentaExterna;
	}

	public String getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(String idVendedor) {
		this.idVendedor = idVendedor;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
		if (this.fecha.substring(3).compareTo(Cons.MES_CORRIENTE) == 0){
			this.esVentaDelMes = true;
		}
	}

	public String getNroFactura() {
		return nroFactura;
	}

	public void setNroFactura(String nroFactura) {
		this.nroFactura = nroFactura;
	}

	public String getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(String idArticulo) {
		this.idArticulo = idArticulo;
	}

	public String getArticulo() {
		return articulo;
	}

	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}

	public String getIdProveedor() {
		return idProveedor;
	}


	public void setIdProveedor(String idProveedor) {
		this.idProveedor = idProveedor;
	}


	public String getProveedor() {
		return proveedor;
	}


	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}


	public String getIdMarca() {
		return idMarca;
	}

	public void setIdMarca(String idMarca) {
		this.idMarca = idMarca;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public boolean isEsCompartida() {
		return esCompartida;
	}

	public void setEsCompartida(boolean esCompartida) {
		this.esCompartida = esCompartida;
		if(this.esCompartida == true){
			this.esCompartidaTexto = "Compartida";
		}else{
			this.esCompartidaTexto = "NO   ";
		}
	}

	public String getCompartidaCon() {
		return compartidaCon;
	}

	public void setCompartidaCon(String compartidaCon) {
		this.compartidaCon = compartidaCon;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentaje) {
		this.porcentaje = porcentaje;
		this.comision = this.importe * this.porcentaje;
	}

	public double getComision() {
		return comision;
	}

	public boolean isContado() {
		return contado;
	}

	
	
	public String getParcial() {
		return parcial;
	}

	public void setParcial() {
		this.parcial = "P";
	}

	public void setContado(boolean contado) {
		this.contado = contado;
		if (this.contado == true){
			this.contadoTexto = "Contado";
		}else{
			this.contadoTexto = "Cred.";
		}
	}

	public boolean isCobrado() {
		return cobrado;
	}

	public void setCobrado(boolean cobrado) {
		this.cobrado = cobrado;
	}

	
	
	public String getNombreVendedor() {
		return nombreVendedor;
	}

	public void setNombreVendedor(String nombreVendedor) {
		this.nombreVendedor = nombreVendedor;
	}

	
	
	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioIva() {
		return precioIva;
	}

	public void setPrecioIva(double precioIva) {
		this.precioIva = precioIva;
	}
	
	
	

	public String getIdPersona() {
		return idPersona;
	}


	public void setIdPersona(String idPersona) {
		this.idPersona = idPersona;
	}


	public String getPersona() {
		return persona;
	}


	public void setPersona(String persona) {
		this.persona = persona;
	}


	public static String printFormatoCabecera() {
		Misc m = new Misc();
		String out = "";
		out += " " + m.formato(" ", 3);
		out += " " + m.formato("Fecha Rec. Numero", 19);
		out += " " + m.formato("Con/Cre", 7, true);
		out += " " + m.formato("Fecha", 10, false);
		out += " " + m.formato("CMP", 5, false);
		out += " " + m.formato("Nro", 8, false);
		out += " " + m.formato("Cliente", 15, true);		
		out += " " + m.formato("ART", 5, false);
		out += " " + m.formato("DESCRIPCION", 15, true);
		out += " " + m.formato("PRO", 5, false);
		out += " " + m.formato("PROVEEDOR", 15, true);
		out += " " + m.formato("MK", 5, false);
		out += " " + m.formato("MARCA", 15, true);
		out += " " + m.formato("Comp", 4, true);
		out += " " + m.formato("USUARIO", 8, true);
		out += " " + m.formato("IMPORTE", 14, false);
		out += " " + m.formato("Por %", 9, false);
		out += " " + m.formato("COMISION", 10, false);
		String raya = m.repeatSrt("-", out.length());
		out = raya +"\n"+ out +"\n"+ raya;
		return out;
	}

	public String printFormato(boolean externo) {
		return printFormato(externo, 1);
	}
	
	
	public String printFormato(boolean externo, double porAsignada) {
		String mes = "";
		if (this.esVentaDelMes == true){
			mes = "si";
		}
		
		Misc m = new Misc();
		String out = "";
		out += " " + m.formato(mes, 3);		
		out += " " + m.formato(this.fechaRecibo, 10);
		out += " " + m.formato(this.nroRecibo, 8, false);
		out += " " + m.formato(this.contadoTexto, 7, true);
		out += " " + m.formato(this.fecha, 10, false);
		out += " " + m.formato(this.getTipoMovimiento(), 5);
		out += " " + m.formato(this.nroFactura, 8, false);
		out += " " + m.formato(this.persona, 15, true);
		out += " " + m.formato(this.idArticulo, 5, false);
		out += " " + m.formato(this.articulo, 15, true);
		out += " " + m.formato(this.idProveedor, 5, false);
		out += " " + m.formato(this.proveedor, 15, true);
		out += " " + m.formato(this.idMarca, 5, false);
		out += " " + m.formato(this.marca, 15, true);
		out += " " + m.formato(this.esCompartidaTexto, 4, true);
		out += " " + m.formato(this.idUsuario, 8, true);
		out += " " + m.formato(this.importe, 14, false);
		out += " " + (m.formato((this.porcentaje * 100), 8, false, true))+"%";
		out += " " + m.formato((this.comision * porAsignada), 10, false, true);
		out += " | " ;

		String nombreAux = " ";
		if ((externo == false)&&(this.esCompartida == true)){
			nombreAux = this.nombreVendedor;
		}
		if ((externo == true)&&(this.esCompartida == true)){
			nombreAux = this.idUsuario;
		}

		//dr
		nombreAux = this.nombreVendedor;
		
		
		out += " " + m.formato(nombreAux, 10);
		out += " " + this.getParcial() + " ("+(m.formato((this.getPorcentajeParcial() * 100), 8, false, true))+"%"+") " + m.formato(this.getGrabadoOriginal(), 14, false);

		//	out += "  (" + m.formato(this.id, 10, false)+")";

		return out;
	}

	
	public String getFecha_AAAA_MM_DD(){
		if (this.getFecha().length() <10){
			Cons.error("error de fecha:"+this.getFecha()+ "  " +this.toString());
			return "err. fecha";
		}
		String f = this.getFecha();
		f = f.substring(6, 10) + "." + f.substring(3, 5) + "." + f.substring(0, 2);
		return f;
	}
	
	
	
	public int xcompareTo(Object o) {
		Venta v = (Venta)o;
		Misc m = new Misc();

		String aux  = this.getFecha_AAAA_MM_DD() + m.formato(this.getNroFactura(),8,true);
		String auxV = v.getFecha_AAAA_MM_DD() + m.formato(v.getNroFactura(),8,true);

		aux  = this.getFecha_AAAA_MM_DD();
		auxV = v.getFecha_AAAA_MM_DD();

		return aux.compareTo(auxV);
	}

	@Override
	public int compare(Object o1, Object o2) {
		Misc m = new Misc();

		Venta v1 = (Venta)o1;
		Venta v2 = (Venta)o2;

		String aux1 = v1.getFecha_AAAA_MM_DD() + m.formato(v1.getNroFactura(),8,true);
		String aux2 = v2.getFecha_AAAA_MM_DD() + m.formato(v2.getNroFactura(),8,true);

		return aux1.compareTo(aux2);
	}



	public void vaciar(){
		this.fecha = "dd.mm.aaaa";
		this.nroFactura = "nroFactura";
		this.idArticulo = "idArticulo";
		this.articulo = "articulo";
		this.idMarca = "idMarca";
		this.marca = "marca";
		this.idProveedor = "idProveedor";
		this.proveedor = "proveedor";
		this.esCompartida = false;
		this.esCompartidaTexto = "No";
		this.compartidaCon = "compartidaCon";
		this.importe = 0.0;
		this.porcentaje = 0.0;
		this.comision = 0.0;
		this.contado = false;
		this.contadoTexto = "Credito";
		this.cobrado = false;
		this.esVentaExterna = false;
		this.idTipoMovimiento = 0;
		this.esVentaDelMes = false;
		this.nroRecibo = "nroRecibo";
		this.fechaRecibo = "--.--.----";
		
		this.cantidad = 0;
		this.precioIva = 0;
		this.montoTotalRecibo = 0;
		this.montoDetalleRecibo = 0;
		this.debe = 0;
		this.parcial = " ";
		this.grabadoOriginal = 0;
		this.porcentajeParcial = 0;
		this.idPersona = "idPersona";
		this.persona = "Persona";
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 255; i++) {
			System.out.println(i+"["+((char)i)+"]");
		}
		
		
	}



}
