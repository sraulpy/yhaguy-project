package com.yhaguy.util.comision;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;

import com.coreweb.util.Misc;

public class ResumenProveedor  implements Comparator {

	private String idProveedor = "no_idProveedor";
	private String nombreProveedor = "No_nombreProveedor";

	private double contadoNoCompartido = 0;
	private double comisionContadoNoCompartido = 0;

	private double contadoCompartido = 0;
	private double comisionContadoCompartido = 0;

	private double creditoNoCompartido = 0;
	private double comisionCreditoNoCompartido = 0;

	private double creditoCompartido = 0;
	private double comisionCreditoCompartido = 0;

	public void addVenta(Venta v) {
		if (v.getNroRecibo().trim().compareTo("0")==0){
			return;
		}
		
		
		if ((v.isContado() == true) && (v.isEsCompartida() == false)) {
			// venta contado NO compartida
			this.contadoNoCompartido += v.getImporte();
			this.comisionContadoNoCompartido += v.getComision();

		} else if ((v.isContado() == true) && (v.isEsCompartida() == true)) {
			// venta contado Compartida
			this.contadoCompartido += v.getImporte();
			this.comisionContadoCompartido += v.getComision();

		} else if ((v.isContado() == false) && (v.isEsCompartida() == false)) {
			// venta credito NO compartida
			this.creditoNoCompartido += v.getImporte();
			this.comisionCreditoNoCompartido += v.getComision();

		} else if ((v.isContado() == false) && (v.isEsCompartida() == true)) {
			// venta credito NO compartida
			this.creditoCompartido += v.getImporte();
			this.comisionCreditoCompartido += v.getComision();

		}else{
			Cons.error("*** ERROR Resumen Proveedor **:" + v.toString());
		}

	}
	
	
	public String toString(){
		Misc m = new Misc();
		NumberFormat f = new DecimalFormat("###########");
		NumberFormat fs = new DecimalFormat("########");
		NumberFormat fp = new DecimalFormat("###.## %");
		
		double total = this.comisionContadoNoCompartido + this.comisionContadoCompartido + this.comisionCreditoNoCompartido + this.comisionCreditoCompartido;
		String strTotal = m.formato(f.format(total), 17, false);

		String tab = m.formato(" ", (25+2));
		String out = "";
		out +=  m.formato("("+this.idProveedor+")  " + this.nombreProveedor,25) + "; ";
		out +=  m.formato(f.format(this.contadoNoCompartido), 17, false) + ";" + m.formato( fs.format(this.comisionContadoNoCompartido), 12, false) + ";"  + m.formato( fp.format(m.div(this.comisionContadoNoCompartido ,  this.contadoNoCompartido)), 7, false)+ ";" ;
		out +=  m.formato(f.format(this.contadoCompartido), 17, false)   + ";" + m.formato( fs.format(this.comisionContadoCompartido), 12, false) + ";"  +   m.formato( fp.format(m.div(this.comisionContadoCompartido , this.contadoCompartido)), 7, false)+ ";" ;
		out +=  m.formato(f.format(this.creditoNoCompartido), 17, false) + ";" + m.formato( fs.format(this.comisionCreditoNoCompartido), 12, false)+ ";" +   m.formato( fp.format(m.div(this.comisionCreditoNoCompartido , this.creditoNoCompartido)), 7, false)+ ";" ;
		out +=  m.formato(f.format(this.creditoCompartido), 17, false)   + ";" + m.formato( fs.format(this.comisionCreditoCompartido), 12, false)+ ";" +     m.formato( fp.format(m.div(this.comisionCreditoCompartido , this.creditoCompartido)), 7, false) + ";" + strTotal+ ";";
		out += "\n";
		return out;
	}

	public String xtoString(){
		Misc m = new Misc();
		NumberFormat f = new DecimalFormat("##,###,###,###");
		NumberFormat fs = new DecimalFormat("##,###,###");
		NumberFormat fp = new DecimalFormat("###.## %");
		
		double total = this.comisionContadoNoCompartido + this.comisionContadoCompartido + this.comisionCreditoNoCompartido + this.comisionCreditoCompartido;
		String strTotal = m.formato(f.format(total), 17, false);

		String tab = m.formato(" ", (25+2));
		String out = "";
		out +=  m.formato("("+this.idProveedor+") " + this.nombreProveedor,25) + " ";
		out +=  m.formato(f.format(this.contadoNoCompartido), 17, false) + m.formato( fs.format(this.comisionContadoNoCompartido), 12, false)   + m.formato( fp.format(m.div(this.comisionContadoNoCompartido , this.contadoNoCompartido)), 7, false) ;
		out +=  m.formato(f.format(this.contadoCompartido), 17, false) + m.formato( fs.format(this.comisionContadoCompartido), 12, false)   + m.formato( fp.format(m.div(this.comisionContadoCompartido , this.contadoCompartido)), 7, false) ;
		out +=  m.formato(f.format(this.creditoNoCompartido), 17, false) + m.formato( fs.format(this.comisionCreditoNoCompartido), 12, false) + m.formato( fp.format(m.div(this.comisionCreditoNoCompartido , this.creditoNoCompartido)), 7, false) ;
		out +=  m.formato(f.format(this.creditoCompartido), 17, false) + m.formato( fs.format(this.comisionCreditoCompartido), 12, false) + m.formato( fp.format(m.div(this.comisionCreditoCompartido , this.creditoCompartido)), 7, false) + strTotal;
		out += "\n";
		return out;
	}





	public String getIdProveedor() {
		return idProveedor;
	}


	public void setIdProveedor(String idProveedor) {
		this.idProveedor = idProveedor;
	}


	public String getNombreProveedor() {
		return nombreProveedor;
	}


	public void setNombreProveedor(String nombreProveedor) {
		this.nombreProveedor = nombreProveedor;
	}


	public int compareTo(Object arg0) {
		ResumenProveedor rm = (ResumenProveedor)arg0;
		return this.compareTo(rm.nombreProveedor);
	}


	public int compare(Object arg0, Object arg1) {
		
		ResumenProveedor rm0 = (ResumenProveedor)arg0;
		ResumenProveedor rm1 = (ResumenProveedor)arg1;
		return rm0.nombreProveedor.compareTo(rm1.nombreProveedor);
	}

	
	
	
}
