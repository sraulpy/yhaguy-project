package com.yhaguy.util.comision;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;

import com.coreweb.util.Misc;

public class ResumenMarca  implements Comparator {

	private String idMarca = "no_idMarca";
	private String nombreMarca = "No_nombreMarca";

	private double contadoTotal = 0;
	private double creditoTotal = 0;
	
	private double ventaContadoTotal = 0;
	private double ventaCreditoTotal = 0;
	
	
	
	
	/*
	private double contadoNoCompartido = 0;
	private double comisionContadoNoCompartido = 0;

	private double contadoCompartido = 0;
	private double comisionContadoCompartido = 0;

	private double creditoNoCompartido = 0;
	private double comisionCreditoNoCompartido = 0;

	private double creditoCompartido = 0;
	private double comisionCreditoCompartido = 0;
	*/

	public double getContadoTotal() {
		return contadoTotal;
	}


	public void setContadoTotal(double contadoTotal) {
		this.contadoTotal = contadoTotal;
	}


	public double getCreditoTotal() {
		return creditoTotal;
	}


	public void setCreditoTotal(double creditoTotal) {
		this.creditoTotal = creditoTotal;
	}


	public double getTotalVenta(){
		return (this.ventaContadoTotal + this.ventaCreditoTotal);
	}
	
	
	public void addVenta(Venta v) {
		if (v.getNroRecibo().trim().compareTo("0")==0){
			
			if (v.isContado()==true){
				ventaContadoTotal += v.getImporte();
			}else{
			    ventaCreditoTotal += v.getImporte();
			}
			return;
		}

		if (v.isContado()==true){
			contadoTotal += v.getImporte();
		}else{
			creditoTotal += v.getImporte();
		}
		
		/*
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
			Cons.error("*** ERROR Resumen Marca **:" + v.toString());
		}
*/
	}
	
	
	public String toString(){
		Misc m = new Misc();
		NumberFormat f = new DecimalFormat("###########");
		NumberFormat fs = new DecimalFormat("########");
		NumberFormat fp = new DecimalFormat("###.## %");
		
		return "====toString ResumenMarca ========";
		/*
		
		
		double total = this.comisionContadoNoCompartido + this.comisionContadoCompartido + this.comisionCreditoNoCompartido + this.comisionCreditoCompartido;
		String strTotal = m.formato(f.format(total), 17, false);

		String tab = m.formato(" ", (25+2));
		String out = "";
		out +=  m.formato("("+this.idMarca+")  " + this.nombreMarca,25) + "; ";
		out +=  m.formato(f.format(this.contadoNoCompartido), 17, false) + ";" + m.formato( fs.format(this.comisionContadoNoCompartido), 12, false) + ";"  + m.formato( fp.format(m.div(this.comisionContadoNoCompartido ,  this.contadoNoCompartido)), 7, false)+ ";" ;
		out +=  m.formato(f.format(this.contadoCompartido), 17, false)   + ";" + m.formato( fs.format(this.comisionContadoCompartido), 12, false) + ";"  +   m.formato( fp.format(m.div(this.comisionContadoCompartido , this.contadoCompartido)), 7, false)+ ";" ;
		out +=  m.formato(f.format(this.creditoNoCompartido), 17, false) + ";" + m.formato( fs.format(this.comisionCreditoNoCompartido), 12, false)+ ";" +   m.formato( fp.format(m.div(this.comisionCreditoNoCompartido , this.creditoNoCompartido)), 7, false)+ ";" ;
		out +=  m.formato(f.format(this.creditoCompartido), 17, false)   + ";" + m.formato( fs.format(this.comisionCreditoCompartido), 12, false)+ ";" +     m.formato( fp.format(m.div(this.comisionCreditoCompartido , this.creditoCompartido)), 7, false) + ";" + strTotal+ ";";
		out += "\n";
		return out;
		*/
		
	}


	public String getIdMarca() {
		return idMarca;
	}


	public void setIdMarca(String idMarca) {
		this.idMarca = idMarca;
		this.nombreMarca = ConsCrespi.getIdPorMarca(this.idMarca);
	}


	public String getNombreMarca() {
		return nombreMarca;
	}


	public void xsetNombreMarca(String nombreMarca) {
		this.nombreMarca = nombreMarca;
	}



	public int compareTo(Object arg0) {
		ResumenMarca rm = (ResumenMarca)arg0;
		return this.compareTo(rm.nombreMarca);
	}


	public int compare(Object arg0, Object arg1) {
		
		ResumenMarca rm0 = (ResumenMarca)arg0;
		ResumenMarca rm1 = (ResumenMarca)arg1;
		return rm0.nombreMarca.compareTo(rm1.nombreMarca);
	}

	
	
	
}
