package com.yhaguy.util.comision;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;

import com.coreweb.util.Misc;

public class VendedorCrespi extends Vendedor {

	
	

	
	public String printResumen(){
		Misc m = new Misc();
		NumberFormat formatter = new DecimalFormat("###,###,###");
		NumberFormat formatterCorto = new DecimalFormat("#,###,###");
		NumberFormat formatterPor = new DecimalFormat("###.00 %");
		
		String strVenta = formatter.format(this.getTotalVenta());
		String strVentaContado = formatter.format(this.getTotalContado());
		String strVentaCredito = formatter.format(this.getTotalCredito());

		String strCobranza = formatter.format(this.getTotalCobranza());
		
		double comision = this.getTotalComision();
		double devolucion = this.getTotalDevolucion();
		double porDevolucion = 0;

		if (this.getTotalVenta() != 0){
			porDevolucion = devolucion  / this.getTotalVenta();
			if (porDevolucion < 0){
				porDevolucion = porDevolucion * -1;
			}
		}
		double plusDev = Cons.calculoDevolucion(this.getTotalVenta(), porDevolucion);
		
		String strDevolucion = formatter.format(devolucion);
		String strPorDevolucion = formatterPor.format(porDevolucion);
		String strPlusDevolucion = formatterCorto.format(plusDev);
		String strComision = formatter.format(comision);
		String strComisionTotal = formatter.format(plusDev+comision);

		String porContado = formatterPor.format(Cons.getPorcMostrador(this.getId(), Cons.CONTADO));
		String porCredito = formatterPor.format(Cons.getPorcMostrador(this.getId(), Cons.COBRANZA));
		
		
		String out = "";
		out +=  m.formato("("+this.getIdFormato()+")", 8);
		out +=  m.formato("["+this.getUsuario()+"]", 14);
		out +=  m.formato(this.getNombre(), 20 ) ;
		
//		out +=  m.formato(porContado, 8, false);		
//		out +=  m.formato(porCredito, 8, false);		
		
		out +=  m.formato(strVentaContado, 17, false);
		out +=  m.formato(strVentaCredito, 17, false);
		out +=  m.formato(strCobranza, 17, false);
		out +=  m.formato(strDevolucion, 17, false);
		out +=  m.formato(strPorDevolucion, 8, false);		
		out +=  m.formato(strPlusDevolucion, 13, false);
		out +=  m.formato( strComision, 17, false);
		out +=  m.formato(strComisionTotal, 17, false);
		return out;
	}

	public static String printResumenCabecera(){
		Misc m = new Misc();

		String out1 = "";
		out1 += "----------------------------------\n";
		out1 += "    	VENDEDORES LOS CRESPI      \n";
		out1 += "----------------------------------\n";
		out1 += "\n";
		
		String out2 = "";
		out2 +=  m.formato("idVend.", 8);
		out2 +=  m.formato("idUser", 14);
		out2 +=  m.formato("Nombre", 20 ) ;
//		out2 +=  m.formato("% Ctdo", 8 , false) ;
//		out2 +=  m.formato("% Cred", 8 , false) ;
		out2 += m.formato("Venta Cdo mes", 17, false);
		out2 += m.formato("Venta Cred mes", 17, false);
		out2 +=  m.formato("Cobranza total", 17, false);
		out2 +=  m.formato("Devolucion", 17, false);
		out2 +=  m.formato("% Dev.", 8, false);
		out2 +=  m.formato("Plus dev.", 13, false);
		out2 +=  m.formato("Com. Venta", 17, false);
		out2 +=  m.formato("Total", 17, false);
		
		String out3 = m.repeatSrt("-", out2.length());

		
		return out1 + out2 + "\n" + out3;
	}

	
	public String printMetasPorMarca(){
		String out ="";
		
		NumberFormat formatter = new DecimalFormat("###,###,###");
		NumberFormat formatterPor = new DecimalFormat("###.00 %");
		
		Enumeration<ResumenMarca> enu = this.getResumenMarca().elements();
		
		Misc m = new Misc();
		
		out+= m.formato("IdMarca", 15); 
		out+= m.formato("Total", 15, false);
		out+= m.formato("Por..", 8, false); 
		out+= "\n";
		out+= "--------------------------------------------------------------\n";
		

		while (enu.hasMoreElements()) {
			ResumenMarca rm = (ResumenMarca) enu.nextElement();
			String idMarca = rm.getIdMarca();
			double total = rm.getTotalVenta();

			String marca = m.formato(rm.getNombreMarca(), 15);
			String strVenta = m.formato(formatter.format(total), 15, false);
			
			
			double por = ConsCrespi.getPorcentaje(this.getId(), idMarca, total, false, true);
			String porStr = m.formato(formatterPor.format(por), 8, false);
			
			
			out+= marca +strVenta + porStr + "     "+idMarca + "\n";
			
		}

		return out;
	}
	
}
