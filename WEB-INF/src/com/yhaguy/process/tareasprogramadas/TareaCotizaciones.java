package com.yhaguy.process.tareasprogramadas;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.util.ConnectHttp;
import com.yhaguy.util.Utiles;

public class TareaCotizaciones {
	
	public static void main(String[] args) {
		try {
			String cotizaciones = ConnectHttp.getCotizaciones();
			JSONParser parser = new JSONParser(); 
			JSONObject json = (JSONObject) parser.parse(cotizaciones);
			JSONObject cab = (JSONObject) json.get("dolarpy");
			JSONObject set = (JSONObject) cab.get("set");
			Double venta = (Double) set.get("venta");
			Double compra = (Double) set.get("compra");
			
			RegisterDomain rr = RegisterDomain.getInstance();
			Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
			Tipo tipo = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CAMBIO_SET);
			TipoCambio tc = new TipoCambio();
			tc.setCompra(compra);
			tc.setVenta(venta);
			tc.setFecha(Utiles.getFechaActual());
			tc.setMoneda(moneda);
			tc.setTipoCambio(tipo);
			rr.saveObject(tc, "sys");
			System.out.println("Agregado " + tc.getCompra());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
