package com.yhaguy.process.tareasprogramadas;

import java.util.List;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.EnviarCorreo;

public class TareaNotificarRestriccionCostos {
	
	/**
	 * notificacion por email de reactivacion restriccion..
	 */
	public static void enviarCorreoRestriccion() {
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			String texto = "Se han reactivado la restricción de costos de los siguientes códigos:";
			
			List<Articulo> arts = rr.getArticulosSinRestriccion();
			for (Articulo articulo : arts) {
				texto += "\n " + articulo.getCodigoInterno();
				articulo.setRestriccionCosto(true);
				rr.saveObject(articulo, "sys");
			}
			
			if (arts.size() > 0) {
				
				String[] send = new String[] { "vanesar@yhaguyrepuestos.com.py" };
				String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
				String asunto = "Notificación Restricción Costos - " + Configuracion.empresa;
				EnviarCorreo enviarCorreo = new EnviarCorreo();
				enviarCorreo.sendMessage(send, sendCCO, asunto,
						texto,
						"Notificacion.pdf", null);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		TareaNotificarRestriccionCostos.enviarCorreoRestriccion();
	}
}
